package de.damcraft.serverseeker.commands;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import de.damcraft.serverseeker.ServerSeeker2000Client;
import de.damcraft.serverseeker.gui.ServerInfoScreen;
import de.damcraft.serverseeker.modules.ServerSeeker2000;
import meteordevelopment.meteorclient.commands.Command;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.Utils;
import net.minecraft.SharedConstants;
import net.minecraft.client.gui.screen.ConnectScreen;
import net.minecraft.client.gui.screen.TitleScreen;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerAddress;
import net.minecraft.client.network.ServerInfo;
import net.minecraft.command.CommandSource;

import static com.mojang.brigadier.Command.SINGLE_SUCCESS;
import static meteordevelopment.meteorclient.MeteorClient.mc;

public class ServerSeekerCommand extends Command {
    public ServerSeekerCommand() {
        super("serverseeker", "ServerSeeker-related commands", "ss");
    }

    private void randomHandler(String m) {

    }

    @Override
    public void build(LiteralArgumentBuilder<CommandSource> builder) {
        builder.then(
            // Get information about the current server
            literal("server_info").executes(ctx -> {
                if (mc.getCurrentServerEntry() == null) {
                    error("Failed to get current server entry. Are you playing on a singleplayer world?");
                    return SINGLE_SUCCESS;
                }
                String addr = mc.getCurrentServerEntry().address;
                System.out.println(addr);
                Utils.screenToOpen = new ServerInfoScreen(addr);
                return SINGLE_SUCCESS;
            }));
        builder.then(literal("join-2k-server")
            .then(argument("id", IntegerArgumentType.integer()).executes(ctx -> {
                int id = ctx.getArgument("id", Integer.class);
                if (Modules.get().get(ServerSeeker2000.class).servers.size() < id) {
                    error("Unknown 2K ID");
                    return SINGLE_SUCCESS;
                }
                mc.world.disconnect();
                ServerInfo info = Modules.get().get(ServerSeeker2000.class).servers.get(id);
                RenderSystem.recordRenderCall(() -> ConnectScreen.connect(null, mc, ServerAddress.parse(info.address), info, true));
                return SINGLE_SUCCESS;
            })));
    }
}
