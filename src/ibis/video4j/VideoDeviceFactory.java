package ibis.video4j;

import ibis.imaging4j.Format;
import ibis.video4j.devices.VideoSource;
import ibis.video4j.devices.directshow.DirectShowDeviceFactory;
import ibis.video4j.devices.directshow.DirectShowDiscovery;
import ibis.video4j.devices.video4linux.Video4LinuxDeviceFactory;
import ibis.video4j.devices.video4linux.Video4LinuxDiscovery;

public abstract class VideoDeviceFactory {
    
    private static boolean libraryLoaded = false;
    
    private static String os;
   
    private static VideoDeviceFactory factory;
    
    private static void loadLibrary() throws Exception {
    
        if (libraryLoaded) { 
            return;
        }
        
        libraryLoaded = true;
        
        // We start by figuring out which library we should load by looking at 
        // the OS specified in the System properties. 
        
        os = System.getProperty("os.name").toLowerCase();
        String arch = System.getProperty("os.arch").toLowerCase();
        String version = System.getProperty("os.version").toLowerCase();
         
        System.out.println("Running on: " + os + ", " + version + ", " + arch);
        
        String library = null;
        
        if (os.equals("linux")) { 
            
            if (arch.equals("i386")) { 
                library = "V4J-Linux-i386";
            } else if (arch.equals("amd64")) { 
                library = "V4J-Linux-amd64";                
            } else if (arch.equals("x86_64")) { 
                library = "V4J-Linux-x86_64";                
            } else { 
                throw new Exception("Unsupported OS/architecture: " + os 
                        + "/" + arch);
            }
            
            factory = new Video4LinuxDeviceFactory();
            
        } else if (os.startsWith("windows")) { 
       
            library = "libV4J-Windows XP-x86";
            
            factory = new DirectShowDeviceFactory();
            
        } else { 
            throw new Exception("Unsupported OS: " + os + "/" + arch);
        }
          
        System.out.println("Library to load: " + library);
        
        try { 
            System.loadLibrary(library);
        } catch (Throwable e) { 
            factory = null;
            System.err.println("Failed to load library: " + library);
            throw new Exception("Failed to load library: " + library, e);
        }
    }
    
    public static VideoDeviceDescription [] availableDevices() 
        throws Exception { 
        
        loadLibrary();
   
        VideoDeviceDiscovery discovery = null;
        
        if (os == null) { 
            // TODO: should not happen ?
            return new VideoDeviceDescription[0];
        } else if (os.equals("linux")) { 
            discovery = new Video4LinuxDiscovery();
        } else if (os.startsWith("windows")) { 
            discovery = new DirectShowDiscovery();
         } else { 
            throw new Exception("Unsupported OS: " + os);            
        }
        
        return discovery.discover();
    }
    
    public static VideoSource openDevice(VideoConsumer consumer, 
            int deviceNumber, int width, int height, int delay) 
        throws Exception {
        
        return openDevice(consumer, deviceNumber, width, height, delay, 
                Format.ARGB32, 0);        
    }
    
    public static VideoSource openDevice(VideoConsumer consumer, 
            String description, int width, int height, int delay) 
        throws Exception {        
    
        return openDevice(consumer, description, width, height, delay, 
                Format.ARGB32, 0);            
    }
    
    public static VideoSource openDevice(VideoConsumer consumer, 
            int deviceNumber, int width, int height, int delay, 
            Format palette, double quality) throws Exception {        
    
        loadLibrary();
        
        if (factory == null) { 
            throw new Exception("Failed to load device factory");     
        }
        
        if (deviceNumber < 0) {
            throw new Exception("Illegal device number!");
        }
        
        return factory.createDevice(consumer, deviceNumber, width, height, 
                delay, palette, quality);
    }
    
    public static VideoSource openDevice(VideoConsumer consumer, 
            String description, int width, int height, int delay, 
            Format palette, double quality) throws Exception {        

        loadLibrary();
        
        if (factory == null) { 
            throw new Exception("Failed to load device factory");     
        }
        
        if (description == null) {
            throw new Exception("Illegal device description");
        }
        
        return factory.createDevice(consumer, description, width, height, 
                delay, palette, quality);
    }
    
    protected abstract VideoSource createDevice(VideoConsumer consumer, 
           int deviceNumber, int width, int height, int delay, 
           Format palette, double quality) throws Exception;
        
    protected abstract VideoSource createDevice(VideoConsumer consumer, 
         String description, int width, int height, int delay,
         Format palette, double quality) throws Exception;
     
}
