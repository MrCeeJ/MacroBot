package com.mrceej.sc2.builds;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.UnitType;
import com.github.ocraft.s2client.protocol.data.Units;
import com.mrceej.sc2.CeejBot;
import com.mrceej.sc2.macroBot.BuildManager;
import com.mrceej.sc2.macroBot.Utils;

import java.util.List;

import static com.github.ocraft.s2client.protocol.data.Units.ZERG_OVERLORD;

public class PureMacro extends Build {

    private static final int DEFAULT_SATURATION = 18;
    int minerals;
    private int supplyUsed;
    private int supplyCap;
    private Utils utils;
    private BuildManager buildManager;
    private List<UnitInPool> drones;
    private List<UnitInPool> bases;

    public PureMacro(CeejBot agent, BuildManager buildManager) {
        super(agent);
        this.utils = new Utils(agent);
        this.buildManager = buildManager;
    }

    @Override
    public UnitType getNextBuildItem() {
        this.minerals = agent.observation().getMinerals();
        this.supplyUsed = agent.observation().getFoodUsed();
        this.supplyCap = agent.observation().getFoodCap();
        this.drones = utils.getDrones();
        this.bases = utils.getBases();

        if (checkOverlords()) {
            return ZERG_OVERLORD;
        } else if (checkBases()) {
            return Units.ZERG_HATCHERY;
        } else if (checkDrones()) {
            return Units.ZERG_DRONE;
        } else {
            return Units.INVALID;
        }
    }

    @Override
    public void update() {
        UnitType unit = getNextBuildItem();
        if (unit != Units.INVALID) {
            buildManager.build(unit);
        }
    }

    private boolean checkDrones() {
        return minerals >= 50 && drones.size() < 90;
    }

    private boolean checkBases() {
        if (drones.stream().anyMatch(u -> u.unit().getOrders().stream().anyMatch(o -> o.getAbility().equals(Abilities.BUILD_HATCHERY)))) {
            return false;
        } else
            return drones.size() / bases.size() > DEFAULT_SATURATION && minerals >= 250;
    }

    private boolean checkOverlords() {
       // if (agent.observation().getUnits(Alliance.SELF, u -> u.unit().getType().equals(Units.ZERG_EGG)).)
        if (buildManager.buildingUnit(ZERG_OVERLORD)) {
            return false;
        }
        int buffer = 2 + (supplyUsed / 10);
        return (supplyCap < 200 && supplyCap < supplyUsed + buffer);

    }
}
