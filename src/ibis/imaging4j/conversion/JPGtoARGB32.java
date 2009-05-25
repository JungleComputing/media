package ibis.imaging4j.conversion;

import ibis.imaging4j.Format;
import ibis.imaging4j.Image;
import ibis.imaging4j.decompression.JPEGImageDecompressor;

import javax.imageio.IIOException;

public class JPGtoARGB32 extends Convertor {

    // FIXME: This is extremely expensive at the moment!

    private final static int COST = 1000;

    private final JPEGImageDecompressor dec2;

    public JPGtoARGB32() throws IIOException {
        super(COST);
        dec2 = new JPEGImageDecompressor();
    }

    @Override
    public Image convert(Image in, Image out) throws ConversionException {

        // Decompress the JPG. We currently don't have an implementation that
        // decompresses to an existing image...
        try {
            Image decoded = dec2.decompress(in);

            // System.out.println("GOT image in " + tmp2.getFormat());

            if (decoded.getFormat() != Format.ARGB32) {

                Convertor c = Conversion.getConvertor(decoded.getFormat(),
                        Format.ARGB32);

                if (c == null) {
                    throw new ConversionException("Cannot convert from "
                            + decoded.getFormat() + " to " + Format.ARGB32
                            + " when decoding jpg");
                }

                // System.out.println("Convert " + tmp2.getFormat() +
                // " to Format.ARGB32 " + out);

                return c.convert(decoded, out);
            }

            if (out == null) {
                return decoded;
            }

            Image.copy(decoded, out);
            return out;

        } catch (Exception e) {
            throw new ConversionException("Failed to convert to JPG", e);
        }
    }
}