package ibis.media.video.devices.directshow;

import java.util.ArrayList;
import java.util.HashMap;
//import java.util.HashMap;

//import ibis.media.imaging.Format;
//import ibis.media.video.Capability;
import ibis.media.imaging.Format;
import ibis.media.video.Capability;
import ibis.media.video.FrameRate;
import ibis.media.video.Resolution;
import ibis.media.video.VideoDeviceDescription;
import ibis.media.video.VideoDeviceDiscovery;

public class DirectShowDiscovery implements VideoDeviceDiscovery {
    
    private native int countDevices();
    private native String getDeviceName(int deviceNumber);   
    private native int initDeviceCapabilities(int deviceNumber);
	private native int getDeviceCapabilities(int capabilityNumber);
	
	HashMap<Format, Capability> capabilities;
    
    public VideoDeviceDescription[] discover() {    	
        ArrayList<VideoDeviceDescription> devices =  new ArrayList<VideoDeviceDescription>();
                
        int numDevices = countDevices();
        
        for (int device=0; device<numDevices; device++) {
	        String description = getDeviceName(device);
	        if (description != null) {
	        	
	        	int numCapabilities = 0;
	        	numCapabilities = initDeviceCapabilities(device);
	        	
	        	capabilities = new HashMap<Format, Capability>();
	        	
	            for (int cap=0; cap<numCapabilities; cap++) {
	            	getDeviceCapabilities(cap);
	            }
	           	        	
	            devices.add(new VideoDeviceDescription("device" + device, description, device, capabilities));
	        }
        }
        return devices.toArray(new VideoDeviceDescription[devices.size()]);        
    }

    private void capability(int nativeNumber, int type, String paletteName, int width, int height) {
    	Capability cap;
    	Format format;
    	if (paletteName.compareTo("RGB24") == 0) {
    		format = Format.RGB24;        	
   		//TODO
    	//} else if (paletteName.compareTo("RGB32") == 0) {    		
    		//format = Format.RGB32);
    	} else if (paletteName.compareTo("ARGB32") == 0) {
    		format = Format.ARGB32;
    	//TODO
    	//} else if (paletteName.compareTo("AYUV") == 0) {
    		//format = Format.AYUV);
   		//TODO
    	//} else if (paletteName.compareTo("YUY2") == 0) {
    		//format = Format.YUY2;
    	} else {
    		return;
    	}
    	
    	//TODO: Debug
    	System.out.println("Adding format: "+paletteName+" "+width+"x"+height);
    	
    	if (!capabilities.containsKey(format)) {    		
			cap = new Capability(format);
			capabilities.put(format, cap);
		} else {
			cap = capabilities.get(format);	
		}
    	
    	cap.addFrameRate(nativeNumber, new Resolution(width, height), new FrameRate(1, 15));    	
    }
}
