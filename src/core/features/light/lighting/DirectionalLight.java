package core.features.light.lighting;

import core.features.light.Light;
import core.math.Vector3f;
import tools.Program;

public class DirectionalLight extends Lighting {

    private Vector3f color;

    private Vector3f direction;

    private float intensity;

    private float lightAngle;

    public DirectionalLight(Vector3f color, Vector3f direction, float intensity) {
        super(Light.DIRECTIONAL_KEY);

        this.lightAngle = -((float) Math.toDegrees(Math.acos(direction.get(0))) - 90);
        this.color = color;
        this.direction = direction;
        this.intensity = intensity;
    }

    public DirectionalLight(DirectionalLight directionalLight) {
        this(directionalLight.color, directionalLight.direction, directionalLight.intensity);
    }

    public Vector3f getColor() {
        return color;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public float getIntensity() {
        return intensity;
    }

    public void setColor(Vector3f color) {
        this.color = color;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public void setIntensity(float intensity) {
        this.intensity = intensity;
    }

    public void update() {
        // Update directional light direction, intensity and colour
        lightAngle += 0.01f;

        if(lightAngle > 90) {
            setIntensity(0);

            if(lightAngle >= 360) {
                lightAngle = -90;
            }
        } else if(lightAngle <= -80 || lightAngle >= 80) {
            float factor = 1 - (Math.abs(lightAngle) - 80) / 10.0f;
            setIntensity(factor);

            getColor().set(1, Math.max(factor, 0.9f));
            getColor().set(2, Math.max(factor, 0.5f));
        } else {
            setIntensity(1);

            getColor().set(1, 1, 1);
        }

        double angRad = Math.toRadians(lightAngle);

        getDirection().set(0, (float) Math.sin(angRad));
        getDirection().set(1, (float) Math.cos(angRad));
    }

    @Override
    public void createUniforms(Program program, String uniformName) {
        program.addUniform(uniformName + ".colour");
        program.addUniform(uniformName + ".direction");
        program.addUniform(uniformName + ".intensity");
    }

    @Override
    public void updateUniforms(Program program, String uniformName) {
        program.updateUniform(uniformName + ".colour", getColor());
        program.updateUniform(uniformName + ".direction", getDirection());
        program.updateUniform(uniformName + ".intensity", getIntensity());
    }
}
