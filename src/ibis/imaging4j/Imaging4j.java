package ibis.imaging4j;

import ibis.imaging4j.conversion.Conversion;
import ibis.imaging4j.conversion.Convertor;
import ibis.imaging4j.conversion.ConvertorToBufferedImage;
import ibis.imaging4j.io.IO;
import ibis.imaging4j.scaling.Scaler;
import ibis.imaging4j.scaling.Scaling;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

/**
 * Main entrypoint of Imaging4j.
 * 
 * @author Niels Drost
 * 
 * 
 */
public final class Imaging4j {

    public static void save(Image image, File file) throws IOException {
        IO.save(image, file);
    }

    public static Image load(File file) throws Exception {
        return IO.load(file);
    }

    public static void scale(Image in, Image out) throws Exception {
        Scaler scaler = Scaling.getScaler(in.getFormat());
        if (scaler == null) {
            throw new UnsupportedFormatException("Cannot scale "
                    + in.getFormat());
        }
        scaler.scale(in, out);
    }

    public static Image scale(Image in, int targetWidth, int targetHeight) throws Exception {
        Scaler scaler = Scaling.getScaler(in.getFormat());
        if (scaler == null) {
            throw new UnsupportedFormatException("Cannot scale "
                    + in.getFormat());
        }
        return scaler.scale(in, targetWidth, targetHeight);
    }

    public static void convert(Image in, Image out) throws Exception {
        Convertor convertor = Conversion.getConvertor(in.getFormat(), out
                .getFormat());
        if (convertor == null) {
            throw new UnsupportedFormatException("Cannot convert from "
                    + in.getFormat() + " to " + out.getFormat());
        }
        convertor.convert(in, out);
    }

    public static Image convert(Image in, Format targetFormat) throws Exception {
        Convertor convertor = Conversion.getConvertor(in.getFormat(), targetFormat);
        if (convertor == null) {
            throw new UnsupportedFormatException("Cannot convert from "
                    + in.getFormat() + " to " + targetFormat);
        }
        return convertor.convert(in, null);
    }

    public static BufferedImage convertToBufferedImage(Image in)
            throws Exception {
        ConvertorToBufferedImage convertor = Conversion
                .getConvertorToBufferedImage(in.getFormat());
        if (convertor == null) {
            throw new UnsupportedFormatException("Cannot convert from "
                    + in.getFormat() + " to BufferedImage");
        }
        return convertor.convert(in);
    }

}
