package ibis.video4j.devices.video4linux.convertors;

import java.nio.ByteBuffer;
import ibis.video4j.devices.video4linux.Convertor;

public final class YUYVtoARGB32 extends Convertor {

    private final static int COST = 4 + (6*4) + 2;
    
    public YUYVtoARGB32() {
        super(COST);
    }

    private static final int clipAndScale(double value) { 
        
        if (value > 255) value = 255;

        if (value < 0) value = 0;

        value = value * 220.0 / 256.0;

        return (int) value;
    }
    
    @Override
    public void convert(int width, int height, Object oin, Object oout) {
        
       // System.out.println("Converting " + width + " " + height);
        
        if (!(oin instanceof ByteBuffer)) { 
            throw new RuntimeException("Expecting ByteBuffer as input!");
        }
        
        if (!(oout instanceof int [])) { 
            throw new RuntimeException("Expecting int [] as output!");
        }
        
        ByteBuffer in = (ByteBuffer) oin;
        int [] out = (int []) oout;
        
       // System.out.println("In size " + in.capacity());
       // System.out.println("Out size 4*" + out.length);
        
        
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

    
    
}
