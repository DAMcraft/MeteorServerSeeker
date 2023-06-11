package de.damcraft.serverseeker.gui;

import de.damcraft.serverseeker.DiscordAuth;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;

public class LoginWithDiscordScreen extends WindowScreen {

    private boolean canClose = false;

    ServerSeekerScreen parent;

    public LoginWithDiscordScreen(ServerSeekerScreen parent) {
        super(GuiThemes.get(), "Login with Discord");
        this.parent = parent;
    }

    @Override
    public void initWidgets() {
        DiscordAuth.auth((apiKey, error) -> {
            if (error != null) {
                this.canClose = true;
                clear();
                add(theme.label("Failed to authenticate with Discord. Reason: " + error));
                return;
            }
            if (apiKey == null) {
                this.canClose = true;
                clear();
                add(theme.label("Failed to authenticate with Discord."));
                return;
            }
            close();
        });
        add(theme.label("Please authenticate with Discord in your browser."));

        WButton cancel = add(theme.button("Cancel")).expandX().widget();
        cancel.action = () -> {
            DiscordAuth.stopServer();
            close();
        };
    }

    @Override
    public void tick() {}

    @Override
    public boolean shouldCloseOnEsc() {
        return this.canClose;
    }
}
