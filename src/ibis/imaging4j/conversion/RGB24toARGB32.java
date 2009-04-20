package ibis.imaging4j.conversion;

import ibis.imaging4j.Format;
import ibis.imaging4j.Image;
import ibis.imaging4j.conversion.util.LowLevelConvert;

public class RGB24toARGB32 extends Convertor {

    private final static int COST = 3 + 1;
    
    public RGB24toARGB32() {
        super(COST);
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
    
        LowLevelConvert.RGB24toARGB32(in.getData(), out.getData());
        
        return out;
    }
}