package com.mrceej.sc2.things;

import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Tag;
import com.mrceej.sc2.macroBot.MacroBot;
import lombok.EqualsAndHashCode;
import lombok.extern.log4j.Log4j2;

import java.util.*;

@Log4j2
@EqualsAndHashCode(callSuper = true)
public class ScoutArmy extends Army {

    private Set<Point2d> enemyBases;
    private Set<Point2d> enemyNaturals;
    private List<Scout> scouts;

    private Map<Point2d, Scout> allocations;
    private List<UnitInPool> towers;

    public void update() {
        for (Scout s : scouts) {
            s.update();
        }
    }

    public ScoutArmy(MacroBot agent) {
        super(agent);
        this.allocations = new HashMap<>();
        this.scouts = new ArrayList<>();
    }

    public int size() {
        return this.scouts.size();
    }

    public void setTargets(Set<Point2d> enemyBases, Set<Point2d> enemyNaturals, List<UnitInPool> towers) {
        this.enemyBases = enemyBases;
        this.enemyNaturals = enemyNaturals;
        this.towers = towers;
        enemyBases.forEach(p -> allocations.put(p, null));
        enemyNaturals.forEach(p -> allocations.put(p, null));
        towers.forEach(p -> allocations.put(p.unit().getPosition().toPoint2d(), null));
    }

    public void add(Scout scout) {
        this.scouts.add(scout);
        for (Point2d p : allocations.keySet()) {
            if (allocations.get(p) == null) {
                allocations.put(p, scout);
                scout.setScoutTarget(p);
                return;
            }
        }
        log.info("Too many scouts!");
    }

    public void onUnitDestroyed(UnitInPool unit) {
        Tag unitTag = unit.getTag();
        Scout deadScout = null;
        for (Scout scout : scouts) {
            if (scout.getTag().equals( unitTag)) {
                deadScout = scout;
            }
        }

        if (deadScout != null) {
            scouts.remove(deadScout);
            Point2d oldAllocation = null;
            for (Point2d point : allocations.keySet()) {
                if (allocations.get(point)!= null && allocations.get(point).equals(deadScout))
                {
                    oldAllocation = point;
                    break;
                }
            }
            if (oldAllocation != null) {
                allocations.put(oldAllocation, null);
            }
        }
    }
}
