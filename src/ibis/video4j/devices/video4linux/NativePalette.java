package ibis.video4j.devices.video4linux;

public enum NativePalette { 

    // Old video4linux palette
    VIDEO_PALETTE_GREY(1, 8, "Linear greyscale"),         
    VIDEO_PALETTE_HI240(2, 8, "High 240 cube"),
    VIDEO_PALETTE_RGB565(3, 16, "16 bit RGB"), 
    VIDEO_PALETTE_RGB24(4, 24, "24 bit RGB"), 
    VIDEO_PALETTE_RGB32(5, 32, "32 bit RGB"),  
    VIDEO_PALETTE_RGB555(6, 15, "15 bit RGB"), 
    VIDEO_PALETTE_YUV422(7, 16, "16 bit YUV422"), 
    VIDEO_PALETTE_YUYV(8, 16, "16 bit YUYV"), 
    VIDEO_PALETTE_UYVY(9, 16, "16 bit UYVY"), 
    VIDEO_PALETTE_YUV420(10, 12, "12 bit YUV420"),
    VIDEO_PALETTE_YUV411(11, 12, "12 bit YUV411"), 
    VIDEO_PALETTE_RAW(12, 8, "8 bit RAW"), 
    VIDEO_PALETTE_YUV422P(13, 16, "16 bit YUV422P"),                 
    VIDEO_PALETTE_YUV411P(14, 12, "12 bit YUV411P"), 
    VIDEO_PALETTE_YUV420P(15, 12, "12 bit YUV420P"), 
    VIDEO_PALETTE_YUV410P(16, 10, "10 bit YUV410P"), 
      
    // New video4linux2 palette
    V4L2_PIX_FMT_RGB332("RGB1", 8, "RGB-3-3-2"), 
    V4L2_PIX_FMT_RGB555("RGB0", 16, "RGB-5-5-5"),
    V4L2_PIX_FMT_RGB565("RGBP", 16, "RGB-5-6-5"), 
    V4L2_PIX_FMT_RGB555X("RGBQ", 16 ,"RGB-5-5-5 BE"), 
    V4L2_PIX_FMT_RGB565X("RGBR", 16, "RGB-5-6-5 BE"), 
    V4L2_PIX_FMT_BGR24("BGR3", 24, "BGR-8-8-8"), 
    V4L2_PIX_FMT_RGB24("RGB3", 24, "RGB-8-8-8"), 
    V4L2_PIX_FMT_BGR32("BGR4", 32, "BGR-8-8-8-8"), 
    V4L2_PIX_FMT_RGB32("RGB4", 32, "RGB-8-8-8-8"), 
    V4L2_PIX_FMT_GREY("GREY", 8, "Greyscale"), 
    V4L2_PIX_FMT_YVU410("YVU9", 9, "YVU 4:1:0"), 
    V4L2_PIX_FMT_YVU420("YV12", 12, "YVU 4:2:0"), 
    V4L2_PIX_FMT_YUYV("YUYV", 16, "YUV 4:2:2"),     
    V4L2_PIX_FMT_UYVY("UYVY", 16, "YUV 4:2:2"),      
    V4L2_PIX_FMT_YUV422P("422P", 16, "YVU422 planar"),
    V4L2_PIX_FMT_YUV411P("411P", 16, "YVU411 planar"),
    V4L2_PIX_FMT_Y41P("Y41P", 12, "YUV 4:1:1"),

    /* two planes -- one Y, one Cr + Cb interleaved  */
    V4L2_PIX_FMT_NV12("NV12", 12, "Y/CbCr 4:2:0"),
    V4L2_PIX_FMT_NV21("NV21", 12, "Y/CrCb 4:2:0"),

    /*  The following formats are not defined in the V4L2 specification */
    V4L2_PIX_FMT_YUV410("YUV9", 9, "YUV 4:1:0"), 
    V4L2_PIX_FMT_YUV420("YU12", 12, "YUV 4:2:0"), 
    V4L2_PIX_FMT_YYUV("YYUV", 16, "YUV 4:2:2"), 
    V4L2_PIX_FMT_HI240("HI24", 8, "8-bit color"), 
    V4L2_PIX_FMT_HM12("HM12", 8, "YUV 4:2:0 16x16 macroblocks"), 
    V4L2_PIX_FMT_RGB444("R444", 16, "xxxxrrrr ggggbbbb"),

