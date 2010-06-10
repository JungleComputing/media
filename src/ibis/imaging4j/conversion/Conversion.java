package ibis.imaging4j.conversion;

import ibis.imaging4j.Format;

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
            addConvertor(Format.ARGB32, Format.TGDOUBLEARGB, new ARGB32toTGDOUBLEARGB());
//            addConvertor(Format.ARGB32, Format.TGDOUBLERGB, new ARGB32toTGDOUBLERGB());
            addConvertor(Format.ARGB32, Format.TGFLOATARGB, new ARGB32toTGFLOATARGB());
            
            addConvertor(Format.ARGB64, new ARGB64toBufferedImage());
            
            addConvertor(Format.TGDOUBLEARGB, Format.ARGB32, new TGDOUBLEARGBtoARGB32());
            addConvertor(Format.TGDOUBLEARGB, Format.TGDOUBLEGREY, new TGDOUBLEARGBtoTGDOUBLEGREY());
            
//            addConvertor(Format.TGDOUBLERGB, Format.ARGB32, new TGDOUBLERGBtoARGB32());
//            addConvertor(Format.TGDOUBLERGB, Format.TGDOUBLEGREY, new TGDOUBLERGBtoTGDOUBLEGREY());
            
            addConvertor(Format.TGDOUBLEGREY, Format.TGDOUBLEARGB, new TGDOUBLEGREYtoTGDOUBLEARGB());
//            addConvertor(Format.TGDOUBLEGREY, Format.TGDOUBLERGB, new TGDOUBLEGREYtoTGDOUBLERGB());
            addConvertor(Format.TGDOUBLEGREY, Format.GREY, new TGDOUBLEGREYtoGREY());
            
            addConvertor(Format.TGFLOATARGB, Format.ARGB32, new TGFLOATARGBtoARGB32());
            addConvertor(Format.TGFLOATARGB, Format.TGFLOATGREY, new TGFLOATARGBtoTGFLOATGREY());
            addConvertor(Format.TGFLOATGREY, Format.TGFLOATARGB, new TGFLOATGREYtoTGFLOATARGB());
            addConvertor(Format.TGFLOATGREY, Format.GREY, new TGFLOATGREYtoGREY());
            
            addConvertor(Format.GREY, Format.ARGB32, new GREYtoARGB32());

            addConvertor(Format.JPG, Format.ARGB32, new JPGtoARGB32());
            addConvertor(Format.JPG, Format.RGB24, new JPGtoRGB24());
            
            addConvertor(Format.MJPG, Format.ARGB32, new MJPGtoARGB32());
            addConvertor(Format.MJPG, Format.JPG, new MJPGtoJPG());
            addConvertor(Format.MJPG, Format.RGB24, new MJPGtoRGB24());
            
            addConvertor(Format.RGB24, Format.ARGB32, new RGB24toARGB32());
            addConvertor(Format.RGB24, new RGB24toBufferedImage());
            addConvertor(Format.RGB24, Format.JPG, new RGB24toJPG());
            
            addConvertor(Format.BGR24, Format.ARGB32, new BGR24toARGB32());
            addConvertor(Format.BGR24, Format.RGB24, new BGR24toRGB24());
            
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
