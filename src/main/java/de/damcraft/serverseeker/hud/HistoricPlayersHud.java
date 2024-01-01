package de.damcraft.serverseeker.hud;

import de.damcraft.serverseeker.ssapi.responses.ServerInfoResponse;
import de.damcraft.serverseeker.utils.HistoricPlayersUpdater;
import meteordevelopment.meteorclient.gui.GuiThemes;
import meteordevelopment.meteorclient.settings.*;
import meteordevelopment.meteorclient.systems.hud.*;
import meteordevelopment.meteorclient.utils.render.color.SettingColor;
import net.minecraft.client.network.PlayerListEntry;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static meteordevelopment.meteorclient.MeteorClient.mc;

public class HistoricPlayersHud extends HudElement {
    public List<ServerInfoResponse.Player> players = List.of();
    public Boolean isCracked = false;

    public final static HudElementInfo<HistoricPlayersHud> INFO = new HudElementInfo<>(Hud.GROUP, "historic-players", "Displays players that were on this server in the past.", HistoricPlayersHud::new);
    private final SettingGroup sgGeneral = settings.getDefaultGroup();

    public HistoricPlayersHud() {
        super(INFO);
        new Thread(HistoricPlayersUpdater::update).start();
    }

    private final Setting<Boolean> showCrackedText = sgGeneral.add(new BoolSetting.Builder()
        .name("show-cracked-text")
        .description("Shows the cracked text.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> showCurrentPlayers = sgGeneral.add(new BoolSetting.Builder()
        .name("show-current-players")
        .description("Shows players that are currently on the server.")
        .defaultValue(true)
        .build()
    );

    private final Setting<Boolean> showWhenEmpty = sgGeneral.add(new BoolSetting.Builder()
        .name("show-when-empty")
        .description("Shows the hud even when there are no historic players.")
        .defaultValue(true)
        .visible(() -> !showCurrentPlayers.get())
        .build()
    );

    private final Setting<SettingColor> currentPlayersColor = sgGeneral.add(new ColorSetting.Builder()
        .name("current-players-color")
        .description("The color of the current players text.")
        .defaultValue(new SettingColor(255, 255, 255))
        .visible(showCurrentPlayers::get)
        .build()
    );

    private final Setting<SettingColor> historicPlayersColor = sgGeneral.add(new ColorSetting.Builder()
        .name("historic-players-name-color")
        .description("The color of the historic players text.")
        .defaultValue(new SettingColor(255, 255, 255))
        .build()
    );

    private final Setting<SettingColor> historicPlayersLastSeenColor = sgGeneral.add(new ColorSetting.Builder()
        .name("last-seen-color")
        .description("The color of the historic players last seen text.")
        .defaultValue(new SettingColor(175, 175, 175))
        .build()
    );

    private final Setting<Integer> limit = sgGeneral.add(new IntSetting.Builder()
        .name("limit")
        .description("The maximum amount of players to display.")
        .defaultValue(10)
        .min(1)
        .max(1000)
        .noSlider()
        .build()
    );

    private final Setting<Alignment> alignment = sgGeneral.add(new EnumSetting.Builder<Alignment>()
        .name("alignment")
        .description("Horizontal alignment.")
        .defaultValue(Alignment.Auto)
        .build()
    );

    @Override
    public void render(HudRenderer renderer) {
        super.render(renderer);
        int line = 0;
        int more = 0;
        if (players.isEmpty() && !showWhenEmpty.get()) return;

        String playersText = "Players:";
        double playerOffset = alignX(renderer.textWidth(playersText), alignment.get());
        renderer.text(playersText, x + playerOffset, y + line * renderer.textHeight(), GuiThemes.get().textColor(), false);
        double longestLine = renderer.textWidth(playersText);

        line++;
        List<String> alreadyDisplayed = new ArrayList<>();
        if (showCurrentPlayers.get() && mc.player != null) {
            for (PlayerListEntry player : mc.player.networkHandler.getListedPlayerListEntries()) {
                if (line >= limit.get()) {
                    more++;
                    continue;
                }
                alreadyDisplayed.add(String.valueOf(player.getProfile().getId()));
                String name = player.getProfile().getName();
                double offset = alignX(renderer.textWidth(name), alignment.get());
                renderer.text(name, x + offset, y + line * renderer.textHeight(), currentPlayersColor.get(), false);
                line++;

                if (renderer.textWidth(name) > longestLine) longestLine = renderer.textWidth(name);
            }
        }
        // Sort players by join time (newest first)
        List<ServerInfoResponse.Player> players = new ArrayList<>(this.players);
        Collections.sort(players, (b, a) -> a.last_seen.compareTo(b.last_seen));
        for (ServerInfoResponse.Player player : players) {
            if (alreadyDisplayed.contains(player.uuid)) continue;
            if (line >= limit.get()) {
                more++;
                continue;
            }
            // Convert last_seen to a human-readable format
            String unit = "s";
            double last_seen = (int) (System.currentTimeMillis() / 1000 - player.last_seen);
            if (last_seen >= 60) {
                last_seen /= 60;
                unit = "min";
            }
            if (last_seen >= 60 && unit.equals("min")) {
                last_seen /= 60;
                unit = "h";
            }
            if (last_seen >= 24 && unit.equals("h")) {
                last_seen /= 24;
                unit = last_seen == 1 ? " day" : " days";
            }
            if (last_seen >= 30 && unit.equals(" days")) {
                last_seen /= 30;
                unit = last_seen == 1 ? " month" : " months";
            }
            if (last_seen >= 12 && (unit.equals(" months"))) {
                last_seen /= 12;
                unit = last_seen == 1 ? " year" : " years";
            }
            // Round to 1 decimal place
            last_seen = Math.round(last_seen * 10) / 10.0;

            double width = renderer.textWidth(player.name) + renderer.textWidth(" (" + last_seen + unit + ")");
            double offset = alignX(width, alignment.get());

            renderer.text(player.name, x + offset, y + line * renderer.textHeight(), historicPlayersColor.get(), false);
            renderer.text(" (" + last_seen + unit + ")", x + offset + renderer.textWidth(player.name), y + line * renderer.textHeight(), historicPlayersLastSeenColor.get(), false);
            line++;

            if (width > longestLine) longestLine = width;
        }

        if (line == 1) {
            String text = "No players found.";
            double offset = alignX(renderer.textWidth(text), alignment.get());
            renderer.text(text, x + offset, y + line * renderer.textHeight(), GuiThemes.get().textColor(), false);
            line++;
            if (renderer.textWidth(text) > longestLine) longestLine = renderer.textWidth("No players found.");
        }

        if (more > 0) {
            String text = "... and " + more + " more";
            double offset = alignX(renderer.textWidth(text), alignment.get());
            renderer.text(text, x + offset, y + line * renderer.textHeight(), GuiThemes.get().textColor(), false);
            line++;
            if (renderer.textWidth(text) > longestLine) longestLine = renderer.textWidth(text);
        }

        if (showCrackedText.get() && isCracked) {
            String text = "Server is cracked";
            double offset = alignX(renderer.textWidth(text), alignment.get());
            renderer.text(text, x + offset, y + line * renderer.textHeight(), GuiThemes.get().textColor(), false);
            line++;
            if (renderer.textWidth(text) > longestLine) longestLine = renderer.textWidth(text);
        }

        box.setSize(longestLine, line * renderer.textHeight());
    }
}
