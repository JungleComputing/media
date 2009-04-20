package ibis.imaging4j.conversion;

import ibis.imaging4j.Image;

public abstract class Convertor {
 
    public final int cost; 
    
    protected Convertor(int cost) {
        this.cost = cost;
    }
    
    public abstract Image convert(Image in, Image out) throws ConversionException;    
    
}
