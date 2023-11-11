package de.damcraft.serverseeker.gui;

import com.google.common.net.HostAndPort;
import de.damcraft.serverseeker.ServerSeeker;
import de.damcraft.serverseeker.SmallHttp;
import de.damcraft.serverseeker.country.Country;
import de.damcraft.serverseeker.country.CountrySetting;
import de.damcraft.serverseeker.ssapi.requests.ServersRequest;
import de.damcraft.serverseeker.ssapi.responses.ServersResponse;
import de.damcraft.serverseeker.utils.MultiplayerScreenUtil;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WContainer;
import meteordevelopment.meteorclient.gui.widgets.containers.WTable;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.utils.network.MeteorExecutor;
import net.minecraft.SharedConstants;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;

import java.util.List;

import static de.damcraft.serverseeker.ServerSeeker.gson;

public class FindNewServersScreen extends WindowScreen {
    private int timer;
    public WButton findButton;
    private boolean threadHasFinished;
    private String threadError;
    private List<ServersResponse.Server> threadServers;

    public enum Cracked {
        Any,
        Yes,
        No;

        public Boolean toBoolOrNull() {
            return switch (this) {
                case Any -> null;
                case Yes -> true;
                case No -> false;
            };
        }
    }

    public enum Version {
        Current,
        Any,
        Custom
    }

    public enum NumRangeType {
        Any,
        Equals,
        At_Least,
        At_Most,
        Between
    }

    // Didn't have a better name
    public enum GeoSearchType {
        None,
        ASN,
        Country
    }

    private final Settings settings = new Settings();
    private final SettingGroup sg = settings.getDefaultGroup();
    WContainer settingsContainer;

    private final Setting<Cracked> crackedSetting = sg.add(new EnumSetting.Builder<Cracked>()
        .name("cracked")
        .description("Whether the server should be cracked or not")
        .defaultValue(Cracked.Any)
        .build()
    );

    private final Setting<NumRangeType> onlinePlayersNumTypeSetting = sg.add(new EnumSetting.Builder<NumRangeType>()
        .name("online-players")
        .description("The type of number range for the online players")
        .defaultValue(NumRangeType.Any)
        .build()
    );

    private final Setting<Integer> equalsOnlinePlayersSetting = sg.add(new IntSetting.Builder()
            .name("online-player-equals")
            .description("The amount of online players the server should have")
            .defaultValue(2)
            .min(0)
            .visible(() -> onlinePlayersNumTypeSetting.get().equals(NumRangeType.Equals))
            .noSlider()
            .build()
    );


    private final Setting<Integer> atLeastOnlinePlayersSetting = sg.add(new IntSetting.Builder()
        .name("online-player-at-least")
        .description("The minimum amount of online players the server should have")
        .defaultValue(1)
        .min(0)
        .visible(() -> onlinePlayersNumTypeSetting.get().equals(NumRangeType.At_Least) || onlinePlayersNumTypeSetting.get().equals(NumRangeType.Between))
        .noSlider()
        .build()
    );

    private final Setting<Integer> atMostOnlinePlayersSetting = sg.add(new IntSetting.Builder()
        .name("online-player-at-most")
        .description("The maximum amount of online players the server should have")
        .defaultValue(20)
        .min(0)
        .visible(() -> onlinePlayersNumTypeSetting.get().equals(NumRangeType.At_Most) || onlinePlayersNumTypeSetting.get().equals(NumRangeType.Between))
        .noSlider()
        .build()
    );


    private final Setting<NumRangeType> maxPlayersNumTypeSetting = sg.add(new EnumSetting.Builder<NumRangeType>()
        .name("max-players")
        .description("The type of number range for the max players")
        .defaultValue(NumRangeType.Any)
        .build()
    );

    private final Setting<Integer> equalsMaxPlayersSetting = sg.add(new IntSetting.Builder()
            .name("max-players-equals")
            .description("The amount of max players the server should have")
            .defaultValue(2)
            .min(0)
            .visible(() -> maxPlayersNumTypeSetting.get().equals(NumRangeType.Equals))
            .noSlider()
            .build()
    );


    private final Setting<Integer> atLeastMaxPlayersSetting = sg.add(new IntSetting.Builder()
        .name("max-players-at-least")
        .description("The minimum amount of max players the server should have")
        .defaultValue(1)
        .min(0)
        .visible(() -> maxPlayersNumTypeSetting.get().equals(NumRangeType.At_Least) || maxPlayersNumTypeSetting.get().equals(NumRangeType.Between))
        .noSlider()
        .build()
    );

