package ibis.media.video;

import ibis.media.imaging.Format;

import java.util.ArrayList;

public class Capability { 
    
    final Format palette;
    final ArrayList<ResolutionCapability> resolutions;
    
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
}
