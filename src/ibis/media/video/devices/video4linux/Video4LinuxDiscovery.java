package ibis.media.video.devices.video4linux;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import ibis.media.imaging.Format;
import ibis.media.video.Capability;
import ibis.media.video.FrameRate;
import ibis.media.video.Resolution;
import ibis.media.video.VideoDeviceDescription;
import ibis.media.video.VideoDeviceDiscovery;

public class Video4LinuxDiscovery implements VideoDeviceDiscovery {
    
    private static final int DISCRETE = 0;
    private static final int CONTINUOUS = 1;
    private static final int STEPWISE = 2;
    
    private static final int MAX_DEVICES = 64;
    private static final String DEVICE_NAME = "/dev/video";
    
    private native boolean testDevice(String device, int number);
    
    //private VideoDeviceDescription current;
    
    private boolean haveDevice = false;
    
    private String deviceName;
    private String deviceDescription;
    private int deviceNumber;
    private boolean v4L1;
    private boolean v4L2;
    
    private HashMap<Format, Capability> capabilities = 
        new HashMap<Format, Capability>();
    
  //  private final Format [] formats;
    
    public VideoDeviceDescription [] discover() { 

        ArrayList<VideoDeviceDescription> devices = 
            new ArrayList<VideoDeviceDescription>();
        
        for (int i=0;i<MAX_DEVICES;i++) { 
            
            haveDevice = false;
            
            String dev = DEVICE_NAME + i;
            
            File tmp = new File(dev);
            
            if (tmp.exists() && testDevice(dev, i)) {
                if (haveDevice) { 
                    System.out.println("Found device: " + dev);
                    devices.add(new Video4LinuxDeviceDescription(deviceName, 
                            deviceDescription, deviceNumber,
                            capabilities, v4L1, v4L2));
                }
            }
        }
    
        haveDevice = false;

        return devices.toArray(new VideoDeviceDescription[devices.size()]);
    }
    // This method is called from the native layer in response to the 
    // initialization of a device
    @SuppressWarnings("unused")
    private void capability(int type, int palette, int minWidth, int minHeight, 
            int maxWidth, int maxHeight, int stepW, int stepH, int numerator, 
            int denominator) { 

        // TODO: Is this correct ?

        try { 
            Format p = Video4LinuxFormat.getFormat(palette);
        
            if (p == null) { 
                System.err.println("Unknown palette!");
                return;
            }
        
            Resolution r = null;
            FrameRate f = null;
            
            if (type == DISCRETE) { 
                System.out.println("DISCRETE DEVICE CAPABILITY " + p + " " 
                        + minWidth + " " + minHeight + " " + numerator + "/" + denominator);
                r = new Resolution(minWidth, minHeight);
            } else if (type == CONTINUOUS) { 
                System.out.println("CONTINUOUS DEVICE CAPABILITY " + 
                        p.getDescription() + " " + type + " " + 
                        minWidth + "x" + minHeight + " ... " + 
                        maxWidth + "x" + maxHeight + " " + 
                        numerator + "/" + denominator);
                r = new Resolution(minWidth, minHeight, maxWidth, maxHeight);
            } else if (type == STEPWISE) { 
                System.out.println("STEPWISE DEVICE CAPABILITY " + 
                        p.getDescription() + " " + type + " " + 
                        minWidth + "x" + minHeight + " ... " + 
                        maxWidth + "x" + maxHeight + " " + 
                        stepW + "x" + stepH + " " + numerator + "/" + denominator);
                r = new Resolution(minWidth, minHeight, maxWidth, maxHeight, stepW, stepH);
            } else { 
                System.out.println("UNKNOWN DEVICE CAPABILITY " + 
                        p.getDescription() + " " + type + " " + 
                        minWidth + "x" + minHeight + " ... " + 
                        maxWidth + "x" + maxHeight + " " + 
                        stepW + "x" + stepH + " " + numerator + "/" + denominator);
            }
        
            Capability c = capabilities.get(p);
            
            if (c == null) { 
                c = new Capability(p);
                capabilities.put(p, c);
            }
    
            c.addFrameRate(r, new FrameRate(numerator, denominator));
        } catch (Throwable e) {
            System.out.println("EEP");
            e.printStackTrace();
        }       
    }
    
    @SuppressWarnings("unused") // Called from native code!
    private void available(String name, String description, int number, 
            boolean v4l1, boolean v4l2) { 
       
        this.deviceName = name;
        this.deviceDescription = description;
        this.deviceNumber = number;
        this.v4L1 = v4l1;
        this.v4L2 = v4l2;
        
        haveDevice = true;
    }
    
}
