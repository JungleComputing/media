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
		System.out.println("TGDOUBLEGREYtoGREY converter:");
		System.out.println("max: " + max);
		System.out.println("min: " + min);
		System.out.println("multiplier: " + multiplier);
		System.out.println("========");		
		
		dataIn.clear();
		dataOut.clear();

		byte grey;

		while (dataIn.hasRemaining()) {

//			int val = (int) (dataIn.get() * 256);
			int val = (int) ((dataIn.get() - min) * multiplier);
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
