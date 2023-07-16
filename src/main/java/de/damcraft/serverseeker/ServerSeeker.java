package de.damcraft.serverseeker;

import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import org.slf4j.Logger;

public class ServerSeeker extends MeteorAddon {
    /*
    Feature list for anticope.ml:
    (creates features matching the RegEx '(?:add\(new )([^(]+)(?:\([^)]*)\)\)', as anticope checks for that.
    add(new Find servers with many parameters, for example: Cracked, Description, Player count, much more...())
    add(new Server database with around 1.000.000 servers!())
    add(new Over 40.000.000 players tracked!())
    add(new Search for ANY server you want!())
     */
    public static final Logger LOG = LogUtils.getLogger();

    @Override
    public void onInitialize() {
        LOG.info("Loaded the ServerSeeker addon!");
    }


    @Override
    public String getPackage() {
        return "de.damcraft.serverseeker";
    }
}
