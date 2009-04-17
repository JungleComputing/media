package ibis.video4j.devices.video4linux.convertors;

import java.nio.ByteBuffer;

import ibis.video4j.devices.video4linux.Convertor;

public class YUV420PtoARGB32 extends Convertor {
    
    private final static int COST = 6 + 12 + 4;
    
    public YUV420PtoARGB32() {
        super(COST);
    }
    
    private static final int clipAndScale(int value) { 
        
        if (value > 255) value = 255;

        if (value < 0) value = 0;

        return (value * 220) / 256;
    }
    
    @Override
    public void convert(int width, int height, Object oin, Object oout) {
       
        if (!(oin instanceof ByteBuffer)) { 
            throw new RuntimeException("Expecting ByteBuffer as input!");
        }
        
        if (!(oout instanceof int [])) { 
            throw new RuntimeException("Expecting int [] as output!");
        }
        
        ByteBuffer in = (ByteBuffer) oin;
        int [] out = (int []) oout;
        
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
                
                int r1 = clipAndScale(( 298 * C1 + 409 * E + 128) >> 8);
                int r2 = clipAndScale(( 298 * C2 + 409 * E + 128) >> 8);
                int r3 = clipAndScale(( 298 * C3 + 409 * E + 128) >> 8);
                int r4 = clipAndScale(( 298 * C4 + 409 * E + 128) >> 8);
            
                int g1 = clipAndScale(( 298 * C1 - 100 * D - 208 * E + 128) >> 8);
                int g2 = clipAndScale(( 298 * C2 - 100 * D - 208 * E + 128) >> 8);
                int g3 = clipAndScale(( 298 * C3 - 100 * D - 208 * E + 128) >> 8);
                int g4 = clipAndScale(( 298 * C4 - 100 * D - 208 * E + 128) >> 8);
                 
                int b1 = clipAndScale((298 * C1 + 516 * D           + 128) >> 8);
                int b2 = clipAndScale((298 * C2 + 516 * D           + 128) >> 8);
                int b3 = clipAndScale((298 * C3 + 516 * D           + 128) >> 8);
                int b4 = clipAndScale((298 * C4 + 516 * D           + 128) >> 8);
                
                out[h*width+w]         = 0xFF000000 | r1 << 16 | g1 << 8 | b1;
                out[h*width+w+1]       = 0xFF000000 | r2 << 16 | g2 << 8 | b2;
                out[(h+1)*width+w]     = 0xFF000000 | r3 << 16 | g3 << 8 | b3;
                out[(h+1)*width+(w+1)] = 0xFF000000 | r4 << 16 | g4 << 8 | b4;
            }
        } 
    }

}
