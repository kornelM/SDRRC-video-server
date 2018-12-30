package com.myapp.videoserver.transforming;

import com.myapp.videoserver.communication.client.UdpClient;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static org.opencv.imgproc.Imgproc.Canny;

@Service
public class ImageService {

    private final UdpClient udpClient;

    @Autowired
    public ImageService(UdpClient udpClient) {
        this.udpClient = udpClient;
    }

    public void performLaneDetection(byte[] bytes) {
        Mat originalMat = bytesToMat(bytes);
        udpClient.sendImagePacket(findLines_1(originalMat));
    }

    private Mat bytesToMat(byte[] bytes) {
        return Imgcodecs.imdecode(new MatOfByte(bytes), Imgcodecs.IMREAD_UNCHANGED);
    }

    private Mat findLines(Mat rawMat) {
        Mat grayMat = new Mat();
        Mat cannyEdges = new Mat();
        Mat lines = new Mat();


        Canny(grayMat, cannyEdges, 50, 200, 3, false);
//        Imgproc.cvtColor(cannyEdges, grayMat, Imgproc.COLOR_BGR2GRAY);

        Imgproc.HoughLinesP(cannyEdges, lines, 1, Math.PI / 180, 50, 20, 20);

        Mat houghLines = new Mat();
        houghLines.create(cannyEdges.rows(), cannyEdges.cols(), CvType.CV_8UC1);

        //Drawing lines on the image
        for (int i = 0; i < lines.cols(); i++) {
            double[] points = lines.get(0, i);
            double x1, y1, x2, y2;

            x1 = points[0];
            y1 = points[1];
            x2 = points[2];
            y2 = points[3];

            Point pt1 = new Point(x1, y1);
            Point pt2 = new Point(x2, y2);

            //Drawing lines on an image
            Imgproc.line(houghLines, pt1, pt2, new Scalar(255, 0, 0), 1);

        }
        return houghLines;
    }

    private Mat findLines_1(Mat src) {

        Mat dst = new Mat();
        Mat cdst = new Mat();


        Imgproc.GaussianBlur(src, dst, new Size(25,25), 0.0);
        // Edge detection
        Imgproc.Canny(src, dst, 150, 200, 3, false);

        // Copy edges to the images that will display the results in BGR
//        Imgproc.cvtColor(dst, cdst, Imgproc.COLOR_GRAY2BGR);

        // Standard Hough Line Transform
        Mat lines = new Mat(); // will hold the results of the detection
//        Imgproc.HoughLines(dst, lines, 1, Math.PI / 180, 150); // runs the actual detection

        // Draw the lines
//        for (int x = 0; x < lines.rows(); x++) {
//            double rho = lines.get(x, 0)[0];
//            double theta = lines.get(x, 0)[1];
//            double a = Math.cos(theta), b = Math.sin(theta);
//            double x0 = a * rho, y0 = b * rho;
//
//            Point pt1 = new Point(Math.round(x0 + 1000 * (-b)), Math.round(y0 + 1000 * (a)));
//            Point pt2 = new Point(Math.round(x0 - 1000 * (-b)), Math.round(y0 - 1000 * (a)));
//
//            Imgproc.line(cdst, pt1, pt2, new Scalar(0, 0, 255), 10, Imgproc.LINE_AA, 0);
//            Imgproc.polylines(cdst, pt1, pt2, new Scalar(0, 0, 255), 10, Imgproc.LINE_AA, 0);
//        }

        // Probabilistic Line Transform
//        Mat linesP = new Mat(); // will hold the results of the detection
//        Imgproc.HoughLinesP(dst, linesP, 1, Math.PI / 180, 150, 50, 10); // runs the actual detection
//
//        // Draw the lines
//        for (int x = 0; x < linesP.rows(); x++) {
//            double[] l = linesP.get(x, 0);
//            Imgproc.line(cdstP, new Point(l[0], l[1]), new Point(l[2], l[3]), new Scalar(0, 0, 255), 3, Imgproc.LINE_AA, 0);
//        }
//        // Show results
//        HighGui.imshow("Source", src);
//        HighGui.imshow("Detected Lines (in red) - Standard Hough Line Transform", cdst);
//        HighGui.imshow("Detected Lines (in red) - Probabilistic Line Transform", cdstP);
        // Wait and Exit
//        HighGui.waitKey();
        return cropImage(dst);
    }

    private Mat cropImage(Mat orginalMat) {
        Rect rectCrop = new Rect(0, orginalMat.rows() / 2, orginalMat.cols(), orginalMat.rows() / 2);//bottom half of frame - from the middle to bottom
        return orginalMat.submat(rectCrop);
    }
}
