package com.myapp.videoserver.server;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;


@Getter
@Component
public class UdpServer implements Runnable {

    private DatagramSocket socket;
    private byte[] buf = new byte[65535];

    @Value("${video.server.port}")
    private Integer serverPort;

    @Value("${video.client.inetAddress}")
    private String inetAddress;

    private InputStream inputStream;

    @PostConstruct
    public void setAll() throws SocketException {
        socket = new DatagramSocket(serverPort);
    }

    @Override
    public void run() {
        boolean running = true;
        System.out.println("Server is running");

        while (running) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);

            this.inputStream = new ByteArrayInputStream(packet.getData());
            System.out.println(this.inputStream.toString());

        }
        socket.close();
    }

    public InputStream testMethod() {

        DatagramPacket datagramPacket = new DatagramPacket(buf, buf.length);

        try {
            socket.receive(datagramPacket);
        } catch (IOException e) {
            e.printStackTrace();
        }

        InetAddress inetAddress = datagramPacket.getAddress();
        int port = datagramPacket.getPort();
        datagramPacket = new DatagramPacket(buf, buf.length, inetAddress, port);

        return new ByteArrayInputStream(datagramPacket.getData());
    }
}