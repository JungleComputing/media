package ibis.imaging4j.conversion;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

import ibis.imaging4j.Format;
import ibis.imaging4j.Image;

public class ARGB32toJPG extends Convertor {

    private final static int COST = 3 + 1;

    public ARGB32toJPG() {
        super(COST);
    }

    @Override
    public Image convert(Image in, Image out) throws ConversionException {
        try {

            if (out != null) {
                throw new ConversionException(
                        "ARGB32 to JPG conversion does not support pre-existing destination image");
            }

            ConvertorToBufferedImage c = Conversion
                    .getConvertorToBufferedImage(in.getFormat());

            BufferedImage b = c.convert(in);

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();

            ImageIO.setUseCache(false);

            ImageOutputStream output;
            output = ImageIO.createImageOutputStream(bytes);

            Iterator writers = ImageIO.getImageWritersByFormatName("jpg");

            if (writers == null || !writers.hasNext()) {
                throw new RuntimeException("No writers!");
            }

            ImageWriter writer = (ImageWriter) writers.next();
            writer.setOutput(output);
            writer.write(b);
            writer.dispose();
            output.flush();

            byte[] tmp = bytes.toByteArray();

            // System.out.println("Compression " + image.width + "x" +
            // image.height + " from " + image.getSize() + " to " + tmp.length +
            // " bytes.");

            return new Image(Format.JPG, in.getWidth(), in.getHeight(), tmp);

        } catch (IOException e) {
            throw new ConversionException("cannot convert " + in, e);
        }

    }

}
