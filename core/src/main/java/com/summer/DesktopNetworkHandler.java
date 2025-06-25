package com.summer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.*;
import java.nio.ByteBuffer;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.*;

import javax.management.RuntimeErrorException;

import com.summer.assets.platform;

public class DesktopNetworkHandler implements NetworkHandler{
    private DatagramSocket socket;
    private InetAddress remoteAddress;
    private int remotePort;
    public ConcurrentHashMap<InetSocketAddress, ClientState> other_clients = new ConcurrentHashMap<>();
    ExecutorService pool = Executors.newFixedThreadPool(4);
    ConcurrentHashMap<Integer, platform> platforms = new ConcurrentHashMap<>();

    public DesktopNetworkHandler(String Host, int port){
        try {
            socket = new DatagramSocket();
            remoteAddress = InetAddress.getByName(Host);
            remotePort = port;
            new Thread(this::receiveLoop).start();

        } catch (Exception e) {
            e.printStackTrace();
            // TODO: handle exception
        }

    }

    public static InetSocketAddress parseInetSocketAddress(String str) {
        String[] parts = str.split(":");
        if (parts.length != 2) {
            throw new IllegalArgumentException("Invalid format: " + str);
        }
    
        String ip = parts[0];
        int port = Integer.parseInt(parts[1]);
    
        return new InetSocketAddress(ip, port);
    }


    @Override
    public void sendPosition(ClientState state){
        // ByteBuffer buffer = ByteBuffer.allocate(8);
        // buffer.putFloat(x);
        // buffer.putFloat(y);
        // byte[] data = buffer.array();
        // DatagramPacket packet = new DatagramPacket(data, data.length, remoteAddress, remotePort);
        // try {
        //     socket.send(packet);
        // } catch (Exception e) {
        //     e.printStackTrace();
        // }
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(state);
            oos.flush();
            byte[] buffer = baos.toByteArray();
            if(buffer.length > 511){
                throw new RuntimeException("CLientState length greater than allowed size");
            }
            DatagramPacket packet = new DatagramPacket(buffer, buffer.length, remoteAddress, remotePort);
            socket.send(packet);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    @Override
    public void receiveLoop(){
        try {
            while(true){
                byte[] buffer = new byte[512];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet);
                byte[] dataCopy = new byte[packet.getLength()];
                System.arraycopy(packet.getData(), 0, dataCopy, 0, packet.getLength());

                byte type = dataCopy[0];
                if(type == 1){
                    pool.submit(() -> handleMesssage(dataCopy));
                }else if(type == 2){
                    pool.submit(() -> handlePlatform(dataCopy));
                }else{
                    throw new RuntimeException("Packet type not recognized");
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            System.err.println("Error in receiveLoop: " + e.getMessage());
        }
    }
    private void handleMesssage(byte[] receivedMessage){
        // try {
        //     ByteBuffer buffer = ByteBuffer.wrap(data);
        //     float x = buffer.getFloat();
        //     float y = buffer.getFloat();
        //     int len = buffer.getInt();
        //     byte[] addrBytes = new byte[len];
        //     buffer.get(addrBytes);
        //     String sender = new String(addrBytes, "UTF-8");

        //     InetSocketAddress addr_sock = parseInetSocketAddress(sender);

        //     other_clients.put(addr_sock, new ClientState(x, y));
        // } catch (Exception e) {
        //     // TODO: handle exception
        // }
        try {
            byte[] data = Arrays.copyOfRange(receivedMessage, 1, receivedMessage.length);
            ByteArrayInputStream bais = new ByteArrayInputStream(data);
            ObjectInputStream ois = new ObjectInputStream(bais);
            ClientState state = (ClientState) ois.readObject();
            other_clients.put(state.sock_address, state);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    private void handlePlatform(byte[] message){
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(message));
        try {
            byte type = dis.readByte();
            int id = dis.readInt();
            float x = dis.readFloat();
            float y = dis.readFloat();
            float w = dis.readFloat();
            float h = dis.readFloat();
            platforms.put(id, new platform(x, y, w, h));
            //if (!platforms.containsKey(id)) {            }        
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}
