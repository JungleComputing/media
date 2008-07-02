package ibis.video4j.devices.video4linux;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.HashMap;

import ibis.video4j.VideoConsumer;
import ibis.video4j.VideoPalette;
import ibis.video4j.devices.VideoSource;

public class Video4LinuxDevice extends VideoSource {
    
    private static final int DEFAULT_WIDTH  = 352;
    private static final int DEFAULT_HEIGHT = 288;
  
    private static final int DEFAULT_BUFFERS = 4;
    
    private static final int DISCRETE = 0;
    private static final int CONTINUOUS = 1;
    private static final int STEPWISE = 2;
      
    public class Resolution { 
        
        final int minW; 
        final int minH;
        
        final int maxW; 
        final int maxH;
        
        final int stepW;
        final int stepH;
        
        public Resolution(int minW, int minH, int maxW, int maxH, int stepW, 
                int stepH) { 
            
            this.minW = minW;
            this.maxW = maxW;
            this.minH = minH;
            this.maxH = maxH;
            this.stepW = stepW;
            this.stepH = stepH;
        }
        
        public Resolution(int minW, int minH, int maxW, int maxH) { 
            this(minW, minH, maxW, maxH, 1, 1);
        }
       
        public Resolution(int x, int y) { 
            this(x, y, x, y, -1, -1);
        }

        @Override // Generated
        public int hashCode() {
            final int PRIME = 31;
            int result = 1;
            result = PRIME * result + maxH;
            result = PRIME * result + maxW;
            result = PRIME * result + minH;
            result = PRIME * result + minW;
            result = PRIME * result + stepH;
            result = PRIME * result + stepW;
            return result;
        }

        @Override // Generated
        public boolean equals(Object obj) {
            if (this == obj)
                return true;
            if (obj == null)
                return false;
            if (getClass() != obj.getClass())
                return false;
            final Resolution other = (Resolution) obj;
            if (maxH != other.maxH)
                return false;
            if (maxW != other.maxW)
                return false;
            if (minH != other.minH)
                return false;
            if (minW != other.minW)
                return false;
            if (stepH != other.stepH)
                return false;
            if (stepW != other.stepW)
                return false;
            return true;
        }
    }
    
    public class FrameRate { 
        
        final int numerator;
        final int denominator;
        
        public FrameRate(int numerator, int denominator) { 
            this.numerator = numerator;
            this.denominator = denominator;
        }    
    }
    
    public class ResolutionCapability { 
        
        final Resolution resolution;
        final ArrayList<FrameRate> rates;
        
        public ResolutionCapability(Resolution resolution) {
            this.resolution = resolution;
            this.rates = new ArrayList<FrameRate>();
        }
        
        public void addFrameRate(FrameRate rate) { 
            rates.add(rate);
        } 
    }

    public class Capability { 
        
        final NativePalette palette;
        final ArrayList<ResolutionCapability> resolutions;
        
        public Capability(NativePalette palette) {
            this.palette = palette;
            this.resolutions = new ArrayList<ResolutionCapability>();
        }
        
        public void addFrameRate(Resolution resolution, FrameRate rate) { 
          
            for (ResolutionCapability r : resolutions) { 
                
                if (r.resolution.equals(resolution)) { 
                    r.rates.add(rate);
                    return;
                }
            }

            // We only end up here if the resulotion was not found!
            ResolutionCapability r = new ResolutionCapability(resolution);
            r.rates.add(rate);
            resolutions.add(r);
        } 
    }
    
    private native int initDevice(String device, int deviceNumber, 
            int api, int buffers);
   
    private native int configureDevice(int deviceNumber, int width, 
            int height, int palette, int fps);
    
    private native int closeDevice(int deviceNumber);
    
    private native int grab(int deviceNumber);
    
    
    
    private HashMap<NativePalette, Capability> capabilities = 
        new HashMap<NativePalette, Capability>();
    
    private final String device; 
    private final int deviceNumber; 
    
    private String name; 
      
    private int minWidth; 
    private int maxWidth;
    
    private int minHeight; 
    private int maxHeight; 
    
    private int currentHeight; 
    private int currentWidth; 
    
    private int availablePalette; 
    
    private ByteBuffer [] buffers;
    
    private NativePalette currentPalette; 
    private Convertor paletteConvertor;
    
