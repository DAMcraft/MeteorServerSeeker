package de.damcraft.serverseeker.ssapi.requests;

import com.google.gson.JsonArray;
import de.damcraft.serverseeker.ServerSeekerSystem;

import static de.damcraft.serverseeker.ServerSeeker.gson;

public class ServersRequest {
    private final String api_key = ServerSeekerSystem.get().apiKey;
    private Integer asn;
    private String country_code;
    private Boolean cracked;
    private String description;
    private JsonArray max_players;
    private Integer online_after;
    private JsonArray online_players;
    private Integer protocol;
    private Boolean ignore_modded;

    public enum Software {
        Any,
        Bukkit,
        Spigot,
        Paper,
        Vanilla
    }
    private Software software;

    public void setAsn(Integer asn) {
        this.asn = asn;
    }

    public void setCountryCode(String cc) {
        this.country_code = cc;
    }

    public void setCracked(Boolean cracked) {
        this.cracked = cracked;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setMaxPlayers(Integer exact) {
        this.max_players = new JsonArray();
        this.max_players.add(exact);
        this.max_players.add(exact);
    }

    public void setMaxPlayers(Integer min, Integer max) {
        this.max_players = new JsonArray();
        this.max_players.add(min);
        if (max == -1) {
            max_players.add("inf");
        }
        else {
            this.max_players.add(max);
        }
    }

    public void setOnlineAfter(Integer unix_timestamp) {
        this.online_after = unix_timestamp;
    }

    public void setOnlinePlayers(Integer exact) {
        this.online_players = new JsonArray();
        this.online_players.add(exact);
        this.online_players.add(exact);
    }

    public void setOnlinePlayers(Integer min, Integer max) {
        this.online_players = new JsonArray();
        this.online_players.add(min);
        if (max == -1) {
            this.online_players.add("inf");
        }
        else {
            this.online_players.add(max);
        }
    }

    public void setProtocolVersion(Integer version) {
        this.protocol = version;
    }

    public void setSoftware(Software software) {
        this.software = software;
    }

    public void setIgnoreModded(Boolean ignore) {
        this.ignore_modded = ignore;
    }

    public String json() {
        return gson.toJson(this);
    }
}
