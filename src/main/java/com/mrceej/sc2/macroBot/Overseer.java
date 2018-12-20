package com.mrceej.sc2.macroBot;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Tag;
import com.mrceej.sc2.things.Base;
import lombok.extern.log4j.Log4j2;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;


@Log4j2
class Overseer {
    private final MacroBot agent;
    private Intel intel;
    private Utils utils;
    private Map<Tag, Base> bases;

    Overseer(MacroBot macroBot) {
        this.agent = macroBot;

    }

    public void init() {
        this.utils = agent.getUtils();
        this.intel = agent.getIntel();
        this.bases = new HashMap<>();

    }

    void update() {
        for (Base b: bases.values()){
            b.update();
        }
    }

    void onUnitCreated(UnitInPool unit) {
        log.info("Unit created :" + unit.unit().getType() + " tag:" + unit.getTag());
        Units type = (Units) unit.unit().getType();
        switch (type) {
            case ZERG_HATCHERY:
                checkForCompleteBuilding(unit);
                break;
            case ZERG_DRONE:
                getNearestBase(unit).allocateWorker(unit);
                break;
            case ZERG_QUEEN:
                allocateQueen(unit);
                break;
        }
    }

    private void allocateQueen(UnitInPool unit) {
        Base base = getNearestBase(unit);
        if (base.hasQueen()) {
            //TODO: add to army
        } else {
            base.allocateQueen(unit);
        }
    }

    private void checkForCompleteBuilding(UnitInPool unit) {
        if (unit.unit().getBuildProgress() == 1f) {
            onBuildingComplete(unit);
        }
    }

    void onBuildingComplete(UnitInPool unit) {
        log.info("Building created :" + unit.unit().getType() + " tag:" + unit.getTag());
        Units type = (Units) unit.unit().getType();
        switch (type) {
            case ZERG_HATCHERY:
                bases.put(unit.getTag(), new Base(agent, utils, unit));
                balanceDrones();
                checkBasesForQueens();
                break;
            case ZERG_LAIR:
            case ZERG_HIVE:
                upgradeBase(unit);
                balanceDrones();
                break;
            case ZERG_SPAWNING_POOL:
                checkBasesForQueens();
                break;
        }
    }

    private void checkBasesForQueens() {
        for (Base base : bases.values()) {
            if (!base.hasQueen()) {
                intel.requestQueen(base);
            }
        }
    }

    private void upgradeBase(UnitInPool unit) {
        Base base = bases.get(unit.getTag());
        base.updateUnit(unit);
    }

    private void balanceDrones() {
        if (bases.size() < 2) {
            return;
        }
        int workers = agent.observation().getFoodWorkers();
        int average = workers / bases.size();

        List<SimpleEntry<Base, Integer>> basesOver = new ArrayList<>();
        List<SimpleEntry<Base, Integer>> basesUnder = new ArrayList<>();

        for (Base base : bases.values()) {
            int assignedWorkers = base.countMineralWorkers();
            if (assignedWorkers > average) {
                basesOver.add(new SimpleEntry<>(base, assignedWorkers - average));
                log.info("Base : " + base.getTag().toString() + " Assigned too many workers :" + assignedWorkers + " - average :" + average);
            } else if (base.countMineralWorkers() < average) {
                basesUnder.add(new SimpleEntry<>(base, average - assignedWorkers));
                log.info("Base : " + base.getTag().toString() + " Assigned too few workers :" + assignedWorkers + " - average :" + average);
            } else {
                log.info("Base : " + base.getTag().toString() + " Assigned just enough workers :" + assignedWorkers + " - average :" + average);
            }
        }
        if (basesOver.size() > 0 && basesUnder.size() > 0) {
            basesOver.sort(getWorkerComparator());
            basesUnder.sort(getWorkerComparator());
            for (SimpleEntry<Base, Integer> overEntry : basesOver) {
                int over = overEntry.getValue();
                Base overBase = overEntry.getKey();
                for (SimpleEntry<Base, Integer> underEntry : basesUnder) {
                    int under = underEntry.getValue();
                    Base underBase = underEntry.getKey();
                    if (over >= under) {
                        moveDrones(under, overBase, underBase);
                        over -= under;
                    } else {
                        moveDrones(over, overBase, underBase);
                        break;
                    }
                    if (over == 0) {
                        break;
                    }
                }
            }
        }
    }

    private void moveDrones(int number, Base overBase, Base underBase) {
        log.info("Moving  : " + number + " drones from base:" + overBase.getTag() + " to :" + underBase.getTag());
        for (int i = 0; i < number; i++) {
            underBase.allocateWorker(overBase.getWorker());
        }
    }

    private Base getNearestBase(UnitInPool unit) {
        return getNearestBase(unit.unit().getPosition().toPoint2d());
    }

    private Base getNearestBase(Point2d point) {
        if (bases.size() == 1) {
            return (Base) bases.values().toArray()[0];
        } else {
            return bases.values().stream().min(utils.getLinearDistanceComparatorForBase(point)).orElse(null);
        }
    }

    private Comparator<SimpleEntry<Base, Integer>> getWorkerComparator() {
        return (b1, b2) -> {
            Integer i1 = b1.getValue();
            Integer i2 = b2.getValue();
            return i1.compareTo(i2);
        };
    }

    void onUnitDestroyed(UnitInPool unitInPool) {
        Units type = (Units) unitInPool.unit().getType();
        switch (type) {
            case ZERG_DRONE:
                removeDroneFromBase(unitInPool);
                break;
            case ZERG_QUEEN:
                removeQueenFromBase(unitInPool);
            case ZERG_HATCHERY:
                break;
        }
    }

    void removeDroneFromBase(UnitInPool unitInPool) {
        for (Base base : bases.values()) {
            base.removeWorker(unitInPool);
        }
    }
    void removeQueenFromBase(UnitInPool unitInPool) {
        for (Base base : bases.values()) {
            base.removeQueen(unitInPool);
        }
    }


    public void onUnitIdle(UnitInPool unitInPool) {
        Units type = (Units) unitInPool.unit().getType();
        switch (type) {
            case ZERG_EGG:
                addEggToBase();
                break;
            case ZERG_HATCHERY:
                break;
        }
    }

    private void addEggToBase() {

    }
}
