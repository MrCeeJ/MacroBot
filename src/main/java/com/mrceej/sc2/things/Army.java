package com.mrceej.sc2.things;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
public class Army {

    public Army() {
        this.units = new ArrayList<>();
    }

    List<UnitInPool> units;

    public void add(UnitInPool unit) {
        this.units.add(unit);
    }
}
