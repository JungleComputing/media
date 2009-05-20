package ibis.imaging4j;

import ibis.imaging4j.Format;
import ibis.imaging4j.conversion.ARGB32toBufferedImage;
import ibis.imaging4j.conversion.ARGB32toJPG;
import ibis.imaging4j.conversion.ARGB32toRGB24;
import ibis.imaging4j.conversion.ARGB64toBufferedImage;
import ibis.imaging4j.conversion.Convertor;
import ibis.imaging4j.conversion.ConvertorToBufferedImage;
import ibis.imaging4j.conversion.JPGtoRGB24;
import ibis.imaging4j.conversion.MJPGtoARGB32;
import ibis.imaging4j.conversion.MJPGtoJPG;
import ibis.imaging4j.conversion.MJPGtoRGB24;
import ibis.imaging4j.conversion.RGB24toARGB32;
import ibis.imaging4j.conversion.RGB24toBufferedImage;
import ibis.imaging4j.conversion.RGB24toJPG;
import ibis.imaging4j.conversion.RGB48toBufferedImage;
import ibis.imaging4j.conversion.YUV420SPtoBufferedImage;
import ibis.imaging4j.conversion.YUV420SPtoRGB24;
import ibis.imaging4j.conversion.YUV422SPtoARGB32;
import ibis.imaging4j.conversion.YUV422SPtoBufferedImage;
import ibis.imaging4j.conversion.YUV422SPtoRGB24;
import ibis.imaging4j.conversion.YUYVtoARGB32;
import ibis.imaging4j.conversion.YUYVtoBufferedImage;
import ibis.imaging4j.conversion.YUYVtoRGB24;

import java.util.HashMap;

public class Conversion {

    static class ConvertorOptions {

        public final Format from;

        public ConvertorToBufferedImage toBufferedImage;

        public final HashMap<Format, Convertor> conv = 
            new HashMap<Format, Convertor>();

        ConvertorOptions(Format from) {
            this.from = from;
        }

        void add(ConvertorToBufferedImage toBufferedImage) {
            this.toBufferedImage = toBufferedImage;
        }

        void add(Format to, Convertor convertor) { 
            conv.put(to, convertor);
        }

        ConvertorToBufferedImage get() { 
            return toBufferedImage;
        }

        Convertor get(Format to) { 
            return conv.get(to);
        }
    }

    private static final HashMap<Format, ConvertorOptions> convertors = 
        new HashMap<Format, ConvertorOptions>();

    static {
        // TODO: should load these dynamically ? 
                // TODO: this is far from complete!

        try {
            addConvertor(Format.ARGB32, new ARGB32toBufferedImage());
            addConvertor(Format.ARGB32, Format.RGB24, new ARGB32toRGB24());
            addConvertor(Format.ARGB32, Format.JPG, new ARGB32toJPG());
            
            addConvertor(Format.ARGB64, new ARGB64toBufferedImage());
            
            addConvertor(Format.JPG, Format.RGB24, new JPGtoRGB24());
            
            addConvertor(Format.MJPG, Format.ARGB32, new MJPGtoARGB32());
            addConvertor(Format.MJPG, Format.JPG, new MJPGtoJPG());
            addConvertor(Format.MJPG, Format.RGB24, new MJPGtoRGB24());
            
            addConvertor(Format.RGB24, Format.ARGB32, new RGB24toARGB32());
            addConvertor(Format.RGB24, new RGB24toBufferedImage());
            addConvertor(Format.RGB24, Format.JPG, new RGB24toJPG());
            
            addConvertor(Format.RGB48, new RGB48toBufferedImage());
            
            addConvertor(Format.YUV420SP, new YUV420SPtoBufferedImage());
            addConvertor(Format.YUV420SP, Format.RGB24, new YUV420SPtoRGB24());

            addConvertor(Format.YUV422SP, Format.ARGB32, new YUV422SPtoARGB32());
            addConvertor(Format.YUV422SP, new YUV422SPtoBufferedImage());
            addConvertor(Format.YUV422SP, Format.RGB24, new YUV422SPtoRGB24());

            addConvertor(Format.YUYV, Format.ARGB32, new YUYVtoARGB32());
            addConvertor(Format.YUYV, new YUYVtoBufferedImage());
            addConvertor(Format.YUYV, Format.RGB24, new YUYVtoRGB24());
        } catch (Exception e) {
            System.err.println("Failed to load convertors!" + e);
            e.printStackTrace();
        }
    }

    public static void addConvertor(Format from, 
            ConvertorToBufferedImage convertor) {

        ConvertorOptions options = convertors.get(from);

        if (options == null) { 
            options = new ConvertorOptions(from);
            convertors.put(from, options);
        }

        options.add(convertor);
    }

    public static void addConvertor(Format from, Format to, 
            Convertor convertor) { 

        ConvertorOptions options = convertors.get(from);

        if (options == null) { 
            options = new ConvertorOptions(from);
            convertors.put(from, options);
        }

        options.add(to, convertor);
    }

    public static Convertor getConvertor(Format from, Format to) { 

        ConvertorOptions options = convertors.get(from);

        if (options == null) { 
            return null;
        }

        return options.get(to);
    }

    public static ConvertorToBufferedImage getConvertorToBufferedImage(Format from) { 

        ConvertorOptions options = convertors.get(from);

        if (options == null) { 
            return null;
        }

        return options.get();
    }

    public static boolean canConvert(Format from, Format to) { 

        ConvertorOptions options = convertors.get(from);

        if (options == null) { 
            return false;
        }

        return (options.get(to) != null);
    }   
}
