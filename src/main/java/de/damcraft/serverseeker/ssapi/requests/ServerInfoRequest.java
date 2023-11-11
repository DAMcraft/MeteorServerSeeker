package de.damcraft.serverseeker.ssapi.requests;

import de.damcraft.serverseeker.ServerSeekerSystem;

import static de.damcraft.serverseeker.ServerSeeker.gson;

public class ServerInfoRequest {
    private final String api_key = ServerSeekerSystem.get().apiKey;

    private String ip;
    private Integer port;

    public void setIpPort(String ip, Integer port) {
        this.ip = ip;
        this.port = port;
    }

    public String json() {
        return gson.toJson(this);
    }
}
