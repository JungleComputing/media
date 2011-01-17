package ibis.video4j.devices.directshow;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ibis.imaging4j.Format;
import ibis.imaging4j.Image;
import ibis.video4j.VideoConsumer;
import ibis.video4j.VideoDeviceDescription;
import ibis.video4j.devices.VideoSource;

public class DirectShowDevice extends VideoSource {
	
	private static final Logger logger = LoggerFactory.getLogger(DirectShowDevice.class);

    private native int configureDevice(int deviceNumber, int width, int height);
    
    private native int grab(int deviceNumber);
    
    private native int grabDone(int deviceNumber);
    
    private native int closeDevice(int deviceNumber);
    
    private final int deviceNumber; 
    
    private ByteBuffer buffer;
    
    public DirectShowDevice(VideoConsumer consumer, VideoDeviceDescription desc,
    		int width,
            int height, int delay, double quality) throws Exception {

        super(consumer, desc, width, height, delay, quality);
        
        this.deviceNumber = desc.deviceNumber;
        
        int result = configureDevice(deviceNumber, width, height);
        
        if (result != 1) { 
            initialized(false);
            throw new Exception("Failed to configure device " + deviceNumber 
                    + "(result = " + result +")");
        }
        
        initialized(true);
    }
    
    @SuppressWarnings("unused")
    private void addBuffer(ByteBuffer buffer) { 
        this.buffer = buffer;        
        System.out.println("GOT BUFFER of size " + buffer.capacity());
    } 
        
    @Override
    public void close() {
        setDone();

    }

    @Override
    public void setResolution(int width, int height) {
        // TODO Auto-generated method stub        
    }

    @Override
    protected void grab() {
        
        while (!getDone()) { 
            
        	logger.debug("Grabbing image!");
            
            if (grab(deviceNumber) == 0) { 
                System.out.println("Failed to grab image!");
                closeDevice(deviceNumber);
                return;
            }
            
            logger.debug("Waiting for image to return!");
            
            int result = grabDone(deviceNumber);
            
            while (result == 0) {
                
                try {
                    Thread.sleep(100);
                } catch (Exception e) {
                    // ignore
                }

                logger.debug("Waiting for image to return!");

                result = grabDone(deviceNumber);
            }
            
            if (result == 1) {
            	Image image = new Image(Format.BGRA32, getWidth(), getHeight(), buffer);
   
                consumer.gotImage(image);
                
            } else { 
                System.out.println("Failed to grab image!");
                closeDevice(deviceNumber);
                return;
            }
        }
        closeDevice(deviceNumber);
    }

    /*
    static MyApp junk;
   
    static { 
        
        boolean done = false;
            
        if (!done) { 
            try { 
                System.loadLibrary("nativemedia");
                done = true;
            } catch (Throwable e) { 
                System.err.println("Failed to load native media library");
                e.printStackTrace(System.err);
            }
        }

        if (!done) { 
            String lib = System.getProperty("user.dir") + "/nativemedia.dll";

            try { 
                System.loadLibrary(lib);
            } catch (Throwable e) { 
                System.err.println("Failed to load " + lib);
                e.printStackTrace(System.err);
            }
        }

        if (!done) { 
            String lib = "e:/chat/nativemedia.dll";

            try { 
                System.loadLibrary(lib);
            } catch (Throwable e) { 
                System.err.println("Failed to load " + lib);
                e.printStackTrace(System.err);
            }
        }

        if (!done) { 
            System.err.println("Failed to load windows webcam library!!!");
        } else {
            junk = new MyApp();
        }
    }
    
    private byte [] tmp;
    
    private int w;
    private int h;
    
    protected DirectShowDevice(VideoConsumer consumer, int width, int height, int delay) {
        super(consumer, width, height, delay);

        System.out.println("Creating windows webcam " + width + "x" + height);
        
        junk.OpenVideo("camera");
        
        w = junk.GetFrameWidth();
        h = junk.GetFrameHeight();
        
        tmp = new byte[w*h*4];

        System.out.println("Camera output " + w + "x" + h);
        
        initialized(true);
    }
    
    @Override
    protected boolean nextImage(int[] pixels) {
        
        junk.NextFrame();
        junk.GetFrameData(tmp);
        
      //  System.out.println("Got frame!");
    
        int index = 0;
        
        for (int i=0;i<w*h;i++) { 
            
            pixels[i] = 0xFF << 24 | 
                        (((int) tmp[index]) & 0xFF) << 16 | 
                        (((int) tmp[index+1]) & 0xFF) << 8 | 
                        (((int) tmp[index+2]) & 0xFF);
            index += 3;
            
            
        }
        
        return true;
    }
    */    
}
