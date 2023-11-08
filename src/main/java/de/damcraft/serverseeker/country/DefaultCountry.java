package de.damcraft.serverseeker.country;

/**
 * Default texture, always loaded, should only be used for UN
 */
public class DefaultCountry extends Country {
    private final CountryTextureData textureData;

    public DefaultCountry(String name, String code) {
        super(name, code);
        this.textureData = computeTextureData();
    }

    @Override
    public CountryTextureData getTextureData() {
        return this.textureData;
    }
}
