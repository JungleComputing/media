package ibis.imaging4j;

import java.io.Serializable;
import java.nio.ByteBuffer;

public class Image implements Serializable {
    
    private static final long serialVersionUID = -6781258283276066646L;

    protected long number;
    
    protected Object metaData;    

    protected final Format format; 
    
    protected final ByteBuffer data;
    
    protected final int width;
    
    protected final int height;
    
    public Image(Format format, int size) {
        this.format = format;
        this.width = -1;
        this.height = -1;
        this.data = ByteBuffer.allocate(size);
    }
    
    public Image(Format format, int width, int height) {
        this.format = format;
        this.width = width;
        this.height = height;
        this.data = ByteBuffer.allocate((int)format.bytesRequired(width, height));
    }
    
    public Image(Format format, int width, int height, byte [] data) {
        this.format = format;        
        this.width = width;
        this.height = height;
        this.data = ByteBuffer.wrap(data);
    }
    
    public Image(Format format, int width, int height, ByteBuffer data) {
        this.format = format;
        this.data = data;
        this.width = width;
        this.height = height;
    }
    
    public Image(Format format, int width, int height, long number, Object metaData) {
        this.format = format;
        this.number = number;
        this.metaData = metaData; 
        this.width = width;
        this.height = height;
        this.data = ByteBuffer.allocate((int) format.bytesRequired(width, height));
    }
    
    public Image(Format format, int width, int height, byte [] data, long number, Object metaData) {
        this.format = format;
        this.number = number;
        this.metaData = metaData; 
        this.width = width;
        this.height = height;
        this.data = ByteBuffer.wrap(data);
    }
    
    public Image(Format format, int width, int height, ByteBuffer data, long number, Object metaData) {
        this.format = format;
        this.number = number;
        this.metaData = metaData; 
        this.data = data;
        this.width = width;
        this.height = height;
    }
    
    public int getWidth() { 
        return width;
    }
    
    public int getHeight() { 
        return height;
    }
    
    public void setNumber(long number) { 
        this.number = number;
    }
 
    public long getNumber() { 
        return number;
    }
    
    public Object getMetaData() { 
        return metaData;
    }
    
    public Format getFormat() { 
        return format;
    }
    
    public ByteBuffer getData() {
        return data;
    }

    public long getSize() {
        return data.capacity();        
    }
    
    public static void copy(Image src, Image dst) throws Exception {

        // TODO: is this correct for compressed images ? 
        if (dst.format != src.format) { 
            throw new Exception("Incompatible format");
        }
        
        if (dst.width != src.width || dst.height != src.height) { 
            throw new Exception("Incompatible size");
        }
        
        ByteBuffer s = src.getData();
        s.position(0);
        s.limit(s.capacity());

        ByteBuffer d = dst.getData();
        d.position(0);
        d.limit(d.capacity());

        d.put(s);
    }
    
}
