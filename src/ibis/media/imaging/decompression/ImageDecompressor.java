package ibis.media.imaging.decompression;

import ibis.media.imaging.Image;

public interface ImageDecompressor {

    public Image decompress(Image image) throws Exception;
    public String getType();
}