    /* see http://www.siliconimaging.com/RGB%20Bayer.htm */
    V4L2_PIX_FMT_SBGGR8("BA81", 8, "BGBG.. GRGR.."), 

    /* compressed formats */
    V4L2_PIX_FMT_MJPEG("MJPG", 0, "Motion-JPEG"), 
    V4L2_PIX_FMT_JPEG("JPEG", 0, "JFIF JPEG"), 
    V4L2_PIX_FMT_DV("dvsd", 0, "1394"), 
    V4L2_PIX_FMT_MPEG("MPEG", 0, "MPEG-1/2/4"),

    /*  Vendor-specific formats   */
    V4L2_PIX_FMT_WNVA("WNVA", 0, "Winnov hw compress"), 
    V4L2_PIX_FMT_SN9C10X("S910", 0, "SN9C10x compression"), 
    V4L2_PIX_FMT_PWC1("PWC1", 0, "pwc older webcam"), 
    V4L2_PIX_FMT_PWC2("PWC2", 0, "pwc newer webcam"), 
    V4L2_PIX_FMT_ET61X251("E625", 0, "ET61X251 compression");
    
    public static final int V4L1_START_PALETTE = 1;
    public static final int V4L1_END_PALETTE = 16;
      
    public static final int V4L2_START_PALETTE = 17;
    public static final int V4L2_END_PALETTE = 51;
    
    private  int number;
    private  int bpp;
    private  String codeX;
    private  String description;

    private NativePalette(int number, int bpp, String description) {
        try { 
            this.number = number;
            this.codeX = null;
            this.bpp = bpp;
            this.description = description;
        } catch (Exception e) {
            System.out.println("EEP!");
            e.printStackTrace();
        }  
    }

    private NativePalette(String code, int bpp, String description) {
        try { 
            this.number = V4L2PaletteStringToNumber(code);
            this.codeX = code;
            this.bpp = bpp;
            this.description = description;
            
            System.out.println("Palette " + code + " -> " + number);
            
        } catch (Exception e) {
            System.out.println("EEP2!");
            e.printStackTrace();
        }  
    }

    String getCodeName() { 
        return codeX;
    }
    
    String V4L2PaletteNumberToString(int palette) { 
        
        StringBuilder tmp = new StringBuilder();
        
        tmp.append((char) (palette       & 0xFF));
        tmp.append((char) (palette >> 8  & 0xFF));
        tmp.append((char) (palette >> 16 & 0xFF));
        tmp.append((char) (palette >> 24 & 0xFF));
        
        return tmp.toString(); 
    }
    
    int V4L2PaletteStringToNumber(String name) { 
        
        if (name == null || name.length() != 4) { 
            return -1;
        }
        
        char c1 = name.charAt(0);
        char c2 = name.charAt(1);
        char c3 = name.charAt(2);
        char c4 = name.charAt(3);
        
        return (c1 & 0xFF) | ((c2 & 0xFF) << 8) | ((c3 & 0xFF) << 16) | ((c4 & 0xFF) << 24); 
    }
    
    public int getBitsPerPixel() { 
        return bpp;
    }
    
    public String getDescription() { 
        return description;
    }
    
    public int getNativeIndex() { 
        return number;
    }
    
    public static NativePalette getPalette(int nativeIndex) { 

        // TODO: Optimize this ?
        for (NativePalette p : NativePalette.values()) { 
            
            if (p.number == nativeIndex) { 
                return p;
            }
        }

        return null;
    }

    /*
    public static NativePalette getPalette(String name) { 

        try { 
            return NativePalette.valueOf(name);
        } catch (Exception e) {
            // ignore
        }
        
        try { 
            return NativePalette.valueOf("V4L2_PIX_FMT_" + name);
        } catch (Exception e) {
            // ignore
        }
        
        try { 
            return NativePalette.valueOf("VIDEO_PALETTE_" + name);
        } catch (Exception e) {
            // ignore
        }
   
        return null;
    }*/

    
}