package dev.phonis.cannontracer.trace;

import dev.phonis.cannontracer.util.Offset;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public
enum OffsetType
{

    BLOCKBOX;

    private final List<Offset> BLOCKBOXList = new ArrayList<>(
        Arrays.asList(new Offset(.49F, .49F, .49F), new Offset(-.49F, .49F, .49F), new Offset(-.49F, -.49F, .49F),
            new Offset(.49F, -.49F, .49F), new Offset(.49F, .49F, -.49F), new Offset(-.49F, .49F, -.49F),
            new Offset(-.49F, -.49F, -.49F), new Offset(.49F, -.49F, -.49F)));

    public
    List<Offset> getOffset()
    {
        return BLOCKBOXList;
    }

}
