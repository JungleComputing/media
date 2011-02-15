package ibis.media.imaging.conversion;

import ibis.media.imaging.Image;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class YUV422SPtoBufferedImage implements ConvertorToBufferedImage {
	
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
	 
    public BufferedImage convert(Image in) throws ConversionException {

        int width = in.getWidth();
        int height = in.getHeight();
        
        BufferedImage b = new BufferedImage(width, height,   
                BufferedImage.TYPE_INT_ARGB);
        
        ByteBuffer data = in.getData();
        
        data.position(0);
        data.limit(data.capacity());
            
        int [] tmp = new int[width*height];

        final int offsetU = width * height;
		// 3 bytes per pixel in a RGB24Image
		
		int index = 0;
		
		for (int h = 0; h < height; h++) {
		    for (int w = 0; w < width; w += 2) {
		
		        int Y1 = (0xff & data.get(h * width + w)) - 16;
		        int Y2 = (0xff & data.get(h * width + (w + 1))) - 16;
		
		        int U = (0xff & data.get(offsetU + h * width + w)) - 128;
		        int V = (0xff & data.get(offsetU + h * width + w + 1)) - 128;
		
		        int r1 = clipAndScale((298 * Y1 + 409 * U + 128) >> 8);
		        int r2 = clipAndScale((298 * Y2 + 409 * U + 128) >> 8);
		
		        int g1 = clipAndScale((298 * Y1 - 100 * V - 208 * U + 128) >> 8);
		        int g2 = clipAndScale((298 * Y2 - 100 * V - 208 * U + 128) >> 8);
		
		        int b1 = clipAndScale((298 * Y1 + 516 * V + 128) >> 8);
		        int b2 = clipAndScale((298 * Y2 + 516 * V + 128) >> 8);
		
		        tmp[index++] = 0xFF000000 & ((byte) (0xff & r1)) << 16
		                & ((byte) (0xff & g1)) << 8 & ((byte) (0xff & b1));
		
		        tmp[index++] = 0xFF000000 & ((byte) (0xff & r2)) << 16
		                & ((byte) (0xff & g2)) << 8 & ((byte) (0xff & b2));
		    }
		}

        b.setRGB(0, 0, width, height, tmp, 0, width);  
        return b; 
    }
}
