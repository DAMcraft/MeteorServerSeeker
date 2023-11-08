package de.damcraft.serverseeker.country;

import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.Reference;
import java.lang.ref.WeakReference;
import java.nio.ByteBuffer;
import java.util.Locale;

import static meteordevelopment.meteorclient.MeteorClient.LOG;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Country {
    public final Identifier identifier;
    public final String name;
    public final String code;
    protected Reference<CountryTextureData> textureData = new WeakReference<>(null);
    private boolean isQueued = false;

    public Country(String name, String code) {
        this.name = name;
        this.code = code.toLowerCase(Locale.ENGLISH);
        this.identifier = new Identifier("serverseeker", "textures/flags/" + this.code + ".png");
        if (mc.getResourceManager().getResource(this.identifier).isEmpty()) {
            LOG.error("Could not find flag for country: " + this.code);
            this.textureData = new WeakReference<>(Countries.UN.getTextureData());
        }
    }

    public CountryTextureData getTextureData() {
        if (textureData.get() == null) {
            queue();
            return Countries.UN.getTextureData();
        } else return textureData.get();
    }

    private void queue() {
        if (isQueued) return;
        isQueued = true;
        MeteorExecutor.execute(() -> {
            textureData = new WeakReference<>(computeTextureData());
            isQueued = false;
        });
    }

    protected CountryTextureData computeTextureData() {
        Resource textureResource = mc.getResourceManager().getResource(this.identifier).orElseThrow();

        try (InputStream imageStream = textureResource.getInputStream()) {
            BufferedImage bufferedImage = ImageIO.read(imageStream);

            int[] pixels = bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null, 0, bufferedImage.getWidth());
            byte[] data = new byte[bufferedImage.getWidth() * bufferedImage.getHeight() * 3];

            for (int i = 0; i < pixels.length; i++) {
                data[3 * i] = (byte) ((pixels[i] >> 16) & 0xFF); // r
                data[3 * i + 1] = (byte) ((pixels[i] >>  8) & 0xFF); // g
                data[3 * i + 2] = (byte) ((pixels[i]) & 0xFF); // b
            }

            return new CountryTextureData(data, bufferedImage.getWidth(), bufferedImage.getHeight());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean equals(Country country) {
        return this.code.equals(country.code);
    }

    public record CountryTextureData(byte[] textureData, int width, int height) {
        public ByteBuffer getBuffer() {
            return BufferUtils.createByteBuffer(textureData.length).put(textureData);
        }

        public boolean isLoaded() {
            return this != Countries.UN.getTextureData();
        }
    }
}
