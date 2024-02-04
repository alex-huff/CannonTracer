package dev.phonis.cannontracer.trace;

import de.biomedical_imaging.edu.wlu.cs.levy.CG.KeyDuplicateException;
import de.biomedical_imaging.edu.wlu.cs.levy.CG.KeyMissingException;
import de.biomedical_imaging.edu.wlu.cs.levy.CG.KeySizeException;
import de.biomedical_imaging.edu.wlu.cs.levy.CG.KDTree;
import org.bukkit.Location;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class ParticleSystem {

    /**
     * Class to hold the different particles that can exist at one point. Each point must hold at least one particle
     */
    private class Point {
        public Location Loc;
        @Nullable
        public TraceParticle PlayerParticle;
        @Nullable
        public TraceParticle SandParticle;
        @Nullable
        public TraceParticle TNTParticle;

        public Point(Location loc) {
            this.Loc = loc;
        }

        /**
         * Overwrites particles of matching type
         */
        public void writeParticle(TraceParticle particle) {
            switch (particle.getType()) {
                case PLAYER:
                    PlayerParticle = particle;
                    break;
                case SAND:
                case SANDENDPOS:
                    SandParticle = particle;
                    break;
                case TNT:
                case TNTENDPOS:
                    TNTParticle = particle;
                    break;
            }
        }

        /**
         * Deletes particle from point. DELETE POINT IF IT IS NULL
         */
        public void delParticle(ParticleType type) {
            switch (type) {
                case PLAYER:
                    PlayerParticle = null;
                    break;
                case SAND:
                case SANDENDPOS:
                    SandParticle = null;
                    break;
                case TNTENDPOS:
                case TNT:
                    TNTParticle = null;
                    break;
            }
        }

        public void delParticleIf(Predicate<TraceParticle> filter) {
            if (PlayerParticle != null && filter.test(PlayerParticle)) PlayerParticle = null;
            if (SandParticle != null && filter.test(SandParticle)) SandParticle = null;
            if (TNTParticle != null && filter.test(TNTParticle)) TNTParticle = null;
        }

        public TraceParticle getParticle(ParticleType type) {
            switch (type) {
                case PLAYER:
                    return PlayerParticle;
                case SAND:
                case SANDENDPOS:
                    return SandParticle;
                case TNTENDPOS:
                case TNT:
                    return TNTParticle;
            }
            return null;
        }

        public boolean isEmpty() {
            return (PlayerParticle == null &&
                    SandParticle == null &&
                    TNTParticle == null);
        }
    }

    private final Map<Location, Point> activePoints = new HashMap<>();
    private KDTree<Point> pointTree;
    private int totalPoints = 0;
    // Deleted points remain in tree, which occasionally needs to be rebuilt
    private int deletedPoints = 0;
    private final TreeMap<Long, List<TraceParticle>> particleDespawnQueue = new TreeMap<>();


    public ParticleSystem() {
        pointTree = new KDTree<>(3);
    }

    /**
     * Overwrites particles of same type at same location
     */
    public void writeParticle(TraceParticle particle) {
        try {
            double[] loc = locationToArray(particle.getLocation());

            Point p = pointTree.search(loc);
            if (p == null) {
                // Add point to tree if none exists at location
                p = new Point(particle.getLocation());
                pointTree.insert(loc, p);
                ++totalPoints;
                activePoints.put(p.Loc, p);
            }
            TraceParticle existingParticle = p.getParticle(particle.getType());
            if (existingParticle != null) {
                dropFromDespawnQueue(existingParticle);
            }
            p.writeParticle(particle);
            addToDespawnQueue(particle);
        } catch (KeySizeException | KeyDuplicateException e) { }
    }

    public List<TraceParticle> getClosestParticles(Location loc, int maxParticles) {
        try {
            // First get the closest points, then iterate over until particle list is full
            List<Point> points = pointTree.nearest(locationToArray(loc), maxParticles);
            List<TraceParticle> particles = new ArrayList<>(maxParticles + 2);
            // For some reason, points in this list go from farthest to closest, so have to iterate backwards
            for (int i = points.size() - 1; i >= 0; i--) {
                if (particles.size() >= maxParticles) break; // Stop if the particle list is already full
                Point point = points.get(i);
                if (point.PlayerParticle != null) particles.add(point.PlayerParticle);
                if (point.SandParticle != null) particles.add(point.SandParticle);
                if (point.TNTParticle != null) particles.add(point.TNTParticle);
            }
            return particles;
        } catch (KeySizeException e) {return new ArrayList<>();}
    }

    public void processDespawnQueue() {
        try {
            while (true) {
                if (particleDespawnQueue.isEmpty()) return;
                long firstKey = particleDespawnQueue.firstKey();
                if (firstKey > System.currentTimeMillis()) return;
                // firstKey is to list of particles at or pass despawn time

                List<TraceParticle> particles = particleDespawnQueue.get(firstKey);

                // For every particle, remove from associated point, and remove point if empty
                for (TraceParticle particle : particles) {
                    Point point = pointTree.search(locationToArray(particle.getLocation()));
                    point.delParticle(particle.getType());
                    if (point.isEmpty()) removeEmptyPoint(point.Loc);
                }
                // Remove particles from despawn queue
                particleDespawnQueue.remove(firstKey);
            }
        } catch (KeySizeException e) { }
    }

    public void clear() {
        activePoints.clear();
        pointTree = new KDTree<>(3);
        particleDespawnQueue.clear();
        totalPoints = 0;
        deletedPoints = 0;
    }

    public void removeIf(Predicate<TraceParticle> filter) {
        for (Point point : activePoints.values()) {
            point.delParticleIf(filter);
            if (point.isEmpty()) removeEmptyPoint(point.Loc);
        }
    }

    public void rebuildIfSaturated() {
        if (!(deletedPoints * 2 > totalPoints)) return;

        pointTree = new KDTree<>(3);
        for (Point p : activePoints.values()) {
            try {
                pointTree.insert(locationToArray(p.Loc), p);
            } catch (KeySizeException | KeyDuplicateException e) { }
        }

        totalPoints -= deletedPoints;
        deletedPoints = 0;
    }

    private void addToDespawnQueue(TraceParticle particle) {
        particleDespawnQueue.computeIfAbsent(particle.getDespawnTimeMillis(), k -> new ArrayList<>()).add(particle);
    }

    private void dropFromDespawnQueue(TraceParticle particle) {
        List<TraceParticle> oldList = particleDespawnQueue.get(particle.getDespawnTimeMillis());
        if (oldList != null) {
            oldList.remove(particle);
            if (oldList.isEmpty()) {
                particleDespawnQueue.remove(particle.getDespawnTimeMillis());
            }
        }
    }

    private void removeEmptyPoint(Location loc) {
        try {
            pointTree.delete(locationToArray(loc));
            activePoints.remove(loc);
            ++deletedPoints;
        } catch (KeySizeException | KeyMissingException e) { }
    }
    private double[] locationToArray(Location location) {
        return new double[] { location.getX(), location.getY(), location.getZ() };
    }
}
