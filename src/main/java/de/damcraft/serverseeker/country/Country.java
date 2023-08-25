package de.damcraft.serverseeker.country;

import de.damcraft.serverseeker.ServerSeeker;
import meteordevelopment.meteorclient.gui.renderer.packer.GuiTexture;
import meteordevelopment.meteorclient.gui.renderer.packer.TexturePacker;
import meteordevelopment.meteorclient.renderer.Texture;
import net.minecraft.client.texture.AbstractTexture;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.resource.Resource;
import net.minecraft.resource.ResourceManager;
import net.minecraft.util.Identifier;
import org.lwjgl.BufferUtils;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.NoSuchElementException;
import java.util.Optional;

import static meteordevelopment.meteorclient.MeteorClient.LOG;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class Country {
    public final String name;
    public final String code;
    public final Texture image;

    Country(String name, String code) {

        System.out.println("Creating country: " + code + " " + name);
        code = code.toLowerCase();
        this.name = name;
        this.code = code;

        GuiTexture tmp_image;
        Identifier identifier = new Identifier("serverseeker", "textures/flags/" + code + ".png");
        Optional<Resource> tmp_resource = mc.getResourceManager().getResource(identifier);
        if (tmp_resource.isEmpty()) {
            identifier = new Identifier("serverseeker", "textures/flags/blank.png");
            tmp_resource = mc.getResourceManager().getResource(identifier);
        }
        InputStream imageStream;
        if (tmp_resource.isEmpty()) {
            LOG.error("Could not find flag for country: " + code);
            this.image = null;
            return;
        }
        Resource resource = tmp_resource.get();
        try {
            imageStream = resource.getInputStream();
            BufferedImage bufferedImage = ImageIO.read(imageStream);
            if (bufferedImage == null) {
                LOG.error("Could not read image for country: " + code);
                this.image = null;
                return;
            }
            byte[] data = new byte[bufferedImage.getWidth() * bufferedImage.getHeight() * 3];
            int[] pixel = new int[4];
            int i = 0;

            for (int y = 0; y < bufferedImage.getHeight(); y++) {
                for (int x = 0; x < bufferedImage.getWidth(); x++) {
                    bufferedImage.getData().getPixel(x, y, pixel);

                    for (int j = 0; j < 3; j++) {
                        data[i] = (byte) pixel[j];
                        i++;
                    }
                }
            }
            Texture texture = new Texture();
            texture.upload(bufferedImage.getWidth(), bufferedImage.getHeight(), BufferUtils.createByteBuffer(data.length).put(data), Texture.Format.RGB, Texture.Filter.Nearest, Texture.Filter.Nearest, false);
            this.image = texture;
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


}
