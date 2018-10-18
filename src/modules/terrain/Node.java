package modules.terrain;

import core.Settings;
import core.features.VertexBufferObject;
import core.math.*;
import tools.Camera;
import tools.Log;
import tools.Program;

import java.util.ArrayList;
import java.util.List;


public class Node {
    private static final int rootNodes = 2;

    private int lod;

    private VertexBufferObject vertexBufferObject;
    private double span;
	private List<Node> children;
	private Camera camera;
	private Vector index;
	private Vector worldindex;
	private Vector location;

	public Node(VertexBufferObject vertexBufferObject, Camera camera, Vector location, Vector index, int lod) {
	    this.vertexBufferObject = vertexBufferObject;
	    this.children = new ArrayList<>();
	    this.camera = camera;
	    this.index = index;
	    this.location = location;
	    this.worldindex = computeWorldPosition();
        this.lod = lod;
        this.span = 1.0f / Math.pow(2.0f, lod);

        addNodes();
	}

	public Vector computeWorldPosition() {
	    Vector4f loc = new Vector4f(location.get(0) * Settings.SCALE_XZ, 0, location.get(1) * Settings.SCALE_XZ, 1.0f);
        return camera.getCamera().multiplyVector(loc);
    }

    public List<Node> getChildren() {
        return children;
    }

    public void addNodes() {
        if(Vector.distanceVectors(worldindex) < Settings.TERRAIN_THRESHOLDS[lod])
            addNodes(lod);
        else removeNodes();
    }

    public void updateNode() {
        worldindex = computeWorldPosition();

        addNodes();
    }

    public void removeNodes() {
	    children.clear();
    }

    public void addNodes(int lod) {
	    if(children.size() != 0) {
            for(Node child : children)
                child.updateNode();
        } else {
            for(int i = 0; i < rootNodes; i++) {
                for(int j = 0; j < rootNodes; j++) {
                    Vector vec = Vector.addition(location, new Vector2f((float) (i /  span), (float) (j / span)));
                    Node node = new Node(vertexBufferObject, camera, vec, new Vector2f(i, j), lod + 1);

                    children.add(node);
                }
            }
        }
    }

    public void updateUniforms(Program program) {
	    program.updateUniform("u_center", Vector.addition(location,
                new Vector2f((float) (index.get(0) / span), (float) (index.get(1) / span))));
	    program.updateUniform("u_location", location.multiply(Settings.SCALE_XZ));
	    program.updateUniform("u_index", index);
        program.updateUniform("u_span", span);
        program.updateUniform("u_lod", lod);
    }

	public void render(Program program) {
	    for(int it = 0; it < children.size(); it++)
	        children.get(it).render(program);

	    if(children.size() == 0) {
            updateUniforms(program);
            vertexBufferObject.render();
        }
    }
}
