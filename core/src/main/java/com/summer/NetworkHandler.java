package com.summer;

public interface NetworkHandler {
    void sendPosition(ClientState state);
    void receiveLoop();
}
