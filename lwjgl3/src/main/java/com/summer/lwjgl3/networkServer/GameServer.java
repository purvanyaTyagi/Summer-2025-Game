package com.summer.lwjgl3.networkServer;


import java.net.*;
import java.io.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import com.summer.*;
import com.summer.assets.*;

public class GameServer implements Runnable{
    private DatagramSocket socket;
    ConcurrentHashMap<InetSocketAddress, ClientState> clients = new ConcurrentHashMap<>();
    private ExecutorService pool = Executors.newFixedThreadPool(8); // Or cached/thread-safe pool
    public volatile boolean running = true;
    ConcurrentHashMap<Integer, CopyOnWriteArrayList<platform>>  stages = new ConcurrentHashMap<>();
    int maxPacketSize = 512; // safe size for most networks
    int platformSize = 1 + 4 + 4 * 4; // type (1) + id (4) + 4 floats

    int maxPlatforms = (maxPacketSize - 1) / platformSize; // leave 1 byte for overall type

    public void run(){
        try {
            socket = new DatagramSocket(9999);
            CopyOnWriteArrayList<platform> stage_0 = new CopyOnWriteArrayList<>();
            PlatformGenerator.generateStackedPlatforms(1500, -385, 3, 254, stage_0);
            stages.put(0, stage_0);
            new Thread(this::broadcastLoop).start();
            new Thread(this::Handle_Platforms).start();
            new Thread(this::cleanupLoop).start();

            while(running){
                byte[] buffer = new byte[512];
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);

                socket.receive(packet);

                byte[] datacopy = new byte[packet.getLength()];
                System.arraycopy(packet.getData(), 0, datacopy, 0, packet.getLength());

                InetAddress senderAddress = packet.getAddress();
                int sender_port = packet.getPort();
                
                pool.submit(() -> handlePacket(senderAddress, sender_port, datacopy));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void handlePacket(InetAddress clientIP, int clientPort, byte[] data){
        InetSocketAddress client_addr = new InetSocketAddress(clientIP, clientPort);

        try {
            // ByteArrayInputStream bais = new ByteArrayInputStream(data);
            // ObjectInputStream ois = new ObjectInputStream(bais);

            // ClientState client_state = (ClientState) ois.readObject();
            ClientState client_state = new ClientState(data);
            client_state.sock_address = client_addr;
            client_state.lastSeen = System.currentTimeMillis();
            clients.put(client_addr, client_state);
            //System.out.println("Received Packet from port No: " + clientPort);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // ByteBuffer bb = ByteBuffer.wrap(data);
        // float x = bb.getFloat();
        // float y = bb.getFloat();
        // clients.put(client_addr, new ClientState(x, y));
    }

    private void broadcastLoop(){
        while(running){
            try{
                for(Map.Entry<InetSocketAddress, ClientState> entry : clients.entrySet()){
                    InetSocketAddress addr = entry.getKey();
                    ClientState state = entry.getValue();
                    // float x = state.x;
                    // float y = state.y;
                    // ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    // ObjectOutputStream oos = new ObjectOutputStream(baos);
                    // oos.writeObject(state);
                    // oos.flush();
                    byte[] serializedState = state.serialize();
                    
                    byte type = 1;
                    byte[] message = new byte[1 + serializedState.length];
                    message[0] = type;
                    System.arraycopy(serializedState, 0, message, 1, serializedState.length);

                    //byte[] message = baos.toByteArray();

                    for (InetSocketAddress target : clients.keySet()) {
                        if (!target.equals(addr)) {
                            DatagramPacket packet = new DatagramPacket(
                                message, message.length,
                                target.getAddress(), target.getPort()
                            );
                            socket.send(packet);
                        }
                }
                byte[] ack_message = new byte[1];
                ack_message[0] = 3;
                DatagramPacket packet = new DatagramPacket(
                                ack_message, ack_message.length,
                                addr.getAddress(), addr.getPort()
                            );
                socket.send(packet);

            }
        //Thread.sleep(1);
        }catch(Exception e){
            e.printStackTrace();
        }
        }
    }

    private void Handle_Platforms(){
        while(running){

            try {
                int count = 1;
                for(Map.Entry<InetSocketAddress, ClientState> entry : clients.entrySet()){
                    InetSocketAddress addr = entry.getKey();

                    CopyOnWriteArrayList<platform> platforms = new CopyOnWriteArrayList<>();
                    platforms = stages.get(entry.getValue().client_stage);

                    if(platforms == null){
                        CopyOnWriteArrayList<platform> new_stage = new CopyOnWriteArrayList<>();
                        PlatformGenerator.generateStackedPlatforms(1500, -385, 3, 254, new_stage);
                        stages.put(entry.getValue().client_stage, new_stage);      
                        platforms = new_stage;
                    }

                    ByteBuffer buffer = ByteBuffer.allocate(1 + platforms.size() * platformSize);
                    buffer.put((byte) 2);
                    for(platform p : platforms){
                        // ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        // DataOutputStream dos = new DataOutputStream(baos);
                        // dos.writeByte(2); // type = 2 for "platform" (optional tag)
                        // dos.writeInt(count);
                        // dos.writeFloat(p.x);
                        // dos.writeFloat(p.y);
                        // dos.writeFloat(p.width);
                        // dos.writeFloat(p.height);
            
                        // byte[] data = baos.toByteArray();
                        // DatagramPacket packet = new DatagramPacket(data, data.length, addr);
                        // socket.send(packet); 
                        // count += 1;
                        buffer.putInt(count);      // ID
                        buffer.putFloat(p.x);        // X
                        buffer.putFloat(p.y);        // Y
                        buffer.putFloat(p.width);    // Width
                        buffer.putFloat(p.height);   // Height    
                        count += 1;                
                    }
                    byte[] data = buffer.array();
                    DatagramPacket packet = new DatagramPacket(data, data.length, addr);
                    socket.send(packet);
                }
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
    }

    public void cleanupLoop(){
        while(running){
            try {
                long now = System.currentTimeMillis();
                for (Map.Entry<InetSocketAddress, ClientState> entry : clients.entrySet()) {
                    if (now - entry.getValue().lastSeen > 5_000) { // 10 seconds timeout
                        clients.remove(entry.getKey());
                        System.out.println("Removed inactive client: " + entry.getKey());
                    }
                }
                for (Integer stageId : stages.keySet()) {
                    boolean ShouldBeRemoved = true;
                    for(ClientState client_states : clients.values()){
                        if(client_states.client_stage <= stageId){
                            ShouldBeRemoved = false;
                        }
                    }
                    if(ShouldBeRemoved && stageId != 0){
                        stages.remove(stageId);
                        System.out.println("Removed Stage No: " + stageId);
                    }
                }                
                Thread.sleep(1000); // Check every second
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void add_stage(){ 

    }
    // public byte[] encodeMessage(float x, float y, InetSocketAddress addr) throws Exception {
    //     byte[] ipStr = addr.toString().getBytes("UTF-8");
    //     ByteBuffer buffer = ByteBuffer.allocate(4 + 4 + ipStr.length + 4); // 2 floats + IP string + string length
    
    //     buffer.putFloat(x);
    //     buffer.putFloat(y);
    //     buffer.putInt(ipStr.length);
    //     buffer.put(ipStr);
    
    //     return buffer.array();
    // }

    public void stop() {
        running = false;
        pool.shutdown();
        try {
            if (!pool.awaitTermination(3, TimeUnit.SECONDS)) {
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            pool.shutdownNow();
        }
        socket.close();
    }
}
