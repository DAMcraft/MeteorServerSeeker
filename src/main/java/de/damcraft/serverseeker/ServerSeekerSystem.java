package de.damcraft.serverseeker;

import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import net.minecraft.nbt.NbtCompound;

public class ServerSeekerSystem extends System<ServerSeekerSystem> {
    public ServerSeekerSystem() {
        super("serverseeker");
    }

    public String apiKey = "";

    public String userId = "";

    public static ServerSeekerSystem get() {
        return Systems.get(ServerSeekerSystem.class);
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();

        tag.putString("apiKey", apiKey);
        tag.putString("userId", userId);

        return tag;
    }

    @Override
    public ServerSeekerSystem fromTag(NbtCompound tag) {
        apiKey = tag.getString("apiKey");
        userId = tag.getString("userId");

        return this;
    }
}
