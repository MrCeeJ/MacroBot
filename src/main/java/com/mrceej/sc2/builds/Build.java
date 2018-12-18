package com.mrceej.sc2.builds;

import com.github.ocraft.s2client.protocol.data.UnitType;
import com.mrceej.sc2.CeejBot;

public abstract class Build {

    CeejBot agent;

    public Build(CeejBot agent) {
        this.agent = agent;
    }
    public abstract UnitType getNextBuildItem();

    public abstract void update();
}
