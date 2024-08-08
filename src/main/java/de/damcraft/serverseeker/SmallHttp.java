package de.damcraft.serverseeker;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;

import static de.damcraft.serverseeker.ServerSeeker.LOG;

public class SmallHttp {
    public static String post(String url, String json) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            return client.send(HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .header("Content-Type", "application/json")
                    .build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            ).body();
        } catch (IOException | InterruptedException e) {
            LOG.error(e.toString());
            return null;
        }
    }

    public static String get(String url) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            return client.send(HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build(),
                HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8)
            ).body();
        } catch (IOException | InterruptedException e) {
            LOG.error(e.toString());
            return null;
        }
    }

    public static HttpResponse<InputStream> download(String url) {
        try (HttpClient client = HttpClient.newHttpClient()) {
            HttpResponse<InputStream> req = client.send(HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .GET()
                    .build(),
                HttpResponse.BodyHandlers.ofInputStream()
            );
            if (req.headers().firstValue("location").isPresent()) {
                return download(req.headers().firstValue("location").get());
            }
            return req;
        } catch (IOException | InterruptedException e) {
            LOG.error(e.toString());
            return null;
        }
    }
}
