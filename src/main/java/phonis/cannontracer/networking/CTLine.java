package phonis.cannontracer.networking;

import java.io.Serializable;
import java.util.List;
import java.util.UUID;

public class CTLine implements Serializable {

    public final UUID world;
    public final CTVec3 start;
    public final CTVec3 finish;
    public final CTLineType type;
    public final List<CTArtifact> artifactList;
    private int ticks;

    public CTLine(UUID world, CTVec3 start, CTVec3 finish, CTLineType type, List<CTArtifact> artifactSet, int ticks) {
        this.world = world;
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

}
