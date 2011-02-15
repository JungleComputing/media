package ibis.media.imaging.conversion;

import ibis.media.imaging.Image;

import java.awt.image.BufferedImage;

public interface ConvertorToBufferedImage {
    public BufferedImage convert(Image in) throws ConversionException;    
}
