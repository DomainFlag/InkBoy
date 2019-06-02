package core.features.light.lighting;

import core.features.light.Light;
import core.math.Vector;
import tools.Program;

public class PointLight extends Lighting {

    private Attenuation attenuation;

    private Vector color;

    private Vector position;

    private float intensity;

    public PointLight(Vector color, Vector position, float intensity) {
        super(Light.POINT_KEY);

        this.color = color;
        this.position = position;
        this.intensity = intensity;
    }

    public PointLight(Vector color, Vector position, Attenuation attenuation, float intensity) {
        this(color, position, intensity);

        this.attenuation = attenuation;
    }

    public Attenuation getAttenuation() {
        return attenuation;
    }

    public Vector getColor() {
        return color;
    }

    public Vector getPosition() {
        return position;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setAttenuation(Attenuation attenuation) {
        this.attenuation = attenuation;
    }

    @Override
    public void createUniforms(Program program, String uniformName) {
        program.addUniform(uniformName + ".colour");
        program.addUniform(uniformName + ".position");
        program.addUniform(uniformName + ".intensity");
        program.addUniform(uniformName + ".attenuation.constant");
        program.addUniform(uniformName + ".attenuation.linear");
        program.addUniform(uniformName + ".attenuation.exponent");
    }

    @Override
    public void updateUniforms(Program program, String uniformName) {
        program.updateUniform(uniformName + ".colour", getColor());
        program.updateUniform(uniformName + ".position", getPosition());
        program.updateUniform(uniformName + ".intensity", getIntensity());

        PointLight.Attenuation att = getAttenuation();

        program.updateUniform(uniformName + ".attenuation.constant", att.getConstant());
        program.updateUniform(uniformName + ".attenuation.linear", att.getLinear());
        program.updateUniform(uniformName + ".attenuation.exponent", att.getExponent());
    }

    public static class Attenuation {

        private float constant;
        private float linear;
        private float exponent;

        public Attenuation(float constant, float linear, float exponent) {
            this.constant = constant;
            this.linear = linear;
            this.exponent = exponent;
        }

        public float getConstant() {
            return constant;
        }

        public float getLinear() {
            return linear;
        }

        public float getExponent() {
            return exponent;
        }
    }
}
