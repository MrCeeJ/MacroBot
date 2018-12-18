package com.mrceej.sc2.macroBot;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Alliance;
import com.mrceej.sc2.CeejBot;
import com.mrceej.sc2.things.Base;

import java.util.Comparator;
import java.util.List;

import static com.github.ocraft.s2client.protocol.data.Units.*;

public class Utils {

    private final CeejBot agent;

    public Utils(CeejBot agent) {
        this.agent = agent;
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

    private Comparator<Point2d> getLinearDistanceComparatorForPoint2d(Point2d source) {
        return (p1, p2) -> {
            Double d1 = p1.distance(source);
            Double d2 = p2.distance(source);
            return d1.compareTo(d2);
        };
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
}
