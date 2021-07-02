package phonis.cannontracer.networking;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CTLine implements CTSerializable {

    public final CTVec3 start;
    public final CTVec3 finish;
    public final CTLineType type;
    public final List<CTArtifact> artifactList;
    private int ticks;

    public CTLine(CTVec3 start, CTVec3 finish, CTLineType type, List<CTArtifact> artifactSet, int ticks) {
        this.start = start;
        this.finish = finish;
        this.type = type;
        this.artifactList = artifactSet;
        this.ticks = ticks;
    }

    public float getR() {
        return this.type.getRGB().r;
    }

    public float getG() {
        return this.type.getRGB().g;
    }

    public float getB() {
        return this.type.getRGB().b;
    }

    public int decrementAndGet() {
        return this.ticks--;
    }

    public int size() {
        return 31 + this.artifactList.size() * 14;
    }

    @Override
    public void toBytes(DataOutputStream dos) throws IOException {
        this.start.toBytes(dos);
        this.finish.toBytes(dos);
        this.type.toBytes(dos);
        dos.writeShort(this.artifactList.size());

        for (CTArtifact artifact : this.artifactList) {
            artifact.toBytes(dos);
        }

        dos.writeInt(this.ticks);
    }

    public static CTLine fromBytes(DataInputStream dis) throws IOException {
        return new CTLine(
            CTVec3.fromBytes(dis),
            CTVec3.fromBytes(dis),
            CTLineType.fromBytes(dis),
            CTLine.readArtifactList(dis),
            dis.readInt()
        );
    }

    private static List<CTArtifact> readArtifactList(DataInputStream dis) throws IOException {
        List<CTArtifact> artifacts = new ArrayList<CTArtifact>();

        short length = dis.readShort();

        for (short i = 0; i < length; i++) {
            artifacts.add(CTArtifact.fromBytes(dis));
        }

        return artifacts;
    }

}
