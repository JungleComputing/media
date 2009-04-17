package ibis.video4j.devices.video4linux;

import ibis.video4j.VideoPalette;
import ibis.video4j.devices.video4linux.convertors.MJPEGtoMJPEG;
import ibis.video4j.devices.video4linux.convertors.RGB24toARGB32;
import ibis.video4j.devices.video4linux.convertors.ARGB32toARGB32;
import ibis.video4j.devices.video4linux.convertors.YUV420PtoARGB32;
import ibis.video4j.devices.video4linux.convertors.YUV420toARGB32;
import ibis.video4j.devices.video4linux.convertors.YUYVtoARGB32;

import java.util.HashMap;

public class Conversion {
    
    static class ConvertorOptions {
        
        public final Video4LinuxPalette from;
        
        public final HashMap<VideoPalette, Convertor> convertors = 
            new HashMap<VideoPalette, Convertor>();
    
        ConvertorOptions(Video4LinuxPalette from) {
            this.from = from;
        }
        
        void add(VideoPalette to, Convertor convertor) { 
            convertors.put(to, convertor);
        }
        
        Convertor get(VideoPalette to) { 
            return convertors.get(to);
        }
    }
        
    private static final HashMap<Video4LinuxPalette, ConvertorOptions> convertors = 
        new HashMap<Video4LinuxPalette, ConvertorOptions>();
    
    static { 
        addConvertor(Video4LinuxPalette.V4L2_YUYV, VideoPalette.ARGB32, new YUYVtoARGB32());
        addConvertor(Video4LinuxPalette.V4L2_YUV420, VideoPalette.ARGB32, new YUV420toARGB32());
        addConvertor(Video4LinuxPalette.V4L1_YUV420P, VideoPalette.ARGB32, new YUV420PtoARGB32());
        addConvertor(Video4LinuxPalette.V4L1_RGB24, VideoPalette.ARGB32, new RGB24toARGB32());
        addConvertor(Video4LinuxPalette.V4L1_RGB32, VideoPalette.ARGB32, new ARGB32toARGB32());
        addConvertor(Video4LinuxPalette.V4L2_MJPEG, VideoPalette.MJPG, new MJPEGtoMJPEG());        
    }
    
    public static void addConvertor(Video4LinuxPalette from, VideoPalette to, 
            Convertor convertor) { 
 
       ConvertorOptions options = convertors.get(from);
        
        if (options == null) { 
            options = new ConvertorOptions(from);
            convertors.put(from, options);
        }
        
        options.add(to, convertor);
    }
    
    public static Convertor getConvertor(Video4LinuxPalette from, VideoPalette to) { 
    
        ConvertorOptions options = convertors.get(from);
        
        if (options == null) { 
            return null;
        }
        
        return options.get(to);
    }
  
    public static boolean canConvert(Video4LinuxPalette from, VideoPalette to) { 
        
        ConvertorOptions options = convertors.get(from);
        
        if (options == null) { 
            return false;
        }
        
        return (options.get(to) != null);
    }
  
    
    
}
