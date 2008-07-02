package ibis.video4j.utils;

import java.nio.ByteBuffer;

public class Convert {

    /*

    C = Y' - 16
    D = U - 128
    E = V - 128

    R = clip(( 298 * C           + 409 * E + 128) >> 8)
    G = clip(( 298 * C - 100 * D - 208 * E + 128) >> 8)
    B = clip(( 298 * C + 516 * D           + 128) >> 8)
    */
 
    private static final int clip(int value) { 
        
        if (value < 0) { 
            return 0;
        } else if (value > 255) { 
            return 255;
        } else { 
            return value;
        }
    }
    
    public static void YUV420PtoRGB32(int width, int height, ByteBuffer in, int [] out) { 
        
        final int offsetU = width * height;
        final int offsetV = offsetU + (width * height)/4;
        
        for (int h=0;h<height;h+=2) { 
            for (int w=0;w<width;w+=2) { 
                
                int C1 = (0xff & in.get(h*width + w)) - 16;
                int C2 = (0xff & in.get(h*width + (w + 1))) - 16;
                int C3 = (0xff & in.get((h+1)*width + w)) - 16;
                int C4 = (0xff & in.get((h+1)*width + (w + 1))) - 16;
 
                int D = (0xff & in.get(offsetU + (h/2) * (width/2) + w/2)) - 128;
                int E = (0xff & in.get(offsetV + (h/2) * (width/2) + w/2)) - 128;
                
                int r1 = clip(( 298 * C1 + 409 * E + 128) >> 8);
                int r2 = clip(( 298 * C2 + 409 * E + 128) >> 8);
                int r3 = clip(( 298 * C3 + 409 * E + 128) >> 8);
                int r4 = clip(( 298 * C4 + 409 * E + 128) >> 8);
            
                int g1 = clip(( 298 * C1 - 100 * D - 208 * E + 128) >> 8);
                int g2 = clip(( 298 * C2 - 100 * D - 208 * E + 128) >> 8);
                int g3 = clip(( 298 * C3 - 100 * D - 208 * E + 128) >> 8);
                int g4 = clip(( 298 * C4 - 100 * D - 208 * E + 128) >> 8);
                 
                int b1 = clip((298 * C1 + 516 * D           + 128) >> 8);
                int b2 = clip((298 * C2 + 516 * D           + 128) >> 8);
                int b3 = clip((298 * C3 + 516 * D           + 128) >> 8);
                int b4 = clip((298 * C4 + 516 * D           + 128) >> 8);
                
                out[h*width+w]         = 0xFF000000 | r1 << 16 | g1 << 8 | b1;
                out[h*width+w+1]       = 0xFF000000 | r2 << 16 | g2 << 8 | b2;
                out[(h+1)*width+w]     = 0xFF000000 | r3 << 16 | g3 << 8 | b3;
                out[(h+1)*width+(w+1)] = 0xFF000000 | r4 << 16 | g4 << 8 | b4;
            }
        } 
    }

    private static final int clipAndScale(double value) { 
        
        if (value > 255) value = 255;

        if (value < 0) value = 0;

        value = value * 220.0 / 256.0;

        return (int) value;
    }
    
    public static void YUYVtoRGB32(int width, int height, ByteBuffer in, int [] out) { 
        
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
    
    public static void RGB24toRGB32(int currentWidth, int currentHeight, 
            ByteBuffer in, int[] pixels) {
  
        final int size  = currentHeight * currentWidth;
        int index = 0;
        
        for (int i=0;i<size;i++) {
            
            final int r = (0xff & in.get(index++));
            final int g = (0xff & in.get(index++));
            final int b = (0xff & in.get(index++));
          
            pixels[i] = 0xff000000 | r << 16 | g << 8 | b;             
        }
 
    }

    public static void NV12toRGB32(int currentWidth, int currentHeight, 
            ByteBuffer in, int[] pixels) {
        
        final int size  = currentHeight * currentWidth;
        int index = 0;
        
        for (int i=0;i<size;i++) {
            
            final int v = (0xff & in.get(index++));
            
            pixels[i] = 0xff000000 | v << 16 | v << 8 | v;             
        }
        
    }
}
