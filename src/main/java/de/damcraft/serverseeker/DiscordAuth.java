package de.damcraft.serverseeker;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;
import de.damcraft.serverseeker.ssapi.responses.UserInfoResponse;
import meteordevelopment.meteorclient.systems.Systems;
import net.minecraft.util.Util;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.BiConsumer;

import static de.damcraft.serverseeker.ServerSeeker.gson;

public class DiscordAuth {
    private static final int port = 7637;

    // Store as a string because it's too big and I don't want to import unnecessary libraries if I join it to a String anyway
    private static final String clientId = "1087083964432404590";

    public static final String url =
        "https://discord.com/api/oauth2/authorize" +
            "?client_id=" + clientId +
            "&redirect_uri=http%3A%2F%2F127.0.0.1%3A" + port + "%2F" +
            "&response_type=code" +
            "&scope=identify";

    private static HttpServer server;

    private static BiConsumer<String, String> callback;

    public static void auth(BiConsumer<String, String> callback) {
        DiscordAuth.callback = callback;
        Util.getOperatingSystem().open(url);
        startServer();
    }

    private static void startServer() {
        try {
            server = HttpServer.create();
            server.bind(new InetSocketAddress("127.0.0.1", port), 0);
            server.createContext("/", new AuthHandler());
            server.start();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void stopServer() {
        if (server == null) return;

        server.stop(0);
        server = null;

        callback = null;
    }

    private static class AuthHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange req) throws IOException {
            if (req.getRequestMethod().equals("GET")) {
                // Login
                List<NameValuePair> query = URLEncodedUtils.parse(req.getRequestURI(), StandardCharsets.UTF_8);

                boolean ok = false;

                for (NameValuePair pair : query) {
                    if (pair.getName().equals("code")) {
                        handleCode(pair.getValue());

                        ok = true;
                        break;
                    }
                }

                if (!ok) {
                    writeText(req, "Cannot authenticate.");
                } else writeText(req, "You may now close this page.");
            }

            stopServer();
        }

        private void writeText(HttpExchange req, String text) throws IOException {
            OutputStream out = req.getResponseBody();

            req.sendResponseHeaders(200, text.length());

            out.write(text.getBytes(StandardCharsets.UTF_8));
            out.flush();
            out.close();
        }

        public void handleCode(String code) {
            // Get the ServerSeeker auth token

            JsonObject params = new JsonObject();

            params.addProperty("code", code);
            params.addProperty("usage", "meteor serverseeker");

            String jsonResp = SmallHttp.post("https://api.serverseeker.net/get_token", params.toString());

            // {"api_key": "..."} or {"error": "..."}

            JsonObject obj = gson.fromJson(jsonResp, JsonObject.class);

            if (obj.has("error")) {
                System.out.println("Error: " + obj.get("error").getAsString());
                callback.accept(null, obj.get("error").getAsString());
                return;
            }
            if (!obj.has("api_key")) {
                System.out.println("Error: No api_key in response.");
                callback.accept(null, "No api_key in response.");
                return;
            }
            String apiKey = obj.get("api_key").getAsString();
            Systems.get(ServerSeekerSystem.class).apiKey = apiKey;

            // Get the discord user info
            params = new JsonObject();

            params.addProperty("api_key", apiKey);

            jsonResp = SmallHttp.post("https://api.serverseeker.net/user_info", params.toString());

            // {
            //                "discord_id": user_id,
            //                "discord_username": discord_username,
            //                "discord_avatar_url": avatar_url
            //            } or {"error": "..."}

            UserInfoResponse userInfo = gson.fromJson(jsonResp, UserInfoResponse.class);

            if (userInfo.isError()) {
                System.out.println("Error: " + userInfo.error);
                callback.accept(null, userInfo.error);
                return;
            }

            String discordId = userInfo.discord_id;
            String discordUsername = userInfo.discord_username;
            String discordAvatarUrl = userInfo.discord_avatar_url;

            Systems.get(ServerSeekerSystem.class).discordId = discordId;
            Systems.get(ServerSeekerSystem.class).discordUsername = discordUsername;
            Systems.get(ServerSeekerSystem.class).discordAvatarUrl = discordAvatarUrl == null ? "" : discordAvatarUrl;

            callback.accept(apiKey, null);
        }
    }
}
