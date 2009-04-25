package ibis.imaging4j.conversion.util;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.ShortBuffer;

public class LowLevelConvert {

    private static final byte clipAndScale(int value) {

        if (value > 255)
            value = 255;

        if (value < 0)
            value = 0;

        return (byte) (0xff & ((value * 220) / 256));
    }

    private static final int clipAndScale(double value) { 

        if (value > 255) value = 255;

        if (value < 0) value = 0;

        value = value * 220.0 / 256.0;

        return (int) value;
    }

    public static final void RGB24toARGB32(byte [] rgbIn, int [] rgbOut) { 

        final int size = rgbIn.length;

        int index = 0;

        for (int i=0;i<size;i+=3) { 

            final int b = (0xff & rgbIn[i]);
            final int g = (0xff & rgbIn[i+1]);
            final int r = (0xff & rgbIn[i+2]);

            rgbOut[index++] = 0xFF000000 | r << 16 | g << 8 | b;
        }
    }

    public static final void RGB24toARGB32(int width, int height, ByteBuffer in, int [] rgbOut) { 

        final int size = width * height * 3;

        int index = 0;

        for (int i=0;i<size;i+=3) { 

            final int b = (0xff & in.get(i));
            final int g = (0xff & in.get(i+1));
            final int r = (0xff & in.get(i+2));

            rgbOut[index++] = 0xFF000000 | r << 16 | g << 8 | b;
        }
    }

    public static final void RGB24toARGB32(int width, int height, ByteBuffer in, ByteBuffer out) { 

        final int size = width * height * 3;

     //   System.out.println("Converting " + size);

        in.limit(width * height * 3);
        in.position(0);
        
        out.limit(width * height * 4);
        out.position(0);
        
        final byte [] tmp = new byte[4];
        tmp[0] = (byte) 0xFF;
        
        for (int i=0;i<size;i+=3) { 
            in.get(tmp, 1, 3);
            
            // Swap R and B
            byte x = tmp[1];
            tmp[1] = tmp[3];
            tmp[3] = x;
            
            out.put(tmp);
        }
    }
    
    public static final void ARGB32toRGB24(ByteBuffer in, ByteBuffer out) { 

        final int size = in.remaining();
        final byte [] tmp = new byte[4];
        
        for (int i=0;i<size;i+=4) { 
            in.get(tmp);
            out.put(tmp, 1, 3);
        }
    }

    
    
    public static final void RGB48toARGB32(short [] rgbIn, int [] rgbOut) { 

        final int size = rgbIn.length;

        int index = 0;

        for (int i=0;i<size;i+=3) { 

            final int b = (byte) (((rgbIn[index] & 0xffff) / 255) & 0xff);
            final int g = (byte) (((rgbIn[index+1] & 0xffff) / 255) & 0xff);
            final int r = (byte) (((rgbIn[index+2] & 0xffff) / 255) & 0xff);

            rgbOut[index++] = 0xFF000000 | r << 16 | g << 8 | b;
        }
    }

    public static final void RGB48toARGB32(ByteBuffer in, int [] rgbOut) { 

        final ShortBuffer tmp = in.asShortBuffer();

        final int size = tmp.remaining();

        int index = 0;

        for (int i=0;i<size;i+=3) { 

            final int b = (byte) (((tmp.get(i) & 0xffff) / 255) & 0xff);
            final int g = (byte) (((tmp.get(i+1) & 0xffff) / 255) & 0xff);
            final int r = (byte) (((tmp.get(i+2) & 0xffff) / 255) & 0xff);

            rgbOut[index++] = 0xFF000000 | r << 16 | g << 8 | b;
        }
    }


    public static final void ARGB64toARGB32(short [] rgbIn, int [] rgbOut) { 

        final int size = rgbIn.length;

        int index = 0;

        for (int i=0;i<size;i+=4) { 

            final int b = (byte) (((rgbIn[index] & 0xffff) / 255) & 0xff);
            final int g = (byte) (((rgbIn[index+1] & 0xffff) / 255) & 0xff);
            final int r = (byte) (((rgbIn[index+2] & 0xffff) / 255) & 0xff);
            final int a = (byte) (((rgbIn[index+3] & 0xffff) / 255) & 0xff);

            rgbOut[index++] = a << 24 | r << 16 | g << 8 | b;
        }
    }

