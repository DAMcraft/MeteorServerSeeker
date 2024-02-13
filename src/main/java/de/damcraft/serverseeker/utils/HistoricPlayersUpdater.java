package de.damcraft.serverseeker.utils;

import de.damcraft.serverseeker.SmallHttp;
import de.damcraft.serverseeker.hud.HistoricPlayersHud;
import de.damcraft.serverseeker.ssapi.requests.ServerInfoRequest;
import de.damcraft.serverseeker.ssapi.responses.ServerInfoResponse;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.systems.hud.Hud;
import meteordevelopment.meteorclient.systems.hud.HudElement;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.client.network.ClientPlayNetworkHandler;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static de.damcraft.serverseeker.ServerSeeker.gson;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class HistoricPlayersUpdater {
    @EventHandler
    private static void onGameJoinEvent(GameJoinedEvent ignoredEvent) {
        // Run in a new thread
        new Thread(HistoricPlayersUpdater::update).start();
    }

    public static void update() {
        // If the Hud contains the HistoricPlayersHud, update the players
        List<HistoricPlayersHud> huds = new ArrayList<>();
        for (HudElement hudElement : Hud.get()) {
            if (hudElement instanceof HistoricPlayersHud && hudElement.isActive()) {
                huds.add((HistoricPlayersHud) hudElement);
            }
        }
        if (huds.isEmpty()) return;

        ClientPlayNetworkHandler networkHandler = mc.getNetworkHandler();
        if (networkHandler == null) return;

        String address = networkHandler.getConnection().getAddress().toString();
        // Split it at "/" and take the second part
        String[] addressParts = address.split("/");
        if (addressParts.length < 2) return;

        String ip = addressParts[1].split(":")[0];
        Integer port = Integer.valueOf(addressParts[1].split(":")[1]);

        ServerInfoRequest request = new ServerInfoRequest();
        request.setIpPort(ip, port);

        String jsonResp = SmallHttp.post("https://api.serverseeker.net/server_info", request.json());

        ServerInfoResponse resp = gson.fromJson(jsonResp, ServerInfoResponse.class);

        for (HistoricPlayersHud hud : huds) {
            hud.players = Objects.requireNonNullElseGet(resp.players, List::of);
            hud.isCracked = resp.cracked != null && resp.cracked;
        }
    }
}
