package com.example.bogdan_stefan.sensorfeed;


interface WebSocketEventHandler {
    void connectionOpened();

    void connectionClosed();

    void exceptionRaised(String errorMessage, Exception e);
}
