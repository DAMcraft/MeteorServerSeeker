package de.damcraft.serverseeker.gui;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import de.damcraft.serverseeker.ServerSeekerSystem;
import de.damcraft.serverseeker.SmallHttp;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.systems.accounts.Account;
import meteordevelopment.meteorclient.systems.accounts.Accounts;
import meteordevelopment.meteorclient.systems.accounts.types.CrackedAccount;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerServerListWidget;
import net.minecraft.client.network.ServerInfo;

import java.time.Instant;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class GetInfoScreen extends WindowScreen {
    MultiplayerServerListWidget.Entry entry;

    private boolean waitingForAuth = false;

    public GetInfoScreen(MultiplayerScreen multiplayerScreen, MultiplayerServerListWidget.Entry entry) {
        super(GuiThemes.get(), "Get players");
        this.parent = multiplayerScreen;
        this.entry = entry;
    }

    @Override
    public void initWidgets() {
        if (entry == null) {
            add(theme.label("No server selected"));
            return;
        }

        String apiKey = ServerSeekerSystem.get().apiKey;
        if (apiKey.isEmpty()) {
            WHorizontalList widgetList = add(theme.horizontalList()).expandX().widget();
            widgetList.add(theme.label("Please authenticate with Discord. "));
            waitingForAuth = true;
            WButton loginButton = widgetList.add(theme.button("Login")).widget();
            loginButton.action = () -> {
                if (this.client == null) return;
                this.client.setScreen(new LoginWithDiscordScreen(this));
            };
            return;
        }

        // Get info about the server
        if (!(entry instanceof MultiplayerServerListWidget.ServerEntry)) {
            add(theme.label("No server selected"));
            return;
        }
        ServerInfo serverInfo = ((MultiplayerServerListWidget.ServerEntry) entry).getServer();
        String address = serverInfo.address;

        // Check if the server matches the regex for ip(:port)
        if (!address.matches("^(?:[0-9]{1,3}\\.){3}[0-9]{1,3}(?::[0-9]{1,5})?$")) {
            add(theme.label("You can only get player info for servers with an IP address"));
            return;
        }
        String ip = address.split(":")[0];
        int port = address.split(":").length > 1 ? Integer.parseInt(address.split(":")[1]) : 25565;

        // Get the players using the API
        /* {
          "api_key": "...", // Your api_key
          "ip": "109.123.240.84", // The ip of the server
          "port": 25565  // The port of the server (defaults to 25565)
        } */
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("api_key", apiKey);

        jsonObject.addProperty("ip", ip);
        jsonObject.addProperty("port", port);

        String json = jsonObject.toString();

        String jsonResp = SmallHttp.post("https://serverseeker.damcraft.de/api/v1/server_info", json);

        Gson gson = new Gson();
        JsonObject resp = gson.fromJson(jsonResp, JsonObject.class);

        // Set error message if there is one
        String error = resp.has("error") ? resp.get("error").getAsString() : null;
        if (error != null) {
            clear();
            add(theme.label(error)).expandX();
            return;
        }
        clear();
        JsonArray players = resp.getAsJsonArray("players");
        if (players.size() == 0) {
            clear();
            add(theme.label("No records of players found.")).expandX();
            return;
        }
        /* "players": [ // An array of when which players were seen on the server. Limited to 1000
            {
              "last_seen": 1683790506, // The last time the player was seen on the server (unix timestamp)
              "name": "DAMcraft", // The name of the player
              "uuid": "68af4d98-24a2-41b6-96bc-a9c2ef9b397b" // The uuid of the player
            }, ...
          ] */
        boolean cracked = false;
        if (!resp.get("cracked").isJsonNull()) {
            cracked = resp.get("cracked").getAsBoolean();
        }

        if (!cracked) {
            add(theme.label("Attention: The server is NOT cracked!")).expandX();
            add(theme.label("")).expandX();
        }

        add(theme.label("Found " + players.size() + " players:"));

        WTable table = add(theme.table()).widget();

        table.add(theme.label("Name "));
        table.add(theme.label("Last seen "));
        table.add(theme.label("Login (cracked)"));
        table.row();

        table.add(theme.horizontalSeparator()).expandX();
        table.row();

        for (int i = 0; i < players.size(); i++) {
            JsonObject player = players.get(i).getAsJsonObject();
            String name = player.get("name").getAsString();
            long lastSeen = player.get("last_seen").getAsLong();
            String lastSeenFormatted = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT)
                .format(Instant.ofEpochSecond(lastSeen).atZone(ZoneId.systemDefault()).toLocalDateTime());

            table.add(theme.label(name + " "));
            table.add(theme.label(lastSeenFormatted + " "));

            if (mc.getSession().getUsername().equals(name)) {
                table.add(theme.label("Logged in")).expandCellX();
            } else {

                WButton loginButton = table.add(theme.button("Login")).widget();
                // Check if the user is currently logged in
                if (mc.getSession().getUsername().equals(name)) {
                    loginButton.visible = false;
                }

                // Log in the user
                loginButton.action = () -> {
                    loginButton.visible = false;
                    if (this.client == null) return;
                    // Check if the account already exists
                    boolean exists = false;
                    for (Account<?> account : Accounts.get()) {
                        if (account instanceof CrackedAccount && account.getUsername().equals(name)) {
                            account.login();
                            exists = true;
                            break;
                        }
                    }
                    if (!exists) {
                        CrackedAccount account = new CrackedAccount(name);
                        account.login();
                        Accounts.get().add(account);
                    }
                    close();
                };
            }
            table.row();
        }
    }

    @Override
    public void tick() {
        if (waitingForAuth) {
            String authToken = ServerSeekerSystem.get().apiKey;
            if (!authToken.isEmpty()) {
                this.reload();
                this.waitingForAuth = false;
            }
        }
    }
}
