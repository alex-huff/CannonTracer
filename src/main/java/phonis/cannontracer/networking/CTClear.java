package phonis.cannontracer.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class CTClear implements CTPacket {

    public final CTLineType type;

    public CTClear(CTLineType type) {
        this.type = type;
    }

    @Override
    public void toBytes(DataOutputStream dos) throws IOException {
        this.type.toBytes(dos);
    }

    public static CTClear fromBytes(DataInputStream dis) throws IOException {
        return new CTClear(
            CTLineType.fromBytes(dis)
        );
    }

    @Override
    public byte packetID() {
        return Packets.clearID;
    }

}
