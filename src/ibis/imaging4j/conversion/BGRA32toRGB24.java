package ibis.imaging4j.conversion;

import java.nio.ByteBuffer;

import ibis.imaging4j.Format;
import ibis.imaging4j.Image;

public class BGRA32toRGB24 extends Convertor {

    private final static int COST = 3 + 1;
    
    public BGRA32toRGB24() {
        super(COST);
    }

    @Override
    public Image convert(Image in, Image out) throws ConversionException {

        if (in.getFormat() != Format.BGRA32) {
            throw new ConversionException("input image not in BGR24 format");
        }

        if (out == null) {
            out = new Image(Format.RGB24, in.getWidth(), in.getHeight());
        }

        if (out.getFormat() != Format.RGB24) {
            throw new ConversionException("output image not in RGB24 format");
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
            
            dataOut.put(r);
            dataOut.put(g);
            dataOut.put(b);
                    }

        return out;

    }
}
