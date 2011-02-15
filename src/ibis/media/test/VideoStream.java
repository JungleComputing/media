/**
 * 
 */
package ibis.media.test;

import ibis.media.imaging.Format;
import ibis.media.imaging.conversion.Conversion;
import ibis.media.imaging.conversion.Convertor;
import ibis.media.imaging.scaling.Scaler;
import ibis.media.imaging.scaling.Scaling;
import ibis.media.video.VideoConsumer;
import ibis.media.video.VideoDeviceFactory;
import ibis.media.video.devices.VideoSource;

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
import java.nio.ByteBuffer;

import javax.swing.BorderFactory;
import javax.swing.JPanel;

class VideoStream extends JPanel implements VideoConsumer { 

    // Generated
    private static final long serialVersionUID = 1L;

    private VideoSource webcam;

    private int camWidth;
    private int camHeight;

    private int scaleWidth;
    private int scaleHeight;
    
    private ibis.media.imaging.Image pixels;
    private ibis.media.imaging.Image pixelsScaled;
    
    private Convertor convertor;

    private Scaler scaler;
    
    private int [] pixelsAsInt;

    private Image offscreen;
    private MemoryImageSource source;

    private long image = 0;

    private long start;

    private String fps = "FPS: ??"; 

    private String message = "No webcam selected";

    public VideoStream(int grabWidth, int grabHeight, int scaleWidth, 
            int scaleHeight) throws Exception { 
        setBackground(Color.white);

        this.camWidth = grabWidth;
        this.camHeight = grabHeight;

        byte [] data = new byte[camWidth*camHeight*4];

        pixels = new ibis.media.imaging.Image(Format.ARGB32, camWidth, camHeight, data);

        if (scaleWidth <= 0) { 
            scaleWidth = grabWidth;
        }
        
        if (scaleHeight <= 0) { 
            scaleHeight = grabHeight;
        }
        
        scaler = null;
        
        if (camWidth != scaleWidth || camHeight != scaleHeight) { 
            scaler = Scaling.getScaler(Format.ARGB32);

            pixelsScaled = new ibis.media.imaging.Image(Format.ARGB32, scaleWidth,scaleHeight);
            
            if (scaler == null) { 
                throw new Exception("Failed to find scaler for ARGB32");
            }            
        } else { 
            pixelsScaled = pixels;
        }

        pixelsAsInt = new int[scaleWidth*scaleHeight];

        source = new MemoryImageSource(scaleWidth, scaleHeight,  
                pixelsAsInt, 0, scaleWidth);

        //source = new MemoryImageSource(width, height, ColorModel.getRGBdefault(), 
        //        data, 0, width);

        //ComponentColorModel cm = new ComponentColorModel(
        //       ColorSpace.getInstance(ColorSpace.CS_LINEAR_RGB), true, true, 
        //       Transparency.OPAQUE, DataBuffer.TYPE_BYTE);

        //source = new MemoryImageSource(width, height, cm, data, 0, width);

        source.setAnimated(true);
        source.setFullBufferUpdates(true);

        offscreen = createImage(source);

        // setFont(getFont().deriveFont(Font.ITALIC));
        setBorder(BorderFactory.createEmptyBorder(10,0,0,0));
        setPreferredSize(new Dimension(scaleWidth, scaleHeight+10));
    }

    public void selectDevice(int device, Format format) throws Exception {  

        if (webcam != null) { 
            // Stop the existing device
            webcam.close();
        }

        if (format == null) { 
            format = Format.ARGB32;
        }

        convertor = null;

        if (format != Format.ARGB32) { 
            convertor = Conversion.getConvertor(format, Format.ARGB32);

            if (convertor == null) { 
                throw new Exception("Failed to find convertor from " + format 
                        + " to ARGB32");
            }            
        }

        if (device >= 0) { 
            webcam = VideoDeviceFactory.openDevice(this, device, camWidth, 
                    camHeight, 0, format, 85);            
            webcam.start();

            image = 0;
            fps = "FPS: ??"; 

        } else { 
            webcam = null;
            message = "No webcam selected";
        }
        
        repaint();
    }

    /*
    public int [] getBuffer(int w, int h, int index) { 
        return pixels;
    }
     */

    public void gotImage(ibis.media.imaging.Image img) {

        //source.newPixels(0, 0, camWidth, camHeight);

  //      System.out.println("Image size " + img.getData().limit());
        
        if (image == 0) { 
            start = System.currentTimeMillis();
        } else if (image == 100) { 
            long tmp = System.currentTimeMillis();
            fps = "FPS: " + (int)(100000.0 / (tmp-start));
            start = tmp;
            image = 0;
        }

        try {

            /*
            if (img.getFormat() == Format.ARGB32) { 
                img.getData().asIntBuffer().get(pixels);
            } else { 

                // HACK
                LowLevelConvert.YUYVtoARGB32(img.getWidth(), img.getHeight(), img.getData(), pixels);
            }*/
            
            if (convertor == null) { 
                // apparently we don't need to convert the image.
                
                if (scaler == null) { 
                    // ... and we don't need top scale it either!
                    ibis.media.imaging.Image.copy(img, pixelsScaled);
                } else { 
                    // ... or we scale directly from the source
                    scaler.scale(img, pixelsScaled);                    
                }
                
            } else { 
                // We do need to convert...
                
                if (scaler == null) {
                    // ... but we don't need to scale
                    convertor.convert(img, pixelsScaled);
                } else {
                    // ... we need to convert and scale!
                    convertor.convert(img, pixels);
                    scaler.scale(pixels, pixelsScaled);
                }
            }
            
            // HACK: I do not understand how to get pixels onto the screen that are not 
            // in a RGB int [] format. When I try, nothing happens, so to untill I figure it out 
            // we create an extra copy here...
            
            ByteBuffer b = pixelsScaled.getData();
            b.position(0);
            b.limit(b.capacity());
            
            b.asIntBuffer().get(pixelsAsInt);            
        
        } catch (Exception e) {
            e.printStackTrace();
        }

        source.newPixels();

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