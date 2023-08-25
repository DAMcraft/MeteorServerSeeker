package de.damcraft.serverseeker.country;

import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.widgets.WLabel;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.IVisible;
import meteordevelopment.meteorclient.settings.Setting;
import net.minecraft.nbt.NbtCompound;

import java.util.function.Consumer;
import java.util.function.Predicate;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class CountrySetting extends Setting<Country> {
    public final Predicate<Country> filter;

    public CountrySetting(String name, String description, Country defaultValue, Consumer<Country> onChanged, Consumer<Setting<Country>> onModuleActivated, IVisible visible, Predicate<Country> filter) {
        super(name, description, defaultValue, onChanged, onModuleActivated, visible);

        this.filter = filter;
    }

    public static void countrySettingW(WTable table, CountrySetting setting, GuiTheme theme) {
        WHorizontalList list = table.add(theme.horizontalList()).expandX().widget();

        WCountry country = list.add(new WCountry(setting.get())).widget();

        String name = setting.get().name;
        WLabel nameLabel = list.add(theme.label(name)).widget();

        WButton select = list.add(theme.button("Select")).widget();
        select.action = () -> {
            CountrySettingScreen screen = new CountrySettingScreen(theme, setting);
            screen.onClosed(() -> {
                country.set(setting.get());
                nameLabel.set(setting.get().name);
            });

            mc.setScreen(screen);
        };
    }

    @Override
    protected Country parseImpl(String str) {
        return null;
    }

    @Override
    protected boolean isValueValid(Country value) {
        if (value.name == null) return false;
        if (value.code == null) return false;
        return true;
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
