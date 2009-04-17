package ibis.video4j;

public class VideoPalette {

    // This VideoPalette contains several formats which are regularly spotted 
    // in the wild. 
    
    // Several straight forward RGB type formats
    public static final VideoPalette GREY = new VideoPalette("GREY", 8, "Linear greyscale");
    public static final VideoPalette RGB555 = new VideoPalette("RGB555", 15, "15 bit RGB");
    public static final VideoPalette RGB565 = new VideoPalette("RGB565", 16, "16 bit RGB"); 
    public static final VideoPalette RGB24 = new VideoPalette("RGB24", 24, "24 bit RGB");
    public static final VideoPalette RGB48 = new VideoPalette("RGB48", 48, "48 bit RGB");
    public static final VideoPalette ARGB32 = new VideoPalette("ARGB32", 32, "32 bit ARGB");  
    public static final VideoPalette ARGB64 = new VideoPalette("ARGB64", 64, "64 bit ARGB");  
        
    // Several Y/Cr/Cb based formats
    public static final VideoPalette YUYV = new VideoPalette("YUYV", 16, "16 bit YUYV");
    public static final VideoPalette UYVY = new VideoPalette("UYVY", 16, "16 bit UYVY"); 
    
    public static final VideoPalette YUV422 = new VideoPalette("YUV422", 16, "16 bit YUV422"); 
    public static final VideoPalette YUV420 = new VideoPalette("YUV420", 12, "12 bit YUV420");
    public static final VideoPalette YUV411 = new VideoPalette("YUV411", 12, "12 bit YUV411"); 
    
    public static final VideoPalette YUV422P = new VideoPalette("YUV422P", 16, "16 bit YUV422P");                 
    public static final VideoPalette YUV411P = new VideoPalette("YUV411P", 12, "12 bit YUV411P"); 
    public static final VideoPalette YUV420P = new VideoPalette("YUV420P", 12, "12 bit YUV420P"); 
    public static final VideoPalette YUV410P = new VideoPalette("YUV410P", 10, "10 bit YUV410P"); 

    // Several compressed formats
    public static final VideoPalette JPG = new VideoPalette("JPG", 0, "JPG Compressed"); 
    public static final VideoPalette MJPG = new VideoPalette("MJPG", 0, "Motion JPG Compressed");
    public static final VideoPalette MPEG = new VideoPalette("MPEG", 0, "MPEG Compressed");

    // RAW format. This allows you to directly retrieve the data produced 
    // by the camera  
    public static final VideoPalette RAW = new VideoPalette("RAW", 0, "RAW"); 

    public final int bpp;
    public final String name;
    public final String description;
    public final boolean compressed; 
    
    protected VideoPalette(String name, int bpp, String description) {
        this(name, bpp, description, false);
    } 
    
    protected VideoPalette(String name, int bpp, String description, 
            boolean compressed) {
        
            this.name = name;
            this.bpp = bpp;
            this.description = description;
            this.compressed = compressed;
    }

    public String getName() { 
        return name;
    }
    
    public int getBitsPerPixel() { 
        return bpp;
    }
    
    public String getDescription() { 
        return description;
    }
    
    public String toString() { 
        return name;
    }    
}
