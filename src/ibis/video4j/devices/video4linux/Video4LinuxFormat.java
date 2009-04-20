package ibis.video4j.devices.video4linux;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import ibis.imaging4j.Format;

public class Video4LinuxFormat { 

    private static HashMap<Format, Integer> mappingV4L1;
    private static HashMap<Format, Integer> mappingV4L2;
    
    static { 
        mappingV4L1 = new HashMap<Format, Integer>(); 
    
        mappingV4L1.put(Format.GREY, 1);
        // mappingV4L1.put(Format.HI240, 2);
        // mappingV4L1.put(Format.RGB565, 3);
        mappingV4L1.put(Format.RGB24, 4);
        mappingV4L1.put(Format.ARGB32, 5);
        mappingV4L1.put(Format.RGB555, 6);
        mappingV4L1.put(Format.YUV422, 7);
        mappingV4L1.put(Format.YUYV, 8);
        //mappingV4L1.put(Format.UYUV, 9);
        mappingV4L1.put(Format.YUV420, 10);
        mappingV4L1.put(Format.YUV411, 11);
        mappingV4L1.put(Format.RAW, 12);        
        mappingV4L1.put(Format.YUV422P, 13);
        mappingV4L1.put(Format.YUV411P, 14);
        mappingV4L1.put(Format.YUV420P, 15);
        mappingV4L1.put(Format.YUV410P, 16);
        
        
        mappingV4L2 = new HashMap<Format, Integer>(); 
        
        //mappingV4L2.put(Format.RGB332, V4L2PaletteStringToNumber("RGB1"));
        mappingV4L2.put(Format.RGB555, V4L2PaletteStringToNumber("RGB0"));
        mappingV4L2.put(Format.RGB565, V4L2PaletteStringToNumber("RGBP"));
        //mappingV4L2.put(Format.RGB555X, V4L2PaletteStringToNumber("RGBQ"));
        //mappingV4L2.put(Format.RGB565X, V4L2PaletteStringToNumber("RGBR"));
        //mappingV4L2.put(Format.BGR24, V4L2PaletteStringToNumber("BGR3"));
        mappingV4L2.put(Format.RGB24, V4L2PaletteStringToNumber("RGB3"));
        //mappingV4L2.put(Format.BGR32, V4L2PaletteStringToNumber("BGR4"));
        //mappingV4L2.put(Format.RGB32, V4L2PaletteStringToNumber("RGB4"));
        mappingV4L2.put(Format.GREY, V4L2PaletteStringToNumber("GREY"));
        //mappingV4L2.put(Format.YVU410, V4L2PaletteStringToNumber("YVU9"));
        //mappingV4L2.put(Format.YVU420, V4L2PaletteStringToNumber("YV12"));
        mappingV4L2.put(Format.YUYV, V4L2PaletteStringToNumber("YUYV"));
        mappingV4L2.put(Format.UYVY, V4L2PaletteStringToNumber("UYVY"));
        mappingV4L2.put(Format.YUV422P, V4L2PaletteStringToNumber("422P"));
        mappingV4L2.put(Format.YUV411P, V4L2PaletteStringToNumber("411P"));
        //mappingV4L2.put(Format.Y41P, V4L2PaletteStringToNumber("Y41P"));
        //mappingV4L2.put(Format.NV12, V4L2PaletteStringToNumber("NV12"));
        //mappingV4L2.put(Format.NV21, V4L2PaletteStringToNumber("NV21"));
        
        //mappingV4L2.put(Format.YUV410, V4L2PaletteStringToNumber("YUV9"));
        mappingV4L2.put(Format.YUV420, V4L2PaletteStringToNumber("YU12"));
        //mappingV4L2.put(Format.YYUV, V4L2PaletteStringToNumber("YYUV"));
        //mappingV4L2.put(Format.HI240, V4L2PaletteStringToNumber("HI24"));
        //mappingV4L2.put(Format.HM12, V4L2PaletteStringToNumber("HM12"));
        //mappingV4L2.put(Format.RGB444, V4L2PaletteStringToNumber("R444"));
        //mappingV4L2.put(Format.SBGGR8, V4L2PaletteStringToNumber("BA81"));
        mappingV4L2.put(Format.MJPG, V4L2PaletteStringToNumber("MJPG"));
        mappingV4L2.put(Format.JPG, V4L2PaletteStringToNumber("JPEG"));
        //mappingV4L2.put(Format.DV, V4L2PaletteStringToNumber("dvsd"));
        mappingV4L2.put(Format.MPEG, V4L2PaletteStringToNumber("MPEG"));
        //mappingV4L2.put(Format.WNVA, V4L2PaletteStringToNumber("WNVA"));
        //mappingV4L2.put(Format.SN9C10X, V4L2PaletteStringToNumber("S910"));
        //mappingV4L2.put(Format.PWC1, V4L2PaletteStringToNumber("PWC1"));
        //mappingV4L2.put(Format.PWC2, V4L2PaletteStringToNumber("PWC2"));
        //mappingV4L2.put(Format.ET61X251, V4L2PaletteStringToNumber("E625"));
    }  

