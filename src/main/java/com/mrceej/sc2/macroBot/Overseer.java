package com.mrceej.sc2.macroBot;

import com.github.ocraft.s2client.protocol.data.UnitType;
import com.github.ocraft.s2client.protocol.data.Units;
import com.mrceej.sc2.CeejBot;
import com.mrceej.sc2.builds.Build;

public class Overseer {
    private final CeejBot agent;
    private Build build;

    public Overseer(CeejBot macroBot, Intel intel, Build build) {
        this.agent = macroBot;
        this.build = build;
    }

    public void update() {
        UnitType buildOrder = build.getNextBuildItem();
        if (buildOrder != Units.INVALID) {

        }
    }
}
