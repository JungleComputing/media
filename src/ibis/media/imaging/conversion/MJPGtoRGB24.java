package ibis.media.imaging.conversion;

import ibis.media.imaging.Format;
import ibis.media.imaging.Image;
import ibis.media.imaging.decompression.JPEGImageDecompressor;

import javax.imageio.IIOException;

public class MJPGtoRGB24 extends Convertor {

    // FIXME: This is extremely expensive at the moment!

    private final static int COST = 1000;

    private final MJPGtoJPG dec;

    private final JPEGImageDecompressor dec2;

    public MJPGtoRGB24() throws IIOException {
        super(COST);
        dec = new MJPGtoJPG();
        dec2 = new JPEGImageDecompressor();
    }

    @Override
    public Image convert(Image in, Image out) throws ConversionException {

        // First convert MJPEG to JPG. This is basically a copy which may insert
        // some missing table data if needed.
        Image tmp = dec.convert(in, null);

        // Next decompress the JPG. We currently don't have an implementation
        // that
        // decompresses to an existing image...
        try {
            Image tmp2 = dec2.decompress(tmp);

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