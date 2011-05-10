package ibis.media.video;

import ibis.media.imaging.Format;
import ibis.media.video.devices.VideoSource;
import ibis.media.video.devices.directshow.DirectShowDeviceFactory;
import ibis.media.video.devices.directshow.DirectShowDiscovery;
import ibis.media.video.devices.video4linux.Video4LinuxDeviceFactory;
import ibis.media.video.devices.video4linux.Video4LinuxDiscovery;

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
                library = "im-Linux-i386";
            } else if (arch.equals("amd64")) {
                library = "im-Linux-amd64";
            } else if (arch.equals("x86_64")) {
                library = "im-Linux-x86_64";
            } else {
                throw new Exception("Unsupported OS/architecture: " + os + "/"
                        + arch);
            }

            factory = new Video4LinuxDeviceFactory();

        } else if (os.startsWith("windows")) {
            if (arch.equals("x86")) {
            	library = "libim-Windows-x86";
            	
            	try {
                    System.loadLibrary("escapi32");
                } catch (Throwable e) {
                    factory = null;
                    System.err.println("Failed to load 32-bit escapi library");
                    e.printStackTrace(System.err);
                    throw new Exception("Failed to load 32-bit escapi library", e);
                }
            } else if (arch.equals("amd64")) {
                 library = "libim-Windows-amd64";
                 
                 try {
                     System.loadLibrary("escapi64");
                 } catch (Throwable e) {
                     factory = null;
                     System.err.println("Failed to load 64-bit escapi library");
                     e.printStackTrace(System.err);
                     throw new Exception("Failed to load 64-bit escapi library", e);
                 }
            } else {
                throw new Exception("Unsupported OS/architecture: " + os + "/"
                        + arch);
            }

            // we need the escapi library too, load it first
            

            factory = new DirectShowDeviceFactory();

        } else if (os.equals("mac os x")) {
            System.out.println("No library needed on osx");
            
            Class<?> factoryClass = Class.forName("ibis.media.video.devices.quicktime.QuickTimeDeviceFactory");

        	factory = (VideoDeviceFactory) factoryClass.getConstructor().newInstance();
            
        } else {

            throw new Exception("Unsupported OS: " + os + "/" + arch);
        }

        if (library != null) {

            System.out.println("Library to load: " + library);

            try {
                System.loadLibrary(library);
            } catch (Throwable e) {
                factory = null;
                System.err.println("Failed to load library: " + library);
                e.printStackTrace(System.err);
                throw new Exception("Failed to load library: " + library, e);
            }
        }
    }

    public static VideoDeviceDescription[] availableDevices() throws Exception {

        loadLibrary();

        VideoDeviceDiscovery discovery = null;

        if (os == null) {
            // TODO: should not happen ?
            return new VideoDeviceDescription[0];
        } else if (os.equals("linux")) {
            discovery = new Video4LinuxDiscovery();
        } else if (os.startsWith("windows")) {
            discovery = new DirectShowDiscovery();
        } else if (os.equals("mac os x")) {
        	Class<?> discoveryClass = Class.forName("ibis.media.video.devices.quicktime.QuickTimeDiscovery");

        	discovery = (VideoDeviceDiscovery) discoveryClass.getConstructor().newInstance();
        	
        	
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
            int deviceNumber, int width, int height, int delay, Format palette,
            double quality) throws Exception {

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
            int deviceNumber, int width, int height, int delay, Format palette,
            double quality) throws Exception;

    protected abstract VideoSource createDevice(VideoConsumer consumer,
            String description, int width, int height, int delay,
            Format palette, double quality) throws Exception;

}
