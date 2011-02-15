package ibis.media.imaging.conversion;

import ibis.media.imaging.Image;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;
import java.nio.ShortBuffer;

public class ARGB64toBufferedImage implements ConvertorToBufferedImage {

    public BufferedImage convert(Image in) throws ConversionException {

        int width = in.getWidth();
        int height = in.getHeight();
        
        BufferedImage b = new BufferedImage(width, height,   
                BufferedImage.TYPE_INT_ARGB);
        
        ByteBuffer data = in.getData();
        
        data.position(0);
        data.limit(data.capacity());
            
        int [] tmp = new int[width*height];

        final ShortBuffer tmp1 = data.asShortBuffer();
		
		final int size = tmp1.remaining();
		
		int index = 0;
		
		for (int i = 0; i < size; i += 4) {
		
		    final int b1 = (byte) (((tmp1.get(i) & 0xffff) / 255) & 0xff);
		    final int g = (byte) (((tmp1.get(i + 1) & 0xffff) / 255) & 0xff);
		    final int r = (byte) (((tmp1.get(i + 2) & 0xffff) / 255) & 0xff);
		    final int a = (byte) (((tmp1.get(i + 3) & 0xffff) / 255) & 0xff);
		
		    tmp[index++] = a << 24 | r << 16 | g << 8 | b1;
		}

        b.setRGB(0, 0, width, height, tmp, 0, width);  
        return b; 
    }
}
