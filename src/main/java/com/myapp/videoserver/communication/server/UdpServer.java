package com.myapp.videoserver.communication.server;

import com.myapp.videoserver.transforming.ImageService;
import lombok.Getter;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.imgcodecs.Imgcodecs;
import org.springframework.beans.factory.annotation.Autowired;
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


@Component
@Getter
public class UdpServer {

    private static final int BUFFER_SIZE = 65535;

    private DatagramSocket socket;
    private byte[] buf = new byte[BUFFER_SIZE];

    @Value("${video.server.port}")
    private Integer serverPort;

    @Value("${video.client.inetAddress}")
    private String inetAddress;

    private InputStream inputStream;
    private boolean isRunning;
    private final ImageService imageService;


    @Autowired
    public UdpServer(ImageService imageService) throws SocketException {
        this.imageService = imageService;
        this.isRunning = true;
    }


    @PostConstruct
    public void setup() throws SocketException {
        this.socket = new DatagramSocket(serverPort);
    }

    public void receiveFrame() {

        while (isRunning) {
            DatagramPacket packet = new DatagramPacket(buf, buf.length);
            try {
                socket.receive(packet);
            } catch (IOException e) {
                e.printStackTrace();
            }

            InetAddress address = packet.getAddress();
            int port = packet.getPort();
            packet = new DatagramPacket(buf, buf.length, address, port);

            Mat mat = Imgcodecs.imdecode(new MatOfByte(packet.getData()), Imgcodecs.IMREAD_UNCHANGED);

            this.inputStream = new ByteArrayInputStream(packet.getData());
            imageService.performLaneDetection(packet.getData());
            System.out.println(this.inputStream.toString());

        }
        socket.close();
    }
}
