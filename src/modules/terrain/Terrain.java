package modules.terrain;

import core.Settings;
import core.features.light.Light;
import core.features.light.lighting.DirectionalLight;
import core.math.Vector3f;
import core.normal.DisplacementMap;
import core.view.Camera;
import tools.Context;
import tools.Program;
import tools.texture.Texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;

public class Terrain extends Program {

    private TerrainQuadtree terrainQuadtree;

    private DisplacementMap displacementMap;

    private Light light = new Light();

	public Terrain(Context context, Camera camera) {
		super(context, "terrain", GL_DYNAMIC_DRAW, GL_TRIANGLES);

		setCamera(camera);

        addSetting(GL_DEPTH_TEST);
        addSetting(GL_CULL_FACE);

        setTessellationShader(3);

        Texture texture = getContext().getContextTexture().addTexture("heightmaps/heightmap.bmp", "u_texture", 0, GL_CLAMP_TO_EDGE);

        this.light.setLighting(new DirectionalLight(
                new Vector3f(0.9f, 0.8f, 0.02f),
                new Vector3f(0, -1.0f, 0),
                4.0f
        ));

        this.displacementMap = new DisplacementMap(context, texture);
        this.displacementMap.draw();

        useProgram();

        this.terrainQuadtree = new TerrainQuadtree(camera);

        createUniforms();
	}

    @Override
    public void createUniforms() {
        this.light.createUniforms(this);

        addUniform("u_scale", Settings.Terrain.SCALE_XZ);
        addUniform("u_height", Settings.Terrain.HEIGHT);
        addUniform("u_center");
        addUniform("u_location");
        addUniform("u_index");
        addUniform("u_span");
        addUniform("u_lod");

        for(int g = 0; g < Settings.Terrain.TERRAIN_THRESHOLDS.length; g++) {
            addUniform("u_morphing_thresholds[" + g + "]", Settings.Terrain.TERRAIN_THRESHOLDS[g]);
        }

        addUniform("u_camera", getCamera().getCamera());
        addUniform("u_projection", getCamera().getProjection());
        addUniform("u_model", getCamera().getModel());
    }

    public void updateUniforms() {
	    this.light.updateUniforms(this);

	    getContext().getContextTexture().bindProgram(this.program);
	    getContext().getContextTexture().bindTexture(this.displacementMap.getNormalMap(), "u_normal_map", 1);

        updateUniform("u_camera", getCamera().getCamera());
        updateUniform("u_projection", getCamera().getProjection());
        updateUniform("u_model", getCamera().getModel());
    }

    @Override
    public void draw() {
	    terrainQuadtree.render(this);
        terrainQuadtree.updateTree();
    }
}

