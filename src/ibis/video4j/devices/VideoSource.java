package ibis.video4j.devices;

import ibis.video4j.VideoConsumer;

public abstract class VideoSource extends Thread {
    
    protected final VideoConsumer consumer;
    protected final int width;
    protected final int height;
    protected final int delay;
    
    private boolean initialized = false;
    private boolean succes = false;
    private boolean done = false;
    private boolean closed = false;
    
    private int [] emptyBuffer;
    private int [] fullBuffer;
    
    protected VideoSource(VideoConsumer consumer, int width, int height, int delay) {
        this.consumer = consumer;
        this.width = width;
        this.height = height;
        this.delay = delay;
    }
    
    public abstract void setResolution(int width, int height);
    
    public int getWidth() { 
        return width;
    }
    
    public int getHeight() { 
        return height;
    }
    
  //  public abstract void setPalette(Palette p);
        
    public abstract void close();
    
    protected abstract void grab();
       
    public synchronized void returnBuffer(int [] pixels) { 
        emptyBuffer = pixels;
    }
    
    protected synchronized int [] getEmptyBuffer() { 
        
        if (emptyBuffer == null) { 
            return new int[width*height];
        } 
        
        int [] tmp = emptyBuffer;
        emptyBuffer = null;
        return tmp;
    }
   
    protected synchronized void putFullBuffer(int [] buffer) { 
        
        if (fullBuffer != null) { 
            emptyBuffer = fullBuffer;
        }
        
        fullBuffer = buffer;
        notifyAll();
    }
    
    protected synchronized int [] getFullBuffer() { 
       
        if (fullBuffer == null) { 
            try { 
                wait(1000);
            } catch (Exception e) {
                // TODO: handle exception
            }
        }
        
        if (fullBuffer == null) { 
            return null;
        }
        
        int [] tmp = fullBuffer;
        fullBuffer = null;
        
        return tmp;
    }
    
    protected synchronized void initialized(boolean succes) { 
        this.initialized = true;
        this.succes = succes;
        notifyAll();
    }
    
    private synchronized void waitForInitialization() {
        
        while (!initialized) { 
            try { 
                wait();
            } catch (Exception e) {
                // ignore
            }
        }
    }
    
    protected synchronized boolean getDone() { 
        return done;
    }
    
    private synchronized void setClosed() { 
        closed = true;
        notifyAll();
    }
    
    protected synchronized boolean setDone() { 
        done = true;
        
        while (!closed) {
            try { 
                wait(5000);
            } catch (InterruptedException e) {
                // ignore
            }
        }
        
        return closed;
    }
    
    
    public void run() { 

        waitForInitialization();
        
        if (!succes) { 
            return;
        }
        
        while (!getDone()) { 
            grab();
        }
        
        setClosed();
     }

}
