package ibis.imaging4j.compression;

import ibis.imaging4j.Format;
import ibis.imaging4j.Image;
import ibis.imaging4j.conversion.Conversion;
import ibis.imaging4j.conversion.ConvertorToBufferedImage;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Iterator;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;

//import org.apache.log4j.Logger;

public class JPEGCompressor implements ImageCompressor {
    
   // private final Logger logger = Logger.getLogger("Compressor.JPG");
    
    private ImageWriter writer;
   // private JPEGImageWriteParam writeParam;
    
    public JPEGCompressor() { 
    
        //      Get a TIFF reader and set its input to the written TIFF image.
             
        // Create the write param.
   //     writeParam = new JPEGImageWriteParam(null);
        //writeParam.setCompressionQuality(90);
    }
    
    public Image addImage(Image image) throws Exception {

        ConvertorToBufferedImage c = Conversion.getConvertorToBufferedImage(image.getFormat());
        
        BufferedImage b = c.convert(image);
        
        ByteArrayOutputStream out = new ByteArrayOutputStream();
     
        ImageIO.setUseCache(false);
        
        ImageOutputStream output = ImageIO.createImageOutputStream(out);
        
        Iterator writers = ImageIO.getImageWritersByFormatName("jpg");

        if (writers == null || !writers.hasNext()) {
            throw new RuntimeException("No writers!");
        }

        writer = (ImageWriter) writers.next();
        writer.setOutput(output);
        writer.write(b);
        writer.dispose();
        
        byte [] tmp = out.toByteArray();

      //  System.out.println("Compression " + image.width + "x" + image.height + " from " + image.getSize() + " to " + tmp.length + " bytes.");
        
        return new Image(Format.JPG, image.getWidth(), image.getHeight(), tmp); 
    }

    public Image flush() throws Exception {
        // Nothing to do here...
        return null;
    }

    public String getType() {
        return "JPG";
    }
}
