package phonis.cannontracer.networking;

import org.bukkit.Location;
import phonis.cannontracer.trace.Artifact;
import phonis.cannontracer.trace.Line;
import phonis.cannontracer.trace.OffsetType;
import phonis.cannontracer.trace.ParticleType;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CTAdapter {

    public static CTLine fromLine(Line line, int life) {
        return new CTLine(
            line.getStart().getWorld().getUID(),
            CTAdapter.fromLocation(line.getStart()),
            CTAdapter.fromLocation(line.getFinish()),
            CTAdapter.fromParticleType(line.getType()),
            CTAdapter.fromArtifacts(line.artifacts),
            life
        );
    }

    private static CTVec3 fromLocation(Location location) {
        return new CTVec3(
            location.getX(),
            location.getY(),
            location.getZ()
        );
    }

    private static CTLineType fromParticleType(ParticleType type) {
        if (type.equals(ParticleType.TNT)) {
            return CTLineType.TNT;
        } else if (type.equals(ParticleType.SAND)) {
            return CTLineType.SAND;
        } else if (type.equals(ParticleType.PLAYER)) {
            return CTLineType.PLAYER;
        } else if (type.equals(ParticleType.TNTENDPOS)) {
            return CTLineType.TNTENDPOS;
        } else if (type.equals(ParticleType.SANDENDPOS)) {
            return CTLineType.SANDENDPOS;
        } else {
            return null;
        }
    }

    private static List<CTArtifact> fromArtifacts(Set<Artifact> artifacts) {
        List<CTArtifact> newArtifacts = new ArrayList<>(artifacts.size());

        for (Artifact artifact : artifacts) {
            newArtifacts.add(CTAdapter.fromArtifact(artifact));
        }

        return newArtifacts;
    }

    private static CTArtifact fromArtifact(Artifact artifact) {
        return new CTArtifact(
            CTAdapter.fromLocation(artifact.getLocation()),
            CTAdapter.fromParticleType(artifact.getType()),
            CTAdapter.fromOffsetType(artifact.getOffsetType())
        );
    }

    private static CTArtifactType fromOffsetType(OffsetType type) {
        if (type.equals(OffsetType.BLOCKBOX)) {
            return CTArtifactType.BLOCKBOX;
        }

        return null;
    }

}
