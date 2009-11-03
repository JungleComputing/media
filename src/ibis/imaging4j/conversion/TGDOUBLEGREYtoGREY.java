package ibis.imaging4j.conversion;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

import ibis.imaging4j.Format;
import ibis.imaging4j.Image;

public class TGDOUBLEGREYtoGREY extends Convertor {

	private final static int COST = 4 + 1;

	public TGDOUBLEGREYtoGREY() {
		super(COST);
	}

	@Override
	public Image convert(Image in, Image out) throws ConversionException {

		if (in.getFormat() != Format.TGDOUBLEGREY) {
			throw new ConversionException(
					"input image not in TGDOUBLEGREY format");
		}

		if (out == null) {
			out = new Image(Format.GREY, in.getWidth(), in.getHeight());
		}

		if (out.getFormat() != Format.GREY) {
			throw new ConversionException("output image not in GREY format");
		}

		if (out.getWidth() != in.getWidth()
				|| out.getHeight() != in.getHeight()) {
			throw new ConversionException("Target image has wrong dimensions!");
		}

		DoubleBuffer dataIn = in.getData().asDoubleBuffer();
		ByteBuffer dataOut = out.getData().duplicate();

		dataIn.clear();
		dataOut.clear();

		byte grey;

		while (dataIn.hasRemaining()) {

			int val = (int) (dataIn.get() * 256);
			if (val == 256) {
				val--;
			}
			grey = intToPseudoUnsignedByte(val);

			// put grey
			dataOut.put(grey);
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
