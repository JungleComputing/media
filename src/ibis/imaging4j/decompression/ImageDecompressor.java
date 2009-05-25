package ibis.imaging4j.decompression;

import ibis.imaging4j.Image;

public interface ImageDecompressor {

    public Image decompress(Image image) throws Exception;
    public String getType();
}
