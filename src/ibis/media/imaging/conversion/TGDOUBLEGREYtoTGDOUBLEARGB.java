package ibis.media.imaging.conversion;

import java.nio.DoubleBuffer;

import ibis.media.imaging.Format;
import ibis.media.imaging.Image;

public class TGDOUBLEGREYtoTGDOUBLEARGB extends Convertor {

	private final static int COST = 4 + 1;

	public TGDOUBLEGREYtoTGDOUBLEARGB() {
		super(COST);
	}

	@Override
	public Image convert(Image in, Image out) throws ConversionException {

		if (in.getFormat() != Format.TGDOUBLEGREY) {
			throw new ConversionException(
					"input image not in TGDOUBLEGREY format");
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

		DoubleBuffer dataIn = in.getData().asDoubleBuffer();
		DoubleBuffer dataOut = out.getData().asDoubleBuffer();

		dataIn.clear();
		dataOut.clear();

		double[] argb = new double[4];

		argb[0] = 1;
		// set a to max (opaque)

		while (dataIn.hasRemaining()) {			
			argb[1] = argb[2] = argb[3] = dataIn.get(); 
			
			dataOut.put(argb);
		}
		return out;
	}
}
