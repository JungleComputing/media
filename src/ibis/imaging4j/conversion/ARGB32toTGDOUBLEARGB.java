package ibis.imaging4j.conversion;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

import ibis.imaging4j.Format;
import ibis.imaging4j.Image;

public class ARGB32toTGDOUBLEARGB extends Convertor {

    private final static int COST = 4 + 1;

    public ARGB32toTGDOUBLEARGB() {
        super(COST);
    }

    @Override
    public Image convert(Image in, Image out) throws ConversionException {

        if (in.getFormat() != Format.ARGB32) {
            throw new ConversionException("input image not in ARGB32 format");
        }

        if (out == null) {
            out = new Image(Format.TGDOUBLEARGB, in.getWidth(), in.getHeight());
        }

        if (out.getFormat() != Format.TGDOUBLEARGB) {
            throw new ConversionException("output image not in TGDOUBLEARGB format");
        }

        if (out.getWidth() != in.getWidth()
                || out.getHeight() != in.getHeight()) {
            throw new ConversionException("Target image has wrong dimensions!");
        }

        ByteBuffer dataIn = in.getData().duplicate();
        DoubleBuffer dataOut = out.getData().asDoubleBuffer();

        dataIn.clear();
        dataOut.clear();

        byte[] argb = new byte[4];
        while (dataIn.hasRemaining()) {
            //get argb value
            dataIn.get(argb);
            //put rgb (skip a)
            for(int i = 0; i < 4; i++) {
            	dataOut.put(((double)(argb[i] & 0xff))/255);
            }
        }
        return out;
    }

}
