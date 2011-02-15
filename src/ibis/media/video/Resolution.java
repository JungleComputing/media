package ibis.media.video;

public class Resolution { 
    
    final int minW; 
    final int minH;
    
    final int maxW; 
    final int maxH;
    
    final int stepW;
    final int stepH;
    
    public Resolution(int minW, int minH, int maxW, int maxH, int stepW, 
            int stepH) { 
        
        this.minW = minW;
        this.maxW = maxW;
        this.minH = minH;
        this.maxH = maxH;
        this.stepW = stepW;
        this.stepH = stepH;
    }
    
    public Resolution(int minW, int minH, int maxW, int maxH) { 
        this(minW, minH, maxW, maxH, 1, 1);
    }
   
    public Resolution(int x, int y) { 
        this(x, y, x, y, -1, -1);
    }

    @Override // Generated
    public int hashCode() {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + maxH;
        result = PRIME * result + maxW;
        result = PRIME * result + minH;
        result = PRIME * result + minW;
        result = PRIME * result + stepH;
        result = PRIME * result + stepW;
        return result;
    }

    @Override // Generated
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final Resolution other = (Resolution) obj;
        if (maxH != other.maxH)
            return false;
        if (maxW != other.maxW)
            return false;
        if (minH != other.minH)
            return false;
        if (minW != other.minW)
            return false;
        if (stepH != other.stepH)
            return false;
        if (stepW != other.stepW)
            return false;
        return true;
    }
}