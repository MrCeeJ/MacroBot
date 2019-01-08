package com.mrceej.sc2.things;

import lombok.EqualsAndHashCode;

import java.util.ArrayList;
import java.util.List;

@EqualsAndHashCode
public class QueenArmy extends Army {

    List<Queen> queens;

    public QueenArmy() {
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