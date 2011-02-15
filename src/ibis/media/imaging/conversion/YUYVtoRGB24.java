package ibis.media.imaging.conversion;

import java.nio.ByteBuffer;

import ibis.media.imaging.Format;
import ibis.media.imaging.Image;

public final class YUYVtoRGB24 extends Convertor {

	private static final byte clipAndScale(int value) {

		if (value > 255) {
			// value = 255;
			return (byte) (0xff & 220);
		}

		if (value < 0) {
			// value = 0;
			return (byte) (0);
		}

		return (byte) (0xff & ((value * 220) / 256));
	}

	private static final int clipAndScale(double value) {

		if (value > 255)
			value = 255;

		if (value < 0)
			value = 0;

		value = value * 220.0 / 256.0;

		return (int) value;
	}

	public static void YUYVtoRGB24Table(int width, int height, ByteBuffer in,
			ByteBuffer out) {

		if (!init) {

			for (int i = 0; i < 256; i++) {
				V1[i] = (short) (1.370705 * (i - 128));
				V2[i] = (short) (0.698001 * (i - 128));

				U1[i] = (short) (0.337633 * (i - 128));
				U2[i] = (short) (1.732446 * (i - 128));
			}

			init = true;
		}

		in.clear();
		out.clear();

		while (in.hasRemaining()) {
			int Y1 = (0xff & in.get());
			int U = (0xff & in.get());
			int Y2 = (0xff & in.get());
			int V = (0xff & in.get());

			out.put((byte) clipAndScale(Y1 + V1[V]));
			out.put((byte) clipAndScale(Y1 - V2[V] - U1[U]));
			out.put((byte) clipAndScale(Y1 + U2[U]));

			out.put((byte) clipAndScale(Y2 + V1[V]));
			out.put((byte) clipAndScale(Y2 - V2[V] - U1[U]));
			out.put((byte) clipAndScale(Y2 + U2[U]));
		}
	}

	private static short[] V1 = new short[256];
	private static short[] V2 = new short[256];
	private static short[] U1 = new short[256];
	private static short[] U2 = new short[256];

	private static boolean init = false;

	private final static int COST = 4 + (6 * 4) + 2;

	public YUYVtoRGB24() {
		super(COST);
	}

	@Override
	public Image convert(Image in, Image out) throws ConversionException {

		if (out == null) {
			out = new Image(Format.RGB24, in.getWidth(), in.getHeight());
		} else {
			if (out.getWidth() != in.getWidth()
					|| out.getHeight() != in.getHeight()) {
				throw new ConversionException("Target image has wrong "
						+ "dimensions! (" + in.getWidth() + "x"
						+ in.getHeight() + ") != (" + out.getWidth() + "x"
						+ out.getHeight() + ")");
			}
		}

		// long start = System.currentTimeMillis();

		YUYVtoRGB24Table(in.getWidth(), in.getHeight(), in.getData(), out
				.getData());

		// long time = System.currentTimeMillis() - start;

		// System.out.println("Conversion took " + time + " ms");
		return out;
	}
}
