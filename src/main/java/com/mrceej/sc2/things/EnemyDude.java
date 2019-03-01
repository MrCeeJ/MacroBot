package com.mrceej.sc2.things;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.unit.Tag;
import com.github.ocraft.s2client.protocol.unit.Unit;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
class EnemyDude {

    private final S2Agent agent;
    @Getter
    private final Tag tag;
    @Getter
    private final UnitInPool unitInPool;
    @Getter
    private final Unit unit;

    public EnemyDude(S2Agent agent, UnitInPool unitInPool) {
        this.agent = agent;
        this.tag = unitInPool.getTag();
        this.unitInPool = unitInPool;
        this.unit = unitInPool.unit();
    }
}