    public static final void ARGB64toARGB32(ByteBuffer in, int [] rgbOut) { 

        final ShortBuffer tmp = in.asShortBuffer();

        final int size = tmp.remaining();

        int index = 0;

        for (int i=0;i<size;i+=4) { 

            final int b = (byte) (((tmp.get(i) & 0xffff) / 255) & 0xff);
            final int g = (byte) (((tmp.get(i+1) & 0xffff) / 255) & 0xff);
            final int r = (byte) (((tmp.get(i+2) & 0xffff) / 255) & 0xff);
            final int a = (byte) (((tmp.get(i+3) & 0xffff) / 255) & 0xff);

            rgbOut[index++] = a << 24 | r << 16 | g << 8 | b;
        }
    }

    public static void YUYVtoARGB32(int width, int height, byte [] dataIn, int[] dataOut) {

        int index = 0;

        for (int h=0;h<height;h++) { 
            for (int w=0;w<width*2;w+=4) { 
                int Y1 = (0xff & dataIn[h*width*2 + w]);
                int U  = (0xff & dataIn[h*width*2 + (w+1)]);
                int Y2 = (0xff & dataIn[h*width*2 + (w+2)]);
                int V  = (0xff & dataIn[h*width*2 + (w+3)]);

                int r1 = clipAndScale(Y1 + (1.370705 * (V-128)));
                int g1 = clipAndScale(Y1 - (0.698001 * (V-128)) - (0.337633 * (U-128)));
                int b1 = clipAndScale(Y1 + (1.732446 * (U-128)));

                int r2 = clipAndScale(Y2 + (1.370705 * (V-128)));
                int g2 = clipAndScale(Y2 - (0.698001 * (V-128)) - (0.337633 * (U-128)));
                int b2 = clipAndScale(Y2 + (1.732446 * (U-128)));

                dataOut[index++] = 0xFF000000 | r1 << 16 | g1 << 8 | b1;
                dataOut[index++] = 0xFF000000 | r2 << 16 | g2 << 8 | b2;
            }
        } 
    }

    public static void YUYVtoARGB32(int width, int height, ByteBuffer in, int[] out) {

        int index = 0;

        for (int h=0;h<height;h++) { 
            for (int w=0;w<width*2;w+=4) { 
                int Y1 = (0xff & in.get(h*width*2 + w));
                int U  = (0xff & in.get(h*width*2 + (w+1)));
                int Y2 = (0xff & in.get(h*width*2 + (w+2)));
                int V  = (0xff & in.get(h*width*2 + (w+3)));

                int r1 = clipAndScale(Y1 + (1.370705 * (V-128)));
                int g1 = clipAndScale(Y1 - (0.698001 * (V-128)) - (0.337633 * (U-128)));
                int b1 = clipAndScale(Y1 + (1.732446 * (U-128)));

                int r2 = clipAndScale(Y2 + (1.370705 * (V-128)));
                int g2 = clipAndScale(Y2 - (0.698001 * (V-128)) - (0.337633 * (U-128)));
                int b2 = clipAndScale(Y2 + (1.732446 * (U-128)));

                out[index++] = 0xFF000000 | r1 << 16 | g1 << 8 | b1;
                out[index++] = 0xFF000000 | r2 << 16 | g2 << 8 | b2;
            }
        } 
    }

