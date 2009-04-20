package ibis.video4j;

import ibis.imaging4j.Format;

public class VideoDeviceDescription {

    public final String deviceName;
    public final String deviceDescription;
    public final int deviceNumber;
    
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
            final Format [] formats) {
        
        this.deviceName = deviceName;
        this.deviceDescription = deviceDescription;
        this.deviceNumber = deviceNumber;
        this.formats = formats;
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
