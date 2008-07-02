package ibis.video4j.devices.video4linux;

import java.io.File;
import java.util.ArrayList;

import ibis.video4j.VideoDeviceDescription;
import ibis.video4j.VideoDeviceDiscovery;

public class Video4LinuxDiscovery implements VideoDeviceDiscovery {
    
    private static final int MAX_DEVICES = 64;
    private static final String DEVICE_NAME = "/dev/video";
    
    private native boolean testDevice(String device, int number);
    
    private VideoDeviceDescription current;
    
    public VideoDeviceDescription [] discover() { 

        ArrayList<VideoDeviceDescription> devices = 
            new ArrayList<VideoDeviceDescription>();
        
        for (int i=0;i<MAX_DEVICES;i++) { 
            
            String deviceName = DEVICE_NAME + i;
            
            File tmp = new File(deviceName);
            
            current = null;
            
            if (tmp.exists() && testDevice(deviceName, i)) { 
                if (current.deviceDescription != null) { 
                    System.out.println("Found device: " + current);
                    devices.add(current);
                }
            }
        }
    
        current = null;

        return devices.toArray(new VideoDeviceDescription[devices.size()]);
    }
   
    @SuppressWarnings("unused") // Called from native code!
    private void available(String name, String description, int number, 
            boolean v4l1, boolean v4l2) { 
       
        current = new Video4LinuxDeviceDescription(name, description, number, 
                v4l1, v4l2);
    }
    
}
