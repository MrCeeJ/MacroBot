package com.mrceej.sc2.builds;

import com.github.ocraft.s2client.protocol.data.UnitType;
import com.github.ocraft.s2client.protocol.data.Units;
import com.mrceej.sc2.macroBot.BuildManager;
import com.mrceej.sc2.macroBot.MacroBot;
import com.mrceej.sc2.macroBot.Utils;
import lombok.extern.log4j.Log4j2;

import java.util.List;

import static com.github.ocraft.s2client.protocol.data.Units.*;

@Log4j2
public class TechUp extends Build {

    private final Utils utils;
    private final BuildManager buildManager;
    private final List<Units> buildOrder = List.of(ZERG_SPAWNING_POOL, ZERG_ROACH_WARREN, ZERG_LAIR, ZERG_HYDRALISK_DEN, ZERG_SPIRE);

    public TechUp(MacroBot agent) {
        super(agent);
        this.utils = agent.getUtils();
        this.buildManager = agent.getBuildManager();
    }

    private UnitType getNextBuildItem() {
        minerals = agent.observation().getMinerals();
        gas = agent.observation().getMinerals();

        for (Units u : buildOrder) {
            if (checkUnit(u)) {
                return u;
            }
        }
        return INVALID;
    }

    private boolean checkUnit(Units u) {
        return countOfBuilding(u) == 0 && checkCanMakeBuilding(u);
    }

    @Override
    public void update() {
        UnitType unit = getNextBuildItem();
        if (unit != Units.INVALID) {
            buildManager.build(unit);
        }
    }


}
