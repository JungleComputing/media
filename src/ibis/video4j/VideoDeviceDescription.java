package ibis.video4j;

import java.util.HashMap;

import ibis.imaging4j.Format;

public class VideoDeviceDescription {

    public final String deviceName;
    public final String deviceDescription;
    public final int deviceNumber;
    
    private HashMap<Format, Capability> capabilities = 
        new HashMap<Format, Capability>();
    
    private final Format [] formats;
    
    /**
     * Construct a new VideoDeviceDescription
     * 
     * @param deviceName
     * @param deviceDescription
     * @param deviceNumber
     */
    public VideoDeviceDescription(final String deviceName, 
            final String deviceDescription, final int deviceNumber, 
            final HashMap<Format, Capability> capabilities) {
        
        this.deviceName = deviceName;
        this.deviceDescription = deviceDescription;
        this.deviceNumber = deviceNumber;
        this.capabilities = capabilities;
        
        formats = capabilities.keySet().toArray(
        		new Format[capabilities.size()]);
    }
    
    public String toString() { 
        return deviceDescription;
    }
    
    public String getSimpleDescription() { 
        return deviceNumber + ": " + deviceName + " (" + deviceDescription + ")";
    }
    
    public Format [] getFormats() { 
        return formats;
    }    
}
