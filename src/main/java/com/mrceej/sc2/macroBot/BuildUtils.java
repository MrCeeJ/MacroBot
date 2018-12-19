package com.mrceej.sc2.macroBot;

import com.github.ocraft.s2client.protocol.data.Units;

import java.util.List;

import static com.github.ocraft.s2client.protocol.data.Units.*;

public class BuildUtils {

    private Utils utils;
    private MacroBot agent;

    public BuildUtils(MacroBot agent) {
        this.agent = agent;
    }
    void init(){
        this.utils = agent.getUtils();
    }



    public boolean checkCanMakeUnit(Units unit, int minerals, int gas) {
        return haveTechForUnit(unit) && canAffordUnit(unit, minerals, gas);
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

    private boolean canAffordUnit(Units unit, int minerals, int gas) {
        return getMineralCost(unit) <= minerals &&
                getGasCost(unit) <= gas;
    }

    private List<Units> getRequirements(Units unit) {
        switch(unit) {
            case ZERG_SPAWNING_POOL: return List.of(ZERG_HATCHERY);
            case ZERG_EVOLUTION_CHAMBER: return List.of(ZERG_HATCHERY);
            case ZERG_EXTRACTOR: return List.of();
            case ZERG_BANELING_NEST: return List.of(ZERG_HATCHERY, ZERG_SPAWNING_POOL);
            case ZERG_ROACH_WARREN: return List.of(ZERG_HATCHERY, ZERG_SPAWNING_POOL);
            case ZERG_HYDRALISK_DEN: return List.of(ZERG_LAIR);
            case ZERG_LURKER_DEN_MP: return List.of(ZERG_LAIR, ZERG_HYDRALISK_DEN);
            case ZERG_SPIRE: return List.of(ZERG_LAIR);
            case ZERG_LAIR: return List.of(ZERG_HATCHERY, ZERG_SPAWNING_POOL);
            case ZERG_GREATER_SPIRE: return List.of(ZERG_HIVE, ZERG_SPIRE);
            case ZERG_ULTRALISK_CAVERN: return List.of(ZERG_HIVE);
            case ZERG_DRONE: return List.of(ZERG_HATCHERY);
            case ZERG_OVERLORD: return List.of(ZERG_HATCHERY);
            case ZERG_QUEEN: return List.of(ZERG_HATCHERY, ZERG_SPAWNING_POOL);
            case ZERG_ZERGLING: return List.of(ZERG_HATCHERY, ZERG_SPAWNING_POOL);
            case ZERG_ROACH: return List.of(ZERG_HATCHERY, ZERG_SPAWNING_POOL, ZERG_ROACH_WARREN);
            case ZERG_HYDRALISK: return List.of(ZERG_LAIR, ZERG_SPAWNING_POOL, ZERG_HYDRALISK_DEN);
            default:
                throw new UnsupportedOperationException("Sorry, I don't know how to make a :"+unit);
        }
    }


    public int getMineralCost(Units unit) {
        return agent.observation().getUnitTypeData(false).get(unit).getMineralCost().orElse(0);
    }

    public int getGasCost(Units unit) {
        return agent.observation().getUnitTypeData(false).get(unit).getVespeneCost().orElse(0);
    }
    public int countOfBuilding(Units unit) {
        return utils.getAllUnitsOfType(unit).size();
    }
}
