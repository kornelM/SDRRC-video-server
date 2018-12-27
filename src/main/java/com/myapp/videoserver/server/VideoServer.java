package com.myapp.videoserver.server;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.stereotype.Component;

import java.io.InputStream;

@Setter
@Component
@NoArgsConstructor
@AllArgsConstructor
public class VideoServer extends Application implements Runnable{

    private InputStream byteArrayInputStream;

    @Override
    public void start(Stage primaryStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Video_Server.fxml"));
            BorderPane rootElement = (BorderPane) loader.load();
            Scene scene = new Scene(rootElement, 800, 600);
            scene.getStylesheets().add(getClass().getResource("/application.css").toExternalForm());
            primaryStage.setTitle("Video processing");
            primaryStage.setScene(scene);
            primaryStage.show();

            VideoControllerServer controller = loader.getController();
            controller.setByteArrayInputStream(byteArrayInputStream);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        Application.launch();

    }
}
