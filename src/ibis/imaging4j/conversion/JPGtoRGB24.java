package ibis.imaging4j.conversion;

import ibis.imaging4j.Format;
import ibis.imaging4j.Image;
import ibis.imaging4j.decompression.JPEGImageDecompressor;

import javax.imageio.IIOException;

public class JPGtoRGB24 extends Convertor {

    // FIXME: This is extremely expensive at the moment!
    
    private final static int COST = 1000;
    
    private final JPEGImageDecompressor dec2;
    
    public JPGtoRGB24() throws IIOException {
        super(COST);
        dec2 = new JPEGImageDecompressor();
    }
    
    @Override
    public Image convert(Image in, Image out) throws ConversionException {

        // Decompress the JPG. We currently don't have an implementation that
        // decompresses to an existing image...
        try {
            Image tmp2 = dec2.decompress(in);

            // System.out.println("GOT image in " + tmp2.getFormat());

            if (tmp2.getFormat() != Format.RGB24) {

                Convertor c = Conversion.getConvertor(tmp2.getFormat(),
                        Format.RGB24);
                // System.out.println("Convert " + tmp2.getFormat() +
                // " to Format.ARGB32 " + out);

                return c.convert(tmp2, out);
            }

            if (out == null) {
                return tmp2;
            }

            Image.copy(tmp2, out);
            return out;

        } catch (Exception e) {
            throw new ConversionException("Failed to convert to JPG", e);
        }
    }
}