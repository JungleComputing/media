package ibis.media.video;

import ibis.media.imaging.Image;


public interface VideoConsumer {
    public void gotImage(Image image);
}
