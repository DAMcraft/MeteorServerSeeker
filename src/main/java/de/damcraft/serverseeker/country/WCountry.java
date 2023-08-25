package de.damcraft.serverseeker.country;

import de.damcraft.serverseeker.ServerSeeker;
import meteordevelopment.meteorclient.gui.renderer.GuiRenderer;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.renderer.Texture;

public class WCountry extends WWidget {

    private Country country;

    public WCountry(Country country) {
        if (country == null) country = ServerSeeker.COUNTRY_MAP.get("un");
        this.country = country;
    }

    public void set(Country country) {
        this.country = country;
    }

    @Override
    protected void onCalculateSize() {
        double s = theme.scale(32);

        width = s;
        height = s;
    }

    @Override
    protected void onRender(GuiRenderer renderer, double mouseX, double mouseY, double delta) {
        if (country == null) {
            country = ServerSeeker.COUNTRY_MAP.get("un");
        }

            Texture texture = country.image;
            if (texture == null) return;
            texture.bind();

            int wanted_height = (int) (super.width * texture.height / texture.width);

            // Center y
            int wanted_y = (int) (y + (super.height - wanted_height) / 2);

            if (texture.isValid()) {
                renderer.texture(x, wanted_y, super.width, wanted_height, 0, texture);
            }

    }

}
