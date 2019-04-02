package modules.fighter;

import core.Settings;
import core.math.Matrix;
import core.math.Vector3f;
import tools.Camera;
import tools.Model;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

public class Fighter extends Model {

    private Matrix model = new Matrix(4);

	public Fighter(Camera camera) {
		super("fighter", GL_STATIC_DRAW, GL_TRIANGLES, "x-fighter");

		setCamera(camera);

		init();

        model.translate(0, 0, 0);

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        addSetting(GL_CULL_FACE);
        addSetting(GL_BLEND);
        addSetting(GL_DEPTH_TEST);

        addUniforms();
	}
	
	public void addUniforms() {
        addUniform("u_camera", getCamera().getCamera());
        addUniform("u_projection", getCamera().getProjection());
        addUniform("u_model", model);

        addUniform("u_ambient_light");
//        addUniform("u_camera_position");
        addUniform("u_specular_power");
	}

	public void updateUniforms() {
        updateUniform("u_camera", getCamera().getCamera());
        updateUniform("u_projection", getCamera().getProjection());
        updateUniform("u_model", model);

        updateUniform("u_ambient_light", new Vector3f(1.0f, 1.0f, 0));
//        updateUniform("u_camera_position", camera.getCamera());
        updateUniform("u_specular_power", 3.0f);
    }

    @Override
    public void draw() {
        glDrawArrays(renderingType, 0, nb);
    }
}

