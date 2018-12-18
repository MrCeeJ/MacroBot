package com.mrceej.sc2.macroBot;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.mrceej.sc2.CeejBot;
import com.mrceej.sc2.builds.Build;
import lombok.extern.log4j.Log4j2;

import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;


@Log4j2
public class Overseer {
    private final CeejBot agent;
    private Build build;
    private Utils utils;

    Overseer(CeejBot macroBot, Intel intel, Build build, Utils utils) {
        this.agent = macroBot;
        this.build = build;
        this.utils = utils;
    }

    void update() {

    }

    void onUnitCreated(UnitInPool unit) {
        Units type = (Units) unit.unit().getType();
        switch (type) {
            case ZERG_DRONE:
                break;
            case ZERG_QUEEN:
                break;
        }
    }

    void onBuildingComplete(UnitInPool unit) {
        Units type = (Units) unit.unit().getType();
        switch (type) {
            case ZERG_HATCHERY:
            case ZERG_LAIR:
            case ZERG_HIVE:
                balanceDrones();
                break;
            case ZERG_SPAWNING_POOL:
                break;
        }
    }




    private void balanceDrones() {
        List<UnitInPool> bases = utils.getBases();
        if (bases.size() < 2) {
            return;
        }
        int workers = agent.observation().getFoodWorkers();
        int average = workers / bases.size();

        List<SimpleEntry<UnitInPool, Integer>> basesOver = new ArrayList<>();
        List<SimpleEntry<UnitInPool, Integer>> basesUnder = new ArrayList<>();

        for (UnitInPool base : bases) {
            int assignedWorkers = base.unit().getAssignedHarvesters().orElse(0);
            if (assignedWorkers > average) {
                log.info("Base : " + base.getTag().toString() + " Assigned too many workers :" + assignedWorkers + " - average :" + average);
                basesOver.add(new SimpleEntry<>(base, assignedWorkers - average));
            } else if (assignedWorkers < average) {
                basesUnder.add(new SimpleEntry<>(base, average - assignedWorkers));
                log.info("Base : " + base.getTag().toString() + " Assigned too few workers :" + assignedWorkers + " - average :" + average);
            } else {
                log.info("Base : " + base.getTag().toString() + " Assigned just enough workers :" + assignedWorkers + " - average :" + average);
            }

            if (basesOver.size() > 0 && basesUnder.size() > 0) {
                basesOver.sort(getWorkerComparator());
                basesUnder.sort(getWorkerComparator());
                for (SimpleEntry<UnitInPool, Integer> overEntry : basesOver) {
                    int over = overEntry.getValue();
                    for (SimpleEntry<UnitInPool, Integer> underEntry : basesOver) {
                        int under = underEntry.getValue();
                        if (over >= under) {
                            reassignWorkers( under, overEntry.getKey(), underEntry.getKey());
                            over -= under;
                        } else {
                            reassignWorkers( over, overEntry.getKey(), underEntry.getKey());
                            break;
                        }
                        if (over == 0) {
                            break;
                        }
                    }
                }
            }
        }
    }

    private Comparator<SimpleEntry<UnitInPool, Integer>> getWorkerComparator() {
        return (b1, b2) -> {
            Integer i1 = b1.getValue();
            Integer i2 = b2.getValue();
            return i1.compareTo(i2);
        };
    }

    private void reassignWorkers(int i, UnitInPool from, UnitInPool to) {
        List<UnitInPool> workers = getNWorkersFromBase(i, from);
        Point2d target = utils.findNearestMineralPatch(to.unit().getPosition().toPoint2d()).unit().getPosition().toPoint2d();
        for (UnitInPool u : workers) {
            agent.actions().unitCommand(u.unit(), Abilities.SMART, target, false);
        }
    }

    private List<UnitInPool> getNWorkersFromBase(int i, UnitInPool from) {
        List<UnitInPool> drones = utils.getAllUnitsOfType(Units.ZERG_DRONE);
        drones.sort(utils.getLinearDistanceComparatorForUnit(from.unit().getPosition().toPoint2d()));
        return drones.subList(0, i);

    }
}
