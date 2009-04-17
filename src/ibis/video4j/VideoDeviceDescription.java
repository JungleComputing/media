package ibis.video4j;

public class VideoDeviceDescription {

    public final String deviceName;
    public final String deviceDescription;
    public final int deviceNumber;
    
    private final VideoPalette [] palette;
    
    /**
     * Construct a new VideoDeviceDescription
     * 
     * @param deviceName
     * @param deviceDescription
     * @param deviceNumber
     */
    public VideoDeviceDescription(final String deviceName, 
            final String deviceDescription, final int deviceNumber, 
            final VideoPalette [] palette) {
        
        this.deviceName = deviceName;
        this.deviceDescription = deviceDescription;
        this.deviceNumber = deviceNumber;
        this.palette = palette;
    }
    
    public String toString() { 
        return deviceDescription;
    }
    
    public String getSimpleDescription() { 
        return deviceNumber + ": " + deviceName + " (" + deviceDescription + ")";
    }
    
    public VideoPalette [] getPalettes() { 
        return palette;
    }    
}
