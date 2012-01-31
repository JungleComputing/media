
package ibis.media.video.devices.video4linux;
import java.nio.ByteBuffer;

import ibis.media.imaging.Format;
import ibis.media.imaging.Image;
import ibis.media.video.VideoConsumer;
import ibis.media.video.devices.VideoSource;

public class Video4LinuxDevice extends VideoSource {
    
    private static final int DEFAULT_BUFFERS = 4;
    
    private native int initDevice(String device, int deviceNumber, int api);
   
    private native int configureDevice(int deviceNumber, int width, 
            int height, int palette, int fps, int quality, int buffers);
    
    private native int closeDevice(int deviceNumber);
    
    private native int grab(int deviceNumber);
    
    private final String device; 
    private final int deviceNumber; 
    
    private String name; 
    
    private int minWidth; 
    private int maxWidth;
    
    private int minHeight; 
    private int maxHeight; 
    
    private int currentHeight; 
    private int currentWidth; 
    
    private ByteBuffer [] buffers;
    private Image [] images; 
    
    private Format currentFormat;
    private int nativeFormat;
    
    public Video4LinuxDevice(VideoConsumer consumer, 
    		Video4LinuxDeviceDescription desc, 
    		int width, int height, int delay, int api, Format format, 
            double quality) throws Exception {        
        
        super(consumer, desc, width, height, delay, quality);
    
        this.deviceNumber = desc.deviceNumber;
        this.device = "/dev/video" + deviceNumber;
        this.buffers = new ByteBuffer[DEFAULT_BUFFERS];
        
        System.out.println("Creating webcam " + width + "x" + height);        
      
        // TODO check if these setting are supported!!!!
        currentWidth = width;
        currentHeight = height;
        currentFormat = format;
        
        int result = initDevice(device, deviceNumber, api);
        
        if (result == 0) { 
            System.out.println("Video4Linux device initialized");
            initialized(true);
        } else { 
            initialized(false);
            resultToException(result);
        }
        
        nativeFormat = -1;

        if (api == 1) { 
            nativeFormat = Video4LinuxFormat.getNativeIndexV4L1(format);
        } else { 
            nativeFormat = Video4LinuxFormat.getNativeIndexV4L2(format);
        }

        if (nativeFormat == -1) { 
            throw new Exception("Format " + format + " not supported!");
        }
        
        int compressionQuality = -1;
        
        if (format.isCompressed()) { 
            
            compressionQuality = (int) (65535 * (quality / 100.0));
            
            if (compressionQuality < 0) { 
                compressionQuality = 0;
            }
            
            if (compressionQuality > 65535) { 
                compressionQuality = 65535;
            }
        }
        
        result = configureDevice(deviceNumber, currentWidth, currentHeight, 
                nativeFormat, 30, compressionQuality, DEFAULT_BUFFERS);      
   
        if (result == 0) { 
            System.out.println("Video4Linux device configured");
        } else { 
            System.out.println("Video4Linux device configuration failed!");
            resultToException(result);
        }
    }
    
    private void resultToException(int result) throws Exception { 
        
        switch (result) { 
        case -1:  throw new Exception("Out of memory");
        case -2:  throw new Exception("Illegal device number");
        case -3:  throw new Exception("Failed to open device");
        case -4:  throw new Exception("Failed to obtain device configuration");
        case -5:  throw new Exception("Failed to configure device");
        case -6:  throw new Exception("No palette found");
        case -7:  throw new Exception("Device does not support image grabbing");
        case -8:  throw new Exception("Failed to request buffers");
        case -9:  throw new Exception("Failed to queue buffers");
        case -10: throw new Exception("Unknown Video4Linux API");
        case -11: throw new Exception("Unknown device");
        case -12: throw new Exception("Failed to prepare callback");
        case -13: throw new Exception("Failed to grab image");
        default:  throw new Exception("Device initialization returned unknown result " + result);
        }
    }
    
    // This method is called from the native layer in response to the 
    // initialization of a device
    /*@SuppressWarnings("unused")
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
    
    // This method is called from the native layer in response to the 
    // initialization of a device
    @SuppressWarnings("unused")
    private void deviceName(String name) {
        
        System.out.println("DEVICENAME " + name);
        
        this.name = name;
    }
    */
    
