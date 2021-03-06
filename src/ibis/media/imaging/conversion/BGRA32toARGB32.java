package ibis.media.imaging.conversion;

import java.nio.ByteBuffer;

import ibis.media.imaging.Format;
import ibis.media.imaging.Image;

public class BGRA32toARGB32 extends Convertor {

    private final static int COST = 4 + 1;
    
    public BGRA32toARGB32() {
        super(COST);
    }

    @Override
    public Image convert(Image in, Image out) throws ConversionException {

        if (in.getFormat() != Format.BGRA32) {
            throw new ConversionException("input image not in BGRA32 format");
        }

        if (out == null) {
            out = new Image(Format.ARGB32, in.getWidth(), in.getHeight());
        }

        if (out.getFormat() != Format.ARGB32) {
            throw new ConversionException("output image not in ARGB32 format");
        }

        if (out.getWidth() != in.getWidth()
                || out.getHeight() != in.getHeight()) {
            throw new ConversionException("Target image has wrong dimensions!");
        }

        ByteBuffer dataIn = in.getData().duplicate();
        ByteBuffer dataOut = out.getData().duplicate();

        dataIn.clear();
        dataOut.clear();
        
        byte b;
        byte g;
        byte r;
        byte a;
        
        while (dataIn.hasRemaining()) {
        	b = dataIn.get();
        	g = dataIn.get();
        	r = dataIn.get();
        	a = dataIn.get();

        	dataOut.put(a);
        	dataOut.put(r);
        	dataOut.put(g);
        	dataOut.put(b);
        }

        return out;

    }
}
