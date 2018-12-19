package com.mrceej.sc2.macroBot;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Ability;
import com.github.ocraft.s2client.protocol.data.UnitType;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.github.ocraft.s2client.protocol.unit.Tag;
import com.github.ocraft.s2client.protocol.unit.Unit;
import com.mrceej.sc2.things.Base;
import lombok.extern.log4j.Log4j2;

import java.util.*;

import static com.github.ocraft.s2client.protocol.data.Units.*;

@Log4j2
public class BuildManager {

    private final MacroBot agent;
    private Utils utils;
    private Overseer overseer;
    private Map<UnitType, Integer> buildingCounts;
    private Random random = new Random();
    private List<Tag> workerTags;

    public BuildManager(MacroBot agent) {
        this.agent = agent;
    }

    public void init() {
        this.utils = agent.getUtils();
        this.overseer = agent.getOverseer();
        this.buildingCounts = new HashMap<>();
        this.workerTags = new ArrayList<>();

    }

    public boolean build(UnitType unit) {
        return build(unit, agent.observation().getStartLocation().toPoint2d());
    }

    public boolean build(UnitType unit, Point2d location) {
        Units item = (Units) unit;
        switch (item) {
            case ZERG_DRONE:
            case ZERG_OVERLORD:
                return buildUnit(unit);
            case ZERG_HATCHERY:
                return buildHatchery();
            case ZERG_SPAWNING_POOL:
            case ZERG_EVOLUTION_CHAMBER:
                return buildBuilding(unit, location);
            default:
                throw new UnsupportedOperationException("Sorry, don't know how to build " + item);
        }
    }

    public boolean build(UnitType unit, Base base) {
        if (base == null) {
            return build(unit);
        }
        Units item = (Units) unit;
        switch (item) {
            case ZERG_QUEEN:
                return buildQueen(base);
            case ZERG_LAIR:
            case ZERG_HIVE:
            default:
                throw new UnsupportedOperationException("Sorry, don't know how to build " + item + " at :" + base);
        }

    }

    private boolean buildQueen(Base base) {
        Unit hatch = base.getBase().unit();
        buildingCounts.put(ZERG_QUEEN, 1);
        agent.actions().unitCommand(hatch, Abilities.TRAIN_QUEEN, false);
        return true;
    }

    public boolean buildingUnit(Units unit) {
        return (buildingCounts.containsKey(unit) && buildingCounts.get(unit) > 0);
    }

    private boolean buildHatchery() {
        if (buildingUnit(ZERG_HATCHERY)) {
            log.info("Not placing hatchery, building " + buildingCounts.get(ZERG_HATCHERY) + " already.");
        } else {
            Point2d location = utils.getNearestExpansionLocationTo(agent.observation().getStartLocation().toPoint2d());
            Optional<UnitInPool> unitOptional = getNearestFreeDrone(location);
            if (unitOptional.isEmpty()) {
                log.info("Insufficient free workers");
            } else if (agent.observation().getMinerals() < 300) {
                log.info("Insufficient minerals :" + agent.observation().getMinerals());
            } else {
                UnitInPool unit = unitOptional.get();
                log.info("Drone " + unit.getTag() + " placing hatchery at :" + location);
                overseer.removeDroneFromBase(unit);
                agent.actions().unitCommand(unit.unit(), Abilities.BUILD_HATCHERY, location, false);
                buildingCounts.put(ZERG_HATCHERY, 1);
                return true;
            }
        }
        return false;
    }

    private Optional<UnitInPool> getNearestFreeDrone(Point2d location) {
        return agent.observation().getUnits(Alliance.SELF, UnitInPool.isUnit(ZERG_DRONE)).stream()
                .filter(unit -> unit.unit().getOrders().size() == 1)
                .filter(unit -> unit.unit().getOrders().get(0).getAbility().equals(Abilities.HARVEST_GATHER))
                .min(utils.getLinearDistanceComparatorForUnit(location));
    }

    private boolean buildBuilding(UnitType unit, Point2d location) {
        List<UnitInPool> drones = utils.getAllUnitsOfType(Units.ZERG_DRONE);
        if (drones.size() > 0) {
            buildingCounts.put(unit, 1);
            agent.actions().unitCommand(drones.get(0).unit(), getAbilityToMakeUnit(unit), getRandomLocationNearLocationForStructure(unit, location), false);
            return true;
        }
        log.warn("Warning, unable to build building :" + unit);
        return false;
    }

    private boolean buildUnit(UnitType unit) {
        List<UnitInPool> larvae = utils.getAllUnitsOfType(Units.ZERG_LARVA);
        if (larvae.size() > 0) {
            buildingCounts.put(unit, 1);
            agent.actions().unitCommand(larvae.get(0).unit(), getAbilityToMakeUnit(unit), false);
            return true;
        }
        return false;
    }

    private Ability getAbilityToMakeUnit(UnitType unitType) {
        return agent.observation().getUnitTypeData(false).get(unitType).getAbility().orElse(Abilities.INVALID);
    }

    public boolean buildingUnit(UnitType units) {
        Ability ability = getAbilityToMakeUnit(units);
        return agent.observation().getUnits(Alliance.SELF, u -> u.unit().getType().equals(ZERG_EGG)).stream()
                .anyMatch(e -> e.unit().getOrders().stream().anyMatch(order -> order.getAbility().equals(ability)));
    }

    void onUnitComplete(UnitInPool unit) {
        UnitType type = unit.unit().getType();
        if (type.equals(ZERG_DRONE)) {
            if (workerTags.contains( unit.getTag())) {
                return;
            } else {
                workerTags.add(unit.getTag());
            }
        }
        if (buildingCounts.containsKey(type)) {
            int number = buildingCounts.get(type);
            buildingCounts.put(type, number - 1);
        } else {
            buildingCounts.put(type, 0);
        }

    }

    private Point2d getRandomLocationNearLocationForStructure(UnitType structure, Point2d location) {
        Ability ability = getAbilityToMakeUnit(structure);
        Point2d newLocation;
        float dx;
        float dy;
        for (int tries = 0; tries < 1000; tries++) {
            dx = 30 * (random.nextFloat() - 1);
            dy = 30 * (random.nextFloat() - 1);
            newLocation = Point2d.of(location.getX() + dx, location.getY() + dy);
            if (location.distance(newLocation) > 5) {
                if (agent.query().placement(ability, newLocation)) {
                    return newLocation;
                }
            }
        }
        log.warn("Warning, unable to place building!");
        return null;
    }

    public boolean handleRequest(BuildingRequest buildingRequest) {
        boolean result = true;
        int done = 0;
        for (int i = 0; i < buildingRequest.count; i++) {
            if (build(buildingRequest.type, buildingRequest.base)) {
                done++;
            } else {
                result = false;
            }
        }
        buildingRequest.count -= done;
        return result;
    }
}
