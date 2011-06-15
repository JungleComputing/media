package ibis.media.video.devices.directshow;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ibis.media.imaging.Format;
import ibis.media.imaging.Image;
import ibis.media.video.VideoConsumer;
import ibis.media.video.VideoDeviceDescription;
import ibis.media.video.devices.VideoSource;

public class DirectShowDevice extends VideoSource {	
	private static final Logger logger = LoggerFactory.getLogger(DirectShowDevice.class);

	private native int getDeviceCapabilities(int deviceNumber);
    private native int configureDevice(int deviceNumber, int width, int height);
    private native int startDevice(int deviceNumber);
    private native int changeSize(int width, int height);    
    private native boolean grabBuffer();
    private native int closeDevice();
    
    private final int deviceNumber;     
    private ByteBuffer buffer;
    
    public DirectShowDevice(VideoConsumer consumer, VideoDeviceDescription desc,
    		int width,
            int height, int delay, double quality) throws Exception {

        super(consumer, desc, width, height, delay, quality);
        
        int result = 0;
        
        this.deviceNumber = desc.deviceNumber;      
        
        result = configureDevice(deviceNumber, width, height);        
        //changeSize(width, height);
        
        if (result != 0) { 
            initialized(false);
            throw new Exception("Failed to configure device " + deviceNumber 
                    + "(result = " + result +")");
        }
        
        result = getDeviceCapabilities(deviceNumber);
        
        result = startDevice(deviceNumber);
        
        initialized(true);
    }
    
    private void addBuffer(ByteBuffer buffer) { 
        this.buffer = buffer;        
        //System.out.println("GOT BUFFER of size " + buffer.capacity());
    } 
    
    //private void grabDone(ByteBuffer buffer) {
    private void grabDone() {
    	Image image = new Image(Format.BGRA32, getWidth(), getHeight(), buffer);    	   
    	consumer.gotImage(image);
    } 
        
    @Override
    public void close() {
    	closeDevice();    	
        setDone();
    }

    @Override
    public void setResolution(int width, int height) {
    	System.out.println("w: "+width+" h: "+height);
        // TODO Auto-generated method stub
    }

    @Override
    protected void grab() {
        while (!getDone()) {
        	logger.debug("Grabbing image!");
            
        	boolean result = grabBuffer();
            if (!result) { 
                System.out.println("Failed to grab image!");
                closeDevice();
                return;
            }
        }
        closeDevice();
    }
}
