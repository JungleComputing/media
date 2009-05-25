package ibis.imaging4j.compression;

import ibis.imaging4j.Image;

public interface ImageCompressor {
    public Image addImage(Image image) throws Exception;
    public Image flush() throws Exception;
    public String getType();
}
