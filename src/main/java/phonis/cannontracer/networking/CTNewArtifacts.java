package phonis.cannontracer.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CTNewArtifacts implements CTPacket {

    public final UUID world;
    public final List<CTArtifact> artifacts;

    public CTNewArtifacts(UUID world, List<CTArtifact> artifacts) {
        this.world = world;
        this.artifacts = artifacts;
    }

    @Override
    public byte packetID() {
        return Packets.newArtifactsID;
    }

    @Override
    public void toBytes(DataOutputStream dos) throws IOException {
        dos.writeUTF(this.world.toString());
        dos.writeShort(this.artifacts.size());

        for (CTArtifact artifact : this.artifacts) {
            artifact.toBytes(dos);
        }
    }

    public static CTNewArtifacts fromBytes(DataInputStream dis) throws IOException {
        return new CTNewArtifacts(
            UUID.fromString(dis.readUTF()),
            CTNewArtifacts.getArtifacts(dis)
        );
    }

    private static List<CTArtifact> getArtifacts(DataInputStream dis) throws IOException {
        List<CTArtifact> ctArtifacts = new ArrayList<CTArtifact>();
        short length = dis.readShort();

        for (short i = 0; i < length; i++) {
            ctArtifacts.add(CTArtifact.fromBytes(dis));
        }

        return ctArtifacts;
    }

}
