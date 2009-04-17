package ibis.video4j.devices.video4linux.convertors;

import ibis.video4j.devices.video4linux.Convertor;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

public class MJPEGtoMJPEG extends Convertor { 

    private final static int COST = 1;

    public MJPEGtoMJPEG() {
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
        
        IntBuffer in = ((ByteBuffer) oin).asIntBuffer();
        int [] out = (int []) oout;
        
        System.out.println("Copying " + in.position() + " bytes");

        in.get(out);
    }
}