    private static int V4L2PaletteStringToNumber(String name) { 

        if (name == null || name.length() != 4) { 
            return -1;
        }

        char c1 = name.charAt(0);
        char c2 = name.charAt(1);
        char c3 = name.charAt(2);
        char c4 = name.charAt(3);

        return (c1 & 0xFF) | ((c2 & 0xFF) << 8) | ((c3 & 0xFF) << 16) | ((c4 & 0xFF) << 24); 
    }

    public static int getNativeIndexV4L1(Format format) { 
        
        Integer result = mappingV4L1.get(format);
        
        if (result == null) { 
            return -1;
        }
        
        return result.intValue();        
    }

    public static int getNativeIndexV4L2(Format format) { 
        
        Integer result = mappingV4L2.get(format);
        
        if (result == null) { 
            return -1;
        }
        
        return result.intValue();        
    }
    
    public static Format getFormat(int nativeIndex) { 

        Set<Entry<Format, Integer>> tmp = mappingV4L2.entrySet();
        
        for (Entry<Format, Integer> e : tmp) { 
            if (e.getValue().intValue() == nativeIndex) { 
                return e.getKey();
            }
        }
    
        tmp = mappingV4L1.entrySet();
            
        for (Entry<Format, Integer> e : tmp) { 
            if (e.getValue().intValue() == nativeIndex) { 
                return e.getKey();
            }
        }
        
        return null;
    }
    
/*    
    // Old video4linux palette
    public static final Video4LinuxPalette V4L1_GREY = new Video4LinuxPalette("V4L1_GREY", 8, "Linear greyscale", 1);         
    public static final Video4LinuxPalette V4L1_HI240 = new Video4LinuxPalette("V4L1_HI240", 8, "High 240 cube", 2);
    public static final Video4LinuxPalette V4L1_RGB565 = new Video4LinuxPalette("V4L1_RGB565", 16, "16 bit RGB", 3); 
    public static final Video4LinuxPalette V4L1_RGB24 = new Video4LinuxPalette("V4L1_RGB24", 24, "24 bit RGB", 4); 
    public static final Video4LinuxPalette V4L1_RGB32 = new Video4LinuxPalette("V4L1_RGB32", 32, "32 bit RGB", 5);  
    public static final Video4LinuxPalette V4L1_RGB555 = new Video4LinuxPalette("V4L1_RGB555", 15, "15 bit RGB", 6); 
    public static final Video4LinuxPalette V4L1_YUV422 = new Video4LinuxPalette("V4L1_YUV422", 16, "16 bit YUV422", 7); 
    public static final Video4LinuxPalette V4L1_YUYV = new Video4LinuxPalette("V4L1_YUYV", 16, "16 bit YUYV", 8); 
    public static final Video4LinuxPalette V4L1_UYVY = new Video4LinuxPalette("V4L1_UYVY", 16, "16 bit UYVY", 9); 
    public static final Video4LinuxPalette V4L1_YUV420 = new Video4LinuxPalette("V4L1_YUV420", 12, "12 bit YUV420", 10);
    public static final Video4LinuxPalette V4L1_YUV411 = new Video4LinuxPalette("V4L1_YUV411", 12, "12 bit YUV411", 11); 
    public static final Video4LinuxPalette V4L1_RAW = new Video4LinuxPalette("V4L1_RAW", 8, "8 bit RAW", 12); 
    public static final Video4LinuxPalette V4L1_YUV422P = new Video4LinuxPalette("V4L1_YUV422P", 16, "16 bit YUV422P", 13);                 
    public static final Video4LinuxPalette V4L1_YUV411P = new Video4LinuxPalette("V4L1_YUV411P", 12, "12 bit YUV411P", 14); 
    public static final Video4LinuxPalette V4L1_YUV420P = new Video4LinuxPalette("V4L1_YUV420P", 12, "12 bit YUV420P", 15); 
    public static final Video4LinuxPalette V4L1_YUV410P = new Video4LinuxPalette("V4L1_YUV410P", 10, "10 bit YUV410P", 16); 

    // New video4linux2 palette
    public static final Video4LinuxPalette V4L2_RGB332 = new Video4LinuxPalette("RGB1", 8, "RGB-3-3-2"); 
    public static final Video4LinuxPalette V4L2_RGB555 = new Video4LinuxPalette("RGB0", 16, "RGB-5-5-5");
    public static final Video4LinuxPalette V4L2_RGB565 = new Video4LinuxPalette("RGBP", 16, "RGB-5-6-5"); 
    public static final Video4LinuxPalette V4L2_RGB555X = new Video4LinuxPalette("RGBQ", 16 ,"RGB-5-5-5 BE"); 
    public static final Video4LinuxPalette V4L2_RGB565X = new Video4LinuxPalette("RGBR", 16, "RGB-5-6-5 BE"); 
    public static final Video4LinuxPalette V4L2_BGR24 = new Video4LinuxPalette("BGR3", 24, "BGR-8-8-8"); 
    public static final Video4LinuxPalette V4L2_RGB24 = new Video4LinuxPalette("RGB3", 24, "RGB-8-8-8"); 
    public static final Video4LinuxPalette V4L2_BGR32 = new Video4LinuxPalette("BGR4", 32, "BGR-8-8-8-8"); 
    public static final Video4LinuxPalette V4L2_RGB32 = new Video4LinuxPalette("RGB4", 32, "RGB-8-8-8-8"); 
    public static final Video4LinuxPalette V4L2_GREY = new Video4LinuxPalette("GREY", 8, "Greyscale"); 
    public static final Video4LinuxPalette V4L2_YVU410 = new Video4LinuxPalette("YVU9", 9, "YVU 4:1:0"); 
    public static final Video4LinuxPalette V4L2_YVU420 = new Video4LinuxPalette("YV12", 12, "YVU 4:2:0"); 
    public static final Video4LinuxPalette V4L2_YUYV = new Video4LinuxPalette("YUYV", 16, "YUV 4:2:2");     
    public static final Video4LinuxPalette V4L2_UYVY = new Video4LinuxPalette("UYVY", 16, "YUV 4:2:2");      
    public static final Video4LinuxPalette V4L2_YUV422P = new Video4LinuxPalette("422P", 16, "YVU422 planar");
    public static final Video4LinuxPalette V4L2_YUV411P = new Video4LinuxPalette("411P", 16, "YVU411 planar");
    public static final Video4LinuxPalette V4L2_Y41P = new Video4LinuxPalette("Y41P", 12, "YUV 4:1:1");

    // two planes -- one Y, one Cr + Cb interleaved 
    public static final Video4LinuxPalette V4L2_NV12 = new Video4LinuxPalette("NV12", 12, "Y/CbCr 4:2:0");
    public static final Video4LinuxPalette V4L2_NV21 = new Video4LinuxPalette("NV21", 12, "Y/CrCb 4:2:0");

    // The following formats are not defined in the V4L2 specification 
    public static final Video4LinuxPalette V4L2_YUV410 = new Video4LinuxPalette("YUV9", 9, "YUV 4:1:0"); 
    public static final Video4LinuxPalette V4L2_YUV420 = new Video4LinuxPalette("YU12", 12, "YUV 4:2:0"); 
    public static final Video4LinuxPalette V4L2_YYUV = new Video4LinuxPalette("YYUV", 16, "YUV 4:2:2"); 
    public static final Video4LinuxPalette V4L2_HI240 = new Video4LinuxPalette("HI24", 8, "8-bit color"); 
    public static final Video4LinuxPalette V4L2_HM12 = new Video4LinuxPalette("HM12", 8, "YUV 4:2:0 16x16 macroblocks"); 
    public static final Video4LinuxPalette V4L2_RGB444 = new Video4LinuxPalette("R444", 16, "xxxxrrrr ggggbbbb");

    // see http://www.siliconimaging.com/RGB%20Bayer.htm 
    public static final Video4LinuxPalette V4L2_SBGGR8 = new Video4LinuxPalette("BA81", 8, "BGBG.. GRGR.."); 

    // compressed formats 
    public static final Video4LinuxPalette V4L2_MJPEG = new Video4LinuxPalette("MJPG", 0, "Motion-JPEG", true); 
    public static final Video4LinuxPalette V4L2_JPEG = new Video4LinuxPalette("JPEG", 0, "JFIF JPEG", true); 
    public static final Video4LinuxPalette V4L2_DV = new Video4LinuxPalette("dvsd", 0, "1394", true); 
    public static final Video4LinuxPalette V4L2_MPEG = new Video4LinuxPalette("MPEG", 0, "MPEG-1/2/4", true);

    // Vendor-specific formats 
    public static final Video4LinuxPalette V4L2_WNVA = new Video4LinuxPalette("WNVA", 0, "Winnov hw compress", true); 
    public static final Video4LinuxPalette V4L2_SN9C10X = new Video4LinuxPalette("S910", 0, "SN9C10x compression", true); 
    public static final Video4LinuxPalette V4L2_PWC1 = new Video4LinuxPalette("PWC1", 0, "pwc older webcam", true); 
    public static final Video4LinuxPalette V4L2_PWC2 = new Video4LinuxPalette("PWC2", 0, "pwc newer webcam", true); 
    public static final Video4LinuxPalette V4L2_ET61X251 = new Video4LinuxPalette("E625", 0, "ET61X251 compression", true);

    
    public static final int V4L1_START_PALETTE = 1;
    public static final int V4L1_END_PALETTE = 16;

    public static final int V4L2_START_PALETTE = 17;
    public static final int V4L2_END_PALETTE = 51;

    public static final Video4LinuxPalette [] values = { 
         V4L1_GREY,         
         V4L1_HI240,
         V4L1_RGB565, 
         V4L1_RGB24, 
         V4L1_RGB32,  
         V4L1_RGB555, 
         V4L1_YUV422, 
         V4L1_YUYV, 
         V4L1_UYVY, 
         V4L1_YUV420,
         V4L1_YUV411, 
         V4L1_RAW, 
         V4L1_YUV422P,                 
         V4L1_YUV411P, 
         V4L1_YUV420P, 
         V4L1_YUV410P, 
         V4L2_RGB332, 
         V4L2_RGB555,
         V4L2_RGB565, 
         V4L2_RGB555X, 
         V4L2_RGB565X, 
         V4L2_BGR24, 
         V4L2_RGB24, 
         V4L2_BGR32, 
         V4L2_RGB32, 
         V4L2_GREY,  
         V4L2_YVU410, 
         V4L2_YVU420,
         V4L2_YUYV,     
         V4L2_UYVY,      
         V4L2_YUV422P,
         V4L2_YUV411P,
         V4L2_Y41P,
         V4L2_NV12,
         V4L2_NV21,
         V4L2_YUV410, 
         V4L2_YUV420, 
         V4L2_YYUV, 
         V4L2_HI240, 
         V4L2_HM12, 
         V4L2_RGB444,
         V4L2_SBGGR8, 
         V4L2_MJPEG,  
         V4L2_JPEG, 
         V4L2_DV, 
         V4L2_MPEG,
         V4L2_WNVA, 
         V4L2_SN9C10X, 
         V4L2_PWC1, 
         V4L2_PWC2, 
         V4L2_ET61X251,
    };
    
    public final int number;
    public final String code;
  
    protected Video4LinuxPalette(String name, int bpp, String description, 
            int number) {
        
        this(name, bpp, description, number, false);
    } 
    
    protected Video4LinuxPalette(String name, int bpp, String description, 
            int number, boolean compressed) {
        
        super(name, bpp, description, compressed);
        this.number = number;
        this.code = null;
    }

    protected Video4LinuxPalette(String code, int bpp, String description) {
        this(code, bpp, description, false);
    }
    
    protected Video4LinuxPalette(String code, int bpp, String description, 
            boolean compressed) {
        
        super(code, bpp, description, compressed);
        
        this.number = V4L2PaletteStringToNumber(code);
        this.code = code;
    }

    protected String V4L2PaletteNumberToString(int palette) { 

        StringBuilder tmp = new StringBuilder();

        tmp.append((char) (palette       & 0xFF));
        tmp.append((char) (palette >> 8  & 0xFF));
        tmp.append((char) (palette >> 16 & 0xFF));
        tmp.append((char) (palette >> 24 & 0xFF));

        return tmp.toString(); 
    }
*/
    
    /*
    
    public int getNativeIndex() { 
        return number;
    }

    public String getNativeCode() { 
        return code;
    }
    
    public static Video4LinuxPalette getPalette(int nativeIndex) { 

        for (int i=0;i<values.length;i++) { 
            if (values[i].number == nativeIndex) { 
                return values[i];
            }
        }

        return null;
    }

    /*
     * public static NativePalette getPalette(String name) {
     * 
     * try { return NativePalette.valueOf(name); } catch (Exception e) { //
     * ignore }
     * 
     * try { return NativePalette.valueOf("V4L2_PIX_FMT_" + name); } catch
     * (Exception e) { // ignore }
     * 
     * try { return NativePalette.valueOf("VIDEO_PALETTE_" + name); } catch
     * (Exception e) { // ignore }
     * 
     * return null; }
     */


}