package com.mrceej.sc2.things;

import com.mrceej.sc2.macroBot.MacroBot;
import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode(callSuper = false)
public class QueenArmy extends Army {

    final List<Queen> queens;

    public QueenArmy(MacroBot agent) {
        super(agent);
        this.queens = new ArrayList<>();
    }

    public void update() {
        for (Queen q : queens) {
            q.update();
        }
    }

    public void add(Queen queen) {
        this.queens.add(queen);
    }
}
