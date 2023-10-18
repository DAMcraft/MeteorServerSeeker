package de.damcraft.serverseeker.gui;

import com.google.common.net.HostAndPort;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import de.damcraft.serverseeker.ServerSeekerSystem;
import de.damcraft.serverseeker.SmallHttp;
import de.damcraft.serverseeker.ssapi_responses.ServerInfoResponse;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.List;

public class ServerInfoScreen extends WindowScreen {
    private final String serverIp;

    public ServerInfoScreen(String serverIp) {
        super(GuiThemes.get(), "Server Info: " + serverIp);
        this.serverIp = serverIp;
    }

    @Override
    public void initWidgets() {
        add(theme.label("Fetching server info..."));
        Gson gson = new GsonBuilder().serializeNulls().create();
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("api_key", ServerSeekerSystem.get().apiKey);
        HostAndPort hap = HostAndPort.fromString(serverIp);
        jsonObject.addProperty("ip", hap.getHost());
        jsonObject.addProperty("port", hap.getPort());
        String jsonResp = SmallHttp.post("https://api.serverseeker.net/server_info", jsonObject.toString());
        ServerInfoResponse resp = gson.fromJson(jsonResp, ServerInfoResponse.class);
        if (resp.isError()) {
            clear();
            add(theme.label(resp.error)).expandX();
            return;
        }
        clear();

        Boolean cracked = resp.cracked;
        String description = resp.description;
        int onlinePlayers = resp.online_players;
        int maxPlayers = resp.max_players;
        int protocol = resp.protocol;
        int lastSeen = resp.last_seen;
        String version = resp.version;
        List<ServerInfoResponse.Player> players = resp.players;

        WTable dataTable = add(theme.table()).widget();
        WTable playersTable = add(theme.table()).expandX().widget();

        dataTable.add(theme.label("Cracked: "));
        dataTable.add(theme.label(cracked == null ? "Unknown" : cracked.toString()));
        dataTable.row();

        dataTable.add(theme.label("Description: "));
        if (description.length() > 100) description = description.substring(0, 100) + "...";
        description = description.replace("\n", "\\n");
        description = description.replace("§r", "");
        dataTable.add(theme.label(description));
        dataTable.row();

        dataTable.add(theme.label("Online Players (last scan): "));
        dataTable.add(theme.label(String.valueOf(onlinePlayers)));
        dataTable.row();

        dataTable.add(theme.label("Max Players: "));
        dataTable.add(theme.label(String.valueOf(maxPlayers)));
        dataTable.row();

        dataTable.add(theme.label("Last Seen: "));
        String lastSeenDate = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
            .format(Instant.ofEpochSecond(lastSeen).atZone(ZoneId.systemDefault()).toLocalDateTime());
        dataTable.add(theme.label(lastSeenDate));
        dataTable.row();

        dataTable.add(theme.label("Version: "));
        dataTable.add(theme.label(version + " (" + protocol + ")"));

        playersTable.add(theme.label(""));
        playersTable.row();
        playersTable.add(theme.label("Players:"));
        playersTable.row();


        playersTable.add(theme.label("Name ")).expandX();
        playersTable.add(theme.label("Last seen ")).expandX();
        playersTable.row();


        playersTable.add(theme.horizontalSeparator()).expandX();
        playersTable.row();

        playersTable.add(theme.label(""));

        for (ServerInfoResponse.Player player : players) {
            String name = player.name;
            long playerLastSeen = player.last_seen;
            String lastSeenFormatted = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                .format(Instant.ofEpochSecond(playerLastSeen).atZone(ZoneId.systemDefault()).toLocalDateTime());

            playersTable.add(theme.label(name + " ")).expandX();
            playersTable.add(theme.label(lastSeenFormatted + " ")).expandX();
            playersTable.row();
        }
        WButton joinServerButton = add(theme.button("Join this Server")).expandX().widget();
        joinServerButton.action = ()
            -> ConnectScreen.connect(new TitleScreen(), MinecraftClient.getInstance(), new ServerAddress(hap.getHost(), hap.getPort()), new ServerInfo("a", hap.toString(), ServerInfo.ServerType.OTHER), false);
    }
}
