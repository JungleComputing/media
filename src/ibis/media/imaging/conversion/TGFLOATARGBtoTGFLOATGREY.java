package ibis.media.imaging.conversion;

import ibis.media.imaging.Format;
import ibis.media.imaging.Image;

import java.nio.FloatBuffer;

public class TGFLOATARGBtoTGFLOATGREY extends Convertor {

	private final static int COST = 4 + 1;

	public TGFLOATARGBtoTGFLOATGREY() {
		super(COST);
	}

	@Override
	public Image convert(Image in, Image out) throws ConversionException {

		if (in.getFormat() != Format.TGFLOATARGB) {
			throw new ConversionException(
					"input image not in TGFLOATARGB format");
		}

		if (out == null) {
			out = new Image(Format.TGFLOATGREY, in.getWidth(), in.getHeight());
		}

		if (out.getFormat() != Format.TGFLOATGREY) {
			throw new ConversionException("output image not in TGFLOATGREY format");
		}

		if (out.getWidth() != in.getWidth()
				|| out.getHeight() != in.getHeight()) {
			throw new ConversionException("Target image has wrong dimensions!");
		}

		FloatBuffer dataIn = in.getData().asFloatBuffer();
		FloatBuffer dataOut = out.getData().asFloatBuffer();

		dataIn.clear();
		dataOut.clear();

		float[] argb = new float[4];

		// set a to max (opaque)

		while (dataIn.hasRemaining()) {
			// get rgb value (skip first "a" element in array)
			dataIn.get(argb);
			
			// calculate average intensity multiplied with the alpha channel 
			float val = ((argb[1] + argb[2] + argb[3])/3) * argb[0];
			
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
