package modules.terrain;

import core.math.Vector2f;
import tools.Camera;

public class TerrainQuadtree {

    private Node rootNode;

    public TerrainQuadtree(Camera camera) {
        rootNode = new Node(generateRootPatch(), camera, null, 0);
    }

    public Node getRootNode() {
        return rootNode;
    }

    public void updateTree() {
        rootNode.updateNode();
    }

    public void render() {
        rootNode.render();
    }

    private Extremity generateRootPatch() {
        return new Extremity(
                new Vector2f( -0.5f, -0.5f),
                new Vector2f(0.5f, 0.5f)
        );
    }
}
