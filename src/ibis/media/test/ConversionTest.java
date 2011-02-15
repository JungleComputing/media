package ibis.media.test;

import java.io.File;
import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ibis.media.imaging.Format;
import ibis.media.imaging.Image;
import ibis.media.imaging.Imaging;
import ibis.media.imaging.io.IO;

public class ConversionTest {

    private static final Logger logger = LoggerFactory
            .getLogger(ConversionTest.class);

    public static final int WIDTH = 1024;
    public static final int HEIGHT = 768;

    private static void drawBlock(ByteBuffer buffer, double from, double to,
            byte a, byte r, byte g, byte b) {
        int beginLine = (int) (HEIGHT * from);
        int endLine = (int) (HEIGHT * to);

        for (int i = beginLine; i < endLine; i++) {
            for (int j = 0; j < WIDTH; j++) {
                int location = ((i * WIDTH) + j) * 4;
                buffer.put(location, a); // A
                buffer.put(location + 1, r); // R
                buffer.put(location + 2, g); // G
                buffer.put(location + 3, b); // B
            }
        }
    }

    private static Image testImage() {
        Image image = new Image(Format.ARGB32, WIDTH, HEIGHT);
        ByteBuffer data = image.getData();

        drawBlock(data, 0.0, 0.2, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF,
                (byte) 0xFF);
        drawBlock(data, 0.2, 0.4, (byte) 0xFF, (byte) 0xFF, (byte) 0x00,
                (byte) 0x00);
        drawBlock(data, 0.4, 0.6, (byte) 0xFF, (byte) 0x00, (byte) 0xFF,
                (byte) 0x00);
        drawBlock(data, 0.6, 0.8, (byte) 0xFF, (byte) 0x00, (byte) 0x00,
                (byte) 0xFF);
        drawBlock(data, 0.8, 0.9, (byte) 0x00, (byte) 0x00, (byte) 0x00,
                (byte) 0x00);
        drawBlock(data, 0.9, 1.0, (byte) 0xFF, (byte) 0x00, (byte) 0x00,
                (byte) 0x00);

        return image;
    }
    
    private static Image whiteImage() {
        Image image = new Image(Format.ARGB32, WIDTH, HEIGHT);
        ByteBuffer data = image.getData();

        data.clear();
        while (data.hasRemaining()) {
            data.put((byte) 0xFF);
        }

        return image;
    }

    public static void main(String[] arguments) {

        ImageViewer viewer = new ImageViewer(1024, 768);

        try {

            for (String argument : arguments) {
                logger.info("loading image " + argument);
                Image loadedImage = IO.load(new File(argument));
                viewer.setImage(loadedImage, argument);
                Thread.sleep(5000);
            }

            Image testImage = testImage();
            
            viewer.setImage(testImage, "buffered image");
            Thread.sleep(5000);
            
            Image rgb24 = Imaging.convert(testImage, Format.RGB24);
            viewer.setImage(rgb24, "RGB24 image");
            Thread.sleep(5000);
            
            Image argb32 = Imaging.convert(rgb24, Format.ARGB32);
            viewer.setImage(argb32, "TestImage(ARGB32)->RGB24->ARGB32 image");
            Thread.sleep(5000);
            
            IO.save(rgb24, new File("image.rgb"));

            viewer.setImage(whiteImage(), "white image");
            Thread.sleep(5000);

        } catch (Throwable t) {
            logger.error("error on conversion test", t);
        }

        viewer.dispose();
    }

}
