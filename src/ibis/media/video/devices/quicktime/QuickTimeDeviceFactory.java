package ibis.media.video.devices.quicktime;

import ibis.media.imaging.Format;
import ibis.media.video.VideoConsumer;
import ibis.media.video.VideoDeviceDescription;
import ibis.media.video.VideoDeviceFactory;
import ibis.media.video.devices.VideoSource;

public class QuickTimeDeviceFactory extends VideoDeviceFactory {

    @Override
    protected VideoSource createDevice(VideoConsumer consumer, int deviceNumber,
            int width, int height, int delay, Format palette, 
            double quality) throws Exception {
        
        QuickTimeDiscovery discovery = new QuickTimeDiscovery();
        
        VideoDeviceDescription [] devices = discovery.discover();
        
        if (devices == null || deviceNumber >= devices.length) {
            throw new Exception("Device " + deviceNumber + " does not exist");
        }
        
        VideoDeviceDescription d = devices[deviceNumber];
    
        return new QuickTimeDevice(consumer, d, width, height, 
                delay, quality);
    }

    @Override
    protected VideoSource createDevice(VideoConsumer consumer, 
            String description, int width, int height, int delay, 
            Format palette, double quality) throws Exception {

        QuickTimeDiscovery discovery = new QuickTimeDiscovery();
        
        VideoDeviceDescription [] devices = discovery.discover();
        
        if (devices == null) {
            throw new Exception("Device " + description + " does not exist");
        }
        
        VideoDeviceDescription device = null;
        
        for (VideoDeviceDescription d : devices) { 
        
            if (d.deviceDescription.equals(description)) { 
                device = (VideoDeviceDescription) d;
                break;
            }
        }
        
        if (device == null) {
            throw new Exception("Device " + description + " does not exist");
        }
        
        return new QuickTimeDevice(consumer, device, width, 
                height, delay, quality);
    }
}
