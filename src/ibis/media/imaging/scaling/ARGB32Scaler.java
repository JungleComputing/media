package ibis.media.imaging.scaling;

import java.nio.ByteBuffer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ibis.media.imaging.Format;
import ibis.media.imaging.Image;
import ibis.media.imaging.conversion.ConversionException;

public class ARGB32Scaler extends Scaler {

    private static final Logger logger = LoggerFactory
            .getLogger(ARGB32Scaler.class);

    private static final int COST = 10; // random value!

    public ARGB32Scaler() {
        super(COST);
    }

    @Override
    public void scale(Image in, Image out) throws Exception {

        // Check the parameters
        if (out == null) {
            throw new NullPointerException("Destionation image is null!");
        }
        
        if (in.getFormat() != Format.ARGB32) {
            throw new Exception("source image is invalid format: " + in.getFormat());
        }
        
        if (out.getFormat() != Format.ARGB32) {
            throw new Exception("target image is invalid format: " + out.getFormat());
        }


        final int width = in.getWidth();
        final int height = in.getHeight();

        final int targetW = out.getWidth();
        final int targetH = out.getHeight();

        if (targetH <= 0 || targetW <= 0) {
            throw new ConversionException("Targer image has "
                    + "invalid dimensions!");
        }

        // If the target has the same dimensions as the source we copy.
        if (targetW == width && targetH == height) {
            Image.copy(in, out);
            return;
        }

        // Otherwise we do a simple interpolation.
        // NOTE: Not necessarily a decent algorithm!
        final double multX = ((double) width) / targetW;
        final double multY = ((double) height) / targetH;

        final ByteBuffer src = in.getData();
        final ByteBuffer dst = out.getData();

        for (int h = 0; h < targetH; h++) {

            for (int w = 0; w < targetW; w++) {
                final int srcW = (int) (w * multX);
                final int srcH = (int) (h * multY);

                final int sourceIndex = ((srcH * width) + srcW) * 4;
                final int targetIndex = ((h * targetW) + w) * 4;

                try {

                    // EEP: this is way to slow
                    dst.put(targetIndex + 0, src.get(sourceIndex + 0));
                    dst.put(targetIndex + 1, src.get(sourceIndex + 1));
                    dst.put(targetIndex + 2, src.get(sourceIndex + 2));
                    dst.put(targetIndex + 3, src.get(sourceIndex + 3));
                } catch (IndexOutOfBoundsException t) {
                    logger.debug(w + " x " + h + " -> " + (targetW * multX)
                            + " x " + (h * multY) + " sourceIndex = " + sourceIndex + " targetIndex = " + targetIndex);
                    throw t;
                }

            }
        }

    }

    @Override
    public Image scale(Image in, int w, int h) throws Exception {

        Image dst = new Image(Format.ARGB32, w, h);

        scale(in, dst);

        return dst;
    }

}
