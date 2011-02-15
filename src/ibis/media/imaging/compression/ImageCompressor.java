package ibis.media.imaging.compression;

import ibis.media.imaging.Image;

public interface ImageCompressor {
    public Image addImage(Image image) throws Exception;
    public Image flush() throws Exception;
    public String getType();
}