    public Video4LinuxDevice(VideoConsumer consumer, int deviceNumber, 
            int width, int height, int delay, int api) throws Exception {        
        
        super(consumer, width, height, delay);
    
        this.deviceNumber = deviceNumber;
        this.device = "/dev/video" + deviceNumber;
        this.buffers = new ByteBuffer[DEFAULT_BUFFERS];
        
        System.out.println("Creating webcam " + width + "x" + height);        
        
        int result = initDevice(device, deviceNumber, api, DEFAULT_BUFFERS);
        
        if (result == 0) { 
            System.out.println("Video4Linux device initialized");
            initialized(true);
        } else { 
            initialized(false);
            resultToException(result);
        }
    
        // TODO check if these setting are supported!!!!
        currentHeight = DEFAULT_HEIGHT;
        currentWidth = DEFAULT_WIDTH;
        
        selectPalette();
        
        if (currentPalette == null || paletteConvertor == null) { 
            throw new RuntimeException("Failed to find suitable palette!"); 
        }
    
        paletteConvertor = Conversion.getConvertor(currentPalette, 
                VideoPalette.RGB32);
        
        result = configureDevice(deviceNumber, currentWidth, currentHeight, 
                currentPalette.getNativeIndex(), 30);      
   
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
    
    private void selectPalette() { 
    
        NativePalette palette = null;
        Convertor convertor = null; 
        
        for (NativePalette p : capabilities.keySet()) { 
        
            if (Conversion.canConvert(p, VideoPalette.RGB32)) { 
       
                Convertor tmp = Conversion.getConvertor(p, VideoPalette.RGB32);
                
                if (palette == null || (tmp.cost < convertor.cost)) { 
                    palette = p;
                    convertor = tmp;
                }   
            }
        }
        
        System.out.println("Selected palette: " + palette);
        
        currentPalette = palette;
        paletteConvertor = convertor;
    }
    
    // This method is called from the native layer in response to the 
    // initialization of a device
    @SuppressWarnings("unused")
    private void capability(int type, int palette, int minWidth, int minHeight, 
            int maxWidth, int maxHeight, int stepW, int stepH, int numerator, 
            int denominator) { 

        // TODO: Is this correct ?

        try { 
            NativePalette p = NativePalette.getPalette(palette);
        
            if (p == null) { 
                System.err.println("Unknown palette!");
                return;
            }
        
            Resolution r = null;
            FrameRate f = null;
            
            if (type == DISCRETE) { 
                System.out.println("DISCRETE DEVICE CAPABILITY " + p.getCodeName() + " " 
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
    
    // This method is called from the native layer in response to the 
    // initialization of a device
    @SuppressWarnings("unused")
    private void bufferCount(int buffers) {
        System.out.println("BUFFERS " + buffers);
        this.buffers = new ByteBuffer[buffers]; 
    }
   
    @SuppressWarnings("unused")
    private void addBuffer(int index, ByteBuffer buffer) { 
        buffers[index] = buffer;        
        System.out.println("GOT BUFFER " + index + " of size " + buffer.capacity());
    } 
    
    protected void grab() { 
        grab(deviceNumber);
    }
    
    @SuppressWarnings("unused")
    private boolean gotImage(int index, int size) { 
        // An image has appeared in buffer[index]
        
        int [] pixels = consumer.getBuffer(currentWidth, currentHeight, index);
        
        // Is this correct ?
        ByteBuffer tmp = buffers[index];
        tmp.rewind();    
        
        if (paletteConvertor != null) { 
            paletteConvertor.convert(width, height, tmp, pixels);
        } else { 
            System.out.println("Unsupported palette conversion:" 
                    + currentPalette.getCodeName() + " to RGB32");
        }
                
        consumer.gotImage(pixels, index);       
    
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
        
        configureDevice(deviceNumber, width, height, 
                currentPalette.getNativeIndex(), 30);
    }
    
    public String toString() { 

        StringBuilder s = new StringBuilder();
        
        s.append("Video4LinuxDevice   : " + name + "\n"); 
        s.append(" width   : " + minWidth + " ... " + maxWidth + "\n"); 
        s.append(" height  : " + minHeight + " ... " + maxHeight + "\n"); 
        s.append(" buffers : " + buffers.length + "\n"); 
        
        s.append(" palette : " );
        
        NativePalette [] options = NativePalette.values();
        
        boolean needComma = false;
        
        for (NativePalette p : options) {
            if ((availablePalette & (1 << p.getNativeIndex()-1)) != 0) {
                
                if (needComma) { 
                    s.append(", ");
                } else { 
                    needComma = true;
                }
                
                s.append(p.getDescription());
            }
        }

        s.append("\n");

        if (currentPalette != null) { 
            s.append(" grab    : " + currentPalette.getDescription());
        }    
    
        return s.toString();
    }
}
