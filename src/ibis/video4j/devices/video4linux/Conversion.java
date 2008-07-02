package ibis.video4j.devices.video4linux;

import ibis.video4j.VideoPalette;
import ibis.video4j.devices.video4linux.convertors.RGB24toRGB32;
import ibis.video4j.devices.video4linux.convertors.RGB32toRGB32;
import ibis.video4j.devices.video4linux.convertors.YUV420PtoRGB32;
import ibis.video4j.devices.video4linux.convertors.YUV420toRGB32;
import ibis.video4j.devices.video4linux.convertors.YUYVtoRGB32;

import java.util.HashMap;

public class Conversion {
    
    static class ConvertorOptions {
        
        public final NativePalette from;
        
        public final HashMap<VideoPalette, Convertor> convertors = 
            new HashMap<VideoPalette, Convertor>();
    
        ConvertorOptions(NativePalette from) {
            this.from = from;
        }
        
        void add(VideoPalette to, Convertor convertor) { 
            convertors.put(to, convertor);
        }
        
        Convertor get(VideoPalette to) { 
            return convertors.get(to);
        }
    }
        
    private static final HashMap<NativePalette, ConvertorOptions> convertors = 
        new HashMap<NativePalette, ConvertorOptions>();
    
    static { 
        addConvertor(NativePalette.V4L2_PIX_FMT_YUYV, VideoPalette.RGB32, new YUYVtoRGB32());
        addConvertor(NativePalette.V4L2_PIX_FMT_YUV420, VideoPalette.RGB32, new YUV420toRGB32());
        addConvertor(NativePalette.VIDEO_PALETTE_YUV420P, VideoPalette.RGB32, new YUV420PtoRGB32());
        addConvertor(NativePalette.VIDEO_PALETTE_RGB24, VideoPalette.RGB32, new RGB24toRGB32());
        addConvertor(NativePalette.VIDEO_PALETTE_RGB32, VideoPalette.RGB32, new RGB32toRGB32());
    }
    
    public static void addConvertor(NativePalette from, VideoPalette to, 
            Convertor convertor) { 
 
       ConvertorOptions options = convertors.get(from);
        
        if (options == null) { 
            options = new ConvertorOptions(from);
            convertors.put(from, options);
        }
        
        options.add(to, convertor);
    }
    
    public static Convertor getConvertor(NativePalette from, VideoPalette to) { 
    
        ConvertorOptions options = convertors.get(from);
        
        if (options == null) { 
            return null;
        }
        
        return options.get(to);
    }
  
    public static boolean canConvert(NativePalette from, VideoPalette to) { 
        
        ConvertorOptions options = convertors.get(from);
        
        if (options == null) { 
            return false;
        }
        
        return (options.get(to) != null);
    }
  
    
    
}
