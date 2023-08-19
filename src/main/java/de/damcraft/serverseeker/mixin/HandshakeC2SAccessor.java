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
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import static meteordevelopment.meteorclient.MeteorClient.mc;

@Mixin(HandshakeC2SPacket.class)
public interface HandshakeC2SAccessor {
    @Mutable
    @Accessor
    void setAddress(String address);

    @Accessor("intendedState")
    NetworkState getNetworkState();

    @Accessor("address")
    String getAddress();
}
