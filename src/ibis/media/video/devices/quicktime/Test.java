package ibis.media.video.devices.quicktime;

import java.awt.*;
import quicktime.*;
import quicktime.std.sg.*;
import quicktime.qd.*;
import quicktime.io.*;

/**
 * Previews and captures a single video frame using QuickTime for Java. Press
 * the space bar to stop previewing. Press the alt key to capture a frame and
 * continue previewing. While previewing, type "+" to zoom in, "-" to zoom out,
 * and "h" to display a histogram. Captures and displays a single frame if
 * called with the argument "grab". Based on the LiveCam example posted to the
 * QuickTime for Java mailing list by Jochen Broz.
 * http://lists.apple.com/archives/quicktime-java/2005/Feb/msg00062.html
 */

@SuppressWarnings({ "deprecation", "restriction" })
public class Test {

    SequenceGrabber grabber;
    SGVideoChannel channel;
    QDRect cameraSize;
    QDGraphics gWorld;
    public int[] pixelData;
    int intsPerRow;
    int width, height;
    boolean grabbing = true;
    int frame;
    boolean grabMode;
    boolean showDialog;
    
    public Test() {
        try {
            QTSession.open();
            initSequenceGrabber();
            width = cameraSize.getWidth();
            height = cameraSize.getHeight();
            intsPerRow = gWorld.getPixMap().getPixelData().getRowBytes() / 4;
            pixelData = new int[intsPerRow * height];

            preview();
        } catch (Exception e) {
            e.printStackTrace(System.err);
        } finally {
            QTSession.close();
        }
    }

    public static void main(String[] args) {
        new Test();
    }

    /**
     * Initializes the SequenceGrabber. Gets it's source video bounds, creates a
     * gWorld with that size. Configures the video channel for grabbing,
     * previewing and playing during recording.
     */
    private void initSequenceGrabber() throws Exception {
        grabber = new SequenceGrabber();
        SGVideoChannel channel = new SGVideoChannel(grabber);
        if (showDialog)
            channel.settingsDialog();
        cameraSize = channel.getSrcVideoBounds();
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        if (cameraSize.getHeight() > screen.height - 40) // iSight camera claims
                                                         // to 1600x1200!
            cameraSize.resize(640, 480);
        gWorld = new QDGraphics(cameraSize);
        grabber.setGWorld(gWorld, null);
        channel.setBounds(cameraSize);
        channel.setUsage(quicktime.std.StdQTConstants.seqGrabRecord
                | quicktime.std.StdQTConstants.seqGrabPreview
                | quicktime.std.StdQTConstants.seqGrabPlayDuringRecord);
        channel.setFrameRate(0);
        channel.setCompressorType(quicktime.std.StdQTConstants.kComponentVideoCodecType);
    }

    /**
     * This is a bit tricky. We do not start Previewing, but recording. By
     * setting the output to a dummy file (which will never be created (hope
     * so)) with the quicktime.std.StdQTConstants.seqGrabDontMakeMovie flag set.
     * This seems to be equivalent to preview mode with the advantage, that it
     * refreshes correctly.
     */
    void preview() throws Exception {
        QTFile movieFile = new QTFile(new java.io.File("NoFile"));
        grabber.setDataOutput(null,
                quicktime.std.StdQTConstants.seqGrabDontMakeMovie);
        grabber.prepare(true, true);
        grabber.startRecord();
        while (grabbing) {
            grabber.idle();
            grabber.update(null);
            // displayFrame();
        }
    }

}
