package ibis.imaging4j.effects;

import java.nio.ByteBuffer;

import ibis.imaging4j.Format;
import ibis.imaging4j.Image;
import ibis.imaging4j.conversion.ConversionException;

public class RGB24Scaler extends Scaler {

    private static final int COST = 10; // random value!
    
    public RGB24Scaler() {
        super(COST);
    }
    
    @Override
    public void scale(Image in, Image out) throws Exception {
     
        // Check the parameters
        if (out == null) { 
            throw new NullPointerException("Destionation image is null!");
        }
    
        final int width = in.getWidth();
        final int height = in.getHeight();
        
        final int targetW = out.getWidth();
        final int targetH = out.getHeight();
        
        if (targetH <= 0 || targetW <= 0) { 
            throw new ConversionException("Targer image has " +
                        "invalid dimensions!");
        }
        
        // If the target has the same dimensions as the source we copy. 
        if (targetW == width && targetH == height) {
            Image.copy(in, out);
            return;
        }
        
        // Otherwise we do a  simple interpolation. 
        // NOTE: Not necessarily a decent algorithm!
        final double multX = ((double) width) / targetW;
        final double multY = ((double) height) / targetH;
        
        final ByteBuffer src = in.getData();        
        final ByteBuffer dst = out.getData();
        
        for (int h=0;h<targetH;h++) { 
            
            for (int w=0;w<targetW;w++) {
                
                final int srcW = (int) (w * multX);
                final int srcH = (int) (h * multY);
                                
           //     System.out.println(w + " x " + h + " -> " + (targetW * multX) 
           //             + " x " + (h * multY));
                
                final int sourceIndex = ((srcH * width) + srcW) * 3;
                final int targetIndex = ((h * targetW) + w) * 3;
                
                // EEP: this is way to slow
                dst.put(targetIndex + 0, src.get(sourceIndex + 0));
                dst.put(targetIndex + 1, src.get(sourceIndex + 1));
                dst.put(targetIndex + 2, src.get(sourceIndex + 2));
            }   
        }
    }

    @Override
    public Image scale(Image in, int w, int h) throws Exception {
        
        Image dst = new Image(Format.RGB24, w, h);

        scale(in, dst);
        
        return dst;
    }

}
