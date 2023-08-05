package de.damcraft.serverseeker.modules;

import de.damcraft.serverseeker.ServerSeeker;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;


public class BungeeSpoof extends Module {
    SettingGroup sgGeneral = settings.getDefaultGroup();

    public Setting<String> spoofedAddress = sgGeneral.add(new StringSetting.Builder()
        .name("spoofed-address")
        .description("The spoofed IP address that will be sent to the server.")
        .defaultValue("127.0.0.1")
        .filter((text, c) -> (text + c).matches("^[0-9a-f\\\\.:]{0,45}$"))
        .build()
    );

    public BungeeSpoof() {
        super(ServerSeeker.CATEGORY, "BungeeSpoof", "Allows you to join servers with an exposed bungeecord backend.");
    }
}
