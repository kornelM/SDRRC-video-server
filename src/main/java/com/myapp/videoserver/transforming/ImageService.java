package com.myapp.videoserver.transforming;

import com.myapp.videoserver.communication.client.UdpClient;
import lombok.Setter;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

import static org.opencv.core.CvType.CV_8U;
import static org.opencv.core.CvType.CV_8UC3;
import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;
import static org.opencv.imgproc.Imgproc.Canny;

@Service
@Setter
public class ImageService {

    private final UdpClient udpClient;
    private int threshold;

    @Autowired
    public ImageService(UdpClient udpClient) {
        this.udpClient = udpClient;
        this.threshold = 150;
    }

    public void performLaneDetection(byte[] bytes) {
        Mat originalMat = bytesToMat(bytes);
        udpClient.sendImagePacket(findLinesTest(originalMat));
    }

    private Mat bytesToMat(byte[] bytes) {
        return Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_UNCHANGED);
    }

    private Mat findLinesTest(Mat source) {
        Mat gray = new Mat();
        Mat blur = new Mat();
        Mat canny = new Mat();

        Imgproc.cvtColor(source, gray, COLOR_BGR2GRAY);
        Imgproc.GaussianBlur(gray, blur, new Size(5, 5), 0);
        Imgproc.Canny(blur, canny, 50.0, 150);

        return regionOfInterest(canny);
    }

    private Mat regionOfInterest(Mat source) {
        int height = source.rows();
        int width = source.cols();

        double oneThird = (double) 1 / 3 * width;
        double twoThird = (double) 2 / 3 * width;
        double halfOfWidth = 0.5 * width;
        double halfOfHeight = 0.5 * height;

        Point point_1 = new Point(oneThird, height);
        Point point_2 = new Point(twoThird, height);
        Point point_3 = new Point(halfOfWidth, halfOfHeight);
        List<Point> polygons = new ArrayList<>();
        polygons.add(point_1);
        polygons.add(point_2);
        polygons.add(point_3);

        Point[] pointArray = new Point[polygons.size()];

        Point pt;
        for (int i = 0; i < polygons.size(); i++) {
            pt = polygons.get(i);
            pointArray[i] = new Point(pt.x, pt.y);
        }

        Mat faceMask = Mat.zeros(source.size(), CV_8U);

        MatOfPoint points = new MatOfPoint(pointArray);

        Imgproc.fillConvexPoly(faceMask, points, new Scalar(255));


        Mat maskedImage = new Mat();
        Core.bitwise_and(source, faceMask, maskedImage);
        return maskedImage;

    }
}