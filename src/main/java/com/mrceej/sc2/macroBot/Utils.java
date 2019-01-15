package com.mrceej.sc2.macroBot;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.game.raw.StartRaw;
import com.github.ocraft.s2client.protocol.response.ResponseGameInfo;
import com.github.ocraft.s2client.protocol.spatial.Point;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.mrceej.sc2.things.Base;
import lombok.extern.log4j.Log4j2;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static com.github.ocraft.s2client.protocol.data.Units.*;

@Log4j2
public class Utils {

    private final MacroBot agent;
    private BuildManager buildManager;

    public Utils(MacroBot agent) {
        this.agent = agent;
        this.buildManager = agent.getBuildManager();
    }

    public List<UnitInPool> getDrones() {
        return getAllUnitsOfType(Units.ZERG_DRONE);
    }

    public List<UnitInPool> getBases() {
        List<UnitInPool> hatcheries = getAllUnitsOfType(ZERG_HATCHERY);
        hatcheries.addAll(getAllUnitsOfType(ZERG_LAIR));
        hatcheries.addAll(getAllUnitsOfType(ZERG_HIVE));
        return hatcheries;
    }

    public List<UnitInPool> getAllUnitsOfType(Units unit) {
        return agent.observation().getUnits(Alliance.SELF, (unitInPool -> unitInPool.unit().getType().equals(unit)));
    }

    public Comparator<UnitInPool> getLinearDistanceComparatorForUnitInPool(Point2d source) {
        return (p1, p2) -> {
            Double d1 = p1.unit().getPosition().toPoint2d().distance(source);
            Double d2 = p2.unit().getPosition().toPoint2d().distance(source);
            return d1.compareTo(d2);
        };
    }

    public Comparator<Point2d> getLinearDistanceComparatorForPoint2d(Point2d source) {
        return (p1, p2) -> {
            Double d1 = p1.distance(source);
            Double d2 = p2.distance(source);
            return d1.compareTo(d2);
        };
    }

    Set<Point2d> getNaturalsForBases(Set<Point2d> bases) {
        Set<Point2d> locations = new HashSet<>();
        for (Point2d s : bases) {
            locations.add(getNearestExpansionLocationTo(s));
        }
        return locations;
    }
    Point2d getNearestExpansionLocationTo(Point2d source) {
        return agent.query().calculateExpansionLocations(agent.observation()).stream()
                .map(Point::toPoint2d)
                .min(getLinearDistanceComparatorForPoint2d(source))
                .orElse(agent.observation().getStartLocation().toPoint2d());
    }

    Comparator<Base> getLinearDistanceComparatorForBase(Point2d location) {
        return (u1, u2) -> {
            Double d1 = u1.getBase().unit().getPosition().toPoint2d().distance(location);
            Double d2 = u2.getBase().unit().getPosition().toPoint2d().distance(location);
            return d1.compareTo(d2);
        };
    }

    Comparator<UnitInPool> getLinearDistanceComparatorForUnit(Point2d location) {
        return (u1, u2) -> {
            Double d1 = u1.unit().getPosition().toPoint2d().distance(location);
            Double d2 = u2.unit().getPosition().toPoint2d().distance(location);
            return d1.compareTo(d2);
        };
    }

    public UnitInPool findNearestMineralPatch(Point2d start) {
        List<UnitInPool> units = agent.observation().getUnits(Alliance.NEUTRAL, UnitInPool.isUnit(NEUTRAL_MINERAL_FIELD));
        double distance = Double.MAX_VALUE;
        UnitInPool target = null;
        for (UnitInPool unitInPool : units) {
            double d = unitInPool.unit().getPosition().toPoint2d().distance(start);
            if (d < distance) {
                distance = d;
                target = unitInPool;
            }
        }
        return target;
    }

    public List<UnitInPool> getUnitsInProduction(Units unit) {
        return agent.observation().getUnits(Alliance.SELF, (unitInPool -> unitInPool.unit().getType().equals(ZERG_EGG))).stream()
                .filter(egg -> egg.unit().getOrders().stream().anyMatch(order -> order.getAbility().equals(buildManager.getAbilityToMakeUnit(unit))))
                .collect(Collectors.toList());

    }

    public Set<Point2d> findEnemyStartLocation() {
        ResponseGameInfo gameInfo = agent.observation().getGameInfo();
        Optional<StartRaw> startRaw = gameInfo.getStartRaw();
        if (startRaw.isPresent()) {
            HashSet<Point2d> startLocations = new HashSet<>(startRaw.get().getStartLocations());
            startLocations.remove(agent.observation().getStartLocation().toPoint2d());
            if (startLocations.size() > 1) {
                log.info("Warning, more than one start location!");
            } else if (startLocations.size() == 1) {
                log.info("Identified enemy base location :" + startLocations.toArray()[0].toString());
            }
            return startLocations;
        } else {
            return null;
        }
    }

    // Tries to find a random location that can be pathed to on the map.
    // Returns Point2d if a new, random location has been found that is pathable by the unit.
    public Optional<Point2d> findEnemyPosition() {
        ResponseGameInfo gameInfo = agent.observation().getGameInfo();

        Optional<StartRaw> startRaw = gameInfo.getStartRaw();
        if (startRaw.isPresent()) {
            Set<Point2d> startLocations = new HashSet<>(startRaw.get().getStartLocations());
            startLocations.remove(agent.observation().getStartLocation().toPoint2d());
            if (startLocations.isEmpty()) return Optional.empty();
            return Optional.of(new ArrayList<>(startLocations)
                    .get(ThreadLocalRandom.current().nextInt(startLocations.size())));
        } else {
            return Optional.empty();
        }
    }
}