    // This method is called from the native layer in response to the 
    // initialization of a device
    @SuppressWarnings("unused")
    private void bufferCount(int buffers) {
        System.out.println("BUFFERS " + buffers);
        this.buffers = new ByteBuffer[buffers];
        this.images = new Image[buffers];
    }
   
    @SuppressWarnings("unused")
    private void addBuffer(int index, ByteBuffer buffer) {         
        buffers[index] = buffer;        
        
        System.out.println("Creating image of " + currentWidth + " x " + currentHeight);
        
        images[index] = new Image(currentFormat, currentWidth, currentHeight, buffer);
        System.out.println("GOT BUFFER " + index + " of size " + buffer.capacity());
    } 
    
    protected void grab() { 
        grab(deviceNumber);
    }
    
    @SuppressWarnings("unused")
    private boolean gotImage(int index, int size) { 
        // An image has appeared in buffer[index]
        
        ByteBuffer tmp = buffers[index];
        
        tmp.position(0);
        tmp.limit(size);
                
        Image img = images[index];
        img.setNumber(deviceNumber);
        
       // System.out.println("Image has size " + img.getWidth() + " x " + img.getHeight());
        
        consumer.gotImage(img);       
        
        /*
        int [] pixels = consumer.getBuffer(currentWidth, currentHeight, index);
        
        // Is this correct ?
        ByteBuffer tmp = buffers[index];
        tmp.position(0);
        tmp.limit(size);
        
        if (paletteConvertor != null) { 
            paletteConvertor.convert(width, height, tmp, pixels);
        } else { 
            System.out.println("Unsupported palette conversion:" 
                    + inputPalette.getName() + " to RGB32");
        }
                
        consumer.gotImage(pixels, index);       
        */
    
        // NOTE: return if we want another frame... 
        return !getDone();
    } 
    
    @Override
    public void close() {
        
        // TODO: this call may fail to respond 
        setDone();
        
        closeDevice(deviceNumber);
    }
    
    public String getDevice() { 
        return device;
    }
    
    public String getDeviceName() { 
        return name;
    }
    
    public int getWidth() { 
        return width;
        
    }
    
    public int getHeight() { 
        return height;
    }
    
    public int getMaxWidth() { 
        return maxWidth;
    }
    
    public int getMaxHeight() { 
        return maxHeight;
    }
   
    public int getMinWidth() { 
        return minWidth;
    }
    
    public int getMinHeight() { 
        return minHeight;
    }
   
    @Override
    public void setResolution(int width, int height) {
    
        currentHeight = width;
        currentWidth = height;
        
        int compressionQuality = -1;
        
        if (currentFormat.isCompressed()) { 
        
            if (quality < 0 || quality > 100) {
                compressionQuality = (int) (65535 * (compressionQuality / 100.0));
            } else { 
                compressionQuality = (int) (65535 * (compressionQuality / 100.0));

                if (compressionQuality < 0) { 
                    compressionQuality = 0;
                }

                if (compressionQuality > 65535) { 
                    compressionQuality = 65535;
                }
            }
        }
        
        configureDevice(deviceNumber, width, height, 
                nativeFormat, 30, compressionQuality, DEFAULT_BUFFERS);
    }
    
    public String toString() { 

        StringBuilder s = new StringBuilder();
        
        s.append("Video4LinuxDevice   : " + name + "\n"); 
        s.append(" width   : " + minWidth + " ... " + maxWidth + "\n"); 
        s.append(" height  : " + minHeight + " ... " + maxHeight + "\n"); 
        s.append(" buffers : " + buffers.length + "\n"); 
        
        s.append(" palette : <FIXME>" );
        
        /*
        boolean needComma = false;
        
        for (Video4LinuxFormat p : Video4LVideo4LinuxFormat) {
            if ((availablePalette & (1 << p.getNativeIndex()-1)) != 0) {
                
                if (needComma) { 
                    s.append(", ");
                } else { 
                    needComma = true;
                }
                
                s.append(p.getDescription());
            }
        }
*/
        s.append("\n");

        if (currentFormat != null) { 
            s.append(" grab    : " + currentFormat.getDescription());
        }    
    
        return s.toString();
    }
}
