package com.mrceej.sc2.builds;

import com.github.ocraft.s2client.protocol.data.Ability;
import com.github.ocraft.s2client.protocol.data.Units;
import lombok.Getter;

public class BuildOrderEntry {
    @Getter
    private final int workers;
    @Getter
    private final Units unit;
    @Getter
    private final boolean unique;
    @Getter
    private final Ability ability;
    public final boolean isUnit;
    public final boolean isUpgrade;


    public BuildOrderEntry(int workers, Units unit, boolean unique){
        this.workers = workers;
        this.unit = unit;
        this.unique = unique;
        this.ability = null;
        this.isUnit = true;
        this.isUpgrade = false;
    }
    public BuildOrderEntry(int workers, Ability upgrade, boolean unique){
        this.workers = workers;
        this.ability = upgrade;
        this.unique = unique;
        this.unit = null;
        this.isUnit = false;
        this.isUpgrade = true;
    }
}
