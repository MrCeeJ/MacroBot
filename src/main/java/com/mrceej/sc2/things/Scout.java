package com.mrceej.sc2.things;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Abilities;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode(callSuper = true)
public class Scout extends Dude {

    private Point2d scoutTarget;
    private double distanceToScoutTarget;
    private int tick;

    public Scout(S2Agent agent, UnitInPool unitInPool) {
        super(agent, unitInPool);
        this.tick = 0;
        this.distanceToScoutTarget = Double.MAX_VALUE;
    }

    public void setScoutTarget(Point2d p) {
        this.scoutTarget = p;
        this.distanceToScoutTarget = this.getUnit().getPosition().toPoint2d().distance(scoutTarget);
    }

    public void update() {
        tick++;
        if (tick > 5) {
            tick = 0;
            distanceToScoutTarget = this.getUnit().getPosition().toPoint2d().distance(scoutTarget);
            if (distanceToScoutTarget > 10d) {
                Command currentCommand = getCurrentCommand();
                if (currentCommand==null || currentCommand.ability != Abilities.MOVE || currentCommand.target != scoutTarget) {
                    this.giveCommand(new Command(Abilities.MOVE, scoutTarget));
                }
            }
        }
    }
}
