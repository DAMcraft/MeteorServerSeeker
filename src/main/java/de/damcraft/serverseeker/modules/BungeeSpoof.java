package de.damcraft.serverseeker.modules;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.damcraft.serverseeker.ServerSeeker;
import de.damcraft.serverseeker.mixin.HandshakeC2SAccessor;
import meteordevelopment.meteorclient.MeteorClient;
import meteordevelopment.meteorclient.events.packets.PacketEvent;
import meteordevelopment.meteorclient.settings.Setting;
import meteordevelopment.meteorclient.settings.SettingGroup;
import meteordevelopment.meteorclient.settings.StringSetting;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.meteorclient.utils.network.Http;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;


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
        MeteorClient.EVENT_BUS.subscribe(new Listener());
    }

    private class Listener {
        @EventHandler
        private void onPacketSend(PacketEvent.Send event) {
            if (!isActive()) return;
            if (!(event.packet instanceof HandshakeC2SPacket)) return;
            HandshakeC2SAccessor packet = (HandshakeC2SAccessor) event.packet;
            if (packet.getNetworkState().getId() != 2) return;
            ServerSeeker.LOG.info("Spoofing bungeecord handshake packet");
            String spoofedUUID = mc.getSession().getUuid();

            String URL = "https://api.mojang.com/users/profiles/minecraft/" + mc.getSession().getUsername();

            Http.Request request = Http.get(URL);
            String response = request.sendString();
            if (response != null) {
                Gson gson = new Gson();

                JsonObject jsonObject = gson.fromJson(response, JsonObject.class);

                if (jsonObject != null && jsonObject.has("id")) {
                    spoofedUUID = jsonObject.get("id").getAsString();
                }
            }

            packet.setAddress(packet.getAddress() + "\u0000" + spoofedAddress.get() + "\u0000" + spoofedUUID);
        }
    }
}
