package modules.terrain;

import core.Settings;
import core.view.Camera;
import tools.Program;

import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;

public class Terrain extends Program {

    private TerrainQuadtree terrainQuadtree;
    private Camera camera;

	public Terrain(Camera camera) {
		super("terrain", GL_DYNAMIC_DRAW, GL_TRIANGLES);

		this.camera = camera;

        addSetting(GL_DEPTH_TEST);

        setTessellationShaders(3);
        addUniforms();

        addTexture("heightmaps/heightmap.bmp", "u_texture", 0, GL_CLAMP_TO_EDGE);

        terrainQuadtree = new TerrainQuadtree(camera);
	}
	
	public void addUniforms() {
	    addUniform("u_scale", Settings.SCALE_XZ);
	    addUniform("u_center");
	    addUniform("u_location");
	    addUniform("u_index");
	    addUniform("u_span");
	    addUniform("u_lod");

	    for(int g = 0; g < Settings.TERRAIN_THRESHOLDS.length; g++) {
	        addUniform("u_morphing_thresholds[" + g + "]", Settings.TERRAIN_THRESHOLDS[g]);
        }

        addUniform("u_camera_position");

        addUniform("u_max_tess_factor", 12.0f);
	    addUniform("u_min_tess_factor", 5.0f);

        addUniform("u_camera", camera.getCamera());
        addUniform("u_projection", camera.getProjection());
        addUniform("u_model", camera.getModel());
	}

	public void updateUniforms() {
        updateUniform("u_camera", camera.getCamera());
        updateUniform("u_projection", camera.getProjection());
        updateUniform("u_model", camera.getModel());
    }

    @Override
    public void draw() {
	    terrainQuadtree.render(this);
        terrainQuadtree.updateTree();
    }
}

