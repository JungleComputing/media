
package ibis.media.imaging.conversion;

import java.nio.ByteBuffer;

import ibis.media.imaging.Format;
import ibis.media.imaging.Image;

public final class YUV420SPtoRGB24 extends Convertor {

    private final static int COST = 6 + (12*4) + 12;

    public YUV420SPtoRGB24() {
        super(COST);
    }
    
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

    @Override
    public Image convert(Image in, Image out) throws ConversionException { 

        if (out == null) {
            out = new Image(Format.RGB24, in.getWidth(), in.getHeight());
        } else { 
            if (out.getWidth() != in.getWidth() || 
                    out.getHeight() != in.getHeight()) { 
                throw new ConversionException("Target image has wrong dimensions!");
            }
        }
		int width = in.getWidth();
		int height = in.getHeight();
		ByteBuffer in1 = in.getData();
		ByteBuffer out1 = out.getData();
        
        final int offsetU = width * height;
		
		for (int h = 0; h < height; h += 2) {
		    for (int w = 0; w < width; w += 2) {
		
		        int U = (0xff & in1.get(offsetU + h * width / 2 + w)) - 128;
		        int V = (0xff & in1.get(offsetU + h * width / 2 + w + 1)) - 128;
		
		        int Y1 = (0xff & in1.get(h * width + w)) - 16;
		        int Y2 = (0xff & in1.get(h * width + (w + 1))) - 16;
		        int Y3 = (0xff & in1.get((h + 1) * width + w)) - 16;
		        int Y4 = (0xff & in1.get((h + 1) * width + (w + 1))) - 16;
		
		        int r1 = clipAndScale((298 * Y1 + 409 * U + 128) >> 8);
		        int r2 = clipAndScale((298 * Y2 + 409 * U + 128) >> 8);
		        int r3 = clipAndScale((298 * Y3 + 409 * U + 128) >> 8);
		        int r4 = clipAndScale((298 * Y4 + 409 * U + 128) >> 8);
		
		        int g1 = clipAndScale((298 * Y1 - 100 * V - 208 * U + 128) >> 8);
		        int g2 = clipAndScale((298 * Y2 - 100 * V - 208 * U + 128) >> 8);
		        int g3 = clipAndScale((298 * Y3 - 100 * V - 208 * U + 128) >> 8);
		        int g4 = clipAndScale((298 * Y4 - 100 * V - 208 * U + 128) >> 8);
		
		        int b1 = clipAndScale((298 * Y1 + 516 * V + 128) >> 8);
		        int b2 = clipAndScale((298 * Y2 + 516 * V + 128) >> 8);
		        int b3 = clipAndScale((298 * Y3 + 516 * V + 128) >> 8);
		        int b4 = clipAndScale((298 * Y4 + 516 * V + 128) >> 8);
		
		        out1.put(h * width * 3 + w * 3, (byte) (0xff & r1));
		        out1.put(h * width * 3 + w * 3 + 1, (byte) (0xff & g1));
		        out1.put(h * width * 3 + w * 3 + 2, (byte) (0xff & b1));
		
		        out1.put(h * width * 3 + w * 3 + 3, (byte) (0xff & r2));
		        out1.put(h * width * 3 + w * 3 + 4, (byte) (0xff & g2));
		        out1.put(h * width * 3 + w * 3 + 5, (byte) (0xff & b2));
		
		        out1.put((h + 1) * width * 3 + w * 3, (byte) (0xff & r3));
		        out1.put((h + 1) * width * 3 + w * 3 + 1, (byte) (0xff & g3));
		        out1.put((h + 1) * width * 3 + w * 3 + 2, (byte) (0xff & b3));
		
		        out1.put((h + 1) * width * 3 + w * 3 + 3, (byte) (0xff & r4));
		        out1.put((h + 1) * width * 3 + w * 3 + 4, (byte) (0xff & g4));
		        out1.put((h + 1) * width * 3 + w * 3 + 5, (byte) (0xff & b4));
		    }
		}
   
        return out;
    }
}
