package ibis.media.test;

import ibis.media.video.VideoDeviceDescription;
import ibis.media.video.VideoDeviceFactory;

public class Scan {
    
    public static void main(String [] args) throws Exception {

        VideoDeviceDescription [] devices = VideoDeviceFactory.availableDevices();
    
        System.out.println("Found " + devices.length + " devices:");
        
        for (VideoDeviceDescription d : devices) { 
            System.out.println(d.getSimpleDescription());
        }    
    }
    
}
