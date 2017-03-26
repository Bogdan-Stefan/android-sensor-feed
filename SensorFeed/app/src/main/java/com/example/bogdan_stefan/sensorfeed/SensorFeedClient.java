package com.example.bogdan_stefan.sensorfeed;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;


class SensorFeedClient extends WebSocketClient {
    private WebSocketEventHandler webSocketEventHandler;

    public SensorFeedClient(URI serverUri, WebSocketEventHandler webSocketEventHandler) {
        super(serverUri);
        this.webSocketEventHandler = webSocketEventHandler;
    }

    @Override
    public void onOpen(ServerHandshake serverHandshake) {
        webSocketEventHandler.connectionOpened();
    }

    @Override
    public void onMessage(String s) {
        System.out.println("Received: " + s);
    }

    @Override
    public void onClose(int i, String s, boolean b) {
        webSocketEventHandler.connectionClosed();
    }

    @Override
    public void onError(Exception e) {
        e.printStackTrace();
        webSocketEventHandler.exceptionRaised("Web Socket Error!");
    }
}
