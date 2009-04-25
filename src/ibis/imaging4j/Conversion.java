package ibis.imaging4j;

import ibis.imaging4j.Format;
import ibis.imaging4j.conversion.ARGB32toBufferedImage;
import ibis.imaging4j.conversion.Convertor;
import ibis.imaging4j.conversion.ConvertorToBufferedImage;
import ibis.imaging4j.conversion.MJPGtoARGB32;
import ibis.imaging4j.conversion.MJPGtoJPG;
import ibis.imaging4j.conversion.RGB24toARGB32;
import ibis.imaging4j.conversion.RGB24toBufferedImage;
import ibis.imaging4j.conversion.YUV420SPtoBufferedImage;
import ibis.imaging4j.conversion.YUV420SPtoRGB24;
import ibis.imaging4j.conversion.YUV422SPtoBufferedImage;
import ibis.imaging4j.conversion.YUV422SPtoRGB24;
import ibis.imaging4j.conversion.YUYVtoARGB32;
import ibis.imaging4j.conversion.YUYVtoBufferedImage;

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
            addConvertor(Format.YUYV, Format.ARGB32, new YUYVtoARGB32());
            addConvertor(Format.YUYV, new YUYVtoBufferedImage());

            addConvertor(Format.YUV420SP, Format.RGB24, new YUV420SPtoRGB24());
            addConvertor(Format.YUV420SP, new YUV420SPtoBufferedImage());

            addConvertor(Format.YUV422SP, Format.RGB24, new YUV422SPtoRGB24());
            addConvertor(Format.YUV422SP, new YUV422SPtoBufferedImage());

            addConvertor(Format.RGB24, Format.ARGB32, new RGB24toARGB32());
            addConvertor(Format.RGB24, new RGB24toBufferedImage());

            addConvertor(Format.ARGB32, new ARGB32toBufferedImage());

            addConvertor(Format.MJPG, Format.JPG, new MJPGtoJPG());
            addConvertor(Format.MJPG, Format.ARGB32, new MJPGtoARGB32());
            
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
