package de.damcraft.serverseeker;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;

import static de.damcraft.serverseeker.ServerSeeker.LOG;

public class ServerSeeker2000Client extends WebSocketClient {
    public interface MessageHandlerInterface {
        void handler(ServerSeeker2000Client client, String message);
    }
    private final MessageHandlerInterface messageHandler;
    public ServerSeeker2000Client(MessageHandlerInterface messageHandler) {
        super(URI.create("wss://api.serverseeker.net/2000"));
        this.addHeader("Authorization", ServerSeekerSystem.get().apiKey);
        this.messageHandler = messageHandler;
    }

    @Override
    public void onOpen(ServerHandshake sh) {
        LOG.info("Connected to ServerSeeker 2000");
    }

    @Override
    public void onMessage(String message) {
        LOG.info("Received message from ServerSeeker 2000: " + message);
        this.messageHandler.handler(this, message);
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        LOG.info("Disconnected from ServerSeeker 2000 with code %d: %s".formatted(code, reason));
    }

    @Override
    public void onError(Exception e) {
        LOG.error("An error occurred in the ServerSeeker 2000 client");
    }
}
