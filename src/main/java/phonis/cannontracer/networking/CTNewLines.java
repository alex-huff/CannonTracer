package phonis.cannontracer.networking;

import java.util.List;

public class CTNewLines implements CTPacket {

    public final List<CTLine> lines;

    public CTNewLines(List<CTLine> lines) {
        this.lines = lines;
    }

}
