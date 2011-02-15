package ibis.media.imaging.conversion;

import java.nio.ByteBuffer;

import ibis.media.imaging.Format;
import ibis.media.imaging.Image;

public final class YUYVtoARGB32 extends Convertor {

    private final static int COST = 4 + (6*4) + 2;
    
    public YUYVtoARGB32() {
        super(COST);
    }
    
    private static final byte clipAndScale(int value) {

        if (value > 255) {
            // value = 255;
            return (byte) (0xff & 220);
        }

        if (value < 0) {
            // value = 0;
            return (byte) (0);
        }

        return (byte) (0xff & ((value * 220) / 256));
    }

    private static short[] V1 = new short[256];
    private static short[] V2 = new short[256];
    private static short[] U1 = new short[256];
    private static short[] U2 = new short[256];

    private static boolean init = false;

    public static void YUYVtoARGB32Table(int width, int height, ByteBuffer in,
            ByteBuffer out) {

        if (!init) {

            for (int i = 0; i < 256; i++) {
                V1[i] = (short) (1.370705 * (i - 128));
                V2[i] = (short) (0.698001 * (i - 128));

                U1[i] = (short) (0.337633 * (i - 128));
                U2[i] = (short) (1.732446 * (i - 128));
            }

            init = true;
        }

        final byte[] tmpIn = new byte[2 * width];
        final byte[] tmpOut = new byte[4 * width];

        // Set alpha values in output
        for (int i = 0; i < 4 * width; i += 4) {
            tmpOut[i] = (byte) 0xFF;
        }

        in.position(0);
        in.limit(in.capacity());

        out.position(0);
        out.limit(out.capacity());

        for (int h = 0; h < height; h++) {

            in.get(tmpIn);

            for (int w = 0; w < 2 * width; w += 4) {

                int Y1 = (0xff & tmpIn[w + 0]);
                int U = (0xff & tmpIn[w + 1]);
                int Y2 = (0xff & tmpIn[w + 2]);
                int V = (0xff & tmpIn[w + 3]);

                tmpOut[2 * w + 1] = (byte) clipAndScale(Y1 + V1[V]);
                tmpOut[2 * w + 2] = (byte) clipAndScale(Y1 - V2[V] - U1[U]);
                tmpOut[2 * w + 3] = (byte) clipAndScale(Y1 + U2[U]);

                tmpOut[2 * w + 5] = (byte) clipAndScale(Y2 + V1[V]);
                tmpOut[2 * w + 6] = (byte) clipAndScale(Y2 - V2[V] - U1[U]);
                tmpOut[2 * w + 7] = (byte) clipAndScale(Y2 + U2[U]);

            }

            out.put(tmpOut);
        }
    }


    @Override
    public Image convert(Image in, Image out) throws ConversionException { 
        
        if (out == null) {
            out = new Image(Format.ARGB32, in.getWidth(), in.getHeight());
        } else { 
            if (out.getWidth() != in.getWidth() || 
                    out.getHeight() != in.getHeight()) { 
                throw new ConversionException("Target image has wrong " +
                                "dimensions! (" + in.getWidth() + "x" + 
                                in.getHeight() + ") != (" + out.getWidth() + 
                                "x" + out.getHeight() + ")");
            }
        }
        
   //     long start = System.currentTimeMillis();
        
        YUYVtoARGB32Table(in.getWidth(), in.getHeight(), 
                in.getData(), out.getData());
     
   //     long time = System.currentTimeMillis() - start;
        
   //     System.out.println("Conversion took " + time + " ms");
        return out;
    }
}
