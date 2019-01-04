package com.mrceej.sc2.macroBot;

import com.github.ocraft.s2client.protocol.data.UnitType;
import com.github.ocraft.s2client.protocol.data.Units;

import java.util.List;

import static com.github.ocraft.s2client.protocol.data.Units.*;

class BuildUtils {

    private Utils utils;
    private final MacroBot agent;

    public BuildUtils(MacroBot agent) {
        this.agent = agent;
    }

    void init() {
        this.utils = agent.getUtils();
    }

    public boolean checkCanMakeUnit(Units unit, int minerals, int gas) {
        return haveTechForUnit(unit) &&
                canAffordUnit(unit, minerals, gas) &&
                haveLarvaeIfNeeded(unit);
    }

    boolean canBuildUnit(UnitType unit) {
        return haveSupplyForUnit(unit) && checkCanMakeUnit((Units) unit, agent.observation().getMinerals(), agent.observation().getVespene());
    }

    private boolean haveSupplyForUnit(UnitType unit) {
        float supplyCost = agent.observation().getUnitTypeData(false).get(unit).getFoodRequired().orElse(0f);
        int supplyUsed = agent.observation().getFoodUsed();
        int supplyCap = agent.observation().getFoodCap();
        return supplyCap - supplyUsed > supplyCost;
    }

    private boolean haveTechForUnit(Units unit) {
        List<Units> requirements = getRequirements(unit);
        for (Units req : requirements) {
            if (countOfBuilding(req) == 0) {
                return false;
            }
        }
        return true;
    }

    private boolean canAffordUnit(Units unit, int minerals, int gas) {
        return getMineralCost(unit) <= minerals &&
                getGasCost(unit) <= gas;
    }

    private boolean haveLarvaeIfNeeded(Units unit) {
        switch (unit) {
            case ZERG_ZERGLING:
            case ZERG_ROACH:
            case ZERG_HYDRALISK:
            case ZERG_MUTALISK:
            case ZERG_OVERLORD:
            case ZERG_CORRUPTOR:
            case ZERG_ULTRALISK:
            case ZERG_INFESTOR:
            case ZERG_SWARM_HOST_MP:
            case ZERG_VIPER:
                return utils.getAllUnitsOfType(ZERG_LARVA).size() > 0;
            default:
                return true;
        }
    }

    private List<Units> getRequirements(Units unit) {
        switch (unit) {
            case ZERG_SPAWNING_POOL:
                return List.of(ZERG_HATCHERY);
            case ZERG_EVOLUTION_CHAMBER:
                return List.of(ZERG_HATCHERY);
            case ZERG_EXTRACTOR:
                return List.of();
            case ZERG_BANELING_NEST:
                return List.of(ZERG_HATCHERY, ZERG_SPAWNING_POOL);
            case ZERG_ROACH_WARREN:
                return List.of(ZERG_HATCHERY, ZERG_SPAWNING_POOL);
            case ZERG_HYDRALISK_DEN:
                return List.of(ZERG_LAIR);
            case ZERG_LURKER_DEN_MP:
                return List.of(ZERG_LAIR, ZERG_HYDRALISK_DEN);
            case ZERG_SPIRE:
                return List.of(ZERG_LAIR);
            case ZERG_LAIR:
                return List.of(ZERG_HATCHERY, ZERG_SPAWNING_POOL);
            case ZERG_GREATER_SPIRE:
                return List.of(ZERG_HIVE, ZERG_SPIRE);
            case ZERG_ULTRALISK_CAVERN:
                return List.of(ZERG_HIVE);
            case ZERG_DRONE:
                return List.of(ZERG_HATCHERY);
            case ZERG_OVERLORD:
                return List.of(ZERG_HATCHERY);
            case ZERG_QUEEN:
                return List.of(ZERG_HATCHERY, ZERG_SPAWNING_POOL);
            case ZERG_ZERGLING:
                return List.of(ZERG_HATCHERY, ZERG_SPAWNING_POOL);
            case ZERG_ROACH:
                return List.of(ZERG_HATCHERY, ZERG_SPAWNING_POOL, ZERG_ROACH_WARREN);
            case ZERG_HYDRALISK:
                return List.of(ZERG_LAIR, ZERG_SPAWNING_POOL, ZERG_HYDRALISK_DEN);
            default:
                throw new UnsupportedOperationException("Sorry, I don't know how to make a :" + unit);
        }
    }

    public int getMineralCost(Units unit) {
        switch (unit) {
            case ZERG_SPAWNING_POOL:
                return 200;
            case ZERG_EVOLUTION_CHAMBER:
                return 150;
            case ZERG_EXTRACTOR:
                return 25;
            case ZERG_BANELING_NEST:
                return 100;
            case ZERG_ROACH_WARREN:
                return 150;
            case ZERG_HYDRALISK_DEN:
                return 100;
            case ZERG_LURKER_DEN_MP:
                return 100;
            case ZERG_SPIRE:
                return 200;
            case ZERG_NYDUS_NETWORK:
                return 150;
            case ZERG_ULTRALISK_CAVERN:
                return 150;
            case ZERG_LAIR:
                return 150;
            case ZERG_HIVE:
                return 200;
            case ZERG_GREATER_SPIRE:
                return 100;
            case ZERG_ZERGLING:
                return 50;
            default:
                return queryMineralCost(unit);
        }
    }

    public int getGasCost(Units unit) {
        switch (unit) {
            case ZERG_SPAWNING_POOL:
                return 0;
            case ZERG_EVOLUTION_CHAMBER:
                return 0;
            case ZERG_EXTRACTOR:
                return 0;
            case ZERG_BANELING_NEST:
                return 50;
            case ZERG_ROACH_WARREN:
                return 0;
            case ZERG_HYDRALISK_DEN:
                return 100;
            case ZERG_LURKER_DEN_MP:
                return 150;
            case ZERG_SPIRE:
                return 200;
            case ZERG_NYDUS_NETWORK:
                return 200;
            case ZERG_ULTRALISK_CAVERN:
                return 200;
            case ZERG_LAIR:
                return 100;
            case ZERG_HIVE:
                return 150;
            case ZERG_GREATER_SPIRE:
                return 150;
            default:
                return queryGasCost(unit);
        }
    }

    private int queryMineralCost(Units unit) {
        return agent.observation().getUnitTypeData(false).get(unit).getMineralCost().orElse(0);
    }

    private int queryGasCost(Units unit) {
        return agent.observation().getUnitTypeData(false).get(unit).getVespeneCost().orElse(0);
    }

    private int countOfBuilding(Units unit) {
        return utils.getAllUnitsOfType(unit).size();
    }
}
