package core.features;

import tools.Program;

public interface ProgramState {

    void createUniforms(Program program, String uniformName);

    void updateUniforms(Program program, String uniformName);
}
