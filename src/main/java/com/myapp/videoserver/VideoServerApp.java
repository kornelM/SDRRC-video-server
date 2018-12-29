package com.myapp.videoserver;

import com.myapp.videoserver.communication.server.UdpServer;
import org.opencv.core.Core;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
@EnableEurekaClient
public class VideoServerApp {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        ConfigurableApplicationContext context = SpringApplication.run(VideoServerApp.class, args);

        UdpServer udpServer = context.getBean(UdpServer.class);
        udpServer.receiveFrame();
    }
}
