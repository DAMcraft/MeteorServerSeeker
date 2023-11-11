package de.damcraft.serverseeker.ssapi.responses;

public class UserInfoResponse {
    public String error;
    public String discord_id;
    public String discord_username;
    public String discord_avatar_url;
    public Integer requests_per_day_server_info;
    public Integer requests_per_day_servers;
    public Integer requests_per_day_whereis;
    public Integer requests_made_server_info;
    public Integer requests_made_servers;
    public Integer requests_made_whereis;

    public boolean isError() {
        return error != null;
    }
}
