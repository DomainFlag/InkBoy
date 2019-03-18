package modules.fighter;

import core.Settings;
import core.math.Matrix;
import core.math.Vector3f;
import tools.Camera;
import tools.Program;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.glUseProgram;

public class Fighter extends Program {

    private Camera camera;

    private Matrix model = new Matrix(4);

    private Vector3f light = new Vector3f(0, 1.0f, 0);

	public Fighter(Camera camera) {
		super("fighter", GL_STATIC_DRAW, GL_TRIANGLES, "x-fighter");

		this.camera = camera;

        model.translate(Settings.SCALE_XZ / 2.0f, 0, Settings.SCALE_XZ / 2.0f - 5);
        model.scaling(0.1f, 0.1f, 0.1f);

        addSetting(GL_CULL_FACE);
        addSetting(GL_DEPTH_TEST);

        addUniforms();
	}
	
	public void addUniforms() {
        addUniform("u_camera", camera.getCamera());
        addUniform("u_projection", camera.getProjection());
        addUniform("u_model", model);
        addUniform("u_light", light.getData());
	}

	public void updateUniforms() {
        updateUniform("u_camera", camera.getCamera());
        updateUniform("u_projection", camera.getProjection());
        updateUniform("u_model", model);
    }

    @Override
    public void draw() {
        glDrawArrays(renderingType, 0, nb);
    }
}

