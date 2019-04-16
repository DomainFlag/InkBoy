package core.features.light;

import tools.Program;

public abstract class Lighting {

    private int type;

    public Lighting(int type) {
        this.type = type;
    }

    public int getType() {
        return type;
    }

    public abstract void createUniforms(Program program, String uniformName);

    public abstract void updateUniforms(Program program, String uniformName);
}
