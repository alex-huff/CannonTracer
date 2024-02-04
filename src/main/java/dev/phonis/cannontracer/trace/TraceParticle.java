package dev.phonis.cannontracer.trace;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Location;

public class TraceParticle {

    private final Location location;
    private final ParticleType type;
    private final long despawnTimeMillis;

    public TraceParticle(Location location, long despawnTimeMillis, ParticleType type) {
        this.location = location;
        this.despawnTimeMillis = despawnTimeMillis;
        this.type = type;
    }

    public Location getLocation() {
        return this.location;
    }

    public ParticleType getType() {
        return this.type;
    }
    public long getDespawnTimeMillis() {
        return this.despawnTimeMillis;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31)
                .append(this.getLocation())
                .append(this.getType())
                .append(this.getDespawnTimeMillis())
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TraceParticle)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        final TraceParticle pl = (TraceParticle) obj;

        return new EqualsBuilder()
                .append(this.getLocation(), pl.getLocation())
                .append(this.getType(), pl.getType())
                .isEquals();
    }

}