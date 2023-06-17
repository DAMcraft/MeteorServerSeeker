package de.damcraft.serverseeker;

import meteordevelopment.meteorclient.systems.System;
import meteordevelopment.meteorclient.systems.Systems;
import net.minecraft.nbt.NbtCompound;

public class ServerSeekerSystem extends System<ServerSeekerSystem> {
    public ServerSeekerSystem() {
        super("serverseeker");
    }

    public String apiKey = "";

    public String discordId = "";

    public String discordUsername = "";

    public String discordAvatarUrl = "";

    public static ServerSeekerSystem get() {
        return Systems.get(ServerSeekerSystem.class);
    }

    @Override
    public NbtCompound toTag() {
        NbtCompound tag = new NbtCompound();

        tag.putString("apiKey", apiKey);
        tag.putString("userId", discordId);
        tag.putString("username", discordUsername);
        tag.putString("avatarUrl", discordAvatarUrl);

        return tag;
    }

    @Override
    public ServerSeekerSystem fromTag(NbtCompound tag) {
        apiKey = tag.getString("apiKey");
        discordId = tag.getString("userId");
        discordUsername = tag.getString("username");
        discordAvatarUrl = tag.getString("avatarUrl");

        return this;
    }
}
