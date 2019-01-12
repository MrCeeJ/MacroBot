package com.mrceej.sc2.macroBot;

import com.github.ocraft.s2client.protocol.data.Units;
import com.github.ocraft.s2client.protocol.spatial.Point2d;
import com.mrceej.sc2.things.Base;
import lombok.Getter;
import lombok.Setter;

public class BuildingRequest {
    final Units type;
    @Getter
    private boolean unique;
    int count;
    @Setter
    Base base;
    final Point2d location;

    public BuildingRequest(Units type) {
        this.type = type;
        this.count = 1;
        this.base = null;
        this.location = null;
        this.unique = false;

    }

    public BuildingRequest(Units type, int count) {
        this.type = type;
        this.count = count;
        this.base = null;
        this.location = null;
        this.unique = false;

    }

    public BuildingRequest(Units type, Base base) {
        this.type = type;
        this.count = 1;
        this.base = base;
        this.location = null;
        this.unique = false;

    }

    public BuildingRequest(Units type, Base base, boolean unique) {
        this.type = type;
        this.base = base;
        this.unique = unique;
        this.count = 1;
        this.location = null;

    }

    public BuildingRequest(Units type, Point2d location) {
        this.type = type;
        this.count = 1;
        this.base = null;
        this.location = location;
        this.unique = false;

    }

    public BuildingRequest(Units type, int i, boolean unique) {
        this.type = type;
        this.count = i;
        this.base = null;
        this.location = null;
        this.unique = unique;
    }

    public BuildingRequest(Units type, boolean unique) {
        this.type = type;
        this.count = 1;
        this.base = null;
        this.location = null;
        this.unique = unique;
    }

    @Override
    public boolean equals(Object other) {
        if (other.getClass() != BuildingRequest.class)
            return false;
        return this.equals((BuildingRequest) other);
    }

    private boolean equals(BuildingRequest other) {
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
}
