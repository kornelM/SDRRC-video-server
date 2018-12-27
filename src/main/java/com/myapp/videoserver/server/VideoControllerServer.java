package com.myapp.videoserver.server;

import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.embed.swing.SwingFXUtils;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.imageio.ImageIO;
import java.io.IOException;
import java.io.InputStream;

@Setter
@Component
public class VideoControllerServer {

    @FXML
    private Button button;
    @FXML
    private CheckBox grayscale;
    @FXML
    private CheckBox logoCheckBox;
    @FXML
    private ImageView histogram;
    @FXML
    private ImageView currentFrame;

    private InputStream byteArrayInputStream;

    @Autowired
    private UdpServer udpServer;

    @FXML
    protected void launchWindow() {
        this.currentFrame.setFitWidth(600);
        this.currentFrame.setPreserveRatio(true);

        while (true) {
            Image image = null;
            try {
//            image = SwingFXUtils.toFXImage(ImageIO.read(byteArrayInputStream), null);
                image = SwingFXUtils.toFXImage(ImageIO.read(udpServer.testMethod()), null);
            } catch (IOException e) {
                e.printStackTrace();
            }
            updateImageView(currentFrame, image);

        }

    }

    private void updateImageView(ImageView view, Image image) {
        onFXThread(view.imageProperty(), image);
    }

    private static <T> void onFXThread(final ObjectProperty<T> property, final T value) {
        Platform.runLater(() -> property.set(value));
    }
}

