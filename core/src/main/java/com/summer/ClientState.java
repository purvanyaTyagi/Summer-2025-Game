package com.summer;
import java.io.Serializable;

import com.badlogic.gdx.graphics.Color;
import java.net.*;

public class ClientState implements Serializable{
    private static final long serialVersionUID = 1L;

    public float x, y;
    public boolean FacingLeft;
    public boolean FacingRight;
    public boolean isOnPlatform;
    public boolean isWalking;
    public boolean rolling;
    public String color;
    public InetSocketAddress sock_address = new InetSocketAddress("127.0.0.1", 9999); //this is the address of the machine that the server receives the packet from. //will get overriden
    public int client_stage = 0;
    public ClientState(float x, float y, boolean FacingRight, boolean isOnPlatform, boolean isWalking, boolean rolling, String color, int client_stage){
        this.x = x;
        this.y = y;
        this.FacingRight = FacingRight;
        this.FacingLeft = !FacingRight;
        this.isOnPlatform = isOnPlatform;
        this.isWalking = isWalking;
        this.rolling = rolling;
        this.color = color;
        this.client_stage = client_stage;
    }
}
