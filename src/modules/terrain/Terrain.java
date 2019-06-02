package modules.terrain;

import core.Settings;
import core.features.fog.Fog;
import core.features.light.Light;
import core.features.light.lighting.DirectionalLight;
import core.math.Vector3f;
import core.normal.DisplacementMap;
import core.tools.Log;
import tools.Context;
import tools.Program;
import tools.texture.material.MaterialTexture;
import tools.texture.Texture;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;

public class Terrain extends Program {

    private TerrainQuadtree terrainQuadtree;

    private DisplacementMap displacementMap;
    private Light light = new Light();
    private Fog fog;

	public Terrain(Context context) {
		super(context, "terrain", GL_DYNAMIC_DRAW, GL_TRIANGLES);

        addSetting(GL_DEPTH_TEST);
        addSetting(GL_CULL_FACE);

        Texture texture = getContext().getContextTexture().addTexture("heightmaps/heightmap.bmp",
                "u_texture", 0, GL_CLAMP_TO_EDGE);

        this.light.setLighting(new DirectionalLight(
                new Vector3f(1.0f, 1.0f, 1.0f),
                new Vector3f(0, 1.0f, 0f),
                0.55f
        ));

        this.fog = new Fog(
                new Vector3f(0.5f, 0.5f, 0.5f),
                0.0005f,
                4.0f
        );

        this.displacementMap = new DisplacementMap(context, texture);
        this.displacementMap.useProgram();
        this.displacementMap.draw();

        useProgram();

        this.terrainQuadtree = new TerrainQuadtree(context);

        MaterialTexture.setMaterialTextures(getContext().getContextTexture(), this, new MaterialTexture[] {
                new MaterialTexture("textures/terrain/grass0", 1.25f, 150.0f),
                new MaterialTexture("textures/terrain/ground0", 1.85f, 300.0f)
        }, "u_material_textures");

        createUniforms();
	}

    @Override
    public void createUniforms() {
        this.light.createUniforms(this);
        this.fog.createUniforms(this, "u_fog");

        addUniform("u_scale", Settings.Terrain.SCALE_XZ);
        addUniform("u_height", Settings.Terrain.MAX_HEIGHT);
        addUniform("u_center");
        addUniform("u_location");
        addUniform("u_index");
        addUniform("u_span");
        addUniform("u_lod");

        // texture material
        addUniform("u_distance", 300.0f);
        addUniform("u_factor", 0.35f);

        for(int g = 0; g < Settings.Terrain.TERRAIN_THRESHOLDS.length; g++) {
            addUniform("u_morphing_thresholds[" + g + "]", Settings.Terrain.TERRAIN_THRESHOLDS[g]);
        }

        addUniform("u_camera", getContext().getCamera().getCamera());
        addUniform("u_projection", getContext().getCamera().getProjection());
        addUniform("u_model", getContext().getCamera().getModel());
    }

    public void updateUniforms() {
	    this.light.updateUniforms(this);
        this.fog.updateUniforms(this, "u_fog");

	    getContext().getContextTexture().bindProgram(this.program);
	    getContext().getContextTexture().bindTexture(this.displacementMap.getNormalMap(), "u_normal_map", 1);

        updateUniform("u_camera", getContext().getCamera().getCamera());
        updateUniform("u_projection", getContext().getCamera().getProjection());
        updateUniform("u_model", getContext().getCamera().getModel());
    }

    @Override
    public void draw() {
	    terrainQuadtree.render(this);
        terrainQuadtree.updateTree();
    }
}

