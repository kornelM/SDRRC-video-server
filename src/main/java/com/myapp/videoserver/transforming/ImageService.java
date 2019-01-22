package com.myapp.videoserver.transforming;

import com.myapp.videoserver.communication.client.UdpClient;
import com.myapp.videoserver.utils.ImageUtils;
import lombok.Setter;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;

@Service
@Setter
public class ImageService {

    private final UdpClient udpClient;
    private final ImageUtils imageUtils;
    private int threshold;

    @Autowired
    public ImageService(UdpClient udpClient,
                        ImageUtils imageUtils) {
        this.udpClient = udpClient;
        this.imageUtils = imageUtils;
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
        Mat faceMask = getZerosMask(source);
        MatOfPoint points = imageUtils.getPolygonMaskPoints(source);
        Imgproc.fillConvexPoly(faceMask, points, new Scalar(255));

        Mat maskedImage = new Mat();
        Core.bitwise_and(source, faceMask, maskedImage);
        return maskedImage;
    }

    private Mat getZerosMask(Mat source) {
        return Mat.zeros(source.size(), CvType.CV_8U);
    }
}