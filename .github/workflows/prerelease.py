import json

import requests
import os

GITHUB_TOKEN = os.environ.get('GITHUB_TOKEN')
VIRUSTOTAL_API_KEY = os.environ.get('VIRUSTOTAL_API_KEY')
DISCORD_WEBHOOK = os.environ.get('DISCORD_WEBHOOK')
LAST_COMMIT = os.environ.get('LAST_COMMIT')
NEW_COMMIT = os.environ.get('NEW_COMMIT')


def main():
    directory = os.listdir('./build/libs')
    jar = [file for file in directory if file.endswith('.jar')][0]
    version = jar.split('-')[2].split('.jar')[0]

    virus_total_link = None
    if VIRUSTOTAL_API_KEY is not None:
        virus_total_req = requests.post(
            f'https://www.virustotal.com/api/v3/files',
            files={'file': (jar, open(f'./build/libs/{jar}', 'rb'), 'application/jar')},
            headers={
                'x-apikey': VIRUSTOTAL_API_KEY,
                'accept': 'application/json'
            }
        )
        if virus_total_req.status_code == 200:
            virus_total_id = virus_total_req.json()['data']['id']
            virus_total_link = f'https://www.virustotal.com/gui/file-analysis/{virus_total_id}/detection'

    changes = {}
    changes_message = '**Changes:**\n'
    if GITHUB_TOKEN is not None:
        github_req = requests.get(
            f'https://api.github.com/repos/DAMcraft/MeteorServerSeeker/compare/{LAST_COMMIT}...{NEW_COMMIT}',
            headers={
                'Authorization': f'Bearer {GITHUB_TOKEN}'
            }
        )
        if github_req.status_code == 200:
            for commit in github_req.json().get('commits', []):
                sha = commit['sha']
                message = commit['commit']['message']
                changes[sha] = message
                changes_message += (f'- [`{sha[:7]}`](https://github.com/DAMcraft/MeteorServerSeeker/commit/{sha}/) '
                                    f'{message}\n')

        # Delete old release
        get_tags = requests.get(
            f"https://api.github.com/repos/DAMcraft/MeteorServerSeeker/releases/tags/latest",
            headers={
                "Authorization": f"Bearer {GITHUB_TOKEN}"
            },
        )
        if get_tags.status_code == 200:
            old_release = get_tags.json()
            release_id = old_release['id']
            del_req = requests.delete(
                f"https://api.github.com/repos/DAMcraft/MeteorServerSeeker/releases/{release_id}",
                headers={
                    "Authorization": f"Bearer {GITHUB_TOKEN}"
                }
            )

        # New release
        req = requests.post(
            f"https://api.github.com/repos/DAMcraft/MeteorServerSeeker/releases",
            headers={
                'Authorization': f'Bearer {GITHUB_TOKEN}'
            },
            json={
                "tag_name": f"latest",
                "target_commitish": f"{NEW_COMMIT}",
                "name": f"Dev Build (Based on {version})",
                "body": changes_message + f"\nVirusTotal: {virus_total_link}",
                "draft": False,
                "prerelease": True,
                "make_latest": 'true'
            }
        )
        release_id = req.json()['id']

        # Upload jar
        upload_req = requests.post(
            f"https://uploads.github.com/repos/DAMcraft/MeteorServerSeeker/releases/{release_id}/assets?name={jar}",
            headers={
                'Authorization': f'Bearer {GITHUB_TOKEN}',
                'Content-Type': 'application/jar'
            },
            data=open(f'./build/libs/{jar}', 'rb')
        )

    if DISCORD_WEBHOOK is not None:
        requests.post(
            DISCORD_WEBHOOK,
            files={
                jar: open(f'./build/libs/{jar}', 'rb'),
                'payload_json': (None, json.dumps({
                    'embeds': [
                        {
                            'title': "New Dev Build!",
                            'description': f'A new dev build based on {version} has been released!\n'
                                           f'[VirusTotal]({virus_total_link})\n'
                                           f'{changes_message}',
                            'color': 0x14c384
                        }
                    ]
                }))
            }
        )


if __name__ == '__main__':
    main()
