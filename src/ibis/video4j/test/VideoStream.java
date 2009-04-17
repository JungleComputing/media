/**
 * 
 */
package ibis.video4j.test;

import ibis.video4j.VideoConsumer;
import ibis.video4j.VideoDeviceFactory;
import ibis.video4j.VideoPalette;
import ibis.video4j.devices.VideoSource;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.image.MemoryImageSource;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

class VideoStream extends JPanel implements VideoConsumer { 

    // Generated
    private static final long serialVersionUID = 1L;

    private VideoSource webcam;
    
    private int camWidth;
    private int camHeight;

    private int [] pixels;
    
    private Image offscreen;
    private MemoryImageSource source;
    
    private long image = 0;

    private long start;
    
    private String fps = "FPS: ??"; 
    
    private String message = "No webcam selected";
    
    public VideoStream(int width, int height) { 
        setBackground(Color.white);
        
        this.camWidth = width;
        this.camHeight = height;
    
        pixels = new int[width*height];
        
        source = new MemoryImageSource(width, height, pixels, 0, width);
        source.setAnimated(true);
        source.setFullBufferUpdates(true);
        
        offscreen = createImage(source);
    
        // setFont(getFont().deriveFont(Font.ITALIC));
        setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
        setPreferredSize(new Dimension(width, height+10));
    }
    
    public void selectDevice(int device, VideoPalette palette) throws Exception {  
        
        if (palette == null) { 
            palette = VideoPalette.ARGB32;
        }
        
        if (webcam != null) { 
            // Stop the existing device
            webcam.close();
        }
        
        if (device >= 0) { 
            webcam = VideoDeviceFactory.openDevice(this, device, camWidth, 
                    camHeight, 0, palette, 85);            
            webcam.start();
            
            image = 0;
            fps = "FPS: ??"; 
            
        } else { 
            webcam = null;
            message = "No webcam selected";
        }
      
        repaint();
    }

    public int [] getBuffer(int w, int h, int index) { 
        return pixels;
    }
    
    public void gotImage(int [] pixels, int index) {
        
        source.newPixels(0, 0, camWidth, camHeight);
       
        if (image == 0) { 
            start = System.currentTimeMillis();
        } else if (image == 100) { 
            long tmp = System.currentTimeMillis();
            fps = "FPS: " + (int)(100000.0 / (tmp-start));
            start = tmp;
            image = 0;
        }
        
        image++;    
        repaint();
    }

    void setMessage(String message) { 
        this.message = message;
        repaint();
    }
    
    public void paint(Graphics g) {
      
        String m = null;
        Color edge = null; 
        Color fill = null;
        int x = -1;
        int y = -1;
       
        Dimension d = getSize();
        
        Graphics2D g2 = (Graphics2D) g;
        FontRenderContext frc = g2.getFontRenderContext();
        
        if (webcam != null) { 
            g2.drawImage(offscreen, 0, 0, this);
            m = fps;
            x = d.width - 40;
            y = d.height - 25;
            edge = Color.BLACK;
            fill = Color.WHITE;
        } else { 
            m = message;
            x = d.width / 2;
            y = d.height / 2;
            edge = Color.WHITE;
            fill = Color.BLACK;        
        }
        
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        
        Font f = getFont().deriveFont(Font.BOLD);
        TextLayout tl = new TextLayout(m, f, frc);
            
        float sw = (float) tl.getBounds().getWidth();
        float sh = (float) tl.getBounds().getHeight();
        Shape sha = tl.getOutline(AffineTransform.getTranslateInstance(
                    x-sw/2, y+sh/2));
        g2.setColor(edge);
        g2.draw(sha);
        g2.setColor(fill);
        g2.fill(sha);
    }
}