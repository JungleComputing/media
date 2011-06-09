package ibis.media.video.devices.directshow;

import java.util.ArrayList;
import java.util.HashMap;
//import java.util.HashMap;

//import ibis.media.imaging.Format;
//import ibis.media.video.Capability;
import ibis.media.imaging.Format;
import ibis.media.video.Capability;
import ibis.media.video.VideoDeviceDescription;
import ibis.media.video.VideoDeviceDiscovery;

public class DirectShowDiscovery implements VideoDeviceDiscovery {
    
    private native int countDevices();
    private native String getDeviceName(int deviceNumber);    
    
    public VideoDeviceDescription[] discover() {    	
        ArrayList<VideoDeviceDescription> devices =  new ArrayList<VideoDeviceDescription>();
                
        int count = countDevices();
        
        for (int i=0;i<count;i++) {
	        String description = getDeviceName(i);
	        if (description != null) {            	
	            HashMap<Format, Capability> capabilities = new HashMap<Format, Capability>();
	            
	            Capability capability = new Capability(Format.BGRA32);
	
	            capabilities.put(Format.BGRA32, capability);
	        	
	            devices.add(new VideoDeviceDescription("device" + i, description, i, capabilities));
	        }
        }
        return devices.toArray(new VideoDeviceDescription[devices.size()]);        
    }
}
