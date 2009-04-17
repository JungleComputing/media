package ibis.video4j.devices.video4linux.convertors;

import java.nio.ByteBuffer;

import ibis.video4j.devices.video4linux.Convertor;

public class RGB24toARGB32 extends Convertor {

    private final static int COST = 3 + 1;
    
    public RGB24toARGB32() {
        super(COST);
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
        
        final int size = width * height * 3;
        
        int index = 0;
        
        for (int i=0;i<size;i+=3) { 
        
            final int b = (0xff & in.get(i));
            final int g = (0xff & in.get(i+1));
            final int r = (0xff & in.get(i+2));
            
            out[index++] = 0xFF000000 | r << 16 | g << 8 | b;
        }
    }

}
