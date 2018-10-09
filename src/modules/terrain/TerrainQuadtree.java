package modules.terrain;

import core.math.Vector;

public class TerrainQuadtree {

    private Node rootNode;

    public TerrainQuadtree() {
        rootNode = new Node(generateRootPatch(), 0);
    }

    public void render() {
        rootNode.render();
    }

    private Extremity generateRootPatch() {
        return new Extremity(
                new Vector(-1.0f, -1.0f),
                new Vector(1.0f, 1.0f)
        );
    }
}
