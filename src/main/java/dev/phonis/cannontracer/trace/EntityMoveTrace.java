package dev.phonis.cannontracer.trace;

import org.bukkit.Location;

import java.util.ArrayList;
import java.util.List;

public abstract class EntityMoveTrace extends Trace {
    protected boolean isConnected;
    private final boolean isHypotenusal;

    public EntityMoveTrace(Location start, Location finish, boolean isConnected, boolean isHypotenusal) {
        super(start, finish);
        this.isConnected = isConnected;
        this.isHypotenusal = isHypotenusal;
    }

    protected abstract ParticleType getType();

    protected abstract ParticleType getStartType();

    protected abstract ParticleType getFinishType();

    protected abstract OffsetType getStartOffsetType();

    protected abstract OffsetType getFinishOffsetType();

    @Override
    public List<Line> getLines() {
        List<Line> ret = new ArrayList<>(3);

        if (this.isHypotenusal) {
            ret.add(
                new Line(
                    this.getStart(),
                    this.getFinish(),
                    this.getType(),
                    this.getStartType(),
                    this.getFinishType(),
                    this.getStartOffsetType(),
                    this.getFinishOffsetType(),
                    isConnected
                )
            );

            return ret;
        }

        Location loc1 = this.getStart().clone();
        loc1.setY(this.getFinish().getY());
        Location loc2 = loc1.clone();
        loc2.setX(this.getFinish().getX());

        ret.add(
            new Line(
                this.getStart(),
                loc1,
                this.getType(),
                this.getStartType(),
                null,
                this.getStartOffsetType(),
                null,
                isConnected
            )
        );

        ret.add(
            new Line(
                loc1,
                loc2,
                this.getType(),
                isConnected
            )
        );

        ret.add(
            new Line(
                loc2,
                this.getFinish(),
                this.getType(),
                null,
                this.getFinishType(),
                null,
                this.getFinishOffsetType(),
                isConnected
            )
        );

        return ret;
    }

}