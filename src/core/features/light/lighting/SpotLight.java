package core.features.light.lighting;

import core.features.light.Light;
import core.math.Vector3f;
import tools.Program;

public class SpotLight extends Lighting {

    private PointLight pointLight;

    private Vector3f direction;

    private float angle;

    public SpotLight(PointLight pointLight, Vector3f direction, float angle) {
        super(Light.SPOT_KEY);

        this.pointLight = pointLight;
        this.direction = direction;
        this.angle = angle;
    }

    public PointLight getPointLight() {
        return pointLight;
    }

    public Vector3f getDirection() {
        return direction;
    }

    public float getAngle() {
        return angle;
    }

    public void setPointLight(PointLight pointLight) {
        this.pointLight = pointLight;
    }

    public void setDirection(Vector3f direction) {
        this.direction = direction;
    }

    public void setAngle(float angle) {
        this.angle = angle;
    }

    @Override
    public void createUniforms(Program program, String uniformName) {
        this.pointLight.createUniforms(program, uniformName + ".pointLight");

        program.addUniform(uniformName + ".direction");
        program.addUniform(uniformName + ".angle");
    }

    @Override
    public void updateUniforms(Program program, String uniformName) {
        this.pointLight.updateUniforms(program, uniformName + ".pointLight");

        program.updateUniform(uniformName + ".direction", this.direction);
        program.updateUniform(uniformName + ".angle", this.angle);
    }
}
