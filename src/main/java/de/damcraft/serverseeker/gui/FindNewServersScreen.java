package de.damcraft.serverseeker.gui;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import de.damcraft.serverseeker.ServerSeekerSystem;
import de.damcraft.serverseeker.SmallHttp;
import de.damcraft.serverseeker.mixin.MultiplayerScreenAccessor;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.Systems;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerInfo;

public class FindNewServersScreen extends WindowScreen {

    public enum cracked {
        Any,
        Yes,
        No
    }

    public enum version {
        Current,
        Any,
        Custom
    }

    private final Settings settings = new Settings();
    private final SettingGroup sg = settings.getDefaultGroup();

    WContainer settingsContainer;

    private final Setting<cracked> crackedSetting = sg.add(new EnumSetting.Builder<cracked>()
        .name("cracked")
        .description("Whether the server should be cracked or not")
        .defaultValue(cracked.Any)
        .build()
    );

    private final Setting<Boolean> anyOnlinePlayersSetting = sg.add(new BoolSetting.Builder()
        .name("any-online-players")
        .defaultValue(true)
        .build()
    );

    private final Setting<Integer> onlinePlayersSetting = sg.add(new IntSetting.Builder()
        .name("online-players")
        .description("The amount of online players the server should have")
        .defaultValue(0)
        .min(0)
        .visible(() -> !anyOnlinePlayersSetting.get())
        .noSlider()
        .build()
    );

    private final Setting<Boolean> anyMaxPlayersSetting = sg.add(new BoolSetting.Builder()
        .name("any-max-players")
        .defaultValue(true)
        .build()
    );

    private final Setting<Integer> maxPlayersSetting = sg.add(new IntSetting.Builder()
        .name("max-players")
        .description("The maximum amount of players the servers should hold")
        .defaultValue(20)
        .min(0)
        .visible(() -> !anyMaxPlayersSetting.get())
        .noSlider()
        .build()
    );

    private final Setting<String> descriptionSetting = sg.add(new StringSetting.Builder()
        .name("description")
        .description("The description (aka motd) the servers should have (empty for any)")
        .defaultValue("")
        .build()
    );

    private final Setting<version> versionSetting = sg.add(new EnumSetting.Builder<version>()
        .name("version")
        .description("The protocol version the servers should have")
        .defaultValue(version.Current)
        .build()
    );

    private final Setting<Integer> customProtocolSetting = sg.add(new IntSetting.Builder()
        .name("protocol")
        .description("The protocol version the servers should have")
        .defaultValue(SharedConstants.getProtocolVersion())
        .visible(() -> versionSetting.get() == version.Custom)
        .min(0)
        .noSlider()
        .build()
    );

    private final Setting<Boolean> online_only = sg.add(new BoolSetting.Builder()
        .name("online-only")
        .description("Whether to only show servers that are online")
        .defaultValue(true)
        .build()
    );


    MultiplayerScreen multiplayerScreen;


    public FindNewServersScreen(MultiplayerScreen multiplayerScreen) {
        super(GuiThemes.get(), "Find new servers");
        this.multiplayerScreen = multiplayerScreen;
    }

    @Override
    public void initWidgets() {

        settingsContainer = add(theme.verticalList()).widget();
        settingsContainer.add(theme.settings(settings));
        WButton findButton = add(theme.button("Find")).expandX().widget();
        findButton.action = () -> {

            String apiKey = Systems.get(ServerSeekerSystem.class).apiKey;

            // Create a new JSON object
            JsonObject jsonObject = new JsonObject();
            jsonObject.addProperty("api_key", apiKey);
            if (!anyOnlinePlayersSetting.get()) {
                jsonObject.addProperty("online_players", onlinePlayersSetting.get());
            }
            if (!anyMaxPlayersSetting.get()) {
                jsonObject.addProperty("max_players", maxPlayersSetting.get());
            }
            if (crackedSetting.get() != cracked.Any) {
                jsonObject.addProperty("cracked", crackedSetting.get() == cracked.Yes);
            }
            if (!descriptionSetting.get().isEmpty()) {
                jsonObject.addProperty("description", descriptionSetting.get());
            }
            if (versionSetting.get() == version.Custom) {
                jsonObject.addProperty("protocol", customProtocolSetting.get());
            } else if (versionSetting.get() == version.Current) {
                jsonObject.addProperty("protocol", SharedConstants.getProtocolVersion());
            }
            if (!online_only.get()) {
                jsonObject.addProperty("online_after", 0);
            }

            String json = jsonObject.toString();

            String jsonResp = SmallHttp.post("https://serverseeker.damcraft.de/api/v1/servers", json);

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
            JsonArray servers = resp.getAsJsonArray("data");
            if (servers.size() == 0) {
                add(theme.label("No servers found")).expandX();
                WButton backButton = add(theme.button("Back")).expandX().widget();
                backButton.action = this::reload;
                return;
            }
            add(theme.label("Found " + servers.size() + " servers")).expandX();
            WButton addAllButton = add(theme.button("Add all")).expandX().widget();
            addAllButton.action = () -> {
                for (JsonElement server : servers) {
                    String ip = server.getAsJsonObject().get("server").getAsString();

                    ServerInfo info = new ServerInfo("ServerSeeker " + ip, ip, false);

                    // Add server to list
                    this.multiplayerScreen.getServerList().add(info, false);
                }
                this.multiplayerScreen.getServerList().saveFile();

                // Reload widget
                ((MultiplayerScreenAccessor) this.multiplayerScreen).getServerListWidget().setServers(this.multiplayerScreen.getServerList());

                // Close screen
                if (this.client == null) return;
                client.setScreen(this.multiplayerScreen);
            };
        };
    }

    @Override
    public void tick() {
        super.tick();
        settings.tick(settingsContainer, theme);
    }
}
