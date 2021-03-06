package ibis.media.video.devices.video4linux;

import ibis.media.imaging.Format;
import ibis.media.video.VideoConsumer;
import ibis.media.video.VideoDeviceDescription;
import ibis.media.video.VideoDeviceFactory;

public class Video4LinuxDeviceFactory extends VideoDeviceFactory {

    protected Video4LinuxDevice createDevice(VideoConsumer consumer, 
            int deviceNumber, int width, int height, int delay, 
            Format palette, double quality) throws Exception { 
        
        Video4LinuxDiscovery discovery = new Video4LinuxDiscovery();
        
        VideoDeviceDescription [] devices = discovery.discover();
        
        if (devices == null || deviceNumber >= devices.length) {
            throw new Exception("Device " + deviceNumber + " does not exist");
        }
        
        Video4LinuxDeviceDescription d = 
            (Video4LinuxDeviceDescription) devices[deviceNumber];
    
        if (d.V4L2Available) { 
            return new Video4LinuxDevice(consumer, d, width, 
                    height, delay, 2, palette, quality);
        }
        
        if (d.V4L1Available) { 
            return new Video4LinuxDevice(consumer, d, width, 
                    height, delay, 1, palette, quality);
        }
        
        throw new Exception("Failed to initialize device " + deviceNumber);
    }
 
    protected Video4LinuxDevice createDevice(VideoConsumer consumer, 
            String description, int width, int height, int delay, 
            Format palette, double quality) throws Exception { 
        
        Video4LinuxDiscovery discovery = new Video4LinuxDiscovery();
        
        VideoDeviceDescription [] devices = discovery.discover();
        
        if (devices == null || devices.length == 0) {
            throw new Exception("Device " + description + " does not exist");
        }
       
        Video4LinuxDeviceDescription device = null;
        
        for (VideoDeviceDescription d : devices) { 
        
            if (d.deviceDescription.equals(description)) { 
                device = (Video4LinuxDeviceDescription) d;
                break;
            }
        }
        
        if (device == null) {
            throw new Exception("Device " + description + " does not exist");
        }
        
        if (device.V4L2Available) { 
            return new Video4LinuxDevice(consumer, device, width, 
                    height, delay, 2, palette, quality);
        }
        
        if (device.V4L1Available) { 
            return new Video4LinuxDevice(consumer, device, width, 
                    height, delay, 1, palette, quality);
        }
        
        throw new Exception("Failed to initialize device " + description);
    }
    
}
