package ibis.video4j;

public class VideoDeviceDescription {

    public final String deviceName;
    public final String deviceDescription;
    public final int deviceNumber;
  
    /**
     * Construct a new VideoDeviceDescription
     * 
     * @param deviceName
     * @param deviceDescription
     * @param deviceNumber
     */
    public VideoDeviceDescription(final String deviceName, 
            final String deviceDescription, final int deviceNumber) {
        this.deviceName = deviceName;
        this.deviceDescription = deviceDescription;
        this.deviceNumber = deviceNumber;
    }
    
    public String toString() { 
        return deviceDescription;
    }
    
    public String getSimpleDescription() { 
        return deviceNumber + ": " + deviceName + " (" + deviceDescription + ")";
    }
    
}
