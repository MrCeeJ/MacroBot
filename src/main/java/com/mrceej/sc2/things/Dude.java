package com.mrceej.sc2.things;

import com.github.ocraft.s2client.bot.S2Agent;
import com.github.ocraft.s2client.bot.gateway.UnitInPool;
import com.github.ocraft.s2client.protocol.unit.Tag;
import com.github.ocraft.s2client.protocol.unit.Unit;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class Dude {

    private final S2Agent agent;
    @Getter
    private final Tag tag;
    @Getter
    private final UnitInPool unitInPool;
    @Getter
    private Unit unit;
    @Getter
    private Command currentCommand;

    public Dude(S2Agent agent, UnitInPool unitInPool) {
        this.agent = agent;
        this.tag = unitInPool.getTag();
        this.unitInPool = unitInPool;
        this.unit = unitInPool.unit();
    }

    public void update(UnitInPool unitInPool) {
        this.unit = unitInPool.unit();
    }

    void setCurrentCommand(Command newCommand) {
        if (currentCommand == null || currentCommand.ability != newCommand.ability || currentCommand.target != newCommand.target) {
            this.currentCommand = newCommand;
            agent.actions().unitCommand(unit, currentCommand.ability, currentCommand.target, currentCommand.queue);
        }
    }
}