    public static void YUYVtoARGB32(int width, int height, ByteBuffer in, ByteBuffer out) {
        
        final byte [] tmpIn = new byte[4];
        final byte [] tmpOut = new byte[8];

        // Set alpha values in output
        tmpOut[0] = (byte) 0xFF;
        tmpOut[4] = (byte) 0xFF;
        
        in.position(0);
        in.limit(in.capacity());
        
        out.position(0);
        out.limit(out.capacity());
        
      //  System.out.println("Converting: " + in.capacity() + " -> " + out.capacity()); 
        
        for (int h=0;h<height;h++) { 
            for (int w=0;w<width*2;w+=4) {
                
                in.get(tmpIn);
                
                int Y1 = (0xff & tmpIn[0]);
                int U  = (0xff & tmpIn[1]) - 128;
                int Y2 = (0xff & tmpIn[2]);
                int V  = (0xff & tmpIn[3]) - 128;
                
                tmpOut[1] = (byte) clipAndScale(Y1 + (1.370705 * V));
                tmpOut[2] = (byte) clipAndScale(Y1 - (0.698001 * V) - (0.337633 * U));
                tmpOut[3] = (byte) clipAndScale(Y1 + + (1.732446 * U));                
                
                tmpOut[5] = (byte) clipAndScale(Y2 + (1.370705 * V));
                tmpOut[6] = (byte) clipAndScale(Y2 - (0.698001 * V) - (0.337633 * U));
                tmpOut[7] = (byte) clipAndScale(Y2 + (1.732446 * U));                 
                 
                /*
                final int Y1 = (0xff & tmpIn[0]);
                final int U  = (0xff & tmpIn[1]);
                final int Y2 = (0xff & tmpIn[2]);
                final int V  = (0xff & tmpIn[3]);
                
                tmpOut[1] = clipAndScale((298 * Y1 + 409 * U + 128) >> 8);
                tmpOut[5] = clipAndScale((298 * Y2 + 409 * U + 128) >> 8);

                tmpOut[2] = clipAndScale((298 * Y1 - 100 * V - 208 * U + 128) >> 8);
                tmpOut[6] = clipAndScale((298 * Y2 - 100 * V - 208 * U + 128) >> 8);

                tmpOut[2] = clipAndScale((298 * Y1 + 516 * V + 128) >> 8);
                tmpOut[7] = clipAndScale((298 * Y2 + 516 * V + 128) >> 8);
                 */
                out.put(tmpOut);
            }
        } 
    }

    
    public static void YUV420SPtoRGB24(int width, int height, byte [] dataIn, byte[] dataOut) {
        
        final int offsetU = width * height;
        // 3 bytes per pixel in a RGB24Image

        for (int h = 0; h < height; h += 2) {
            for (int w = 0; w < width; w += 2) {
                int U = (0xff & dataIn[offsetU + h * width / 2 + w]) - 128;
                int V = (0xff & dataIn[offsetU + h * width / 2 + w + 1]) - 128;

                int Y1 = (0xff & dataIn[h * width + w]) - 16;
                int Y2 = (0xff & dataIn[h * width + (w + 1)]) - 16;
                int Y3 = (0xff & dataIn[(h + 1) * width + w]) - 16;
                int Y4 = (0xff & dataIn[(h + 1) * width + (w + 1)]) - 16;

                int r1 = clipAndScale((298 * Y1 + 409 * U + 128) >> 8);
                int r2 = clipAndScale((298 * Y2 + 409 * U + 128) >> 8);
                int r3 = clipAndScale((298 * Y3 + 409 * U + 128) >> 8);
                int r4 = clipAndScale((298 * Y4 + 409 * U + 128) >> 8);

                int g1 = clipAndScale((298 * Y1 - 100 * V - 208 * U + 128) >> 8);
                int g2 = clipAndScale((298 * Y2 - 100 * V - 208 * U + 128) >> 8);
                int g3 = clipAndScale((298 * Y3 - 100 * V - 208 * U + 128) >> 8);
                int g4 = clipAndScale((298 * Y4 - 100 * V - 208 * U + 128) >> 8);

                int b1 = clipAndScale((298 * Y1 + 516 * V + 128) >> 8);
                int b2 = clipAndScale((298 * Y2 + 516 * V + 128) >> 8);
                int b3 = clipAndScale((298 * Y3 + 516 * V + 128) >> 8);
                int b4 = clipAndScale((298 * Y4 + 516 * V + 128) >> 8);

                dataOut[h * width * 3 + w * 3] = (byte) (0xff & r1);
                dataOut[h * width * 3 + w * 3 + 1] = (byte) (0xff & g1);
                dataOut[h * width * 3 + w * 3 + 2] = (byte) (0xff & b1);

                dataOut[h * width * 3 + w * 3 + 3] = (byte) (0xff & r2);
                dataOut[h * width * 3 + w * 3 + 4] = (byte) (0xff & g2);
                dataOut[h * width * 3 + w * 3 + 5] = (byte) (0xff & b2);

                dataOut[(h + 1) * width * 3 + w * 3] = (byte) (0xff & r3);
                dataOut[(h + 1) * width * 3 + w * 3 + 1] = (byte) (0xff & g3);
                dataOut[(h + 1) * width * 3 + w * 3 + 2] = (byte) (0xff & b3);

                dataOut[(h + 1) * width * 3 + w * 3 + 3] = (byte) (0xff & r4);
                dataOut[(h + 1) * width * 3 + w * 3 + 4] = (byte) (0xff & g4);
                dataOut[(h + 1) * width * 3 + w * 3 + 5] = (byte) (0xff & b4);
            }
        }
    }
 
