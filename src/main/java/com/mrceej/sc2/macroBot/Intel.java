package com.mrceej.sc2.macroBot;

import com.github.ocraft.s2client.protocol.data.Units;
import com.mrceej.sc2.builds.Build;
import com.mrceej.sc2.builds.PureMacro;
import com.mrceej.sc2.builds.TechUp;

class Intel {
    private Build currentBuild;
    private final Utils utils;
    private final MacroBot agent;

    public Intel(MacroBot agent) {
        this.agent = agent;
        this.currentBuild = new PureMacro(agent);
        this.utils = agent.getUtils();
    }

    public void update() {
        updateBuilds();
        currentBuild.update();
    }

    private void updateBuilds() {
        if (requireAdditionalTech()) {
            this.currentBuild = new TechUp(agent);
        } else {
            this.currentBuild = new PureMacro(agent);
        }


    }

    private boolean requireAdditionalTech() {
        int workers = utils.getAllUnitsOfType(Units.ZERG_DRONE).size();
        if (workers > 12) {
            if (utils.getAllUnitsOfType(Units.ZERG_SPAWNING_POOL).size() == 0) {
                return true;
            }
        }
        return false;
    }
}
