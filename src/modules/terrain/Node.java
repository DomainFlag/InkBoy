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
	private List<Node> children;
	private Camera camera;
	private Vector location;
	private Vector worldLocation;
	private Vector center;

	private Extremity extremity;

	public Node(Extremity extremity, Camera camera, Vector center, Vector location, int lod) {
	    this.children = new ArrayList<>();
	    this.camera = camera;
	    this.extremity = extremity;
	    this.location = location;
	    this.center = center;
	    this.worldLocation = computeWorldPosition();
        this.lod = lod;

	    float[] data = extremity.extractLinesData();

        vertexBufferObject = new VertexBufferObject();
        vertexBufferObject.allocate(data, 3);

        addNodes();
	}

	public Vector computeWorldPosition() {
        Vector vector = camera.getCamera().multiplyVector(extremity.getLocation());
        return vector;
    }

    public List<Node> getChildren() {
        return children;
    }

    public void addNodes() {
        if(Vector.distanceVectors(this.worldLocation) < Settings.TERRAIN_THRESHOLDS[lod])
            addNodes(lod);
        else removeNodes();
    }

    public void updateNode() {
        this.worldLocation = computeWorldPosition();

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
                    Extremity childExtremity = extremity.extract(i, j);

                    Node node = new Node(childExtremity, camera, extremity.getLocation(), new Vector2f(i, j), lod + 1);

                    children.add(node);
                }
            }
        }
    }

    public void updateUniforms(Program program) {
	    program.updateUniform("u_center", center);
	    program.updateUniform("u_location", location);
        program.updateUniform("u_span", extremity.getSpan());
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
