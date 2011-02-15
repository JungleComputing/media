package ibis.media.video.devices.quicktime;

import java.util.ArrayList;
import java.util.HashMap;

import ibis.media.imaging.Format;
import ibis.media.video.Capability;
import ibis.media.video.VideoDeviceDescription;
import ibis.media.video.VideoDeviceDiscovery;

public class QuickTimeDiscovery implements VideoDeviceDiscovery {

    public VideoDeviceDescription[] discover() {

        ArrayList<VideoDeviceDescription> devices = new ArrayList<VideoDeviceDescription>();

        HashMap<Format, Capability> capabilities = new HashMap<Format, Capability>();

        Capability capability = new Capability(Format.BGRA32);

        capabilities.put(Format.BGRA32, capability);

        devices.add(new VideoDeviceDescription("device" + 0, "default", 0,
                capabilities));

        return devices.toArray(new VideoDeviceDescription[devices.size()]);
    }
}
