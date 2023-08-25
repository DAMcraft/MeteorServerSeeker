package de.damcraft.serverseeker;

import com.mojang.logging.LogUtils;
import de.damcraft.serverseeker.country.Country;
import de.damcraft.serverseeker.modules.BungeeSpoof;
import de.damcraft.serverseeker.country.CountrySetting;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import meteordevelopment.meteorclient.gui.GuiTheme;
import meteordevelopment.meteorclient.gui.utils.SettingsWidgetFactory;
import meteordevelopment.meteorclient.systems.modules.Category;
import meteordevelopment.meteorclient.systems.modules.Modules;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.item.*;
import org.slf4j.Logger;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import static de.damcraft.serverseeker.country.Countries.COUNTRIES;

public class ServerSeeker extends MeteorAddon {
    /*
    Feature list for anticope.ml:
    (creates features matching the RegEx '(?:add\(new )([^(]+)(?:\([^)]*)\)\)', as anticope checks for that.
    add(new Find servers with many parameters, for example: Cracked, Description, Player count, much more...())
    add(new Server database with around 1.000.000 servers!())
    add(new Over 40.000.000 players tracked!())
    add(new Search for ANY server you want!())
    add(new Join misconfigured BungeeCord backends with any name you want!())
     */
    public static final Logger LOG = LogUtils.getLogger();
    public static final Category CATEGORY = new Category("ServerSeeker", Items.SPYGLASS.getDefaultStack());
    public static final Map<String, Country> COUNTRY_MAP = new HashMap<>();
    @Override
    public void onInitialize() {
        LOG.info("Loaded the ServerSeeker addon!");


        // Map of country codes to country

        for (Country country : COUNTRIES) {
            COUNTRY_MAP.put(country.code, country);
        }
        Modules.get().add( new BungeeSpoof() );
        SettingsWidgetFactory.registerCustomFactory(CountrySetting.class, (theme) -> (table, setting) -> {
            CountrySetting.countrySettingW(table, (CountrySetting) setting, theme);
        });
    }
    @Override
    public void onRegisterCategories() {
        Modules.registerCategory(CATEGORY);
    }

    @Override
    public String getPackage() {
        return "de.damcraft.serverseeker";
    }
}
