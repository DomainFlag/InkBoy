package core.features.light.lighting;

import core.features.ProgramState;

public abstract class Lighting implements ProgramState {

    private int type;

    public Lighting(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }
}
