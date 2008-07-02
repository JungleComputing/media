package ibis.video4j;


public interface VideoConsumer {
    
    public int [] getBuffer(int w, int h, int index);
    public void gotImage(int [] buffer, int index);
}
