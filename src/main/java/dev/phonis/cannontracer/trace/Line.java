package dev.phonis.cannontracer.trace;

import org.bukkit.Location;
import org.bukkit.util.Vector;

import java.util.*;

public class Line implements Comparable<Line> {

    private Location start;
    private Location finish;
    private final Vector direction;
    private double startIndex;
    private double finishIndex;
    private final ParticleType type;
    private final boolean connected;
    private final LineIntercepts lineIntercepts;
    public final Set<Artifact> artifacts = new HashSet<>();

    public Line(Location p1, Location p2, ParticleType type, ParticleType startType, ParticleType finishType, OffsetType startOffsetType, OffsetType finishOffsetType, boolean connected) throws IllegalArgumentException {
        // Assign indexes and points to start/finish:
        // 1. Find first indexable dimension in specific order
        // 2. Assign points to start/finish by comparing positions in that dimension
        // 3. Assign indexes as positions in that dimension
        Vector direction1 = p2.clone().subtract(p1).toVector().normalize();
        if (direction1.getX() != 0) {
            if (p1.getX() < p2.getX()) {
                start = p1;
                finish = p2;
            }
            else {
                start = p2;
                finish = p1;
            }
            startIndex = start.getX();
            finishIndex = finish.getX();
        }
        else if (direction1.getY() != 0) {
            if (p1.getY() < p2.getY()) {
                start = p1;
                finish = p2;
            }
            else {
                start = p2;
                finish = p1;
            }
            startIndex = start.getY();
            finishIndex = finish.getY();
        }
        else if (direction1.getZ() != 0) {
            if (p1.getZ() < p2.getZ()) {
                start = p1;
                finish = p2;
            }
            else {
                start = p2;
                finish = p1;
            }
            startIndex = start.getZ();
            finishIndex = finish.getZ();
        }
        else {
            throw new IllegalArgumentException("Points did not describe a line! "
                    + "p1 = `{ " + p1.getX() + ", " + p1.getY() + ", " + p1.getZ() + " }`, "
                    + "p2 = `{ " + p2.getX() + ", " + p2.getY() + ", " + p2.getZ() + " }`");
        }

        direction1 = finish.clone().subtract(start).toVector().normalize();

        direction = direction1;
        this.type = type;
        this.connected = connected;
        this.lineIntercepts = new LineIntercepts(direction, this.start);

        if (startType != null && startOffsetType != null) {
            this.artifacts.add(new Artifact(this.start, startType, startOffsetType));
        }

        if (finishType != null && finishOffsetType != null) {
            this.artifacts.add(new Artifact(this.finish, finishType, finishOffsetType));
        }
    }

    public Line(Location p1, Location p2, ParticleType type, boolean connected) {
        this(p1, p2, type, null, null, null, null, connected);
    }

    public Location getStart() {
        return this.start;
    }

    public Location getFinish() {
        return this.finish;
    }

    public ParticleType getType() {
        return this.type;
    }

    public int compareTo(Line other) {
        return Double.compare(startIndex, other.startIndex);
    }

    public boolean overlaps(Line other) {
        return startIndex <= other.finishIndex && other.startIndex <= finishIndex;
    }

    public void merge(Line other) {
        if (startIndex > other.startIndex) {
            startIndex = other.startIndex;
            start = other.start;
        }
        if (finishIndex < other.finishIndex) {
            finishIndex = other.finishIndex;
            finish = other.finish;
        }

        addArtifacts(other);
    }

    public void addArtifacts(Line other) {
        if (!other.artifacts.isEmpty()) artifacts.addAll(other.artifacts);
    }

    public LineIntercepts getLineIntercepts() {
        return this.lineIntercepts;
    }

    private List<TraceParticle> getEndParticles(int life) {
        List<TraceParticle> ret = new ArrayList<>();

        ret.add(new TraceParticle(this.start, System.currentTimeMillis() + life * 50L, this.type)); // TODO convert life upstream to timestamp
        ret.add(new TraceParticle(this.finish, System.currentTimeMillis() + life * 50L, this.type)); // TODO convert life upstream to timestamp

        return ret;
    }

    private List<TraceParticle> getLineParticles(int life) {
        double distance = this.start.distance(this.finish);
        Vector intervalDirection = this.direction.multiply(.25);
        Vector di2 = intervalDirection.clone();

        List<TraceParticle> ret = this.getEndParticles(life);

        while (di2.length() < distance) {
            ret.add(new TraceParticle(this.start.clone().add(di2.getX(), di2.getY(), di2.getZ()), System.currentTimeMillis() + life * 50L, this.type)); // TODO convert life upstream to timestamp
            di2.add(intervalDirection);
        }

        return ret;
    }

    public List<TraceParticle> getParticles(int life) {
        List<TraceParticle> ret = new ArrayList<>();

        for (Artifact artifact : this.artifacts) {
            ret.addAll(artifact.getParticles(life));
        }

        if (this.connected) ret.addAll(this.getLineParticles(life));
        else ret.addAll(this.getEndParticles(life));

        return ret;
    }
}
