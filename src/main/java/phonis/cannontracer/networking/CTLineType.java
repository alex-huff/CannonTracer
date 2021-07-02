package phonis.cannontracer.networking;

import java.io.Serializable;

public enum CTLineType implements Serializable {

    SAND, PLAYER, TNT, TNTENDPOS, SANDENDPOS;

    final static CTColor colorSand = new CTColor(1f, 1f, 0);
    final static CTColor colorTNT = new CTColor(1f, 0, 0);
    final static CTColor colorExplosion = new CTColor(1f, 0, 1f);
    final static CTColor colorPlayer = new CTColor(0, 0, 1f);

    public CTColor getRGB() {
        if (this == SAND) {
            return colorSand;
        } else if (this == TNT) {
            return colorTNT;
        } else if (this == PLAYER) {
            return colorPlayer;
        } else {
            return colorExplosion;
        }
    }

}
