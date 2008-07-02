package ibis.video4j.devices.file;

import ibis.video4j.VideoConsumer;
import ibis.video4j.devices.VideoSource;
import ibis.video4j.utils.ImageUtils;

import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class TestDevice extends VideoSource {

    private BufferedImage testcard;
    
    private int index = 0;
    
    public TestDevice(VideoConsumer consumer, int width, int height, int delay) {
        super(consumer, width, height, delay);
        
        generateTestCard();
        
        initialized(true);
    }
    
    private void generateTestCard() { 

        try { 
            BufferedImage bi = ImageIO.read(new File("images/testbeeld.jpg"));
            testcard = ImageUtils.scale(bi, width, height, false);
        } catch (Exception e) {
            System.err.println("Failed to load test card!");
        }
    }

    protected boolean nextImage(int [] pixels) {

        try {
         /*
            BufferedImage bi = new BufferedImage(width, height,
                    BufferedImage.TYPE_INT_RGB);

            Graphics2D g = bi.createGraphics();

            g.setColor(new Color(1.0f, 1.0f, 1.0f));
            g.drawLine(10, 10, width-10, height-10);
            g.drawLine(10, height-10, width-10, 10);

            g.drawLine(10, 10, width-10, 10);
            g.drawLine(10, 10, 10, height-10);

            g.drawLine(width-10, 10, width-10, height-10);
            g.drawLine(10, height-10, width-10, height-10);

            if (name == null) { 
                g.drawString("No image! (" + (index++) + ")", 15, height-50);      
            } else { 
                g.drawString("No image for " + name + " (" + (index++) + ")", 15, height-50); 
            }
            
            bi.getRGB(0, 0, width, height, pixels, 0, width);
           */
            
            if (testcard != null) { 
                testcard.getRGB(0, 0, width, height, pixels, 0, width);           
            } else { 
                // generate noise....
                for (int i=0;i<pixels.length;i++) {

                    int r = (int)(Math.random ()*255);
                    int g = (int)(Math.random ()*255);
                    int b = (int)(Math.random ()*255);

                    pixels[i] = 0xFF000000 | (r << 16) | (g << 8) | b;
                }
            }
        } catch (Exception e) { 
            System.err.println("TestDevice: Failed to generate image!" + e);
            return false;
        }
        
      // System.err.println("TestDevice: produced image!");
        
        return true;
    }

    @Override
    public void close() {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setResolution(int width, int height) {
        // TODO Auto-generated method stub
        
    }

    @Override
    protected void grab() {
        // TODO Auto-generated method stub
        
    }
}
