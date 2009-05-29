package ibis.imaging4j;

import java.io.Serializable;

public enum Format implements Serializable {

    GREY("GREY", 8, "Linear greyscale"),
    
    // Several straight forward RGB type formats
    RGB555("RGB555", 15, "15 bit RGB"),
    RGB565("RGB565", 16, "16 bit RGB"), 
    RGB24("RGB24", 24, "24 bit RGB"),
    RGB48("RGB48", 48, "48 bit RGB"),
    ARGB32("ARGB32", 32, "32 bit ARGB"),  
    ARGB64("ARGB64", 64, "64 bit ARGB"),
    
    BGR24("BGR24", 24, "24 bit BGR"),
        
    // Several Y/Cr/Cb based formats
    YUYV("YUYV", 16, "16 bit YUYV"),
    UYVY("UYVY", 16, "16 bit UYVY"), 
    
    YUV422("YUV422", 16, "16 bit YUV422"), 
    YUV420("YUV420", 12, "12 bit YUV420"),
    YUV411("YUV411", 12, "12 bit YUV411"), 
    
    YUV422P("YUV422P", 16, "16 bit YUV422P"),                 
    YUV411P("YUV411P", 12, "12 bit YUV411P"), 
    YUV420P("YUV420P", 12, "12 bit YUV420P"), 
    YUV410P("YUV410P", 10, "10 bit YUV410P"), 

    YUV420SP("YUV420SP", 12, "12 bit YUV420SP"), 
    YUV422SP("YUV422SP", 16, "16 bit YUV422SP"), 
        
    // Several compressed formats
    JPG("JPG", 0, "JPG Compressed", true), 
    MJPG("MJPG", 0, "Motion JPG Compressed", true),
    MPEG("MPEG", 0, "MPEG Compressed", true),
    TIFF("TIFF", 0, "TIFF Compressed", true),
    PNG("PNG", 0, "PNG Compressed", true),
    
    // RAW format. This allows you to directly access the bytes produced 
    // by a camera. The exact format is unspecified  
    RAW("RAW", 0, "RAW"), 

    // NONE format. It is sometime convenient to throw away all image data 
    // except for the meta information. Use this format to indicate that the 
    // image doe not contain any real pixels. 
    NONE("NONE", 0, "NONE");
    
    private final int bpp;
    private final String name;
    private final String description;
    private final boolean compressed;

    Format(String name, int bpp, String description) {
        this(name, bpp, description, false);
    }

    Format(String name, int bpp, String description, boolean compressed) {
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

    public boolean isCompressed() {
        return compressed;
    }   
}
