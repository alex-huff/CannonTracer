package phonis.cannontracer.networking;

public class CTClear implements CTPacket {

    public final CTLineType type;

    public CTClear(CTLineType type) {
        this.type = type;
    }

}
