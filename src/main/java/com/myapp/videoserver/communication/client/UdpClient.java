package com.myapp.videoserver.communication.client;

import com.myapp.videoserver.utils.ImageUtils;
import lombok.Getter;
import org.opencv.core.Mat;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;

@Component
@Getter
public class UdpClient {

    private Logger LOGGER = LoggerFactory.getLogger(UdpClient.class);

    private static final String JPG_FORMAT = "jpg";

    @Value("${video.gui.port}")
    private Integer portGui;

    @Value("${video.gui.address}")
    private String inetAddressGui;

    private final DatagramSocket socketGui;
    private final InetAddress addressGui;
    private final ImageUtils imageUtils;

    @Autowired
    public UdpClient(ImageUtils imageUtils) throws SocketException, UnknownHostException {
        this.socketGui = new DatagramSocket();
        this.addressGui = InetAddress.getByName(inetAddressGui);
        this.imageUtils = imageUtils;
    }

    public boolean sendImagePacket(Mat mat) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {
            ImageIO.write(imageUtils.matToBufferedImage(mat), JPG_FORMAT, baos);
        } catch (IOException e) {
            LOGGER.error("Could not convert BufferedImage into byte array!", e);
        }

        byte[] bytes = baos.toByteArray();
        DatagramPacket packetGui = new DatagramPacket(bytes, bytes.length, addressGui, portGui);

        try {
            socketGui.send(packetGui);
            return true;
        } catch (IOException e) {
            LOGGER.error("Could not send packet to gui!", e.getCause());
            return false;
        }
    }

    void close() {
        socketGui.close();
    }
}