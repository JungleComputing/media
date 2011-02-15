package ibis.media.video.devices.quicktime;

import ibis.media.imaging.Format;
import ibis.media.imaging.Image;
import ibis.media.video.VideoConsumer;
import ibis.media.video.VideoDeviceDescription;
import ibis.media.video.devices.VideoSource;

//import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.WritableRaster;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import quicktime.QTRuntimeException;
import quicktime.QTRuntimeHandler;
import quicktime.QTSession;
import quicktime.qd.PixMap;
import quicktime.qd.QDGraphics;
import quicktime.qd.QDRect;
import quicktime.std.StdQTConstants;
import quicktime.std.movies.media.UserData;
import quicktime.std.sg.SGDeviceList;
import quicktime.std.sg.SequenceGrabber;
import quicktime.std.sg.SGVideoChannel;
import quicktime.util.RawEncodedImage;

@SuppressWarnings({ "restriction", "deprecation" })
public class QuickTimeDevice extends VideoSource {

    private static final Logger logger = LoggerFactory
            .getLogger(QuickTimeDevice.class);

    private SequenceGrabber grabber;
    private SGVideoChannel channel;
    private RawEncodedImage rawEncodedImage;

    private int width;
    private int height;
    private int videoWidth;

    private int[] pixels;
    private BufferedImage image;
    private WritableRaster raster;
    private PixMap pixMap;

    public QuickTimeDevice(VideoConsumer consumer, VideoDeviceDescription desc,
            int width, int height, int delay, double quality) throws Exception {

        super(consumer, desc, width, height, delay, quality);
        this.width = width;
        this.height = height;
        try {
            QTSession.open();
            System.err.println(1);
            QDRect bounds = new QDRect(width, height);
            QDGraphics graphics = new QDGraphics(bounds);
            System.err.println(2);
            grabber = new SequenceGrabber();
            
            grabber.setGWorld(graphics, null);
            channel = new SGVideoChannel(grabber);
            System.err.println(3);
            channel.setBounds(bounds);
            channel.setUsage(StdQTConstants.seqGrabPreview);
            //channel.settingsDialog();
            System.err.println(4);
            grabber.prepare(true, false);
            grabber.startPreview();
            pixMap = graphics.getPixMap();
            rawEncodedImage = pixMap.getPixelData();
            System.err.println(5);

            videoWidth = width + (rawEncodedImage.getRowBytes() - width * 4)
                    / 4;
            pixels = new int[videoWidth * height];
            image = new BufferedImage(videoWidth, height,
                    BufferedImage.TYPE_INT_RGB);
            raster = WritableRaster.createPackedRaster(DataBuffer.TYPE_INT,
                    videoWidth, height, new int[] { 0x00ff0000, 0x0000ff00,
                            0x000000ff }, null);
            raster.setDataElements(0, 0, videoWidth, height, pixels);
            image.setData(raster);
            QTRuntimeException.registerHandler(new QTRuntimeHandler() {
                public void exceptionOccurred(QTRuntimeException e,
                        Object eGenerator, String methodNameIfKnown,
                        boolean unrecoverableFlag) {
                    System.out.println("what should i do?");
                }
            });
        } catch (Exception e) {
            QTSession.close();
            throw e;
        }
        System.err.println(6);
        initialized(true);
    }

    public void dispose() {
        try {
            grabber.stop();
            grabber.release();
            grabber.disposeChannel(channel);
            image.flush();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            QTSession.close();
        }
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public int getVideoWidth() {
        return videoWidth;
    }

    public int getVideoHeight() {
        return height;
    }

    public void getNextPixels(int[] pixels) throws Exception {
        grabber.idle();
        rawEncodedImage.copyToArray(0, pixels, 0, pixels.length);
    }

//    public Image getNextImage() throws Exception {
//        grabber.idle();
//        rowEncodedImage.copyToArray(0, pixels, 0, pixels.length);
//        raster.setDataElements(0, 0, videoWidth, height, pixels);
//        image.setData(raster);
//        return image;
//    }

    @Override
    public void close() {
        setDone();

    }

    @Override
    protected void grab() {
        ByteBuffer buffer = ByteBuffer.allocate(width * height * 4);

        Image image = new Image(Format.BGRA32, getWidth(), getHeight(),
                buffer);
        while (!getDone()) {

            logger.debug("Grabbing image!");

            // appearantly grabs an image :-)
            try {
                grabber.idle();
            } catch (Exception e) {
                e.printStackTrace();
            }
         
        
            rawEncodedImage.copyToArray(0, buffer.array(), 0, buffer.array().length);

            consumer.gotImage(image);

            
            //limit framerate somewhat
            try {
                Thread.sleep(25);
            } catch (InterruptedException e) {
              
            }
        }
        dispose();
    }

    @Override
    public void setResolution(int width, int height) {
        // TODO Auto-generated method stub

    }

}
