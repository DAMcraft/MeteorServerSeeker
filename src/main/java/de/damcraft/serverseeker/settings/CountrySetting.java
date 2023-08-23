package de.damcraft.serverseeker.settings;

import meteordevelopment.meteorclient.settings.BlockSetting;
import meteordevelopment.meteorclient.settings.IVisible;
import meteordevelopment.meteorclient.settings.Setting;
import net.minecraft.block.Block;
import net.minecraft.nbt.NbtCompound;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class CountrySetting extends Setting<Country> {
    public final Predicate<Country> filter;

    public CountrySetting(String name, String description, Country defaultValue, Consumer<Country> onChanged, Consumer<Setting<Country>> onModuleActivated, IVisible visible, Predicate<Country> filter) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);

        this.filter = filter;
    }

    @Override
    protected Country parseImpl(String str) {
        return null;
    }

    @Override
    protected boolean isValueValid(Country value) {
        return false;
    }

    @Override
    protected NbtCompound save(NbtCompound tag) {
        return null;
    }

    @Override
    protected Country load(NbtCompound tag) {
        return null;
    }

    public static class Builder extends SettingBuilder<Builder, Country, CountrySetting> {
        private Predicate<Country> filter;

        public Builder() {
            super(null);
        }

        public CountrySetting.Builder filter(Predicate<Country> filter) {
            this.filter = filter;
            return this;
        }

        @Override
        public CountrySetting build() {
            return new CountrySetting(name, description, defaultValue, onChanged, onModuleActivated, visible, filter);
        }
    }
}
