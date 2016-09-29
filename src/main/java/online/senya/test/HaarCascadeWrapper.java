package online.senya.test;

import org.bytedeco.javacpp.Pointer;
import org.bytedeco.javacpp.opencv_objdetect;
import org.bytedeco.javacpp.opencv_objdetect.CvHaarClassifierCascade;

import static org.bytedeco.javacpp.opencv_core.cvRelease;

/**
 * Created by dsv on 9/28/16.
 */
public class HaarCascadeWrapper {

    private final Pointer xmlPointer;

    private CvHaarClassifierCascade current = null;

    public HaarCascadeWrapper(Pointer xmlPointer) {
        this.xmlPointer = xmlPointer;
    }

    public CvHaarClassifierCascade prepare() {
        if (current != null) {
            current.release();
        }
        current = new CvHaarClassifierCascade(xmlPointer);
        return current;
    }

    public void release() {
        if (current != null) {
            current.release();
        }
        cvRelease(xmlPointer);
    }

}
