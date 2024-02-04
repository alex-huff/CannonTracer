package dev.phonis.cannontracer.trace;

import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;
import org.bukkit.Location;
import org.bukkit.util.Vector;
import dev.phonis.cannontracer.networking.CTAdapter;
import dev.phonis.cannontracer.networking.CTVec3;

public class LineIntercepts {

    private final double xyIntx;
    private final double xyInty;
    private final double yzInty;
    private final double yzIntz;
    private final double zxIntz;
    private final double zxIntx;

    public LineIntercepts(CTVec3 direction, Location point) {
        double xyScalar = point.getZ() / direction.z;
        double yzScalar = point.getX() / direction.x;
        double zxScalar = point.getY() / direction.y;

        xyIntx = direction.x * xyScalar + point.getX();
        xyInty = direction.y * xyScalar + point.getY();
        yzInty = direction.y * yzScalar + point.getY();
        yzIntz = direction.z * yzScalar + point.getZ();
        zxIntz = direction.z * zxScalar + point.getZ();
        zxIntx = direction.x * zxScalar + point.getX();
    }

    public LineIntercepts(Vector direction, Location location) {
        this(CTAdapter.fromVector(direction), location);
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 31)
                .append(this.xyIntx)
                .append(this.xyInty)
                .append(this.yzInty)
                .append(this.yzIntz)
                .append(this.zxIntz)
                .append(this.zxIntx)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof LineIntercepts)) {
            return false;
        }

        if (obj == this) {
            return true;
        }

        final LineIntercepts other = (LineIntercepts) obj;

        return new EqualsBuilder()
                .append(this.yzInty, other.yzInty)
                .append(this.yzIntz, other.yzIntz)
                .append(this.xyInty, other.xyInty)
                .append(this.xyIntx, other.xyIntx)
                .append(this.zxIntz, other.zxIntz)
                .append(this.zxIntx, other.zxIntx)
                .isEquals();
    }

}
