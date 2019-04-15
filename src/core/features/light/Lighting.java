package core.features.light;

import tools.Program;

public abstract class Lighting {

    public abstract void createUniforms(Program program, String uniformName);

    public abstract void updateUniforms(Program program, String uniformName);
}
