package ibis.media.imaging.scaling;

import ibis.media.imaging.Image;

public abstract class Scaler {
 
    public final int cost; 
    
    protected Scaler(int cost) {
        this.cost = cost;
    }
    
    public abstract void scale(Image in, Image out) throws Exception;        
    public abstract Image scale(Image in, int w, int h) throws Exception;    
}
