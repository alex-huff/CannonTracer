package dev.phonis.cannontracer.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class CTSetWorld implements CTPacket {

    public final UUID world;

    public CTSetWorld(UUID world) {
        this.world = world;
    }

    @Override
    public void toBytes(DataOutputStream dos) throws IOException {
        dos.writeUTF(this.world.toString());
    }

    public static CTSetWorld fromBytes(DataInputStream dis) throws IOException {
        return new CTSetWorld(
            UUID.fromString(dis.readUTF())
        );
    }

    @Override
    public byte packetID() {
        return Packets.setWorldID;
    }

}
