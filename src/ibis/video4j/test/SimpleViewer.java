package ibis.video4j.test;
import ibis.video4j.VideoConsumer;
import ibis.video4j.VideoDeviceFactory;
import ibis.video4j.devices.VideoSource;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.MemoryImageSource;

import javax.swing.JApplet;
import javax.swing.JPanel;

public class SimpleViewer extends JApplet {

    // NOTE: This is weird shit, but I've long given up on trying to understand 
    // windowing libraries....
    
    // Generated
    private static final long serialVersionUID = 1L;
   
    private Painter demo;

    public void init(int device, int w, int h) throws Exception {
        getContentPane().add(demo = new Painter(device, w, h));
        // getContentPane().add("North", new DemoControls(demo));
    }

    public void start() {
  //      demo.start();
    }
  
    public void stop() {
   //     demo.stop();
    }
    
    static class Painter extends JPanel implements VideoConsumer { 
   
        // Generated
        private static final long serialVersionUID = 1L;

        private VideoSource webcam;
        
        private int camWidth = 352;
        private int camHeight = 288;

        private int [] pixels;
        
        private Image offscreen;
        private MemoryImageSource source;
        
      //  private Thread thread;
        
        private long image = 0;
      
        private long start;
        
        private String fps = "FPS: ??"; 
        
        public Painter(int device, int width, int height) throws Exception { 
            setBackground(Color.white);
            
            this.camWidth = width;
            this.camHeight = height;
        
            pixels = new int[width*height];
            
            source = new MemoryImageSource(width, height, pixels, 0, width);
            source.setAnimated(true);
            source.setFullBufferUpdates(true);
            
            offscreen = createImage(source);
            
            webcam = VideoDeviceFactory.openDevice(this, device, camWidth, camHeight, 0);            
            webcam.start();
            
            System.out.println(webcam.toString());
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

        public void paint(Graphics g) {
            g.drawImage(offscreen, 0, 0, this);
            g.setColor(Color.PINK);
            g.drawString(fps, 20, 20);
        }
    }
    
    
    public static void main(String [] args) throws Exception {
        
        int w = 352; 
        int h = 288;
        int device = 0;
        
        for (int i=0;i<args.length;i++) { 
            
            if (args[i].equals("-device")) { 
                device = Integer.parseInt(args[++i]);
            } else if (args[i].equals("-width")) { 
                w = Integer.parseInt(args[++i]);
            } else if (args[i].equals("-height")) { 
                h = Integer.parseInt(args[++i]);                
            } else { 
                System.err.println("UNKNOWN OPTION: " + args[i]);
            }
        }
        
        System.err.println("DirectShowDevice = " + device);
       
        final SimpleViewer demo = new SimpleViewer();
        demo.init(device, w, h);
        
        Frame f = new Frame("video4J Demo");
        f.addWindowListener(new WindowAdapter() {
            public void windowClosing(WindowEvent e) {System.exit(0);}
            public void windowDeiconified(WindowEvent e) { demo.start(); }
            public void windowIconified(WindowEvent e) { demo.stop(); }
        });
        f.add(demo);
        f.pack();
        f.setSize(new Dimension(w,h));
        f.show();
        demo.start();
    }
}
