package ibis.imaging4j;

public class Format {

    // This VideoPalette contains several formats which are regularly spotted 
    // in the wild. 
    
    // Several straight forward RGB type formats
    public static final Format GREY = new Format("GREY", 8, "Linear greyscale");
    public static final Format RGB555 = new Format("RGB555", 15, "15 bit RGB");
    public static final Format RGB565 = new Format("RGB565", 16, "16 bit RGB"); 
    public static final Format RGB24 = new Format("RGB24", 24, "24 bit RGB");
    public static final Format RGB48 = new Format("RGB48", 48, "48 bit RGB");
    public static final Format ARGB32 = new Format("ARGB32", 32, "32 bit ARGB");  
    public static final Format ARGB64 = new Format("ARGB64", 64, "64 bit ARGB");  
        
    // Several Y/Cr/Cb based formats
    public static final Format YUYV = new Format("YUYV", 16, "16 bit YUYV");
    public static final Format UYVY = new Format("UYVY", 16, "16 bit UYVY"); 
    
    public static final Format YUV422 = new Format("YUV422", 16, "16 bit YUV422"); 
    public static final Format YUV420 = new Format("YUV420", 12, "12 bit YUV420");
    public static final Format YUV411 = new Format("YUV411", 12, "12 bit YUV411"); 
    
    public static final Format YUV422P = new Format("YUV422P", 16, "16 bit YUV422P");                 
    public static final Format YUV411P = new Format("YUV411P", 12, "12 bit YUV411P"); 
    public static final Format YUV420P = new Format("YUV420P", 12, "12 bit YUV420P"); 
    public static final Format YUV410P = new Format("YUV410P", 10, "10 bit YUV410P"); 

    public static final Format YUV420SP = new Format("YUV420SP", 12, "12 bit YUV420SP"); 
    public static final Format YUV422SP = new Format("YUV422SP", 16, "16 bit YUV422SP"); 
        
    // Several compressed formats
    public static final Format JPG = new Format("JPG", 0, "JPG Compressed"); 
    public static final Format MJPG = new Format("MJPG", 0, "Motion JPG Compressed");
    public static final Format MPEG = new Format("MPEG", 0, "MPEG Compressed");
    public static final Format TIFF = new Format("TIFF", 0, "TIFF Compressed");
    public static final Format PNG = new Format("PNG", 0, "PNG Compressed");
    
    // RAW format. This allows you to directly access the byets produced 
    // by a camera. The exact format is unspecified  
    public static final Format RAW = new Format("RAW", 0, "RAW"); 

    // NONE format. It is sometime convenient to throw away all image data 
    // except for the meta information. Use this format to indicate that the 
    // image doe not contain any real pixels. 
    public static final Format NONE = new Format("NONE", 0, "NONE"); 
    
    public final int bpp;
    public final String name;
    public final String description;
    public final boolean compressed; 
    
    protected Format(String name, int bpp, String description) {
        this(name, bpp, description, false);
    } 
    
    protected Format(String name, int bpp, String description, 
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
    
    public long bytesRequired(int width, int height) {
        return (long) Math.ceil((width * height * bpp) / 8.0); 
    }   
}
