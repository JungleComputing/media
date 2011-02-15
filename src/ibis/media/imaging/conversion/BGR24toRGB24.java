package ibis.media.imaging.conversion;

import java.nio.ByteBuffer;

import ibis.media.imaging.Format;
import ibis.media.imaging.Image;

public class BGR24toRGB24 extends Convertor {

    private final static int COST = 3 + 1;
    
    public BGR24toRGB24() {
        super(COST);
    }

    @Override
    public Image convert(Image in, Image out) throws ConversionException {

        if (in.getFormat() != Format.BGR24) {
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

        byte[] rgb = new byte[3];
        
        while (dataIn.hasRemaining()) {
            //get bgr value
            dataIn.get(rgb);
            
            //swap b and r to get to rgb instead of bgr
            byte tmp = rgb[0];
            rgb[0] = rgb[2];
            rgb[2] = tmp;

            //put rgb
            dataOut.put(rgb);
        }

        return out;

    }
}
