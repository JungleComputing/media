package ibis.media.imaging.scaling;

import ibis.media.imaging.Format;

import java.util.HashMap;

public class Scaling {

    private static final HashMap<Format, Scaler> scalers = 
        new HashMap<Format, Scaler>();

    static {
        // TODO: should load these dynamically ? 
        // TODO: this is far from complete!

        try { 
            addScaler(Format.ARGB32, new ARGB32Scaler());
            addScaler(Format.RGB24, new RGB24Scaler());
        } catch (Exception e) {
            System.err.println("Failed to load convertors!" + e);
            e.printStackTrace();
        }
    }

    public static void addScaler(Format format, Scaler scaler) { 
        scalers.put(format, scaler);
    }

    public static Scaler getScaler(Format format) { 
        return scalers.get(format);
    }

    public static boolean canScale(Format format) { 
        return (scalers.containsKey(format));
    }   
}
