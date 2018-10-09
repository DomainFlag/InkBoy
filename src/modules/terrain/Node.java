package modules.terrain;

import core.Settings;
import core.math.Matrix;
import core.math.MatrixCore;
import core.math.Vector;
import tools.Program;

import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

public class Node {

    private static int maxDepth = 4;
    private static int rootNodes = 2;

	private List<Node> children;

	private Extremity extremity;

	public Node(Extremity extremity, int depth) {
	    this.children = new ArrayList<>();
	    this.extremity = extremity;

        if(depth < maxDepth)
            addNodes(depth);
	}

	public void addNodes(int depth) {
        for(int i = 0; i < rootNodes; i++) {
            for(int j = 0; j < rootNodes; j++) {
                Extremity childExtremity = extremity.extract(i, j);

                Node node = new Node(childExtremity, depth + 1);

                addChild(node);
            }
        }
    }

	public void render() {
	    bindToBuffer();
    }

    public void bindToBuffer() {
        int buffer = Program.createBoundBuffer(extremity.data());
    }

	public void addChild(Node node) {
        children.add(node);
    }
}
