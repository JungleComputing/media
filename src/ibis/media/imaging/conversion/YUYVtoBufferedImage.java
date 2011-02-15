package ibis.media.imaging.conversion;

import ibis.media.imaging.Image;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class YUYVtoBufferedImage implements ConvertorToBufferedImage {

	private static final int clipAndScale(double value) {

		if (value > 255)
			value = 255;

		if (value < 0)
			value = 0;

		value = value * 220.0 / 256.0;

		return (int) value;
	}

	public static void YUYVtoARGB32(int width, int height, ByteBuffer in,
			int[] out) {

		int index = 0;

		for (int h = 0; h < height; h++) {
			for (int w = 0; w < width * 2; w += 4) {
				int Y1 = (0xff & in.get(h * width * 2 + w));
				int U = (0xff & in.get(h * width * 2 + (w + 1)));
				int Y2 = (0xff & in.get(h * width * 2 + (w + 2)));
				int V = (0xff & in.get(h * width * 2 + (w + 3)));

				int r1 = clipAndScale(Y1 + (1.370705 * (V - 128)));
				int g1 = clipAndScale(Y1 - (0.698001 * (V - 128))
						- (0.337633 * (U - 128)));
				int b1 = clipAndScale(Y1 + (1.732446 * (U - 128)));

				int r2 = clipAndScale(Y2 + (1.370705 * (V - 128)));
				int g2 = clipAndScale(Y2 - (0.698001 * (V - 128))
						- (0.337633 * (U - 128)));
				int b2 = clipAndScale(Y2 + (1.732446 * (U - 128)));

				out[index++] = 0xFF000000 | r1 << 16 | g1 << 8 | b1;
				out[index++] = 0xFF000000 | r2 << 16 | g2 << 8 | b2;
			}
		}
	}

	public BufferedImage convert(Image in) throws ConversionException {

		int width = in.getWidth();
		int height = in.getHeight();

		BufferedImage b = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);

		ByteBuffer data = in.getData();

		data.position(0);
		data.limit(data.capacity());

		int[] tmp = new int[width * height];

		YUYVtoARGB32(width, height, data, tmp);

		b.setRGB(0, 0, width, height, tmp, 0, width);
		return b;
	}
}