    private final Setting<Integer> atMostMaxPlayersSetting = sg.add(new IntSetting.Builder()
        .name("max-players-at-most")
        .description("The maximum amount of max players the server should have")
        .defaultValue(20)
        .min(0)
        .visible(() -> maxPlayersNumTypeSetting.get().equals(NumRangeType.At_Most) || maxPlayersNumTypeSetting.get().equals(NumRangeType.Between))
        .noSlider()
        .build()
    );

    private final Setting<String> descriptionSetting = sg.add(new StringSetting.Builder()
        .name("description")
        .description("The description (aka motd) the servers should have (empty for any)")
        .defaultValue("")
        .build()
    );

    private final Setting<ServersRequest.Software> softwareSetting = sg.add(new EnumSetting.Builder<ServersRequest.Software>()
        .name("software")
        .description("The software the servers should have")
        .defaultValue(ServersRequest.Software.Any)
        .build()
    );

    private final Setting<Version> versionSetting = sg.add(new EnumSetting.Builder<Version>()
        .name("version")
        .description("The protocol version the servers should have")
        .defaultValue(Version.Current)
        .build()
    );

    private final Setting<Integer> customProtocolSetting = sg.add(new IntSetting.Builder()
        .name("protocol")
        .description("The protocol version the servers should have")
        .defaultValue(SharedConstants.getProtocolVersion())
        .visible(() -> versionSetting.get() == Version.Custom)
        .min(0)
        .noSlider()
        .build()
    );

    private final Setting<Boolean> onlineOnlySetting = sg.add(new BoolSetting.Builder()
        .name("online-only")
        .description("Whether to only show servers that are online")
        .defaultValue(true)
        .build()
    );

    private final Setting<GeoSearchType> geoSearchTypeSetting = sg.add(new EnumSetting.Builder<GeoSearchType>()
        .name("geo-search-type")
        .description("Whether to search by ASN or country code")
        .defaultValue(GeoSearchType.Country)
        .build()
    );

    private final Setting<Integer> asnNumberSetting = sg.add(new IntSetting.Builder()
        .name("asn")
        .description("The ASN of the server")
        .defaultValue(24940)
        .noSlider()
        .visible(() -> geoSearchTypeSetting.get() == GeoSearchType.ASN)
        .build()
    );

    private final Setting<Country> countrySetting = sg.add(new CountrySetting.Builder()
        .name("country")
        .description("The country the server should be located in")
        .defaultValue(ServerSeeker.COUNTRY_MAP.get("UN"))
        .visible(() -> geoSearchTypeSetting.get() == GeoSearchType.Country)
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
        findButton = add(theme.button("Find")).expandX().widget();
        findButton.action = () -> {
            ServersRequest request = new ServersRequest();

            switch (onlinePlayersNumTypeSetting.get()) {
                // [n, "inf"]
                case At_Least -> request.setOnlinePlayers(atLeastOnlinePlayersSetting.get(), -1);

                // [0, n]
                case At_Most -> request.setOnlinePlayers(0, atMostOnlinePlayersSetting.get());

                // [min, max]
                case Between -> request.setOnlinePlayers(atLeastOnlinePlayersSetting.get(), atMostOnlinePlayersSetting.get());

                // [n, n]
                case Equals -> request.setOnlinePlayers(equalsOnlinePlayersSetting.get());
            }

            switch (maxPlayersNumTypeSetting.get()) {
                // [n, "inf"]
                case At_Least -> request.setMaxPlayers(atLeastMaxPlayersSetting.get(), -1);

                // [0, n]
                case At_Most -> request.setMaxPlayers(0, atMostMaxPlayersSetting.get());

                // [min, max]
                case Between -> request.setMaxPlayers(atLeastMaxPlayersSetting.get(), atMostMaxPlayersSetting.get());

                // [n, n]
                case Equals -> request.setMaxPlayers(equalsMaxPlayersSetting.get());
            }


            switch (geoSearchTypeSetting.get()) {
                case ASN -> request.setAsn(asnNumberSetting.get());
                case Country -> {
                    if (countrySetting.get().name.equalsIgnoreCase("any")) break;
                    request.setCountryCode(countrySetting.get().code);
                }
            }

            request.setCracked(crackedSetting.get().toBoolOrNull());
            request.setDescription(descriptionSetting.get());
            request.setSoftware(softwareSetting.get());

            switch (versionSetting.get()) {
                case Custom -> request.setProtocolVersion(customProtocolSetting.get());
                case Current -> request.setProtocolVersion(SharedConstants.getProtocolVersion());
            }

            if (!onlineOnlySetting.get()) request.setOnlineAfter(0);


            this.locked = true;

            this.threadHasFinished = false;
            this.threadError = null;
            this.threadServers = null;


            MeteorExecutor.execute(() -> {
                String jsonResp = SmallHttp.post("https://api.serverseeker.net/servers", request.json());

                ServersResponse resp = gson.fromJson(jsonResp, ServersResponse.class);

                // Set error message if there is one
                if (resp.isError()) {
                    this.threadError = resp.error;
                    this.threadHasFinished = true;
                    return;
                }
                this.threadServers = resp.data;
                this.threadHasFinished = true;
            });
        };
    }

