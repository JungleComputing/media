package ibis.media.imaging.conversion;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import ibis.media.imaging.Format;
import ibis.media.imaging.Image;

public class ARGB32toTGFLOATARGB extends Convertor {

    private final static int COST = 4 + 1;

    public ARGB32toTGFLOATARGB() {
        super(COST);
    }

    @Override
    public Image convert(Image in, Image out) throws ConversionException {

        if (in.getFormat() != Format.ARGB32) {
            throw new ConversionException("input image not in ARGB32 format");
        }

        if (out == null) {
            out = new Image(Format.TGFLOATARGB, in.getWidth(), in.getHeight());
        }

        if (out.getFormat() != Format.TGFLOATARGB) {
            throw new ConversionException("output image not in TGFLOATARGB format");
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
            //put rgb (skip a)
            for(int i = 0; i < 4; i++) {
            	dataOut.put(((float)(argb[i] & 0xff))/255);
            }
        }
        return out;
    }

}