    public static void YUV420SPtoRGB24(int width, int height,ByteBuffer in, byte[] dataOut) {
        
        final int offsetU = width * height;
        // 3 bytes per pixel in a RGB24Image

        for (int h = 0; h < height; h += 2) {
            for (int w = 0; w < width; w += 2) {
                int U = (0xff & in.get(offsetU + h * width / 2 + w)) - 128;
                int V = (0xff & in.get(offsetU + h * width / 2 + w + 1)) - 128;

                int Y1 = (0xff & in.get(h * width + w)) - 16;
                int Y2 = (0xff & in.get(h * width + (w + 1))) - 16;
                int Y3 = (0xff & in.get((h + 1) * width + w)) - 16;
                int Y4 = (0xff & in.get((h + 1) * width + (w + 1))) - 16;

                int r1 = clipAndScale((298 * Y1 + 409 * U + 128) >> 8);
                int r2 = clipAndScale((298 * Y2 + 409 * U + 128) >> 8);
                int r3 = clipAndScale((298 * Y3 + 409 * U + 128) >> 8);
                int r4 = clipAndScale((298 * Y4 + 409 * U + 128) >> 8);

                int g1 = clipAndScale((298 * Y1 - 100 * V - 208 * U + 128) >> 8);
                int g2 = clipAndScale((298 * Y2 - 100 * V - 208 * U + 128) >> 8);
                int g3 = clipAndScale((298 * Y3 - 100 * V - 208 * U + 128) >> 8);
                int g4 = clipAndScale((298 * Y4 - 100 * V - 208 * U + 128) >> 8);

                int b1 = clipAndScale((298 * Y1 + 516 * V + 128) >> 8);
                int b2 = clipAndScale((298 * Y2 + 516 * V + 128) >> 8);
                int b3 = clipAndScale((298 * Y3 + 516 * V + 128) >> 8);
                int b4 = clipAndScale((298 * Y4 + 516 * V + 128) >> 8);

                dataOut[h * width * 3 + w * 3] = (byte) (0xff & r1);
                dataOut[h * width * 3 + w * 3 + 1] = (byte) (0xff & g1);
                dataOut[h * width * 3 + w * 3 + 2] = (byte) (0xff & b1);

                dataOut[h * width * 3 + w * 3 + 3] = (byte) (0xff & r2);
                dataOut[h * width * 3 + w * 3 + 4] = (byte) (0xff & g2);
                dataOut[h * width * 3 + w * 3 + 5] = (byte) (0xff & b2);

                dataOut[(h + 1) * width * 3 + w * 3] = (byte) (0xff & r3);
                dataOut[(h + 1) * width * 3 + w * 3 + 1] = (byte) (0xff & g3);
                dataOut[(h + 1) * width * 3 + w * 3 + 2] = (byte) (0xff & b3);

                dataOut[(h + 1) * width * 3 + w * 3 + 3] = (byte) (0xff & r4);
                dataOut[(h + 1) * width * 3 + w * 3 + 4] = (byte) (0xff & g4);
                dataOut[(h + 1) * width * 3 + w * 3 + 5] = (byte) (0xff & b4);
            }
        }
    }
    
