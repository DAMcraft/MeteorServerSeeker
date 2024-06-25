package de.damcraft.serverseeker.country;

import meteordevelopment.meteorclient.renderer.Texture;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import static meteordevelopment.meteorclient.MeteorClient.LOG;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Country implements Comparable<Country> {
    public final Identifier identifier;
    public final String name;
    public final String code;
    private final TextureData textureData;

    public Country(String name, String code) {
        this.name = name;
        this.code = code.toLowerCase(Locale.ENGLISH);
        this.identifier = Identifier.of("serverseeker", "textures/flags/" + this.code + ".png");
        if (mc.getResourceManager().getResource(this.identifier).isEmpty()) {
            LOG.error("Could not find flag for country: " + this.code);
            this.textureData = new EmptyTextureData();
        } else this.textureData = new CountryTextureData();
    }

    @Nullable
    public Texture getTexture() {
        Texture texture = this.textureData.get();
        if (texture == null) return this == Countries.UN ? null : Countries.UN.getTexture();
        return texture;
    }

    public void dispose() {
        this.textureData.dispose();
    }

    @Override
    public int compareTo(@NotNull Country o) {
        return this.name.compareTo(o.name);
    }

    public sealed interface TextureData {
        @Nullable
        default Texture get() {
            return null;
        }

        default void dispose() {}
    }

    public final class CountryTextureData implements TextureData {
        private Texture texture = null;
        private State state = State.EMPTY;

        @Nullable
        @Override
        public Texture get() {
            if (this.state == State.DONE) return this.texture;
            else {
                if (this.state == State.EMPTY) MeteorExecutor.execute(this::load);
                return null;
            }
        }

        @Override
        public void dispose() {
            if (this.state == State.DONE) {
                this.texture.dispose();
                this.texture = null;
            }
            this.state = State.EMPTY;
        }

        private void load() {
            this.state = State.LOADING;
            Resource textureResource = mc.getResourceManager().getResource(Country.this.identifier).orElseThrow();

            try (InputStream imageStream = textureResource.getInputStream()) {
                BufferedImage bufferedImage = ImageIO.read(imageStream);

                int[] pixels = bufferedImage.getRGB(0, 0, bufferedImage.getWidth(), bufferedImage.getHeight(), null, 0, bufferedImage.getWidth());
                byte[] data = new byte[bufferedImage.getWidth() * bufferedImage.getHeight() * 3];

                for (int i = 0; i < pixels.length; i++) {
                    data[3 * i] = (byte) ((pixels[i] >> 16) & 0xFF); // r
                    data[3 * i + 1] = (byte) ((pixels[i] >>  8) & 0xFF); // g
                    data[3 * i + 2] = (byte) ((pixels[i]) & 0xFF); // b
                }

                this.texture = new Texture(bufferedImage.getWidth(), bufferedImage.getHeight(), data, Texture.Format.RGB, Texture.Filter.Nearest, Texture.Filter.Nearest);
                this.state = State.DONE;
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        public enum State {
            EMPTY,
            LOADING,
            DONE
        }
    }

    public static final class EmptyTextureData implements TextureData {}
}
