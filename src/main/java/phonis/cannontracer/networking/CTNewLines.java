package phonis.cannontracer.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CTNewLines implements CTPacket {

    public final UUID world;
    public final short ticks;
    public final List<CTLine> lines;

    public CTNewLines(UUID world, short ticks, List<CTLine> lines) {
        this.world = world;
        this.ticks = ticks;
        this.lines = lines;
    }

    @Override
    public void toBytes(DataOutputStream dos) throws IOException {
        dos.writeUTF(this.world.toString());
        dos.writeShort(this.ticks);
        dos.writeShort(this.lines.size());

        for (CTLine line : this.lines) {
            line.toBytes(dos);
        }
    }

    public static CTNewLines fromBytes(DataInputStream dis) throws IOException {
        UUID worldUUID = UUID.fromString(dis.readUTF());
        short tickTime = dis.readShort();

        return new CTNewLines(
            worldUUID,
            tickTime,
            CTNewLines.getLines(dis, tickTime)
        );
    }

    public static List<CTLine> getLines(DataInputStream dis, short ticks) throws IOException {
        List<CTLine> ctLines = new ArrayList<CTLine>();
        short length = dis.readShort();

        for (short i = 0; i < length; i++) {
            ctLines.add(CTLine.fromBytes(dis, ticks));
        }

        return ctLines;
    }

    @Override
    public byte packetID() {
        return Packets.newLinesID;
    }

}
