package ibis.imaging4j.conversion;

import ibis.imaging4j.Format;
import ibis.imaging4j.Image;
import ibis.imaging4j.conversion.util.LowLevelConvert;

public final class YUYVtoARGB32 extends Convertor {

    private final static int COST = 4 + (6*4) + 2;
    
    public YUYVtoARGB32() {
        super(COST);
    }

    @Override
    public Image convert(Image in, Image out) throws ConversionException { 
        
        if (out == null) {
            out = new Image(Format.ARGB32, in.getWidth(), in.getHeight());
        } else { 
            if (out.getWidth() != in.getWidth() || 
                    out.getHeight() != in.getHeight()) { 
                throw new ConversionException("Target image has wrong " +
                                "dimensions! (" + in.getWidth() + "x" + 
                                in.getHeight() + ") != (" + out.getWidth() + 
                                "x" + out.getHeight() + ")");
            }
        }
        
   //     long start = System.currentTimeMillis();
        
        LowLevelConvert.YUYVtoARGB32Table(in.getWidth(), in.getHeight(), 
                in.getData(), out.getData());
     
   //     long time = System.currentTimeMillis() - start;
        
   //     System.out.println("Conversion took " + time + " ms");
        return out;
    }
}
