package modules.terrain;

import core.Settings;
import core.features.VertexBufferObject;
import core.math.*;
import tools.Camera;
import tools.Log;

import java.util.ArrayList;
import java.util.List;

public class Node {

    private static final int maxDepth = 2;
    private static final int rootNodes = 2;

    private int depth;

    private VertexBufferObject vertexBufferObject;
	private List<Node> children;
	private Camera camera;
	private Vector location;
	private Vector worldLocation;

	private Extremity extremity;

	public Node(Extremity extremity, Camera camera, Vector location, int depth) {
	    this.children = new ArrayList<>();
	    this.camera = camera;
	    this.extremity = extremity;
	    this.location = location;
	    this.worldLocation = computeWorldPosition();

        this.depth = depth;

	    float[] data = extremity.extractLinesData();

        vertexBufferObject = new VertexBufferObject();
        vertexBufferObject.allocate(data, 3);

        addNodes();
	}

	public Vector computeWorldPosition() {
        return camera.getCamera().multiplyVector(extremity.getLocation());
    }

    public List<Node> getChildren() {
        return children;
    }

    public void addNodes() {
        if(Vector.distanceVectors(this.worldLocation) < Settings.TERRAIN_THRESHOLDS[depth])
            addNodes(depth);
        else removeNodes();
    }

    public void updateNode() {
        this.worldLocation = computeWorldPosition();

        addNodes();
    }

    public void removeNodes() {
	    children.clear();
    }

    public void addNodes(int depth) {
	    if(children.size() != 0) {
            for(Node child : children)
                child.updateNode();
        } else {
            for(int i = 0; i < rootNodes; i++) {
                for(int j = 0; j < rootNodes; j++) {
                    Extremity childExtremity = extremity.extract(i, j);

                    Node node = new Node(childExtremity, camera, new Vector2f(i, j), depth + 1);

                    children.add(node);
                }
            }
        }
    }

	public void render() {
	    for(int it = 0; it < children.size(); it++)
	        children.get(it).render();

        vertexBufferObject.render();
    }
}
