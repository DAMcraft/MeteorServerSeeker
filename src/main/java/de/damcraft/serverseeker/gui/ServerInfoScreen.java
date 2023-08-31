package de.damcraft.serverseeker.gui;

import com.google.common.net.HostAndPort;
import com.google.gson.*;
import de.damcraft.serverseeker.ServerSeekerSystem;
import de.damcraft.serverseeker.SmallHttp;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

public class ServerInfoScreen extends WindowScreen {
    private String serverIp;
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
        JsonObject resp = gson.fromJson(jsonResp, JsonObject.class);
        String error = resp.has("error") ? resp.get("error").getAsString() : null;
        if (error != null) {
            clear();
            add(theme.label(error)).expandX();
            return;
        }
        clear();

        JsonElement cracked = resp.get("cracked");
        String description = resp.get("description").getAsString();
        int onlinePlayers = resp.get("online_players").getAsInt();
        int maxPlayers = resp.get("max_players").getAsInt();
        int protocol = resp.get("protocol").getAsInt();
        int lastSeen = resp.get("last_seen").getAsInt();
        String version = resp.get("version").getAsString();
        JsonArray players = resp.get("players").getAsJsonArray();

        WTable dataTable = add(theme.table()).widget();
        WTable playersTable = add(theme.table()).expandX().widget();

        dataTable.add(theme.label("Cracked: "));
        dataTable.add(theme.label((cracked instanceof JsonNull) ? "Unknown" : cracked.getAsString()));
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


        playersTable.add(theme.label("Name "));
        playersTable.add(theme.label("Last seen "));
        playersTable.row();


        playersTable.add(theme.horizontalSeparator()).expandX();
        playersTable.row();

        for (int i = 0; i < players.size(); i++) {
            if (i > 0) playersTable.row();
            JsonObject player = players.get(i).getAsJsonObject();
            String name = player.get("name").getAsString();
            long playerLastSeen = player.get("last_seen").getAsLong();
            String lastSeenFormatted = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.SHORT)
                .format(Instant.ofEpochSecond(playerLastSeen).atZone(ZoneId.systemDefault()).toLocalDateTime());

            playersTable.add(theme.label(name + " "));
            playersTable.add(theme.label(lastSeenFormatted + " "));
        }
    }
}
