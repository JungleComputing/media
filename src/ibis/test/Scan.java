package ibis.test;

import ibis.video4j.VideoDeviceDescription;
import ibis.video4j.VideoDeviceFactory;

public class Scan {
    
    public static void main(String [] args) throws Exception {

        VideoDeviceDescription [] devices = VideoDeviceFactory.availableDevices();
    
        System.out.println("Found " + devices.length + " devices:");
        
        for (VideoDeviceDescription d : devices) { 
            System.out.println(d.getSimpleDescription());
        }    
    }
    
}
