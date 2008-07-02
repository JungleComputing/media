package ibis.video4j.devices.video4linux;

public abstract class Convertor {
 
    public final int cost; 
    
    protected Convertor(int cost) {
        this.cost = cost;
    }
    
    public abstract void convert(int width, int height, Object in, Object out);   
}
