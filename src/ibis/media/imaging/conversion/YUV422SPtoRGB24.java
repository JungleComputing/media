package ibis.media.imaging.conversion;

import java.nio.ByteBuffer;

import ibis.media.imaging.Format;
import ibis.media.imaging.Image;

public final class YUV422SPtoRGB24 extends Convertor {
    
    private final static int COST = 4 + (6*4) + 6;
    
    public YUV422SPtoRGB24() {
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
		// 3 bytes per pixel in a RGB24Image
		
		for (int h = 0; h < height; h++) {
		    for (int w = 0; w < width; w += 2) {
		
		        int Y1 = (0xff & in1.get(h * width + w)) - 16;
		        int Y2 = (0xff & in1.get(h * width + (w + 1))) - 16;
		
		        int U = (0xff & in1.get(offsetU + h * width + w)) - 128;
		        int V = (0xff & in1.get(offsetU + h * width + w + 1)) - 128;
		
		        int r1 = clipAndScale((298 * Y1 + 409 * U + 128) >> 8);
		        int r2 = clipAndScale((298 * Y2 + 409 * U + 128) >> 8);
		
		        int g1 = clipAndScale((298 * Y1 - 100 * V - 208 * U + 128) >> 8);
		        int g2 = clipAndScale((298 * Y2 - 100 * V - 208 * U + 128) >> 8);
		
		        int b1 = clipAndScale((298 * Y1 + 516 * V + 128) >> 8);
		        int b2 = clipAndScale((298 * Y2 + 516 * V + 128) >> 8);
		
		        out1.put(h * width * 3 + w * 3, (byte) (0xff & r1));
		        out1.put(h * width * 3 + w * 3 + 1, (byte) (0xff & g1));
		        out1.put(h * width * 3 + w * 3 + 2, (byte) (0xff & b1));
		
		        out1.put(h * width * 3 + w * 3 + 3, (byte) (0xff & r2));
		        out1.put(h * width * 3 + w * 3 + 4, (byte) (0xff & g2));
		        out1.put(h * width * 3 + w * 3 + 5, (byte) (0xff & b2));
		    }
		}
           
        return out;
    }
}
