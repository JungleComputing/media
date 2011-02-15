package ibis.media.video.devices.video4linux;

import java.util.HashMap;

import ibis.media.imaging.Format;
import ibis.media.video.Capability;
import ibis.media.video.VideoDeviceDescription;

public class Video4LinuxDeviceDescription extends VideoDeviceDescription {

    public final boolean V4L1Available; 
    public final boolean V4L2Available; 
    
    public Video4LinuxDeviceDescription(String deviceName, 
            String deviceDescription, int deviceNumber, 
            HashMap<Format, Capability> capabilities, 
            boolean V4L1Available, boolean V4L2Available) {
        
        super(deviceName, deviceDescription, deviceNumber, capabilities);
        
        this.V4L1Available = V4L1Available;
        this.V4L2Available = V4L2Available;
    }
    
    public String getSimpleDescription() { 
        return deviceNumber + ": " + deviceName + " (" + deviceDescription 
            + (V4L1Available ? " v4l1" : "") + (V4L2Available ? " v4l2" : "") 
            + ")";
    }
}
