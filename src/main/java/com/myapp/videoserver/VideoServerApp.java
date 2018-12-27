package com.myapp.videoserver;

import com.myapp.videoserver.server.UdpServer;
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

        //todo uruchamiam wątek odbierania ramek
        UdpServer udpServer = context.getBean(UdpServer.class);
//        VideoServer videoServer = context.getBean(VideoServer.class);

        Thread udpServerThread = new Thread(udpServer);
        udpServerThread.run();

//        Thread myThread = new Thread(videoServer);
//        myThread.run();
    }
}