    public static void YUV420SPtoARGB32(int width, int height,ByteBuffer in, int [] dataOut) {
        
        final int offsetU = width * height;
        // 3 bytes per pixel in a RGB24Image

        int index = 0;
        
        for (int h = 0; h < height; h += 2) {
            for (int w = 0; w < width; w += 2) {
                int U = (0xff & in.get(offsetU + h * width / 2 + w)) - 128;
                int V = (0xff & in.get(offsetU + h * width / 2 + w + 1)) - 128;

                int Y1 = (0xff & in.get(h * width + w)) - 16;
                int Y2 = (0xff & in.get(h * width + (w + 1))) - 16;
                int Y3 = (0xff & in.get((h + 1) * width + w)) - 16;
                int Y4 = (0xff & in.get((h + 1) * width + (w + 1))) - 16;

                int r1 = clipAndScale((298 * Y1 + 409 * U + 128) >> 8);
                int r2 = clipAndScale((298 * Y2 + 409 * U + 128) >> 8);
                int r3 = clipAndScale((298 * Y3 + 409 * U + 128) >> 8);
                int r4 = clipAndScale((298 * Y4 + 409 * U + 128) >> 8);

                int g1 = clipAndScale((298 * Y1 - 100 * V - 208 * U + 128) >> 8);
                int g2 = clipAndScale((298 * Y2 - 100 * V - 208 * U + 128) >> 8);
                int g3 = clipAndScale((298 * Y3 - 100 * V - 208 * U + 128) >> 8);
                int g4 = clipAndScale((298 * Y4 - 100 * V - 208 * U + 128) >> 8);

                int b1 = clipAndScale((298 * Y1 + 516 * V + 128) >> 8);
                int b2 = clipAndScale((298 * Y2 + 516 * V + 128) >> 8);
                int b3 = clipAndScale((298 * Y3 + 516 * V + 128) >> 8);
                int b4 = clipAndScale((298 * Y4 + 516 * V + 128) >> 8);

                dataOut[index++] = 0xFF000000 & 
                    ((byte) (0xff & r1)) << 16 & 
                    ((byte) (0xff & g1)) << 8 & 
                    ((byte) (0xff & b1));

                dataOut[index++] = 0xFF000000 & 
                    ((byte) (0xff & r2)) << 16 & 
                    ((byte) (0xff & g2)) << 8 & 
                    ((byte) (0xff & b2));

                dataOut[index++] = 0xFF000000 & 
                    ((byte) (0xff & r3)) << 16 & 
                    ((byte) (0xff & g3)) << 8 & 
                    ((byte) (0xff & b3));
                
                dataOut[index++] = 0xFF000000 & 
                    ((byte) (0xff & r4)) << 16 & 
                    ((byte) (0xff & g4)) << 8 & 
                    ((byte) (0xff & b4));
            }
        }
    }

    
    public static void YUV420SPtoRGB24(int width, int height,ByteBuffer in, ByteBuffer out) {
        
        // TODO: optimize this!
        
        final int offsetU = width * height;

        for (int h = 0; h < height; h += 2) {
            for (int w = 0; w < width; w += 2) {
                
                int U = (0xff & in.get(offsetU + h * width / 2 + w)) - 128;
                int V = (0xff & in.get(offsetU + h * width / 2 + w + 1)) - 128;

                int Y1 = (0xff & in.get(h * width + w)) - 16;
                int Y2 = (0xff & in.get(h * width + (w + 1))) - 16;
                int Y3 = (0xff & in.get((h + 1) * width + w)) - 16;
                int Y4 = (0xff & in.get((h + 1) * width + (w + 1))) - 16;

                int r1 = clipAndScale((298 * Y1 + 409 * U + 128) >> 8);
                int r2 = clipAndScale((298 * Y2 + 409 * U + 128) >> 8);
                int r3 = clipAndScale((298 * Y3 + 409 * U + 128) >> 8);
                int r4 = clipAndScale((298 * Y4 + 409 * U + 128) >> 8);

                int g1 = clipAndScale((298 * Y1 - 100 * V - 208 * U + 128) >> 8);
                int g2 = clipAndScale((298 * Y2 - 100 * V - 208 * U + 128) >> 8);
                int g3 = clipAndScale((298 * Y3 - 100 * V - 208 * U + 128) >> 8);
                int g4 = clipAndScale((298 * Y4 - 100 * V - 208 * U + 128) >> 8);

                int b1 = clipAndScale((298 * Y1 + 516 * V + 128) >> 8);
                int b2 = clipAndScale((298 * Y2 + 516 * V + 128) >> 8);
                int b3 = clipAndScale((298 * Y3 + 516 * V + 128) >> 8);
                int b4 = clipAndScale((298 * Y4 + 516 * V + 128) >> 8);

                out.put(h * width * 3 + w * 3, (byte) (0xff & r1));
                out.put(h * width * 3 + w * 3 + 1, (byte) (0xff & g1));
                out.put(h * width * 3 + w * 3 + 2, (byte) (0xff & b1));

                out.put(h * width * 3 + w * 3 + 3, (byte) (0xff & r2));
                out.put(h * width * 3 + w * 3 + 4, (byte) (0xff & g2));
                out.put(h * width * 3 + w * 3 + 5, (byte) (0xff & b2));

                out.put((h + 1) * width * 3 + w * 3, (byte) (0xff & r3));
                out.put((h + 1) * width * 3 + w * 3 + 1, (byte) (0xff & g3));
                out.put((h + 1) * width * 3 + w * 3 + 2, (byte) (0xff & b3));

                out.put((h + 1) * width * 3 + w * 3 + 3, (byte) (0xff & r4));
                out.put((h + 1) * width * 3 + w * 3 + 4, (byte) (0xff & g4));
                out.put((h + 1) * width * 3 + w * 3 + 5, (byte) (0xff & b4));
            }
        }
    }
    
