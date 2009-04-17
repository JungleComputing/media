package ibis.video4j.devices.directshow;

import java.util.ArrayList;

import ibis.video4j.VideoDeviceDescription;
import ibis.video4j.VideoDeviceDiscovery;
import ibis.video4j.VideoPalette;

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
                devices.add(new VideoDeviceDescription("device" + i, 
                        description, i, 
                        new VideoPalette [] { VideoPalette.RGB24 }));
            }
        
        }
        return devices.toArray(new VideoDeviceDescription[devices.size()]);
    }
}
