package ibis.imaging4j.conversion;

import java.nio.ByteBuffer;

import javax.imageio.IIOException;

import ibis.imaging4j.Format;
import ibis.imaging4j.Image;
import ibis.imaging4j.decompression.JPEGImageDecompressor;
import ibis.imaging4j.decompression.MJPEGDecompressor;

public class MJPGtoARGB32 extends Convertor {

    private final static int COST = 1000;
    
    private final MJPEGDecompressor dec;
    
    public MJPGtoARGB32() throws IIOException {
        super(COST);
        
        dec = new MJPEGDecompressor();
    }

    @Override
    public Image convert(Image in, Image out) throws ConversionException {
       
        if (out == null) {
            out = new Image(Format.ARGB32, in.getWidth(), in.getHeight());
        } else { 
            if (out.getWidth() != in.getWidth() || 
                    out.getHeight() != in.getHeight()) { 
                throw new ConversionException("Target image has wrong dimensions!");
            }
        }
   
        try { 
   
            ByteBuffer buf = in.getData();
            byte [] tmp = new byte[buf.limit()];
            buf.get(tmp);
            
            dec.RTjpeg_init_decompress(tmp, in.getWidth(), in.getHeight());
            dec.RTjpeg_decompress(tmp, out.getData().array());
            
            return out;
            
        } catch (Exception e) {
            e.printStackTrace();
            throw new ConversionException("Failed to decode MJPEG", e);
            
        }
    }
}