package ibis.video4j;

import ibis.imaging4j.Image;


public interface VideoConsumer {
    public void gotImage(Image image);
}
