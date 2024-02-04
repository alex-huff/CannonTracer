package dev.phonis.cannontracer.trace;

import java.util.*;

public class LineSet implements Iterable<Line> {

    // Used when isConnected
    private TreeSet<Line> segments = new TreeSet<>();
    // Used when !isConnected. When not connected shouldn't be using LineSet but don't feel like rewriting more stuff
    private List<Line> lines = new ArrayList<>();
    private final boolean isConnected;

    public LineSet(boolean isConnected) {
        this.isConnected = isConnected;
    }

    @Override
    public Iterator<Line> iterator() {
        if (isConnected) return segments.iterator();
        return lines.iterator();
    }

    public void add(Line toAdd) {
        if (!this.isConnected) {
            for (Line line : lines) {
                if (toAdd.getStart().equals(line.getStart()) && toAdd.getFinish().equals(line.getFinish())) {
                    line.addArtifacts(toAdd);
                    return;
                }
            }
            lines.add(toAdd);
            return;
        }

        Line floor = segments.floor(toAdd);
        Line ceiling = segments.ceiling(toAdd);

        if (floor != null && floor.overlaps(toAdd)) {
            toAdd.merge(floor);
            segments.remove(floor);
        }
        if (ceiling != null && ceiling.overlaps(toAdd)) {
            toAdd.merge(ceiling);
            segments.remove(ceiling);
        }

        segments.add(toAdd);
    }
}
