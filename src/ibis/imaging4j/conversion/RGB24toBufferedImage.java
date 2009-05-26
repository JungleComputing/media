package ibis.imaging4j.conversion;

import ibis.imaging4j.Image;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class RGB24toBufferedImage implements ConvertorToBufferedImage {

    public BufferedImage convert(Image in) throws ConversionException {

        int width = in.getWidth();
        int height = in.getHeight();
        
        BufferedImage b = new BufferedImage(width, height,   
                BufferedImage.TYPE_INT_ARGB);
        
        ByteBuffer data = in.getData();
        
        data.position(0);
        data.limit(data.capacity());
            
        int [] tmp = new int[width*height];

        RGB24toARGB32(in.getWidth(), in.getHeight(), data, tmp);

        b.setRGB(0, 0, width, height, tmp, 0, width);  
        return b; 
    }
    
    public static final void RGB24toARGB32(int width, int height, ByteBuffer in, int [] rgbOut) { 

        final int size = width * height * 3;

        int index = 0;

        for (int i=0;i<size;i+=3) { 

            final int r = (0xff & in.get(i));
            final int g = (0xff & in.get(i+1));
            final int b = (0xff & in.get(i+2));

            rgbOut[index++] = 0xFF000000 | r << 16 | g << 8 | b;
        }
    }
}
