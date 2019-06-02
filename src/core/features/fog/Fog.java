package core.features.fog;

import core.features.ProgramState;
import core.math.Vector3f;
import tools.Program;

public class Fog implements ProgramState {

    private Vector3f colour;
    private int activate = 1;
    private float density;
    private float exponent;

    public Fog(Vector3f colour, float density, float exponent) {
        this.colour = colour;
        this.density = density;
        this.exponent = exponent;
    }

    public Vector3f getColour() {
        return colour;
    }

    public float getDensity() {
        return density;
    }

    public float getExponent() {
        return exponent;
    }

    @Override
    public void createUniforms(Program program, String uniformName) {
        program.addUniform(uniformName + ".colour", colour);
        program.addUniform(uniformName + ".density", density);
        program.addUniform(uniformName + ".exponent", exponent);
        program.addUniform(uniformName + ".activate", activate);
    }

    @Override
    public void updateUniforms(Program program, String uniformName) {
        program.updateUniform(uniformName + ".activate", activate);
    }
}
