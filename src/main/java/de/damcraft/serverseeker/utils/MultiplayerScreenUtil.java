package de.damcraft.serverseeker.utils;

import de.damcraft.serverseeker.mixin.MultiplayerScreenAccessor;
import net.minecraft.client.gui.screen.multiplayer.MultiplayerScreen;
import net.minecraft.client.network.ServerInfo;

public class MultiplayerScreenUtil {

    public static void addInfoToServerList(MultiplayerScreen mps, ServerInfo info) {
        MultiplayerScreenAccessor mpsAccessor = (MultiplayerScreenAccessor) mps;
        mps.getServerList().add(info, false);
        mps.getServerList().saveFile();
        mpsAccessor.getServerListWidget().setServers(mps.getServerList());
    }
    public static void addInfoToServerList(MultiplayerScreen mps, ServerInfo info, boolean reload) {
        MultiplayerScreenAccessor mpsAccessor = (MultiplayerScreenAccessor) mps;
        mps.getServerList().add(info, false);
        if (reload) mpsAccessor.getServerListWidget().setServers(mps.getServerList());
    }

    public static void addNameIpToServerList(MultiplayerScreen mps, String name, String ip) {
        MultiplayerScreenAccessor mpsAccessor = (MultiplayerScreenAccessor) mps;
        ServerInfo info = new ServerInfo(name, ip, false);
        mps.getServerList().add(info, false);
        mpsAccessor.getServerListWidget().setServers(mps.getServerList());
        mps.getServerList().saveFile();
    }
    public static void addNameIpToServerList(MultiplayerScreen mps, String name, String ip, boolean reload) {
        MultiplayerScreenAccessor mpsAccessor = (MultiplayerScreenAccessor) mps;
        ServerInfo info = new ServerInfo(name, ip, false);
        mps.getServerList().add(info, false);
        if (reload) mpsAccessor.getServerListWidget().setServers(mps.getServerList());
    }

    public static void reloadServerList(MultiplayerScreen mps) {
        MultiplayerScreenAccessor mpsAccessor = (MultiplayerScreenAccessor) mps;
        mpsAccessor.getServerListWidget().setServers(mps.getServerList());
    }

    public static void saveList(MultiplayerScreen mps) {
        mps.getServerList().saveFile();
    }
}
