package ibis.imaging4j.conversion;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;

import ibis.imaging4j.Format;
import ibis.imaging4j.Image;

public class TGFLOATARGBtoARGB32 extends Convertor {

	private final static int COST = 4 + 1;

	public TGFLOATARGBtoARGB32() {
		super(COST);
	}

	@Override
	public Image convert(Image in, Image out) throws ConversionException {

		if (in.getFormat() != Format.TGFLOATARGB) {
			throw new ConversionException(
					"input image not in TGFLOATARGB format");
		}

		if (out == null) {
			out = new Image(Format.ARGB32, in.getWidth(), in.getHeight());
		}

		if (out.getFormat() != Format.ARGB32) {
			throw new ConversionException("output image not in ARGB32 format");
		}

		if (out.getWidth() != in.getWidth()
				|| out.getHeight() != in.getHeight()) {
			throw new ConversionException("Target image has wrong dimensions!");
		}

		FloatBuffer dataIn = in.getData().asFloatBuffer();
		ByteBuffer dataOut = out.getData().duplicate();

		dataIn.clear();
		dataOut.clear();

		byte[] argb = new byte[4];

		while (dataIn.hasRemaining()) {
			for (int i = 0; i < 4; i++) {
				int val = (int) (dataIn.get() * 256);
				if (val == 256) {
					val--;
				}
				argb[i] = intToPseudoUnsignedByte(val);
			}

			// put argb
			dataOut.put(argb);
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
