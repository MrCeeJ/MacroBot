package com.mrceej.sc2.builds;

import com.github.ocraft.s2client.protocol.data.Units;
import com.mrceej.sc2.macroBot.MacroBot;
import com.mrceej.sc2.macroBot.Utils;
import java.util.List;
import static com.github.ocraft.s2client.protocol.data.Units.*;

public abstract class Build {

    final MacroBot agent;
    private final Utils utils;
    int minerals;
    int gas;

    Build(MacroBot agent) {
        this.agent = agent;
        this.utils = agent.getUtils();
    }

    public abstract void update();

    boolean checkCanMakeBuilding(Units unit) {
        return haveTechForUnit(unit) && canAffordUnit(unit);
    }

    private boolean haveTechForUnit(Units unit) {
        List<Units> requirements = getRequirements(unit);
        for (Units req : requirements) {
            if (countOfBuilding(req) == 0){
                return false;
            }
        }
        return true;
    }

    private List<Units> getRequirements(Units unit) {
        switch(unit) {
            case ZERG_SPAWNING_POOL: return List.of(ZERG_HATCHERY);
            case ZERG_BANELING_NEST: return List.of(ZERG_HATCHERY, ZERG_SPAWNING_POOL);
            case ZERG_ROACH_WARREN: return List.of(ZERG_HATCHERY, ZERG_SPAWNING_POOL);
            case ZERG_HYDRALISK_DEN: return List.of(ZERG_LAIR);
            case ZERG_LURKER_DEN_MP: return List.of(ZERG_LAIR, ZERG_HYDRALISK_DEN);
            case ZERG_SPIRE: return List.of(ZERG_LAIR);
            case ZERG_GREATER_SPIRE: return List.of(ZERG_HIVE, ZERG_SPIRE);
            default:
            case ZERG_ULTRALISK_CAVERN: return List.of(ZERG_HIVE);
        }
    }

    private boolean canAffordUnit(Units unit) {
        return getMineralCost(unit) <= minerals &&
                getGasCost(unit) <= gas;
    }

    private int getMineralCost(Units unit) {
        return agent.observation().getUnitTypeData(false).get(unit).getMineralCost().orElse(0);
    }

    private int getGasCost(Units unit) {
        return agent.observation().getUnitTypeData(false).get(unit).getVespeneCost().orElse(0);
    }
    int countOfBuilding(Units unit) {
        return utils.getAllUnitsOfType(unit).size();
    }
}
