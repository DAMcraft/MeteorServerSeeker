package de.damcraft.serverseeker.gui;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.damcraft.serverseeker.SmallHttp;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.gui.widget.TextWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Util;

import java.io.InputStream;
import java.net.http.HttpResponse;
import java.nio.file.Path;
import java.util.Optional;

public class InstallMeteorScreen extends Screen {
    public InstallMeteorScreen() {
        super(Text.of("Meteor Client is not installed!"));
    }

    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        context.drawCenteredTextWithShadow(this.textRenderer, this.title, this.width / 2, this.height / 4 - 60 + 20, 16777215);
    }

    protected void init() {
        super.init();
        this.addDrawableChild(ButtonWidget.builder(Text.of("Automatically install Meteor (§arecommended§r)"), (button) -> {
            String result = SmallHttp.get("https://meteorclient.com/api/stats");
            if (result == null) {
                this.clearChildren();
                // Render error
                this.addDrawableChild(new TextWidget(
                    this.width / 2 - 250,
                    this.height / 4,
                    500,
                    20,
                    Text.of("Failed to get install meteor automatically! Please install it manually."),
                    this.textRenderer
                ));
                this.addDrawableChild(ButtonWidget.builder(Text.of("Open install FAQ"), (button2) -> {
                    Util.getOperatingSystem().open("https://meteorclient.com/faq/installation");
                }).dimensions(this.width / 2 - 150, this.height / 4 + 100, 300, 20).build());
            }
            Gson gson = new Gson();
            JsonObject json = gson.fromJson(result, JsonObject.class);
            String currentVersion = SharedConstants.getGameVersion().getName();
            String stableVersion = json.get("mc_version").getAsString();
            String devBuildVersion = json.get("dev_build_mc_version").getAsString();
            String url;
            if (currentVersion.equals(stableVersion)) {
                url = "https://meteorclient.com/api/download";
            } else if (currentVersion.equals(devBuildVersion)) {
                url = "https://meteorclient.com/api/download?devBuild=latest";
            } else {
                this.clearChildren();
                // Render error
                this.addDrawableChild(new TextWidget(
                    this.width / 2 - 250,
                    this.height / 4,
                    500,
                    20,
                    Text.of("Failed to find Meteor for your current version."),
                    this.textRenderer
                ));
                this.addDrawableChild(ButtonWidget.builder(Text.of("Open install FAQ"), (button2) -> {
                    Util.getOperatingSystem().open("https://meteorclient.com/faq/installation");
                }).dimensions(this.width / 2 - 150, this.height / 4 + 100, 300, 20).build());
                return;
            }
            HttpResponse<InputStream> file = SmallHttp.download(url);
            if (file == null) {
                this.clearChildren();
                // Render error
                this.addDrawableChild(new TextWidget(
                    this.width / 2 - 250,
                    this.height / 4,
                    500,
                    20,
                    Text.of("Failed to download Meteor! Please install it manually."),
                    this.textRenderer
                ));
                this.addDrawableChild(ButtonWidget.builder(Text.of("Open install FAQ"), (button2) -> {
                    Util.getOperatingSystem().open("https://meteorclient.com/faq/installation");
                }).dimensions(this.width / 2 - 150, this.height / 4 + 100, 300, 20).build());
                return;
            }
            Optional<String> filenameT= file.headers().firstValue("Content-Disposition");
            String filename = "meteor-client.jar";
            if (filenameT.isPresent()) {
                filename = filenameT.get().split("filename=")[1];
            }
            System.out.println(filename);
            // Get the mods folder
            Path modsFolder = FabricLoader.getInstance().getGameDir().resolve("mods");
            if (!modsFolder.toFile().exists()) {
               this.clearChildren();
                // Render error
                this.addDrawableChild(new TextWidget(
                    this.width / 2 - 250,
                    this.height / 4,
                    500,
                    20,
                    Text.of("Failed to find mods folder! Please install Meteor manually."),
                    this.textRenderer
                ));
                this.addDrawableChild(ButtonWidget.builder(Text.of("Open install FAQ"), (button2) -> {
                    Util.getOperatingSystem().open("https://meteorclient.com/faq/installation");
                }).dimensions(this.width / 2 - 150, this.height / 4 + 100, 300, 20).build());
            }
            // Save the file
            try {
                java.nio.file.Files.copy(file.body(), modsFolder.resolve(filename));
            } catch (Exception e) {
                this.clearChildren();
                // Render error
                this.addDrawableChild(new TextWidget(
                    this.width / 2 - 250,
                    this.height / 4,
                    500,
                    20,
                    Text.of("Failed to save Meteor! Please install it manually."),
                    this.textRenderer
                ));
                this.addDrawableChild(ButtonWidget.builder(Text.of("Open install FAQ"), (button2) -> {
                    Util.getOperatingSystem().open("https://meteorclient.com/faq/installation");
                }).dimensions(this.width / 2 - 150, this.height / 4 + 100, 300, 20).build());
                return;
            }
            // Success message
            this.clearChildren();
            this.addDrawableChild(new TextWidget(
                this.width / 2 - 250,
                this.height / 4,
                500,
                20,
                Text.of("Successfully installed Meteor! Please restart your game."),
                this.textRenderer
            ));
        }).dimensions(this.width / 2 - 150, this.height / 4 + 100, 300, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.of("Manual installation"), (button) -> {
            Util.getOperatingSystem().open("https://meteorclient.com/faq/installation");
        }).dimensions(this.width / 2 - 150, this.height / 4 + 100 + 25, 148, 20).build());
        this.addDrawableChild(ButtonWidget.builder(Text.translatable("menu.quit"), (button) -> {
            this.client.scheduleStop();
        }).dimensions(this.width / 2 + 2, this.height / 4 + 100 + 25, 148, 20).build());
    }
}
