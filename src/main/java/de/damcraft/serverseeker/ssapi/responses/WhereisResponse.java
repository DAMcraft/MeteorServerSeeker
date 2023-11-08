package de.damcraft.serverseeker.ssapi.responses;

import java.util.List;

public class WhereisResponse {
    public String error;

    public static class Record {
        public String server;
        public String uuid;
        public String name;
        public Integer last_seen;
    }

    public List<Record> data;

    public boolean isError() {
        return error != null;
    }
}
