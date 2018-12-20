package com.mrceej.sc2.things;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.data.Ability;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.github.ocraft.s2client.protocol.unit.Tag;
import com.github.ocraft.s2client.protocol.unit.Unit;
import lombok.Getter;

class Dude {

    private final S2Agent agent;
    @Getter
    private final Tag tag;
    @Getter
    private final UnitInPool unitInPool;
    @Getter
    private Unit unit;

    public Dude (S2Agent agent, UnitInPool unitInPool) {
        this.agent = agent;
        this.tag = unitInPool.getTag();
        this.unitInPool = unitInPool;
        this.unit = unitInPool.unit();
    }

    public void update(UnitInPool unitInPool) {
        this.unit = unitInPool.unit();
    }

    public void command(Ability ability, Point2d location) {
        agent.actions().unitCommand(this.unit, ability, location, false);
    }

}
