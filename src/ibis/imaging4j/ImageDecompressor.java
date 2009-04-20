package ibis.imaging4j;

public interface ImageDecompressor {

    public Image decompress(Image image) throws Exception;
    public String getType();
}
