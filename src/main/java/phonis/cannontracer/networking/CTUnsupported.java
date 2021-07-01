package phonis.cannontracer.networking;

public class CTUnsupported implements CTPacket {

    public final int protocolVersion;

    public CTUnsupported(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

}
