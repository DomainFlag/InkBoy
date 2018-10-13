package modules.terrain;

import core.math.Vector2f;
import tools.Camera;
import tools.Program;

public class TerrainQuadtree {

    private Node rootNode;

    public TerrainQuadtree(Camera camera) {
        rootNode = new Node(generateRootPatch(), camera, null,null, 0);
    }

    public Node getRootNode() {
        return rootNode;
    }

    public void updateTree() {
        rootNode.updateNode();
    }

    public void render(Program program) {
        for(Node child : rootNode.getChildren()) {
            child.render(program);
        }
    }

    private Extremity generateRootPatch() {
        return new Extremity(
                new Vector2f( -0.5f, -0.5f),
                new Vector2f(0.5f, 0.5f)
        );
    }
}
