package com.mrceej.sc2.macroBot;

import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.mrceej.sc2.things.Base;
import lombok.Getter;
import lombok.Setter;

public class BuildRequest {
    final Units type;
    @Getter
    private final boolean unique;
    int count;
    @Setter
    Base base;
    private final Point2d location;


    private BuildRequest(Units type) {
        this.type = type;
        this.count = 1;
        this.base = null;
        this.location = null;
        this.unique = false;

    }

    private BuildRequest(Units type, int count) {
        this.type = type;
        this.count = count;
        this.base = null;
        this.location = null;
        this.unique = false;

    }

    private BuildRequest(Units type, Base base) {
        this.type = type;
        this.count = 1;
        this.base = base;
        this.location = null;
        this.unique = false;

    }

    public BuildRequest(Units type, Base base, boolean unique) {
        this.type = type;
        this.base = base;
        this.unique = unique;
        this.count = 1;
        this.location = null;

    }

    private BuildRequest(Units type, Point2d location) {
        this.type = type;
        this.count = 1;
        this.base = null;
        this.location = location;
        this.unique = false;

    }

    public BuildRequest(Units type, int i, boolean unique) {
        this.type = type;
        this.count = i;
        this.base = null;
        this.location = null;
        this.unique = unique;
    }

    public BuildRequest(Units type, boolean unique) {
        this.type = type;
        this.count = 1;
        this.base = null;
        this.location = null;
        this.unique = unique;
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() != BuildRequest.class)
            return false;
        return this.equals((BuildRequest) other);
    }

    private boolean equals(BuildRequest other) {
        return this.type == other.type &&
                this.count == other.count &&
                this.base == other.base &&
                this.location == other.location &&
                this.unique == other.unique;
    }

    @Override
    public int hashCode() {
        int result = (count + 7) * 31;
        result = 31 * result + (type != null ? type.hashCode() : 5);
        result = 31 * result + (base != null ? base.hashCode() : 5);
        result = 31 * result + (location != null ? location.hashCode() : 5);
        result = 31 * result + (unique ? 2 : 1);
        return result;
    }

    public boolean canFulfilRequest(int minerals, int gas) {
       return true; //TODO: WIP
       //return buildUtils.checkCanMakeUnit(this.type, minerals, gas);
    }
}
