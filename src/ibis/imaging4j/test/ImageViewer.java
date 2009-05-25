package ibis.imaging4j.test;

import ibis.imaging4j.Format;
import ibis.imaging4j.Image;
import ibis.imaging4j.Imaging4j;
import ibis.imaging4j.conversion.Conversion;
import ibis.imaging4j.conversion.Convertor;
import ibis.imaging4j.scaling.Scaler;
import ibis.imaging4j.scaling.Scaling;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.WindowEvent;
import java.awt.image.MemoryImageSource;
import java.nio.ByteBuffer;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageViewer extends JPanel {
    
    private static final Logger logger = LoggerFactory
            .getLogger(ImageViewer.class);
    
    private static final long serialVersionUID = 1L;
    
    private final int width;
    private final int height;
    
    private final JFrame frame;
    
    private int[] pixelsAsInt;
    private final java.awt.Image image; 
    private final MemoryImageSource source;
    
    private String text;

    public ImageViewer(int width, int height) {
        this.width = width;
        this.height = height;
        pixelsAsInt = new int[width * height];
        
        text = "unset";

        source = new MemoryImageSource(width, height,  
                pixelsAsInt, 0, width);
        
        source.setAnimated(true);
        source.setFullBufferUpdates(true);

        image = createImage(source);
        
        source.newPixels();
        repaint();
        
        frame = new JFrame("ConversionTest");
        frame.setMinimumSize(new Dimension(width,height));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //Create and set up the content pane.
        this.setOpaque(true); //content panes must be opaque
        this.setMinimumSize(new Dimension(width,height));
        frame.setContentPane(this);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }
    
    public void dispose() {
        frame.dispose();
    }
    
    public void setImage(Image image, String text) throws Exception {
        this.text = text;
        
        Image converted;
        Image scaled;
        
        if (image.getFormat() == Format.ARGB32) {
            converted = image;
            logger.info("not converted");
        } else {
            converted = Imaging4j.convert(image, Format.ARGB32);
            logger.info("converted from " + image.getFormat() + " to " + Format.ARGB32);
        }
        
        if (converted.getWidth() == width && converted.getHeight() == height) {
            scaled = converted;
            logger.info("not scaled");
        } else {
            scaled = Imaging4j.scale(converted, width, height);
            logger.info("scaled from " + converted.getWidth() + "x" + converted.getHeight() + " to " + width + "x" + height);
        }
        
        ByteBuffer b = scaled.getData();
        b.clear();
        
        b.asIntBuffer().get(pixelsAsInt);
        source.newPixels();
        repaint();
    }
    
    public void paint(Graphics g) {
        g.drawImage(image, 0 , 0, this);
        g.setColor(Color.PINK);
        g.drawString(text, 20, 20);
    }
    
}
