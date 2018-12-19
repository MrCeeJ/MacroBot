package com.mrceej.sc2.macroBot;

import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.mrceej.sc2.things.Base;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
public class BuildingRequest {
    Units type;
    int count;
    Base base;
    Point2d location;

    public BuildingRequest(Units type) {
        this.type = type;
        this.count = 1;
        this.base = null;
        this.location = null;
    }
    public BuildingRequest(Units type, int count) {
        this.type = type;
        this.count = count;
        this.base = null;
        this.location = null;

    }
    public BuildingRequest(Units type, Base base) {
        this.type = type;
        this.count = 1;
        this.base = base;
        this.location = null;

    }
    public BuildingRequest(Units type, Point2d location) {
        this.type = type;
        this.count = 1;
        this.base = null;
        this.location = location;

    }
}
