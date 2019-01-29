package com.myapp.videoserver.transforming;

import com.myapp.videoserver.communication.client.UdpClient;
import com.myapp.videoserver.utils.ImageUtils;
import lombok.Setter;
import org.apache.commons.lang.ArrayUtils;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

import static org.opencv.imgproc.Imgproc.COLOR_BGR2GRAY;

@Service
@Setter
public class ImageService {

    private final UdpClient udpClient;
    private final ImageUtils imageUtils;
    private int thresholdBottom;
    private int thresholdTop;
    private double rho;
    private double maxLineGap;
    private double minLineLength;
    private int houghLinesPThreshold;

    @Autowired
    public ImageService(UdpClient udpClient,
                        ImageUtils imageUtils) {
        this.udpClient = udpClient;
        this.imageUtils = imageUtils;
        this.thresholdBottom = 50;
        this.thresholdBottom = 150;
        this.rho = 2.0;
        this.maxLineGap = 5.0;
        this.minLineLength = 40.0;
        this.houghLinesPThreshold = 100;
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
        Imgproc.Canny(blur, canny, thresholdBottom, thresholdTop);

        source = regionOfInterest(canny);

        Mat updatedLines = new Mat();
        Mat lines = drawLine(applyHoughLinesP(source), source);
        Core.addWeighted(lines, 0.8, lines, 1, 1, updatedLines);


        //todo
        double[] smoothLines = averageLines(source, lines);

        return updatedLines;
    }

    private Mat regionOfInterest(Mat source) {
        Mat faceMask = getZerosMask(source);
        MatOfPoint points = imageUtils.getPolygonMaskPoints(source);
        Imgproc.fillConvexPoly(faceMask, points, new Scalar(255)); //black color

        Mat maskedImage = new Mat();
        Core.bitwise_and(source, faceMask, maskedImage);
        return maskedImage;
    }

    private Mat applyHoughLinesP(Mat source) {
        Mat lines = new Mat();
        Imgproc.HoughLinesP(source, lines, rho, Math.PI / 180, houghLinesPThreshold, minLineLength, maxLineGap);
        if (lines.rows() == 0 || lines.cols() == 0) {
            return source;
        }
        return lines;
    }

    private Mat drawLine(Mat matAfterHoughLinesP, Mat source) {
        Mat lines = getZerosMask(source);
        double x1, y1, x2, y2;
        for (int i = 0; i < matAfterHoughLinesP.cols(); i++) {
            for (int j = 0; j < matAfterHoughLinesP.rows(); j++) {
                double[] table = matAfterHoughLinesP.get(j, i);
                x1 = table[0];
                y1 = table[1];
                x2 = table[2];
                y2 = table[3];
                Imgproc.line(lines, new Point(x1, y1), new Point(x2, y2), new Scalar(100, 100, 100), 10);
            }
        }
        return lines;
    }

    private Mat getZerosMask(Mat source) {
        return Mat.zeros(source.size(), CvType.CV_8U);
    }

    //todo should return Mat
    private double[] averageLines(Mat source, Mat lines) {
        HashMap<Double, Double> left = new HashMap<>();
        HashMap<Double, Double> right = new HashMap<>();
        double x1, y1, x2, y2;

        for (int i = 0; i < lines.cols(); i++) {
            for (int j = 0; j < lines.rows(); j++) {
                double[] table = lines.get(j, i);
                x1 = table[0];
                y1 = table[1];
                x2 = table[2];
                y2 = table[3];

                double slope = slope(x1, y1, x2, y2);
                double intercept = interception(x2, y2, slope);

                if (slope < 0) {
                    left.put(slope, intercept);
                } else {
                    right.put(slope, intercept);
                }


            }
        }
        //todo average slope left/right, intercept left/right
        double leftSlopeAverage = left.entrySet()
                .stream()
                .mapToDouble(Map.Entry::getValue)
                .average()
                .getAsDouble();

        double leftInterceptAverage = left.values()
                .stream()
                .mapToDouble(d -> d)
                .average()
                .getAsDouble();

        double[] leftFitAverage = new double[]{leftSlopeAverage, leftInterceptAverage};

        double rightSlopeAverage = right.entrySet()
                .stream()
                .mapToDouble(Map.Entry::getValue)
                .average()
                .getAsDouble();

        double rightInterceptAverage = right.values()
                .stream()
                .mapToDouble(d -> d)
                .average()
                .getAsDouble();

        double[] rightFitAverage = new double[]{rightSlopeAverage, rightInterceptAverage};

        double[] leftLine = makeCoordinates(source, leftFitAverage);
        double[] rightLine = makeCoordinates(source, rightFitAverage);

        return ArrayUtils.addAll(leftLine, rightLine);
    }

    //todo make coordinates
    private double[] makeCoordinates(Mat source, double[] slopeAndIntercept) {
        double slope = slopeAndIntercept[0];
        double intercept = slopeAndIntercept[1];

        int y1 = source.rows();
        int y2 = (int) (y1 * (3.0 / 5.0));
        int x1 = (int) ((y1 - intercept) / slope);
        int x2 = (int) ((y2 - intercept) / slope);

        return new double[]{x1, y1, x2, y2};

    }

    private double[] getSlopeAndIntercept(double x1, double x2, double y1, double y2) {
        double slope = (y2 - y1) / (x2 - x1);
        double interception = y2 - (slope * x2);

        return new double[]{slope, interception};
    }

    private double slope(double x1, double y1, double x2, double y2) {
        return (y2 - y1) / (x2 - x1);
    }

    private double interception(double x2, double y2, double slope) {
        return y2 - (slope * x2);
    }

}


