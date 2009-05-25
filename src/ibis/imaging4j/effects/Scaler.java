package ibis.imaging4j.effects;

import ibis.imaging4j.Image;

public abstract class Scaler {
 
    public final int cost; 
    
    protected Scaler(int cost) {
        this.cost = cost;
    }
    
    public abstract void scale(Image in, Image out) throws Exception;        
    public abstract Image scale(Image in, int w, int h) throws Exception;    
}
