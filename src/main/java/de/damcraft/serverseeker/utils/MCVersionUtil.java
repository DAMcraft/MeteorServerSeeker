package de.damcraft.serverseeker.utils;

import java.util.HashMap;

public class MCVersionUtil {
    private static HashMap<String, Integer> versions = new HashMap<>() {
        {
            put("1.20.2", 764);
            put("1.20.1", 763);
            put("1.20",   763);

            put("1.19.4", 762);
            put("1.19.3", 761);
            put("1.19.2", 760);
            put("1.19.1", 760);
            put("1.19",   759);

            put("1.18.2", 758);
            put("1.18.1", 757);
            put("1.18",   757);

            put("1.17.1", 756);
            put("1.17",   755);

            put("1.16.5", 754);
            put("1.16.4", 754);
            put("1.16.3", 753);
            put("1.16.2", 751);
            put("1.16.1", 736);
            put("1.16",   735);

            put("1.15.2", 578);
            put("1.15.1", 575);
            put("1.15",   753);

            put("1.14.4", 498);
            put("1.14.3", 490);
            put("1.14.2", 485);
            put("1.14.1", 480);
            put("1.14",   477);

            put("1.13.2", 404);
            put("1.13.1", 401);
            put("1.13",   393);

            put("1.12.2", 340);
            put("1.12.1", 338);
            put("1.12",   335);

            put("1.11.2", 316);
            put("1.11.1", 316);
            put("1.11",   316);

            put("1.10.2", 210);
            put("1.10.1", 210);
            put("1.10",   210);

            put("1.9.4",  110);
            put("1.9.3",  110);
            put("1.9.2",  109);
            put("1.9.1",  108);

            put("1.9",    107);
            put("1.8.9",   47);
            put("1.8.8",   47);
            put("1.8.7",   47);
            put("1.8.6",   47);
            put("1.8.5",   47);
            put("1.8.4",   47);
            put("1.8.3",   47);
            put("1.8.2",   47);
            put("1.8.1",   47);
            put("1.8",     47);

            put("1.7.10",   5);
            put("1.7.9",    5);
            put("1.7.8",    5);
            put("1.7.7",    5);
            put("1.7.6",    5);
            put("1.7.5",    4);
            put("1.7.4",    4);
            put("1.7.2",    3);
            put("1.7.1",    3);
        }
    };

    public static Integer versionToProtocol(String versionString) {
        return versions.get(versionString);
    }
}
