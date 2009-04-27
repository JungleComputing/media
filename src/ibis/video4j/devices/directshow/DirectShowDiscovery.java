package ibis.video4j.devices.directshow;

import java.util.ArrayList;

import ibis.imaging4j.Format;
import ibis.video4j.VideoDeviceDescription;
import ibis.video4j.VideoDeviceDiscovery;

public class DirectShowDiscovery implements VideoDeviceDiscovery {
    
    private native int countDevices();
    private native String getDeviceName(int device);
        
    public VideoDeviceDescription[] discover() {
    
        ArrayList<VideoDeviceDescription> devices = 
            new ArrayList<VideoDeviceDescription>();
        
        int count = countDevices();
        
        for (int i=0;i<count;i++) { 
            
            String description = getDeviceName(i);  

            if (description != null) {
            	
            	//FIXME
            	
            	
                devices.add(new VideoDeviceDescription("device" + i, 
                        description, i, 
                        null));
            }
        
        }
        
        return devices.toArray(new VideoDeviceDescription[devices.size()]);
    }
}
