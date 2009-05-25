package ibis.imaging4j.conversion;

import java.nio.ByteBuffer;

import ibis.imaging4j.Format;
import ibis.imaging4j.Image;

public class GREYtoARGB32 extends Convertor {

    private final static int COST = 3 + 1;
    
    public GREYtoARGB32() {
        super(COST);
    }

    @Override
    public Image convert(Image in, Image out) throws ConversionException {
        
        if (in.getFormat() != Format.GREY) {
            throw new ConversionException("input image not in GREY format");
        }
        
        if (out == null) {
            out = new Image(Format.ARGB32, in.getWidth(), in.getHeight());
        }
        
        if (out.getFormat() != Format.ARGB32) {
            throw new ConversionException("output image not in ARGB32 format");
        }
        
        if (out.getWidth() != in.getWidth() || 
                out.getHeight() != in.getHeight()) { 
            throw new ConversionException("Target image has wrong dimensions!");
        }
        
        ByteBuffer dataIn = in.getData().duplicate();
        ByteBuffer dataOut = out.getData().duplicate();
        
        dataIn.clear();
        dataOut.clear();
        
        while(dataIn.hasRemaining()) {
            byte value = dataIn.get();
            
            dataOut.put((byte) 0xFF);
            dataOut.put(value);
            dataOut.put(value);
            dataOut.put(value);
        }
        
        return out;
    }
}
