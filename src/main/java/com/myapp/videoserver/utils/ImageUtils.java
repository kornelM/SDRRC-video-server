package com.myapp.videoserver.utils;

import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

@Component
public class ImageUtils {

    @Value("${video.polygon.leftPointPosition}")
    private String leftPointPosition;

    @Value("${video.polygon.centerPointPosition.width}")
    private String centerPointPositionWidth;

    @Value("${video.polygon.centerPointPosition.height}")
    private String centerPointPositionHeight;

    @Value("${video.polygon.rightPointPosition}")
    private String rightPointPosition;

    private MatOfPoint polygon;

    public BufferedImage matToBufferedImage(Mat original) {
        BufferedImage image;
        int width = original.width();
        int height = original.height();
        int channels = original.channels();

        byte[] sourcePixels = new byte[width * height * channels];
        original.get(0, 0, sourcePixels);

        if (original.channels() > 1) {
            image = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
        } else {
            image = new BufferedImage(width, height, BufferedImage.TYPE_BYTE_GRAY);
        }
        final byte[] targetPixels = ((DataBufferByte) image.getRaster().getDataBuffer()).getData();
        System.arraycopy(sourcePixels, 0, targetPixels, 0, sourcePixels.length);

        return image;
    }


    public MatOfPoint getPolygonMaskPoints(Mat source) {
        if (polygon != null) {
            return polygon;
        } else {
            polygon = new MatOfPoint(getPolygon(source));
            return polygon;
        }
    }

    private Point[] getPolygon(Mat source) {
        int height = source.rows();
        int width = source.cols();

        double oneThird = Double.valueOf(leftPointPosition) * width;
        double twoThird = Double.valueOf(rightPointPosition) * width;
        double halfOfWidth = Double.valueOf(centerPointPositionWidth) * width;
        double halfOfHeight = Double.valueOf(centerPointPositionHeight) * height;

        return new Point[]{
                new Point(oneThird, height),
                new Point(twoThird, height),
                new Point(halfOfWidth, halfOfHeight)
        };
    }
}
