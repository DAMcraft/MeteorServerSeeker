package de.damcraft.serverseeker.modules;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.damcraft.serverseeker.ServerSeeker;
import de.damcraft.serverseeker.ServerSeeker2000Client;
import meteordevelopment.meteorclient.events.game.GameJoinedEvent;
import meteordevelopment.meteorclient.events.game.GameLeftEvent;
import meteordevelopment.meteorclient.systems.modules.Module;
import meteordevelopment.orbit.EventHandler;
import net.minecraft.SharedConstants;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.text.*;
import net.minecraft.util.Formatting;

import java.util.ArrayList;
import java.util.List;

public class ServerSeeker2000 extends Module {
    public List<ServerInfo> servers = new ArrayList<>();

    private ServerSeeker2000Client wsClient;

    public ServerSeeker2000() {
        super(ServerSeeker.CATEGORY, "2000", "Prints servers from ServerSeeker 2000 in the chat.");
        runInMainMenu = true;
    }

    private ServerSeeker2000Client defaultWsClient() {
        Gson gson = new Gson();
        return new ServerSeeker2000Client((c, m) -> {
            JsonObject j = gson.fromJson(m, JsonObject.class);
            servers.add(new ServerInfo(
                "",
                j.get("server").getAsString(),
                ServerInfo.ServerType.OTHER
            ));
            int serverProtocol = j.get("protocol").getAsInt();
            int currentProtocol = SharedConstants.getProtocolVersion();
            info("Received server: %s".formatted(j.get("server").getAsString()));
            info(" Version: %s".formatted(j.get("version").getAsString()));
            info(" Cracked: %b".formatted(j.get("cracked").getAsBoolean()));
            info(" Online players: %d".formatted(j.get("online_players").getAsInt()));
            Style style = Style.EMPTY;
            style = style.withClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, ".serverseeker join-2k-server " + (servers.size()-1)));

            if (serverProtocol != currentProtocol) {
                style = style
                    .withColor(Formatting.RED)
                    .withHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT, Text.of("This server is not on your current version!")));
            }

            MutableText text = Text.literal("^ Join this server ^\n").setStyle(style);
            info(text);
        });
    }

    @Override
    public void onActivate() {
        wsClient = defaultWsClient();
        wsClient.connect();
    }

    @EventHandler
    public void onGameLeft(GameLeftEvent e) {
        wsClient.close();
    }

    @EventHandler
    public void onGameJoined(GameJoinedEvent e) {
        wsClient.close();
        wsClient = defaultWsClient();
        wsClient.connect();
    }

    @Override
    public void onDeactivate() {
        wsClient.close();
    }
}
