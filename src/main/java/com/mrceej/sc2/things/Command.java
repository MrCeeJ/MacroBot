package com.mrceej.sc2.things;

import com.github.ocraft.s2client.protocol.data.Ability;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
class Command {

    final Ability ability;
    final Point2d target;
    boolean queue;

    public Command(Ability ability, Point2d target) {
        this.ability = ability;
        this.target = target;
        this.queue = false;
    }

    public Command(Ability ability, Point2d target, boolean queue) {
        this(ability, target);
        this.queue = queue;
    }
}
