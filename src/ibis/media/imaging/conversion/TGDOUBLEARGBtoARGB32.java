package ibis.media.imaging.conversion;

import java.nio.ByteBuffer;
import java.nio.DoubleBuffer;

import ibis.media.imaging.Format;
import ibis.media.imaging.Image;

public class TGDOUBLEARGBtoARGB32 extends Convertor {

	private final static int COST = 4 + 1;

	public TGDOUBLEARGBtoARGB32() {
		super(COST);
	}

	@Override
	public Image convert(Image in, Image out) throws ConversionException {

		if (in.getFormat() != Format.TGDOUBLEARGB) {
			throw new ConversionException(
					"input image not in TGDOUBLEARGB format");
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

		DoubleBuffer dataIn = in.getData().asDoubleBuffer();
		ByteBuffer dataOut = out.getData().duplicate();

		dataIn.clear();
		dataOut.clear();

		
		double max = dataIn.get();
		double min = max;
		while (dataIn.hasRemaining()) {
			final double val = dataIn.get();
			if(val > max) {
				max = val;
			} else if(val < min) {
				min = val;
			}
		}
		dataIn.clear();
		
		double multiplier = 256 / (max - min);
		
		System.out.println("========");
		System.out.println("TGDOUBLEARGBtoARGB32 converter:");
		System.out.println("max: " + max);
		System.out.println("min: " + min);
		System.out.println("multiplier: " + multiplier);
		System.out.println("========");
		
		
		
		byte[] argb = new byte[4];

		while (dataIn.hasRemaining()) {
			for (int i = 0; i < 4; i++) {
//				int val = (int) (dataIn.get() * 256);
				int val = (int) ((dataIn.get() - min) * multiplier);
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
