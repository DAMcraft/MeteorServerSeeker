package de.damcraft.serverseeker.mixin;

import de.damcraft.serverseeker.settings.CountrySetting;
import de.damcraft.serverseeker.settings.CountrySettingScreen;
import de.damcraft.serverseeker.settings.WCountry;
import meteordevelopment.meteorclient.gui.DefaultSettingsWidgetFactory;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.screens.settings.BlockSettingScreen;
import meteordevelopment.meteorclient.gui.utils.SettingsWidgetFactory;
import meteordevelopment.meteorclient.gui.widgets.WWidget;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.Settings;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(value = DefaultSettingsWidgetFactory.class, remap = false)
public abstract class AddToWidgetFactory extends SettingsWidgetFactory {
    @Shadow protected abstract void reset(WContainer c, Setting<?> setting, Runnable action);

    public AddToWidgetFactory(GuiTheme theme) {super(theme);}

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void onInit(GuiTheme theme, CallbackInfo ci) {
        factories.put(CountrySetting.class, (table, setting) -> countrySettingW(table, (CountrySetting) setting));
    }

    @Unique
    private void countrySettingW(WTable table, CountrySetting setting) {
        WHorizontalList list = table.add(theme.horizontalList()).expandX().widget();

        WCountry wCountry = list.add(new WCountry(setting.get())).widget();

        WButton select = list.add(theme.button("Select")).widget();
        select.action = () -> {
            CountrySettingScreen screen = new CountrySettingScreen(theme, setting);
            screen.onClosed(() -> wCountry.set(setting.get()));

            mc.setScreen(screen);
        };

        reset(table, setting, () -> wCountry.set(setting.get()));
    }

    @Override
    public WWidget create(GuiTheme theme, Settings settings, String filter) {
        return null;
    }
}
