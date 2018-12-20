package com.mrceej.sc2.builds;

import com.mrceej.sc2.macroBot.MacroBot;
import com.mrceej.sc2.macroBot.Utils;

public abstract class Build {

    final MacroBot agent;
    private final Utils utils;
    int minerals;
    int gas;

    Build(MacroBot agent) {
        this.agent = agent;
        this.utils = agent.getUtils();
    }

    public abstract boolean build();
}
