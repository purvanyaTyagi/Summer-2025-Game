package com.summer;
import java.io.Serializable;

import com.badlogic.gdx.graphics.Color;
import java.net.*;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

public class ClientState{
    public float x, y;
    public boolean FacingLeft;
    public boolean FacingRight;
    public boolean isOnPlatform;
    public boolean isWalking;
    public boolean rolling;
    public String color;
    public InetSocketAddress sock_address = new InetSocketAddress("127.0.0.1", 9999); //this is the address of the machine that the server receives the packet from. //will get overriden
    public int client_stage = 0;
    public long lastSeen = System.currentTimeMillis();
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

    public ClientState(byte[] data){
        ByteBuffer buffer = ByteBuffer.wrap(data);

        x = buffer.getFloat();
        y = buffer.getFloat();
        FacingRight = buffer.get() == 1;
        FacingLeft = !FacingRight;
        isOnPlatform = buffer.get() == 1;
        isWalking = buffer.get() == 1;
        rolling = buffer.get() == 1;
    
        int colorLen = buffer.getInt();
        byte[] colorBytes = new byte[colorLen];
        buffer.get(colorBytes);
        color = new String(colorBytes, StandardCharsets.UTF_8);
    
        int ipLen = buffer.getInt();
        byte[] ipBytes = new byte[ipLen];
        buffer.get(ipBytes);
        String ip = new String(ipBytes, StandardCharsets.UTF_8);
    
        int port = buffer.getInt();
        sock_address = new InetSocketAddress(ip, port);
    
        client_stage = buffer.getInt();
        lastSeen = buffer.getLong();
    }

    public byte[] serialize(){
        byte[] colorBytes = color.getBytes(StandardCharsets.UTF_8);
        byte[] ipStr = sock_address.getAddress().getHostAddress().getBytes(StandardCharsets.UTF_8);
        int totalSize = (
            4 + 4 +                 // x, y
            1 + 1 + 1 + 1 +         // 4 booleans
            4 + colorBytes.length +// color bytes size + color
            4 + ipStr.length +   // IP string
            4 +                    // port
            4 +                    // client_stage
            8                     // lastSeen  
        );
        ByteBuffer buffer = ByteBuffer.allocate(totalSize);
        
        buffer.putFloat(this.x);
        buffer.putFloat(this.y);
        buffer.put((byte) (this.FacingRight ? 1 : 0));
        buffer.put((byte) (this.isOnPlatform ? 1 : 0));
        buffer.put((byte) (this.isWalking ? 1 : 0));
        buffer.put((byte) (this.rolling ? 1 : 0));
    
        buffer.putInt(colorBytes.length);
        buffer.put(colorBytes);
    
        buffer.putInt(ipStr.length);
        buffer.put(ipStr);
    
        buffer.putInt(sock_address.getPort());
    
        buffer.putInt(client_stage);
    
        buffer.putLong(lastSeen);
    
        return buffer.array();    
    }
}
