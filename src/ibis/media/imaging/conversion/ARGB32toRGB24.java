package ibis.media.imaging.conversion;

import java.nio.ByteBuffer;

import ibis.media.imaging.Format;
import ibis.media.imaging.Image;

public class ARGB32toRGB24 extends Convertor {

    private final static int COST = 3 + 1;

    public ARGB32toRGB24() {
        super(COST);
    }

    @Override
    public Image convert(Image in, Image out) throws ConversionException {

        if (in.getFormat() != Format.ARGB32) {
            throw new ConversionException("input image not in ARGB32 format");
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

        byte[] argb = new byte[4];
        while (dataIn.hasRemaining()) {
            //get argb value
            dataIn.get(argb);
            
            //put rgb (skip a)
            dataOut.put(argb, 1, 3);
        }

        return out;

    }

}
