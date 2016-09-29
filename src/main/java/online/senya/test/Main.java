package online.senya.test;

import org.bytedeco.javacpp.opencv_core;
import org.bytedeco.javacv.*;

import static java.lang.StrictMath.atan;
import static org.bytedeco.javacpp.avutil.M_PI;
import static org.bytedeco.javacpp.helper.opencv_objdetect.cvHaarDetectObjects;
import static org.bytedeco.javacpp.opencv_core.*;
import static org.bytedeco.javacpp.opencv_highgui.cvWaitKey;
import static org.bytedeco.javacpp.opencv_imgcodecs.cvSaveImage;
import static org.bytedeco.javacpp.opencv_imgproc.CV_AA;
import static org.bytedeco.javacpp.opencv_imgproc.cvRectangle;
import static org.bytedeco.javacpp.opencv_objdetect.*;
import static org.bytedeco.javacpp.opencv_objdetect.CV_HAAR_DO_CANNY_PRUNING;

/**
 * Created by dsv on 9/27/16.
 */
public class Main {

    public static Pool<Pair<Object, Object>> pairPool = new Pool<Pair<Object, Object>>(new PairFactory());

    public static final String FACE_HAAR =
            "/home/dsv/IdeaProjects/opcvt/src/main/resources/haarcascade_frontalface_default.xml";


    public static final String EYE_HAAR =
            "/home/dsv/IdeaProjects/opcvt/src/main/resources/haarcascade_eye.xml";

    //Detect for face using classifier XML file
    public static Pair<Object, Object> detect(final IplImage src, final HaarCascadeWrapper cascade) {

        Pair<Object, Object> points = pairPool.get();
        Pair<Object, Object> point1 = null;
        Pair<Object, Object> point2 = null;

        opencv_core.CvMemStorage storage = opencv_core.CvMemStorage.create();

        //Detect objects
        opencv_core.CvSeq sign = cvHaarDetectObjects(
                src,
                cascade.prepare(),
                storage,
                1.5,
                3,
                CV_HAAR_DO_CANNY_PRUNING);

        cvClearMemStorage(storage);
        storage.deallocate();

        int total = sign.total();


        //Draw rectangles around detected objects
//        if (total == 2) {
//            point1 = pairPool.get();
//            point2 = pairPool.get();
//
//            Pair<Object, Object> curPoint = point1;
//            points.first = curPoint;
//            points.second = point2;
//
//            for (int i = 0; i < total; i++) {
//                opencv_core.CvRect r = new opencv_core.CvRect(cvGetSeqElem(sign, i));
//                CvPoint pt1 = cvPoint(r.x(), r.y());
//
//                CvPoint pt2 = cvPoint(r.width() + r.x(), r.height() + r.y());
//
//                curPoint.first = (r.width() / 2) + r.x();
//                curPoint.second = (r.height() / 2)+ r.y();
//
//                cvRectangle(
//                        src,
//                        pt1,
//                        pt2,
//                        opencv_core.CvScalar.RED,
//                        2,
//                        CV_AA,
//                        0);
//                r.close();
//                pt1.deallocate();
//                pt2.deallocate();
//
//                curPoint = point2;
//            }
//        }

        for (int i = 0; i < total; i++) {
            opencv_core.CvRect r = new opencv_core.CvRect(cvGetSeqElem(sign, i));
            CvPoint pt1 = cvPoint(r.x(), r.y());

            CvPoint pt2 = cvPoint(r.width() + r.x(), r.height() + r.y());


            cvRectangle(
                    src,
                    pt1,
                    pt2,
                    opencv_core.CvScalar.RED,
                    2,
                    CV_AA,
                    0);
            r.close();
            pt1.deallocate();
            pt2.deallocate();
        }

        sign.deallocate();

        //Display result
        return points;
    }

    public static void main(String[] args) {
        CanvasFrame canvas = new CanvasFrame("eyefacedetecter");

        canvas.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);

        FrameGrabber grabber = new OpenCVFrameGrabber("");

        // нужно чтобы загрузилась статическая библиотека
        CvHaarClassifierCascade zzz = new CvHaarClassifierCascade(cvLoad(EYE_HAAR));
        zzz.release();

        final HaarCascadeWrapper eyesCascade = new HaarCascadeWrapper(cvLoad(EYE_HAAR));
        final HaarCascadeWrapper faceCascade = new HaarCascadeWrapper(cvLoad(FACE_HAAR));


        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                eyesCascade.release();
                faceCascade.release();
            }
        });

        try {
            grabber.start();
            opencv_core.IplImage img = null;
            OpenCVFrameConverter.ToIplImage converter = new OpenCVFrameConverter.ToIplImage();
            int i = 0;
            while (true) {
                final IplImage temp = img;
                Frame grab = grabber.grab();
                img = converter.convert(grab);
                if (temp != null) {
                    temp.release();
                }
                canvas.setCanvasSize(grabber.getImageWidth(), grabber.getImageHeight());
                if (img != null) {
                    cvFlip(img, img, 1);
                    detect(img, faceCascade);
                    Pair<Object, Object> detected = detect(img, eyesCascade);

                    Pair<Integer, Integer> leftEye = (Pair<Integer, Integer>) detected.first;
                    Pair<Integer, Integer> rightEye = (Pair<Integer, Integer>) detected.second;

                    if (leftEye != null && rightEye != null) {

                        int d1 = leftEye.second - rightEye.second;
                        int d2 = leftEye.first - rightEye.first;
                        if (d2 != 0) {
                            double atan = atan(d1 / d2);

                            double deg = atan * (180.0 / M_PI);

                            System.out.println("Angle is " + deg);
                        }
                        leftEye.release();
                        rightEye.release();
                    }

                    detected.release();

                    Frame convert = converter.convert(img);
                    canvas.showImage(convert);
                }
                i++;
                if (i > 50) {
                    i = 0;
                    System.gc();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
