package com.mrceej.sc2.macroBot;

import com.mrceej.sc2.builds.Plan;
import com.mrceej.sc2.builds.PoolFirstExpand;

public class Adviser {


    private MacroBot agent;
    private Plan currentPlan;

    Adviser(MacroBot macroBot) {
        this.agent = macroBot;
    }

    public boolean isSafe() {
        return true;
    }

    public void init() {
        currentPlan = new PoolFirstExpand();
    }

    public void update() {
        // Check if plan needs changing
    }

    Plan getCurrentPlan() {
        return currentPlan;
    }
}
