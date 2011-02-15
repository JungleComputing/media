package ibis.media.imaging.conversion;

import java.nio.FloatBuffer;

import ibis.media.imaging.Format;
import ibis.media.imaging.Image;

public class TGFLOATGREYtoTGFLOATARGB extends Convertor {

	private final static int COST = 4 + 1;

	public TGFLOATGREYtoTGFLOATARGB() {
		super(COST);
	}

	@Override
	public Image convert(Image in, Image out) throws ConversionException {

		if (in.getFormat() != Format.TGFLOATGREY) {
			throw new ConversionException(
					"input image not in TGFLOATGREY format");
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

		FloatBuffer dataIn = in.getData().asFloatBuffer();
		FloatBuffer dataOut = out.getData().asFloatBuffer();

		dataIn.clear();
		dataOut.clear();

		float[] argb = new float[4];

		argb[0] = 1;
		// set a to max (opaque)

		while (dataIn.hasRemaining()) {			
			argb[1] = argb[2] = argb[3] = dataIn.get(); 
			
			dataOut.put(argb);
		}
		return out;
	}
}
