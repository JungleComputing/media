package ibis.imaging4j.conversion;

import java.nio.ByteBuffer;

import ibis.imaging4j.Format;
import ibis.imaging4j.Image;

public class BGR24toARGB32 extends Convertor {

    private final static int COST = 3 + 1;
    
    public BGR24toARGB32() {
        super(COST);
    }

    @Override
    public Image convert(Image in, Image out) throws ConversionException {

        if (in.getFormat() != Format.BGR24) {
            throw new ConversionException("input image not in RGB24 format");
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

        byte[] argb = new byte[4];
        
        //set a to max (opaque)
        argb[0] = (byte) 0xFF;
        while (dataIn.hasRemaining()) {
            //get bgr value (skip first "a" element in array)
            dataIn.get(argb, 1, 3);
            
            //swap b and r to get to argb instead of abgr
            byte tmp = argb[1];
            argb[1] = argb[3];
            argb[3] = tmp;

            //put argb
            dataOut.put(argb);
        }

        return out;

    }
}
