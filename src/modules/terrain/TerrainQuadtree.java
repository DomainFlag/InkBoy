package modules.terrain;

import core.math.Vector2f;

public class TerrainQuadtree {

    private Node rootNode;

    public TerrainQuadtree() {
        rootNode = new Node(generateRootPatch(), null, 0);
    }

    public Node getRootNode() {
        return rootNode;
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
