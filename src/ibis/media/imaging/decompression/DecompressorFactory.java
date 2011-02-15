package ibis.media.imaging.decompression;

import ibis.media.imaging.Format;

import java.util.HashMap;

public class DecompressorFactory {

    @SuppressWarnings("unused")
    private final HashMap<String, String> options;
    
    public DecompressorFactory(HashMap<String, String> options) { 
        this.options = new HashMap<String, String>(options);
    }
    
    public ImageDecompressor create(Format format) throws Exception { 
        
        if (format == Format.JPG) { 
            return new JPEGImageDecompressor();
        } 
        
        throw new Exception("Unknown compressor " + format);        
    }
}
