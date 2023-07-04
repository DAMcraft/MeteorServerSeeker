package de.damcraft.serverseeker.gui;

import de.damcraft.serverseeker.DiscordAvatar;
import de.damcraft.serverseeker.ServerSeekerSystem;
import de.damcraft.serverseeker.mixin.MultiplayerScreenAccessor;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.containers.WHorizontalList;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;


public class ServerSeekerScreen extends WindowScreen {
    private final MultiplayerScreen multiplayerScreen;

    public ServerSeekerScreen(MultiplayerScreen multiplayerScreen) {
        super(GuiThemes.get(), "ServerSeeker");
        this.multiplayerScreen = multiplayerScreen;
    }
    private boolean waitingForAuth = false;

    @Override
    public void initWidgets() {
        String authToken = ServerSeekerSystem.get().apiKey;

        if (authToken.isEmpty()) {
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

        WHorizontalList accountList = add(theme.horizontalList()).expandX().widget();
        // Add an image of the user's avatar
        if (!ServerSeekerSystem.get().discordAvatarUrl.isEmpty()) {
            accountList.add(theme.texture(32, 32, 0, new DiscordAvatar(ServerSeekerSystem.get().discordAvatarUrl + "?size=32")));
        }
        accountList.add(theme.label(ServerSeekerSystem.get().discordUsername)).expandX();
        WButton logoutButton = accountList.add(theme.button("Logout")).widget();
        logoutButton.action = () -> {
            ServerSeekerSystem.get().apiKey = "";
            ServerSeekerSystem.get().discordId = "";
            ServerSeekerSystem.get().discordUsername = "";
            ServerSeekerSystem.get().discordAvatarUrl = "";
            ServerSeekerSystem.get().save();
            reload();
        };

        WHorizontalList widgetList = add(theme.horizontalList()).expandX().widget();
        WButton newServersButton = widgetList.add(this.theme.button("Find new servers")).expandX().widget();
        WButton findPlayersButton = widgetList.add(this.theme.button("Search players")).expandX().widget();
        WButton cleanUpServersButton = widgetList.add(this.theme.button("Clean up")).expandX().widget();
        newServersButton.action = () -> {
            if (this.client == null) return;
            this.client.setScreen(new FindNewServersScreen(this.multiplayerScreen));
        };
        findPlayersButton.action = () -> {
            if (this.client == null) return;
            this.client.setScreen(new FindPlayerScreen(this.multiplayerScreen));
        };
        cleanUpServersButton.action = () -> {
            if (this.client == null) return;
            clear();
            add(theme.label("Are you sure you want to clean up your server list?"));
            add(theme.label("This will remove all servers that start with \"ServerSeeker\""));
            WHorizontalList buttonList = add(theme.horizontalList()).expandX().widget();
            WButton backButton = buttonList.add(theme.button("Back")).expandX().widget();
            backButton.action = this::reload;
            WButton confirmButton = buttonList.add(theme.button("Confirm")).expandX().widget();
            confirmButton.action = this::cleanUpServers;
        };
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

    public void cleanUpServers() {
        if (this.client == null) return;

        for (int i = 0; i < this.multiplayerScreen.getServerList().size(); i++) {
            if (this.multiplayerScreen.getServerList().get(i).name.startsWith("ServerSeeker")) {
                this.multiplayerScreen.getServerList().remove(this.multiplayerScreen.getServerList().get(i));
                i--;
            }
        }
        this.multiplayerScreen.getServerList().saveFile();
        ((MultiplayerScreenAccessor) this.multiplayerScreen).getServerListWidget().setServers(this.multiplayerScreen.getServerList());

        client.setScreen(this.multiplayerScreen);
    }
}
