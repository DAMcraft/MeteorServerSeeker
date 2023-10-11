package de.damcraft.serverseeker.gui;

import de.damcraft.serverseeker.DiscordAuth;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.gui.WindowScreen;
import meteordevelopment.meteorclient.gui.widgets.pressable.WButton;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class LoginWithDiscordScreen extends WindowScreen {

    private boolean canClose = false;

    WindowScreen parent;

    public LoginWithDiscordScreen(WindowScreen parent) {
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

        add(theme.label("The browser didn't open? Click below to copy the link and open it manually")).expandX();
        WButton copy = add(theme.button("Copy")).expandX().widget();
        copy.action = () -> {
            String url = DiscordAuth.url;
            mc.keyboard.setClipboard(url);
        };

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
