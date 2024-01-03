package de.damcraft.serverseeker.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.damcraft.serverseeker.gui.ServerInfoScreen;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class ServerInfoCommand extends Command {
    public ServerInfoCommand() {
        super("server_info", "Get Info about a server", "si");
    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.executes(ctx -> {
            if (mc.getCurrentServerEntry() == null) {
                error("Failed to get current server entry. Are you playing on a singleplayer world?");
                return SINGLE_SUCCESS;
            }
            String addr = mc.getCurrentServerEntry().address;
            Utils.screenToOpen = new ServerInfoScreen(addr);
            return SINGLE_SUCCESS;
        });
    }
}