    public static void YUV422SPtoRGB24(int width, int height, byte [] dataIn, byte[] dataOut) {
        
        final int offsetU = width * height;
        // 3 bytes per pixel in a RGB24Image
        
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w += 2) {

                int Y1 = (0xff & dataIn[h * width + w]) - 16;
                int Y2 = (0xff & dataIn[h * width + (w + 1)]) - 16;

                int U = (0xff & dataIn[offsetU + h * width + w]) - 128;
                int V = (0xff & dataIn[offsetU + h * width + w + 1]) - 128;

                int r1 = clipAndScale((298 * Y1 + 409 * U + 128) >> 8);
                int r2 = clipAndScale((298 * Y2 + 409 * U + 128) >> 8);

                int g1 = clipAndScale((298 * Y1 - 100 * V - 208 * U + 128) >> 8);
                int g2 = clipAndScale((298 * Y2 - 100 * V - 208 * U + 128) >> 8);

                int b1 = clipAndScale((298 * Y1 + 516 * V + 128) >> 8);
                int b2 = clipAndScale((298 * Y2 + 516 * V + 128) >> 8);

                dataOut[h * width * 3 + w * 3] = (byte) (0xff & r1);
                dataOut[h * width * 3 + w * 3 + 1] = (byte) (0xff & g1);
                dataOut[h * width * 3 + w * 3 + 2] = (byte) (0xff & b1);

                dataOut[h * width * 3 + w * 3 + 3] = (byte) (0xff & r2);
                dataOut[h * width * 3 + w * 3 + 4] = (byte) (0xff & g2);
                dataOut[h * width * 3 + w * 3 + 5] = (byte) (0xff & b2);
            }
        }
    }
 
    public static void YUV422SPtoRGB24(int width, int height,ByteBuffer in, byte[] dataOut) {
        
        final int offsetU = width * height;
        // 3 bytes per pixel in a RGB24Image
        
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w += 2) {

                int Y1 = (0xff & in.get(h * width + w)) - 16;
                int Y2 = (0xff & in.get(h * width + (w + 1))) - 16;

                int U = (0xff & in.get(offsetU + h * width + w)) - 128;
                int V = (0xff & in.get(offsetU + h * width + w + 1)) - 128;

                int r1 = clipAndScale((298 * Y1 + 409 * U + 128) >> 8);
                int r2 = clipAndScale((298 * Y2 + 409 * U + 128) >> 8);

                int g1 = clipAndScale((298 * Y1 - 100 * V - 208 * U + 128) >> 8);
                int g2 = clipAndScale((298 * Y2 - 100 * V - 208 * U + 128) >> 8);

                int b1 = clipAndScale((298 * Y1 + 516 * V + 128) >> 8);
                int b2 = clipAndScale((298 * Y2 + 516 * V + 128) >> 8);

                dataOut[h * width * 3 + w * 3] = (byte) (0xff & r1);
                dataOut[h * width * 3 + w * 3 + 1] = (byte) (0xff & g1);
                dataOut[h * width * 3 + w * 3 + 2] = (byte) (0xff & b1);

                dataOut[h * width * 3 + w * 3 + 3] = (byte) (0xff & r2);
                dataOut[h * width * 3 + w * 3 + 4] = (byte) (0xff & g2);
                dataOut[h * width * 3 + w * 3 + 5] = (byte) (0xff & b2);
            }
        }
    }

    public static void YUV422SPtoARGB32(int width, int height,ByteBuffer in, int [] dataOut) {
        
        final int offsetU = width * height;
        // 3 bytes per pixel in a RGB24Image
        
        int index = 0;
        
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w += 2) {

                int Y1 = (0xff & in.get(h * width + w)) - 16;
                int Y2 = (0xff & in.get(h * width + (w + 1))) - 16;

                int U = (0xff & in.get(offsetU + h * width + w)) - 128;
                int V = (0xff & in.get(offsetU + h * width + w + 1)) - 128;

                int r1 = clipAndScale((298 * Y1 + 409 * U + 128) >> 8);
                int r2 = clipAndScale((298 * Y2 + 409 * U + 128) >> 8);

                int g1 = clipAndScale((298 * Y1 - 100 * V - 208 * U + 128) >> 8);
                int g2 = clipAndScale((298 * Y2 - 100 * V - 208 * U + 128) >> 8);

                int b1 = clipAndScale((298 * Y1 + 516 * V + 128) >> 8);
                int b2 = clipAndScale((298 * Y2 + 516 * V + 128) >> 8);

                dataOut[index++] = 0xFF000000 & 
                    ((byte) (0xff & r1)) << 16 & 
                    ((byte) (0xff & g1)) << 8 & 
                    ((byte) (0xff & b1));
                    
                dataOut[index++] = 0xFF000000 & 
                    ((byte) (0xff & r2)) << 16 & 
                    ((byte) (0xff & g2)) << 8 & 
                    ((byte) (0xff & b2));
            }
        }
    }

    public static void YUV422SPtoARGB32(int width, int height, ByteBuffer in, ByteBuffer out) {

        // TODO: Optimize!
        
        final int offsetU = width * height;
        // 3 bytes per pixel in a RGB24Image
        
        IntBuffer iout = out.asIntBuffer();
        
        int index = 0;
        
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w += 2) {

                int Y1 = (0xff & in.get(h * width + w)) - 16;
                int Y2 = (0xff & in.get(h * width + (w + 1))) - 16;

                int U = (0xff & in.get(offsetU + h * width + w)) - 128;
                int V = (0xff & in.get(offsetU + h * width + w + 1)) - 128;

                int r1 = clipAndScale((298 * Y1 + 409 * U + 128) >> 8);
                int r2 = clipAndScale((298 * Y2 + 409 * U + 128) >> 8);

                int g1 = clipAndScale((298 * Y1 - 100 * V - 208 * U + 128) >> 8);
                int g2 = clipAndScale((298 * Y2 - 100 * V - 208 * U + 128) >> 8);

                int b1 = clipAndScale((298 * Y1 + 516 * V + 128) >> 8);
                int b2 = clipAndScale((298 * Y2 + 516 * V + 128) >> 8);

                iout.put(index++, 0xFF000000 & 
                    ((byte) (0xff & r1)) << 16 & 
                    ((byte) (0xff & g1)) << 8 & 
                    ((byte) (0xff & b1)));
                    
                iout.put(index++, 0xFF000000 & 
                    ((byte) (0xff & r2)) << 16 & 
                    ((byte) (0xff & g2)) << 8 & 
                    ((byte) (0xff & b2)));
            }
        }
    }

    
    public static void YUV422SPtoRGB24(int width, int height,ByteBuffer in, ByteBuffer out) {
    
        // TODO: Optimize this!
        
        final int offsetU = width * height;
        // 3 bytes per pixel in a RGB24Image
        
        for (int h = 0; h < height; h++) {
            for (int w = 0; w < width; w += 2) {

                int Y1 = (0xff & in.get(h * width + w)) - 16;
                int Y2 = (0xff & in.get(h * width + (w + 1))) - 16;

                int U = (0xff & in.get(offsetU + h * width + w)) - 128;
                int V = (0xff & in.get(offsetU + h * width + w + 1)) - 128;

                int r1 = clipAndScale((298 * Y1 + 409 * U + 128) >> 8);
                int r2 = clipAndScale((298 * Y2 + 409 * U + 128) >> 8);

                int g1 = clipAndScale((298 * Y1 - 100 * V - 208 * U + 128) >> 8);
                int g2 = clipAndScale((298 * Y2 - 100 * V - 208 * U + 128) >> 8);

                int b1 = clipAndScale((298 * Y1 + 516 * V + 128) >> 8);
                int b2 = clipAndScale((298 * Y2 + 516 * V + 128) >> 8);

                out.put(h * width * 3 + w * 3, (byte) (0xff & r1));
                out.put(h * width * 3 + w * 3 + 1, (byte) (0xff & g1));
                out.put(h * width * 3 + w * 3 + 2, (byte) (0xff & b1));

                out.put(h * width * 3 + w * 3 + 3, (byte) (0xff & r2));
                out.put(h * width * 3 + w * 3 + 4, (byte) (0xff & g2));
                out.put(h * width * 3 + w * 3 + 5, (byte) (0xff & b2));
            }
        }
    }
}
