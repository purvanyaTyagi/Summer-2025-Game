package com.summer.lwjgl3.networkServer;


import java.net.*;
import java.nio.Buffer;
import java.nio.ByteBuffer;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import com.summer.*;

public class GameServer implements Runnable{
    private DatagramSocket socket;
    ConcurrentHashMap<InetSocketAddress, ClientState> clients = new ConcurrentHashMap<>();
    private ExecutorService pool = Executors.newFixedThreadPool(4); // Or cached/thread-safe pool
    public volatile boolean running = true;
    public void run(){
        try {
            socket = new DatagramSocket(9999);
            new Thread(this::broadcastLoop).start();

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

        ByteBuffer bb = ByteBuffer.wrap(data);
        float x = bb.getFloat();
        float y = bb.getFloat();
        clients.put(client_addr, new ClientState(x, y));
        System.out.println("Received Packet from port No: " + clientPort);
    }

    private void broadcastLoop(){
        while(running){
            try{
                for(Map.Entry<InetSocketAddress, ClientState> entry : clients.entrySet()){
                    InetSocketAddress addr = entry.getKey();
                    ClientState state = entry.getValue();
                    float x = state.x;
                    float y = state.y;

                    byte[] message = encodeMessage(x, y, addr);

                    for (InetSocketAddress target : clients.keySet()) {
                        if (!target.equals(addr)) {
                            DatagramPacket packet = new DatagramPacket(
                                message, message.length,
                                target.getAddress(), target.getPort()
                            );
                            socket.send(packet);
                        }
                }     
            }
        Thread.sleep(1);
        }catch(Exception e){
            e.printStackTrace();
        }
        }
    }

    public byte[] encodeMessage(float x, float y, InetSocketAddress addr) throws Exception {
        byte[] ipStr = addr.toString().getBytes("UTF-8");
        ByteBuffer buffer = ByteBuffer.allocate(4 + 4 + ipStr.length + 4); // 2 floats + IP string + string length
    
        buffer.putFloat(x);
        buffer.putFloat(y);
        buffer.putInt(ipStr.length);
        buffer.put(ipStr);
    
        return buffer.array();
    }

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
