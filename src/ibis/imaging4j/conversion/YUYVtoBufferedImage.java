package ibis.imaging4j.conversion;

import ibis.imaging4j.Image;
import ibis.imaging4j.conversion.util.LowLevelConvert;

import java.awt.image.BufferedImage;
import java.nio.ByteBuffer;

public class YUYVtoBufferedImage implements ConvertorToBufferedImage {

    public BufferedImage convert(Image in) throws ConversionException {

        int width = in.getWidth();
        int height = in.getHeight();
        
        BufferedImage b = new BufferedImage(width, height,   
                BufferedImage.TYPE_INT_ARGB);
        
        ByteBuffer data = in.getData();
        
        data.position(0);
        data.limit(data.capacity());
            
        int [] tmp = new int[width*height];

        LowLevelConvert.YUYVtoARGB32(width, height, data, tmp);

        b.setRGB(0, 0, width, height, tmp, 0, width);  
        return b; 
    }
}
