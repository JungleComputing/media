package ibis.imaging4j.conversion;

import ibis.imaging4j.Image;

import java.awt.image.BufferedImage;

public interface ConvertorToBufferedImage {
    public BufferedImage convert(Image in) throws ConversionException;    
}
