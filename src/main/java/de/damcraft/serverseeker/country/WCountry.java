package de.damcraft.serverseeker.country;

import de.damcraft.serverseeker.ServerSeeker;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.renderer.Texture;

public class WCountry extends WWidget {

    private Country country;
    private Country.CountryTextureData cache = null;

    public WCountry(Country country) {
        this.country = country == null ? ServerSeeker.COUNTRY_MAP.get("UN") : country;
    }

    public void set(Country country) {
        this.country = country == null ? ServerSeeker.COUNTRY_MAP.get("UN") : country;
    }

    @Override
    protected void onCalculateSize() {
        double s = theme.scale(32);

        width = s;
        height = s;
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        Country.CountryTextureData textureData;
        if (this.cache == null) {
            textureData = this.country.getTextureData();
            if (textureData.isLoaded()) this.cache = textureData;
        } else textureData = this.cache;

        Texture texture = new Texture();
        texture.upload(textureData.width(), textureData.height(), textureData.getBuffer(), Texture.Format.RGB, Texture.Filter.Nearest, Texture.Filter.Nearest, false);

        texture.bind();

        int wanted_height = (int) (super.width * texture.height / texture.width);

        // Center y
        int wanted_y = (int) (y + (super.height - wanted_height) / 2);

        if (texture.isValid()) {
            renderer.texture(x, wanted_y, super.width, wanted_height, 0, texture);
        }
    }

}
