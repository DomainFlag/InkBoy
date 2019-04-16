package modules.fighter;

import core.features.light.DirectionalLight;
import core.features.light.Light;
import core.features.light.PointLight;
import core.features.light.SpotLight;
import core.math.Matrix;
import core.math.Vector3f;
import core.math.Vector4f;
import core.view.Camera;
import tools.Model;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

public class Fighter extends Model {

    private Light light;

    private PointLight pointLight;

    private DirectionalLight directionalLight;

    private SpotLight spotLight;

    private Matrix model = new Matrix(4);

	public Fighter(Camera camera) {
		super("fighter", GL_STATIC_DRAW, GL_TRIANGLES, "cube");

		this.light = new Light();

        /* Point Light */
		this.light.setLighting(
		        new PointLight(
		                new Vector3f(0.6f, 0.2f, 0.1f),
                        new Vector4f(0.0f, 0.0f, 4.0f, 1.0f),
                        new PointLight.Attenuation(1.0f, 0.5f, 0.5f),
                        1.0f
                )
        );

		/* Directional Light */
        this.light.setLighting(
                new DirectionalLight(
                        new Vector3f(1.0f, 1.0f, 1.0f),
                        new Vector3f(-1.0f, 0, 0.0f),
                        1.0f
                )
        );

        /* Spot Light */
        this.light.setLighting(
                new SpotLight(
                        new PointLight(
                                new Vector3f(1.0f, 1.0f, 1.0f),
                                new Vector4f(0.0f, 0.0f, 4.0f, 1.0f),
                                new PointLight.Attenuation(1.0f, 0.5f, 0.5f),
                                1.0f
                        ),
                        new Vector3f(0f, 0, -1.0f),
                        0.99f
                )
        );

        this.light.setLighting(
                new SpotLight(
                        new PointLight(
                                new Vector3f(1.0f, 1.0f, 1.0f),
                                new Vector4f(0.0f, 0.0f, -4.0f, 1.0f),
                                new PointLight.Attenuation(1.0f, 0.5f, 0.5f),
                                1.0f
                        ),
                        new Vector3f(0f, 0, 1.0f),
                        0.99f
                )
        );

		setCamera(camera);

		init();

        model.translate(0, 0, 0);

        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        addSetting(GL_BLEND);
        addSetting(GL_DEPTH_TEST);

        addUniforms();
	}

	public void addUniforms() {
	    this.light.createUniforms(this);

        addUniform("u_camera", getCamera().getCamera());
        addUniform("u_projection", getCamera().getProjection());
        addUniform("u_model", model);

        addUniform("u_ambient_light");
        addUniform("u_specular_power");
	}

	@Override
	public void updateUniforms() {
	    this.light.updateUniforms(this);

        updateUniform("u_camera", getCamera().getCamera());
        updateUniform("u_projection", getCamera().getProjection());
        updateUniform("u_model", model);

        updateUniform("u_ambient_light", new Vector3f(1.0f, 1.0f, 0));
        updateUniform("u_specular_power", 3.0f);
    }

    @Override
    public void draw() {
        glDrawArrays(renderingType, 0, nb);
    }
}

