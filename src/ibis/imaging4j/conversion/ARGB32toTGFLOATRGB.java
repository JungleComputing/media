package ibis.imaging4j.conversion;

import ibis.imaging4j.Format;
import ibis.imaging4j.Image;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

public class ARGB32toTGFLOATRGB extends Convertor {

    private final static int COST = 4 + 1;

    public ARGB32toTGFLOATRGB() {
        super(COST);
    }

    @Override
    public Image convert(Image in, Image out) throws ConversionException {

        if (in.getFormat() != Format.ARGB32) {
            throw new ConversionException("input image not in ARGB32 format");
        }

        if (out == null) {
            out = new Image(Format.TGFLOATRGB, in.getWidth(), in.getHeight());
        }

        if (out.getFormat() != Format.TGFLOATRGB) {
            throw new ConversionException("output image not in TGFLOATRGB format");
        }

        if (out.getWidth() != in.getWidth()
                || out.getHeight() != in.getHeight()) {
            throw new ConversionException("Target image has wrong dimensions!");
        }

        ByteBuffer dataIn = in.getData().duplicate();
        FloatBuffer dataOut = out.getData().asFloatBuffer();

        dataIn.clear();
        dataOut.clear();

        byte[] argb = new byte[4];
        while (dataIn.hasRemaining()) {
            //get argb value
            dataIn.get(argb);
            final float a = ((float)(argb[3] & 0xff))/255f;
            //put rgb (skip a)
            for(int i = 0; i < 3; i++) {
            	final float val = ((float)(argb[i] & 0xff))/255f;
            	dataOut.put(val*a);
//            	dataOut.put((double)(argb[i] & 0xff));
            }
        }
        return out;
    }

}
