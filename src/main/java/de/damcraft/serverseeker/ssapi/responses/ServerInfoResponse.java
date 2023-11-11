package de.damcraft.serverseeker.ssapi.responses;

import java.util.List;

public class ServerInfoResponse {
    public String error;

    public Boolean cracked;
    public String description;
    public Integer last_seen;
    public Integer max_players;
    public Integer online_players;
    public Integer protocol;
    public String version;
    public static class Player {
        public String name;
        public String uuid;
        public Integer last_seen;
    }

    public List<Player> players;

    public boolean isError() {
        return error != null;
    }
}
