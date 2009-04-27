package ibis.video4j;

import java.util.ArrayList;

public class ResolutionCapability { 
    
    final Resolution resolution;
    final ArrayList<FrameRate> rates;
    
    public ResolutionCapability(Resolution resolution) {
        this.resolution = resolution;
        this.rates = new ArrayList<FrameRate>();
    }
    
    public void addFrameRate(FrameRate rate) { 
        rates.add(rate);
    } 
}