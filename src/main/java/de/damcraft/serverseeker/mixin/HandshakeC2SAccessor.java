package de.damcraft.serverseeker.mixin;

import net.minecraft.network.NetworkState;
import net.minecraft.network.packet.c2s.handshake.ConnectionIntent;
import net.minecraft.network.packet.c2s.handshake.HandshakeC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(HandshakeC2SPacket.class)
public interface HandshakeC2SAccessor {
    @Mutable
    @Accessor
    void setAddress(String address);

    @Accessor("intendedState")
    ConnectionIntent getNetworkState();

    @Accessor("address")
    String getAddress();
}
