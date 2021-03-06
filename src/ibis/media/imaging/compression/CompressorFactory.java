package ibis.media.imaging.compression;

import ibis.media.imaging.Format;

import java.util.HashMap;

public class CompressorFactory {
    
    public static ImageCompressor create(Format format, HashMap<String, String> options) throws Exception { 
        
        if (format == Format.JPG) { 
            return new JPEGCompressor();
        }
       // } else if (type.equals("TIF")) { 
       //     return new TIFCompressor();
       // } else if (type.equals("NULL")) {
       //     return new NullCompressor();
       // }
        
        throw new Exception("Unknown compressor " + format);        
    }

    
}
