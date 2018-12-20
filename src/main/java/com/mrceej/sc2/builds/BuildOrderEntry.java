package com.mrceej.sc2.builds;

import com.github.ocraft.s2client.protocol.data.Units;
import lombok.Getter;

public class BuildOrderEntry {
    @Getter
    private final int workers;
    @Getter
    private final Units unit;
    @Getter
    private final boolean unique;

    public BuildOrderEntry(int workers, Units unit, boolean unique){
        this.workers = workers;
        this.unit = unit;
        this.unique = unique;
    }
}
