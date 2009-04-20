package ibis.imaging4j;

public interface ImageCompressor {
    public Image addImage(Image image) throws Exception;
    public Image flush() throws Exception;
    public String getType();
}
