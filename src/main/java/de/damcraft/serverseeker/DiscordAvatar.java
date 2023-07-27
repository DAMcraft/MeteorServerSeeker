package de.damcraft.serverseeker;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.renderer.Texture;
import meteordevelopment.meteorclient.systems.Systems;
import meteordevelopment.meteorclient.utils.network.Http;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;

public class DiscordAvatar extends Texture {
    public DiscordAvatar(String url) {
        BufferedImage avatar;
        try {
            InputStream stream = Http.get(url).sendInputStream();
            if (stream == null) {
                JsonObject params = new JsonObject();

                params.addProperty("api_key", Systems.get(ServerSeekerSystem.class).apiKey);

                String jsonResp = SmallHttp.post("https://api.serverseeker.net/user_info", params.toString());

                JsonObject obj = (new Gson()).fromJson(jsonResp, JsonObject.class);
                if (obj.has("error")) {
                    System.out.println("Error: " + obj.get("error").getAsString());
                    return;
                }
                String discordId = obj.get("discord_id").getAsString();
                String discordUsername = obj.get("discord_username").getAsString();
                String discordAvatarUrl = obj.get("discord_avatar_url").getAsString();

                Systems.get(ServerSeekerSystem.class).discordId = discordId;
                Systems.get(ServerSeekerSystem.class).discordUsername = discordUsername;
                Systems.get(ServerSeekerSystem.class).discordAvatarUrl = discordAvatarUrl;

                stream = Http.get(discordAvatarUrl).sendInputStream();
            }
            avatar = ImageIO.read(stream);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        byte[] data = new byte[avatar.getWidth() * avatar.getHeight() * 3];
        int[] pixel = new int[4];
        int i = 0;

        for (int y = 0; y < avatar.getHeight(); y++) {
            for (int x = 0; x < avatar.getWidth(); x++) {
                avatar.getData().getPixel(x, y, pixel);

                for (int j = 0; j < 3; j++) {
                    data[i] = (byte) pixel[j];
                    i++;
                }
            }
        }

        upload(BufferUtils.createByteBuffer(data.length).put(data));
    }

    private void upload(ByteBuffer data) {
        Runnable action = () -> upload(32, 32, data, Texture.Format.RGB, Texture.Filter.Nearest, Texture.Filter.Nearest, false);
        if (RenderSystem.isOnRenderThread()) action.run();
        else RenderSystem.recordRenderCall(action::run);
    }
}
