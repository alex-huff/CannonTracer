package phonis.cannontracer.networking;

public class CTRegister implements CTPacket {

    public final int protocolVersion;

    public CTRegister(int protocolVersion) {
        this.protocolVersion = protocolVersion;
    }

}
