package com.mrceej.sc2.macroBot;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Tag;
import com.mrceej.sc2.things.Base;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.log4j.Log4j2;

import java.util.AbstractMap.SimpleEntry;
import java.util.*;


@Log4j2
class UnitManager {
    private final MacroBot agent;
    private MacroManager macroManager;
    private Utils utils;
    @Getter
    private Map<Tag, Base> bases;
    @Getter
    @Setter
    private Base main;
    private ArmyManager armyManager;

    UnitManager(MacroBot macroBot) {
        this.agent = macroBot;

    }

    public void init() {
        this.utils = agent.getUtils();
        this.macroManager = agent.getMacroManager();
        this.bases = new HashMap<>();
        this.armyManager = new ArmyManager();

    }

    void update() {
        for (Base b : bases.values()) {
            b.update();
        }
    }

    void onUnitCreated(UnitInPool unit) {
        log.info("Unit created :" + unit.unit().getType() + " tag:" + unit.getTag());
        Units type = (Units) unit.unit().getType();
        switch (type) {
            case ZERG_HATCHERY:
                checkForMain(unit);
                break;
            case ZERG_DRONE:
                getNearestBase(unit).allocateWorker(unit);
                break;
            case ZERG_QUEEN:
                allocateQueen(unit);
                break;
            case ZERG_EXTRACTOR:
                allocateExtractor(unit);
                break;
            case ZERG_ZERGLING:
            case ZERG_ROACH:
            case ZERG_HYDRALISK:
            case ZERG_MUTALISK:
            case ZERG_ULTRALISK:
            case ZERG_CORRUPTOR:
            case ZERG_BROODLORD:
            case ZERG_RAVAGER:
            case ZERG_LURKER_MP:
                allocateSoldier(unit);
                break;
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
                macroManager.onBuildingComplete(type);
                break;
            case ZERG_EXTRACTOR:
                activateExtractor(unit);
                break;
            case ZERG_ROACH_WARREN:
            default:
                macroManager.onBuildingComplete(type);
                break;
        }
    }

    private void allocateSoldier(UnitInPool unit) {
        Base base = getNearestBase(unit);
        base.allocateUnitToArmy(unit);
        armyManager.addUnit(unit);
    }
    private void activateExtractor(UnitInPool unit) {
        Base base = getNearestBase(unit);
        base.transferDronesToExtractor(unit);
    }

    private void allocateExtractor(UnitInPool unit) {
        Base base = getNearestBase(unit);
        base.allocateExtractor(unit);
    }

    private void allocateQueen(UnitInPool unit) {
        Base base = getNearestBase(unit);
        if (base.hasQueen()) {
            base.allocateUnitToArmy(unit);
        } else {
            base.allocateQueen(unit);
        }
    }

    private void checkForMain(UnitInPool unit) {
        if (unit.unit().getBuildProgress() == 1f) {
            Base main = new Base(agent, utils, unit);
            bases.put(unit.getTag(), main);
            setMain(main);
        }
    }

    private void checkBasesForQueens() {
        for (Base base : bases.values()) {
            if (!base.hasQueen()) {
                macroManager.requestQueen(base);
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

    Base getNearestBase(Point2d point) {
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
                removeBase(unitInPool);
                break;
        }
    }

    private void removeBase(UnitInPool unitInPool) {
        bases.remove(unitInPool.getTag());
    }

    void removeDroneFromBase(UnitInPool unitInPool) {
        for (Base base : bases.values()) {
            base.removeWorker(unitInPool);
        }
    }

    private void removeQueenFromBase(UnitInPool unitInPool) {
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