    @Override
    public void tick() {
        super.tick();
        settings.tick(settingsContainer, theme);

        if (threadHasFinished) handleThreadFinish();

        if (locked) {
            if (timer > 2) {
                findButton.set(getNext(findButton));
                timer = 0;
            }
            else {
                timer++;
            }
        }

        else if (!findButton.getText().equals("Find")) {
            findButton.set("Find");
        }
    }

    @Override
    protected void onClosed() {
        ServerSeeker.COUNTRY_MAP.values().forEach(Country::dispose);
    }

    private String getNext(WButton add) {
        return switch (add.getText()) {
            case "Find", "oo0" -> "ooo";
            case "ooo" -> "0oo";
            case "0oo" -> "o0o";
            case "o0o" -> "oo0";
            default -> "Find";
        };
    }

    private void handleThreadFinish() {
        this.threadHasFinished = false;
        this.locked = false;
        if (this.threadError != null) {
            clear();
            add(theme.label(this.threadError)).expandX();
            WButton backButton = add(theme.button("Back")).expandX().widget();
            backButton.action = this::reload;
            this.locked = false;
            return;
        }
        clear();
        List<ServersResponse.Server> servers = this.threadServers;

        if (servers.isEmpty()) {
            add(theme.label("No servers found")).expandX();
            WButton backButton = add(theme.button("Back")).expandX().widget();
            backButton.action = this::reload;
            this.locked = false;
            return;
        }
        add(theme.label("Found " + servers.size() + " servers")).expandX();
        WButton addAllButton = add(theme.button("Add all")).expandX().widget();
        addAllButton.action = () -> {
            for (ServersResponse.Server server : servers) {
                String ip = server.server;

                // Add server to list
                MultiplayerScreenUtil.addNameIpToServerList(multiplayerScreen, "ServerSeeker " + ip, ip, false);
            }
            MultiplayerScreenUtil.saveList(multiplayerScreen);

            // Reload widget
            MultiplayerScreenUtil.reloadServerList(multiplayerScreen);

            // Close screen
            if (this.client == null) return;
            client.setScreen(this.multiplayerScreen);
        };

        WTable table = add(theme.table()).widget();

        table.add(theme.label("Server IP"));
        table.add(theme.label("Version"));


        table.row();

        table.add(theme.horizontalSeparator()).expandX();
        table.row();


        for (ServersResponse.Server server : servers) {
            final String serverIP = server.server;
            String serverVersion = server.version;

            table.add(theme.label(serverIP));
            table.add(theme.label(serverVersion));

            WButton addServerButton = theme.button("Add Server");
            addServerButton.action = () -> {
                System.out.println(multiplayerScreen.getServerList() == null);
                ServerInfo info = new ServerInfo("ServerSeeker " + serverIP, serverIP, ServerInfo.ServerType.OTHER);
                MultiplayerScreenUtil.addInfoToServerList(multiplayerScreen, info);
                addServerButton.visible = false;
            };

            WButton joinServerButton = theme.button("Join Server");
            HostAndPort hap = HostAndPort.fromString(serverIP);

            joinServerButton.action = ()
                -> ConnectScreen.connect(new TitleScreen(), MinecraftClient.getInstance(), new ServerAddress(hap.getHost(), hap.getPort()), new ServerInfo("a", hap.toString(), ServerInfo.ServerType.OTHER), false);

            WButton serverInfoButton = theme.button("Server Info");
            serverInfoButton.action = () -> this.client.setScreen(new ServerInfoScreen(serverIP));

            table.add(addServerButton);
            table.add(joinServerButton);
            table.add(serverInfoButton);

            table.row();
        }

        this.locked = false;
    }
}
