package ibis.media.imaging.conversion;

import java.nio.DoubleBuffer;

import ibis.media.imaging.Format;
import ibis.media.imaging.Image;

public class TGDOUBLEARGBtoTGDOUBLEGREY extends Convertor {

	private final static int COST = 4 + 1;

	public TGDOUBLEARGBtoTGDOUBLEGREY() {
		super(COST);
	}

	@Override
	public Image convert(Image in, Image out) throws ConversionException {

		if (in.getFormat() != Format.TGDOUBLEARGB) {
			throw new ConversionException(
					"input image not in TGDOUBLEARGB format");
		}

		if (out == null) {
			out = new Image(Format.TGDOUBLEGREY, in.getWidth(), in.getHeight());
		}

		if (out.getFormat() != Format.TGDOUBLEGREY) {
			throw new ConversionException("output image not in TGDOUBLEGREY format");
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

		// set a to max (opaque)

		while (dataIn.hasRemaining()) {
			// get rgb value (skip first "a" element in array)
			dataIn.get(argb);
			
			// calculate average intensity multiplied with the alpha channel 
			double val = ((argb[1] + argb[2] + argb[3])/3) * argb[0];
			
			dataOut.put(val);
		}

		return out;

	}

	private byte intToPseudoUnsignedByte(int n) {
		if (n < 128) {
			return (byte) n;
		} else {
			return (byte) (n - 256);
		}
	}
}
