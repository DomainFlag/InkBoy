package modules.terrain;

import tools.Camera;
import tools.Program;

import static org.lwjgl.opengl.GL11.GL_CULL_FACE;
import static org.lwjgl.opengl.GL11.GL_DEPTH_TEST;
import static org.lwjgl.opengl.GL11.GL_TRIANGLES;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;

public class Terrain extends Program {

    private TerrainQuadtree terrainQuadtree;
    private Camera camera;

	public Terrain(Camera camera) {
		super("terrain", GL_DYNAMIC_DRAW, GL_TRIANGLES, null);

		this.camera = camera;

        addSetting(GL_CULL_FACE);
        addSetting(GL_DEPTH_TEST);

        updateUniforms();

        terrainQuadtree = new TerrainQuadtree(camera);
	}
	
	public void updateUniforms() {
        updateUniform("u_camera", camera.getCamera());
        updateUniform("u_projection", camera.getProjection());
        updateUniform("u_model", camera.getModel());
	}

    @Override
    public void render() {
        camera.change();

	    terrainQuadtree.render();
        terrainQuadtree.updateTree();

        updateUniform("u_camera", camera.getCamera());
        updateUniform("u_projection", camera.getProjection());
        updateUniform("u_model", camera.getModel());
    }
}

