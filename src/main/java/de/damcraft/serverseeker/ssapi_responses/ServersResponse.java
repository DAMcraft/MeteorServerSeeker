package de.damcraft.serverseeker.ssapi_responses;

import java.util.List;

public class ServersResponse {
    public String error;

    public static class Server {
        public String server;
        public Boolean cracked;
        public String description;
        public Integer last_seen;
        public Integer max_players;
        public Integer online_players;
        public Integer protocol;
        public String version;
    }

    public List<Server> data;

    public boolean isError() {
        return error != null;
    }
}
