package com.mrceej.sc2.builds;

import com.mrceej.sc2.macroBot.MacroBot;

public abstract class Build {

    final MacroBot agent;

    Build(MacroBot agent) {
        this.agent = agent;
    }

    public abstract void update();
    public abstract void init();
}
