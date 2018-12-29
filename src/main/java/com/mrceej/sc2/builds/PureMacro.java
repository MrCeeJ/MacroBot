package com.mrceej.sc2.builds;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.UnitType;
import com.github.ocraft.s2client.protocol.data.Units;
import com.mrceej.sc2.macroBot.BuildManager;
import com.mrceej.sc2.macroBot.MacroBot;
import com.mrceej.sc2.macroBot.Utils;
import lombok.extern.log4j.Log4j2;

import java.util.List;

import static com.github.ocraft.s2client.protocol.data.Units.ZERG_LARVA;
import static com.github.ocraft.s2client.protocol.data.Units.ZERG_OVERLORD;
import static java.lang.Math.max;

@Log4j2
public class PureMacro extends Build {

    private static final int DEFAULT_SATURATION = 14;
    private int minerals;
    private int supplyUsed;
    private int supplyCap;
    private final Utils utils;
    private final BuildManager buildManager;
    private List<UnitInPool> drones;
    private List<UnitInPool> bases;

    public PureMacro(MacroBot agent) {
        super(agent);
        this.utils = new Utils(agent);
        this.buildManager = agent.getBuildManager();

    }

    private UnitType getNextBuildItem() {
        if (checkOverlords() && hasLarvae()) {
            return ZERG_OVERLORD;
        } else if (checkBases()) {
            log.info("Need a Hatchery STAT!");
            return Units.ZERG_HATCHERY;
        } else if (checkDrones() && hasLarvae()) {
            return Units.ZERG_DRONE;
        } else {
            return Units.INVALID;
        }
    }

    private boolean hasLarvae() {
        return utils.getAllUnitsOfType(ZERG_LARVA).size() > 0;
    }

    public boolean build() {
        this.minerals = agent.observation().getMinerals();
        this.supplyUsed = agent.observation().getFoodUsed();
        this.supplyCap = agent.observation().getFoodCap();
        this.drones = utils.getDrones();
        this.bases = utils.getBases();

        UnitType unit = getNextBuildItem();
        if (unit != Units.INVALID) {
            return buildManager.build(unit);
        }
        return false;
    }

    private boolean checkDrones() {
        return supplyUsed < supplyCap &&
                minerals >= 50 &&
                drones.size() < 90;
    }

    private boolean checkBases() {
        if (bases.size() == 0) {
            return true;
        }

        if (drones.stream().anyMatch(u -> u.unit().getOrders().stream().anyMatch(o -> o.getAbility().equals(Abilities.BUILD_HATCHERY)))) {
            return false;
        } else
            return drones.size() / bases.size() > DEFAULT_SATURATION && minerals >= 300;
    }

    private boolean checkOverlords() {
//        if (buildManager.buildingUnit(ZERG_OVERLORD)) {
//            return false;
//        }
        int eggs = utils.getAllUnitsOfType(ZERG_LARVA).size();
        int bases = utils.getBases().size();
        int realBuffer = (eggs + bases) * 2;
        int defaultBuffer = supplyUsed / 6;
        int supplyInProduction = utils.getUnitsInProduction(ZERG_OVERLORD).size() *8;

        return (minerals >= 100 &&
                supplyCap < 200 &&
                supplyCap + supplyInProduction < supplyUsed + max(realBuffer,defaultBuffer));

    }
}
