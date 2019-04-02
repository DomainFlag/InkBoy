package core.features.light;

import core.math.Vector;

public class PointLight {

    private Attenuation attenuation;

    private Vector color;

    private Vector position;

    private float intensity;

    public PointLight(Vector color, Vector position, float intensity) {
        this.color = color;
        this.position = position;
        this.intensity = intensity;
    }

    public Attenuation getAttenuation() {
        return attenuation;
    }

    public void setAttenuation(Attenuation attenuation) {
        this.attenuation = attenuation;
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
