package ibis.media.imaging.conversion;

import ibis.media.imaging.Image;

public abstract class Convertor {
 
    public final int cost; 
    
    protected Convertor(int cost) {
        this.cost = cost;
    }
    
    public abstract Image convert(Image in, Image out) throws ConversionException;    
    
}
