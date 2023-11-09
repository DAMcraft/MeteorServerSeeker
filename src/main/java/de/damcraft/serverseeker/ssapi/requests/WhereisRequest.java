package de.damcraft.serverseeker.ssapi.requests;

import com.google.gson.JsonObject;
import de.damcraft.serverseeker.ServerSeekerSystem;

public class WhereisRequest {
    private final String api_key = ServerSeekerSystem.get().apiKey;
    private enum PlayerSearchType {
        Name,
        Uuid
    }
    private PlayerSearchType playerSearchType;
    private String playerSearchValue;
    public WhereisRequest() {

    }
    public void setName(String name) {
        playerSearchType = PlayerSearchType.Name;
        playerSearchValue = name;
    }

    public void setUuid(String uuid) {
        playerSearchType = PlayerSearchType.Uuid;
        playerSearchValue = uuid;
    }

    public String json() {
        JsonObject jo = new JsonObject();
        jo.addProperty(playerSearchType.name().toLowerCase(), playerSearchValue);
        jo.addProperty("api_key", api_key);
        return jo.toString();
    }
}
