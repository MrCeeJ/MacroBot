package com.mrceej.sc2.macroBot;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.data.Ability;
import com.github.ocraft.s2client.protocol.data.UnitType;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.mrceej.sc2.CeejBot;

import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static com.github.ocraft.s2client.protocol.data.Units.ZERG_DRONE;

public class BuildManager {

    private CeejBot agent;
    private Utils utils;

    public BuildManager(CeejBot agent, Utils utils) {
        this.agent = agent;
        this.utils = utils;
    }

    public void build(UnitType unit) {
        Units item = (Units)unit;
        switch (item) {
            case ZERG_DRONE:
            case ZERG_OVERLORD:
                buildUnit(unit);
                break;
            case ZERG_HATCHERY:
                buildHatchery();
                break;
            case ZERG_SPAWNING_POOL:
                buildBuilding(unit);
                break;

        }
    }
    private void buildHatchery() {
        Point2d location = getNearestExpansionLocationTo(agent.observation().getStartLocation().toPoint2d());
        Optional<UnitInPool> unitOptional = getNearestFreeDrone(location);
        unitOptional.ifPresent(unit -> agent.actions().unitCommand(unit.unit(), Abilities.BUILD_HATCHERY, location, false));
    }

    Optional<UnitInPool> getNearestFreeDrone(Point2d location) {
        return agent.observation().getUnits(Alliance.SELF, UnitInPool.isUnit(ZERG_DRONE)).stream()
                .filter(unit -> unit.unit().getOrders().size() == 1)
                .filter(unit -> unit.unit().getOrders().get(0).getAbility().equals(Abilities.HARVEST_GATHER))
                .min(getLinearDistanceComparatorForUnit(location));
    }

    private void buildBuilding(UnitType unit) {
        List<UnitInPool> drones = utils.getAllUnitsOfType(Units.ZERG_DRONE);
        if (drones.size() > 0) {
            agent.actions().unitCommand(drones.get(0).unit(), getAbilityToMakeUnit(unit), false);
        }
    }

    public void buildUnit(UnitType unit) {
        List<UnitInPool> larvae = utils.getAllUnitsOfType(Units.ZERG_LARVA);
        if (larvae.size() > 0) {
            agent.actions().unitCommand(larvae.get(0).unit(), getAbilityToMakeUnit(unit), false);
        }
    }

    private Ability getAbilityToMakeUnit(UnitType unitType) {
        return agent.observation().getUnitTypeData(false).get(unitType).getAbility().orElse(Abilities.INVALID);
    }

    private Point2d getNearestExpansionLocationTo(Point2d source) {
        return agent.query().calculateExpansionLocations(agent.observation()).stream()
                .map(Point::toPoint2d)
                .min(getLinearDistanceComparatorForPoint2d(source))
                .orElseGet(null);
    }

    private Comparator<UnitInPool> getLinearDistanceComparatorForUnit(Point2d location) {
        return (u1, u2) -> {
            Double d1 = u1.unit().getPosition().toPoint2d().distance(location);
            Double d2 = u2.unit().getPosition().toPoint2d().distance(location);
            return d1.compareTo(d2);
        };
    }

    private Comparator<Point2d> getLinearDistanceComparatorForPoint2d(Point2d source) {
        return (p1, p2) -> {
            Double d1 = p1.distance(source);
            Double d2 = p2.distance(source);
            return d1.compareTo(d2);
        };
    }
}
