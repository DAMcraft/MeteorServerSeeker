package de.damcraft.serverseeker.mixin;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import de.damcraft.serverseeker.ServerSeeker;
import de.damcraft.serverseeker.modules.BungeeSpoof;
import meteordevelopment.meteorclient.systems.modules.Modules;
import meteordevelopment.meteorclient.utils.network.Http;
import net.minecraft.network.NetworkState;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(HandshakeC2SPacket.class)
public class HandshakeC2SMixin {
    @Shadow
    @Final
    private NetworkState intendedState;

    @Shadow
    @Final
    private int protocolVersion;

    @Shadow
    @Final
    private String address;

    @Shadow
    @Final
    private int port;

    @Inject(method = "write", at = @At("HEAD"), cancellable = true)
    private void handshakePacket(PacketByteBuf buf, CallbackInfo ci) {
        BungeeSpoof bungeeSpoof = Modules.get().get(BungeeSpoof.class);
        if (!bungeeSpoof.isActive()) return;
        if (this.intendedState.getId() != 2) return;
        ServerSeeker.LOG.info("Spoofing bungeecord handshake packet");
        String spoofedUUID = mc.getSession().getUuid();

        String URL = "https://api.mojang.com/users/profiles/minecraft/" + mc.getSession().getUsername();

        Http.Request request = Http.get(URL);
        String response = request.sendString();
        if (response != null) {
            Gson gson = new Gson();

            JsonObject jsonObject = gson.fromJson(response, JsonObject.class);

            if (jsonObject != null && jsonObject.has("id")) {
                spoofedUUID = jsonObject.get("id").getAsString();
            }
        }



        buf.writeVarInt(this.protocolVersion);
        buf.writeString(
            this.address + "\u0000" +
            bungeeSpoof.spoofedAddress.get() + "\u0000" +
            spoofedUUID
        );
        buf.writeShort(this.port);
        buf.writeVarInt(this.intendedState.getId());
        // Stop the rest of the method from running
        ci.cancel();
    }
}
