package de.damcraft.serverseeker;

import com.mojang.blaze3d.systems.RenderSystem;
import meteordevelopment.meteorclient.renderer.Texture;
import meteordevelopment.meteorclient.utils.network.Http;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.nio.ByteBuffer;

public class DiscordAvatar extends Texture {
    public DiscordAvatar(String url) {
        BufferedImage avatar;
        try {
            avatar = ImageIO.read(Http.get(url).sendInputStream());
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
