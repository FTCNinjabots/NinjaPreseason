package org.firstinspires.ftc.teamcode.robot.vision;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;
import org.openftc.easyopencv.OpenCvPipeline;


public class CameraPipeline extends OpenCvPipeline {
    /*public enum DuckPosition{
        ONE,
        TWO,
        THREE,
        UNKNOWN
    }

    static final Scalar BLUE = new Scalar(0, 0, 255);
    static final Scalar GREEN = new Scalar(0, 255, 0);
    static final Scalar RED = new Scalar(255, 0, 0);

    // Region Heights and Width
    static final int REGION_HEIGHT = 50;
    static final int REGION_WIDTH = 50;
    private static Point[] REGION = new Point[]{new Point(115, 75), new Point(200, 75), new Point(285, 75)};
    public  volatile DuckPosition position = DuckPosition.UNKNOWN;


    Mat region1;
    Mat region2;
    Mat region3;
    int avg1;
    int avg2;
    int avg3;
    int maxAvg;

    Mat HSV = new Mat();
    Mat yellow = new Mat();
    static final int lowerY = 45;
    static final int upperY = 60;
    void inputToYellow(Mat input) {
        Imgproc.cvtColor(input, HSV, Imgproc.COLOR_RGB2HSV);
        Core.inRange(input, new Scalar(lowerY, 100, 100, 0), new Scalar(upperY, 255, 255, 0), yellow);
    }

    @Override
    public void init(Mat firstFrame) {
        inputToYellow(firstFrame);

        region1 = yellow.submat(new Rect(REGION[0], new Point(REGION[0].x + REGION_WIDTH, REGION[0].y + REGION_HEIGHT)));
        region2 = yellow.submat(new Rect(REGION[1], new Point(REGION[1].x + REGION_WIDTH, REGION[1].y + REGION_HEIGHT)));
        region3 = yellow.submat(new Rect(REGION[2], new Point(REGION[2].x + REGION_WIDTH, REGION[2].y + REGION_HEIGHT)));

    }

    @Override
    public Mat processFrame(Mat input) {
        inputToYellow(input);
        avg1 = (int) Core.mean(region1).val[0];
        avg2 = (int) Core.mean(region2).val[0];
        avg3 = (int) Core.mean(region3).val[0];

        Imgproc.rectangle( // Region 0
                input, // Buffer to draw on
                REGION[0], // First point which defines the rectangle
                new Point(REGION[0].x + REGION_WIDTH, REGION[0].y + REGION_HEIGHT), // Second point which defines the rectangle
                BLUE, // The color the rectangle is drawn in
                2); // Thickness of the rectangle lines

        Imgproc.rectangle( // Region 0
                input, // Buffer to draw on
                REGION[1], // First point which defines the rectangle
                new Point(REGION[1].x + REGION_WIDTH, REGION[1].y + REGION_HEIGHT), // Second point which defines the rectangle
                BLUE, // The color the rectangle is drawn in
                2); // Thickness of the rectangle lines

        Imgproc.rectangle( // Region 0
                input, // Buffer to draw on
                REGION[2], // First point which defines the rectangle
                new Point(REGION[2].x + REGION_WIDTH, REGION[2].y + REGION_HEIGHT), // Second point which defines the rectangle
                BLUE, // The color the rectangle is drawn in
                2); // Thickness of the rectangle lines

        maxAvg = Math.max(Math.max(avg1, avg2), avg3);
        if(maxAvg == avg1){
            position = DuckPosition.ONE;
        }
        else if (maxAvg == avg2){
            position = DuckPosition.TWO;
        }
        else{
            position = DuckPosition.THREE;
        }

        return input;
    }*/

    public enum RingPosition {
        FOUR,
        ONE,
        NONE,
        UNKNOWN
    }

    /*
     * Some color constants
     */
    static final Scalar BLUE = new Scalar(0, 0, 255);
    static final Scalar GREEN = new Scalar(0, 255, 0);

    /*
     * The core values which define the location and size of the sample regions
     */
    static final Point REGION1_TOPLEFT_ANCHOR_POINT = new Point(115, 75);
    //static final Point REGION1_TOPLEFT_ANCHOR_POINT = new Point(181, 98);

    static final int REGION_WIDTH = 50;
    static final int REGION_HEIGHT = 50;

    final int FOUR_RING_THRESHOLD = 133;
    final int ONE_RING_THRESHOLD = 129;

    Point region1_pointA = new Point(
            REGION1_TOPLEFT_ANCHOR_POINT.x,
            REGION1_TOPLEFT_ANCHOR_POINT.y);
    Point region1_pointB = new Point(
            REGION1_TOPLEFT_ANCHOR_POINT.x + REGION_WIDTH,
            REGION1_TOPLEFT_ANCHOR_POINT.y + REGION_HEIGHT);

    /*
     * Working variables
     */
    Mat region1_Cb;
    Mat YCrCb = new Mat();
    Mat Cb = new Mat();
    int avg1;

    // Volatile since accessed by OpMode thread w/o synchronization
    public static volatile RingPosition position = RingPosition.UNKNOWN;

    /*
     * This function takes the RGB frame, converts to YCrCb,
     * and extracts the Cb channel to the 'Cb' variable
     */
    void inputToCb(Mat input) {
        Imgproc.cvtColor(input, YCrCb, Imgproc.COLOR_RGB2YCrCb);
        Core.extractChannel(YCrCb, Cb, 1);
    }

    @Override
    public void init(Mat firstFrame) {
        inputToCb(firstFrame);

        region1_Cb = Cb.submat(new Rect(region1_pointA, region1_pointB));
    }

    @Override
    public Mat processFrame(Mat input) {
        inputToCb(input);

        avg1 = (int) Core.mean(region1_Cb).val[0];

        Imgproc.rectangle(
                input, // Buffer to draw on
                region1_pointA, // First point which defines the rectangle
                region1_pointB, // Second point which defines the rectangle
                BLUE, // The color the rectangle is drawn in
                2); // Thickness of the rectangle lines

        position = RingPosition.FOUR; // Record our analysis
        if (avg1 > FOUR_RING_THRESHOLD) {
            position = RingPosition.FOUR;
        } else if (avg1 > ONE_RING_THRESHOLD) {
            position = RingPosition.ONE;
        } else {
            position = RingPosition.NONE;
        }

        Imgproc.rectangle(
                input, // Buffer to draw on
                region1_pointA, // First point which defines the rectangle
                region1_pointB, // Second point which defines the rectangle
                GREEN, // The color the rectangle is drawn in
                -1); // Negative thickness means solid fill

        return input;
    }

    public int getAnalysis(){return avg1;}
}
