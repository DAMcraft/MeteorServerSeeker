package de.damcraft.serverseeker;

import com.mojang.logging.LogUtils;
import meteordevelopment.meteorclient.addons.MeteorAddon;
import org.slf4j.Logger;

public class ServerSeeker extends MeteorAddon {
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
