package ibis.media.video;

import ibis.media.imaging.Format;

import java.util.ArrayList;
import java.util.HashMap;

public class Capability {	
    final Format palette;
    final ArrayList<ResolutionCapability> resolutions;
    
    HashMap<Resolution, Integer> nativeNumbers = new HashMap<Resolution, Integer>();;
    
    public Capability(Format palette) {
        this.palette = palette;
        this.resolutions = new ArrayList<ResolutionCapability>();
    }
    
    public void addFrameRate(Resolution resolution, FrameRate rate) {       
        for (ResolutionCapability r : resolutions) { 
            
            if (r.resolution.equals(resolution)) { 
                r.rates.add(rate);
                return;
            }
        }

        // We only end up here if the resulotion was not found!
        ResolutionCapability r = new ResolutionCapability(resolution);
        r.rates.add(rate);
        resolutions.add(r);
    }
    
    public void addFrameRate(int nativeNumber, Resolution resolution, FrameRate rate) { 
    	for (ResolutionCapability r : resolutions) {
            if (r.resolution.equals(resolution)) { 
                r.rates.add(rate);
                return;
            }
        }

        // We only end up here if the resulotion was not found!
        ResolutionCapability r = new ResolutionCapability(resolution);
        r.rates.add(rate);
        resolutions.add(r);
        
        nativeNumbers.put(resolution, nativeNumber);
    } 
    
    public int getNativeNumber(Resolution resolution) {
    	return nativeNumbers.get(resolution);
    }
}
