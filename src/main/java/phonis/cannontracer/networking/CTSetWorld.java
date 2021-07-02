package phonis.cannontracer.networking;

import java.util.UUID;

public class CTSetWorld implements CTPacket {

    public final UUID world;

    public CTSetWorld(UUID world) {
        this.world = world;
    }

}
