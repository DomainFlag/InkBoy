package modules.terrain;

import core.Settings;
import core.features.VertexBufferObject;
import core.math.Matrix;
import core.math.MatrixCore;
import core.math.Vector;
import core.math.Vector2f;
import java.util.ArrayList;
import java.util.List;

public class Node {

    private static final int maxDepth = 5;
    private static final int rootNodes = 2;

    private int currentDepth;

    private VertexBufferObject vertexBufferObject;
	private List<Node> children;
	private Vector location;

	private Extremity extremity;

	public Node(Extremity extremity, Vector location, int depth) {
	    this.children = new ArrayList<>();
	    this.extremity = extremity;
	    this.location = location;
        currentDepth = depth;

	    float[] data = extremity.extractTrianglesData();

        vertexBufferObject = new VertexBufferObject();
        vertexBufferObject.allocate(data, 3);

        if(depth < maxDepth)
            addNodes(depth);
	}

    public List<Node> getChildren() {
        return children;
    }

    public void addNodes(int depth) {
        for(int i = 0; i < rootNodes; i++) {
            for(int j = 0; j < rootNodes; j++) {
                Extremity childExtremity = extremity.extract(i, j);

                Node node = new Node(childExtremity, new Vector2f(i, j), depth + 1);

                addChild(node);
            }
        }
    }

	public void render() {
	    for(int it = 0; it < children.size(); it++)
	        children.get(it).render();

        vertexBufferObject.render();
    }

	public void addChild(Node node) {
        children.add(node);
    }
}
