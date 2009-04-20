package ibis.test;

import ibis.imaging4j.Image;
import ibis.video4j.VideoConsumer;
import ibis.video4j.VideoDeviceFactory;
import ibis.video4j.devices.VideoSource;
import ibis.video4j.utils.ImageUtils;

public class Convert implements VideoConsumer {

    private VideoSource webcam;

    private int [] buffer;
    
    private int w;
    private int h;
    
    public Convert(int w, int h, int device) throws Exception { 
        
        this.w = w;
        this.h = h;

        webcam = VideoDeviceFactory.openDevice(this, device, w, h, 0);            
        webcam.start();
    }

    public void gotImage(Image image) {

        try {
            
            long start = System.currentTimeMillis();
            
            // FIX FIX FIX!
          //  byte [] out = ImageUtils.encode(buffer, w, h, 85);
            
            long end = System.currentTimeMillis();
            
            //System.out.println("Image enode took " + (end-start) + " ms. " 
            //        + "Compressed from " + (buffer.length*4) + " to " 
             //       + out.length + " bytes.");

        } catch (Exception e) {
            System.out.println("Failed to encode image!");
            e.printStackTrace();
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
        
        new Convert(w, h, device);
    }
    
}