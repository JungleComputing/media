package ibis.imaging4j.decompression;

import ibis.imaging4j.Format;
import ibis.imaging4j.Image;

import java.awt.image.BufferedImage;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.PixelInterleavedSampleModel;
import java.awt.image.Raster;
import java.io.ByteArrayInputStream;
import java.nio.ByteBuffer;
import java.util.Iterator;

import javax.imageio.IIOException;
import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.plugins.jpeg.JPEGImageReadParam;
import javax.imageio.stream.ImageInputStream;

public class JPEGImageDecompressor implements ImageDecompressor {
    
    // private final Logger logger = Logger.getLogger("Decompressor.JPG");
    
    private JPEGImageReadParam readParam;
    private ImageReader reader;
    
    public JPEGImageDecompressor() throws IIOException { 
        // Get a TIFF reader and set its input to the written TIFF image.
        ImageIO.setUseCache(false);
        
        Iterator readers = ImageIO.getImageReadersByFormatName("jpg");

        if (readers == null || !readers.hasNext()) {
            throw new RuntimeException("No readers!");
        }

        reader = (ImageReader) readers.next();

        // Create the read param.
        readParam = new JPEGImageReadParam();
    }

    public String getType() {
        return "JPEG";
    }
    
    public Image decompress(Image cim) throws Exception { 

        Format src = cim.getFormat();
        
        if (src != Format.JPG && src != Format.MJPG) { 
            throw new Exception("JPG Decompressor cannot handle " + cim.getFormat());
        }

        // FIXME: This may fail!        
        
        ByteBuffer dataIn = cim.getData();
        
        byte [] tmp = null;
        
        if (dataIn.hasArray()) { 
            tmp = cim.getData().array();
        } else { 
            // FIXME: EEP were making a copy here!
            tmp = new byte[dataIn.limit()];
            dataIn.get(tmp);
        }
        
        ByteArrayInputStream in = 
            new ByteArrayInputStream(tmp);
        
        ImageInputStream input = ImageIO.createImageInputStream(in);
        
        if (input == null) { 
            System.err.println("Input = " + input);
            System.exit(1);
        }

        reader.setInput(input);
        
        // Read the image.
        BufferedImage image = reader.read(0, readParam);
        Raster r = image.getData();
        
        final int height = r.getHeight();
        final int width = r.getWidth();
        
        PixelInterleavedSampleModel sm = 
            (PixelInterleavedSampleModel) r.getSampleModel();
        
        DataBuffer buf = r.getDataBuffer();
        
        int banks = buf.getNumBanks();       
        
        if (banks != 1) {
            throw new RuntimeException("Unsupported image buffer type!");
        }

        int type = buf.getDataType();
        
        /*
        if (type == DataBuffer.TYPE_USHORT) { 

            
            int stride = sm.getPixelStride();

            short [] data = ((DataBufferUShort) buf).getData();
            
            if (stride == 3) {
                short [] result = new short[width*height*3];
                System.arraycopy(data, 0, result, 0, width*height*3);
                return new RGB48Image(width, height, result);
            } else if (stride == 4) {
                short [] result = new short[width*height*4];
                System.arraycopy(data, 0, result, 0, width*height*4);
                return new RGB48Image(width, height, result);
            } else { 
                throw new RuntimeException("Unsupported image buffer type!");
            }
          
        } else*/
        
        if (type == DataBuffer.TYPE_BYTE) { 
            
            int stride = sm.getPixelStride();

            byte [] data = ((DataBufferByte) buf).getData();
            
            if (stride == 3) {
                return new Image(Format.RGB24, width, height, data);
           // } else if (stride == 4) {
           //     byte [] result = new byte[width*height*3];
           //     RGB32toRGB24(data, result);
            } else if (stride == 1) {
                return new Image(Format.GREY, width, height, data);
            } else { 
                throw new RuntimeException("Unsupported image buffer stride: " + stride);
            }
         
        } else if (type == DataBuffer.TYPE_INT) { 
            
          //  System.out.println("GOT IMAGE OF TYPE INT");
            
            throw new RuntimeException("Unsupported image buffer type (INT)!");
        } 
         
        //  System.out.println("GOT IMAGE OF TYPE OTHER");
        throw new RuntimeException("Unsupported image buffer type (other)!");          
    }
}
