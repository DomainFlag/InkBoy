package modules.terrain;

import core.features.VertexBufferObject;
import core.math.Vector2f;
import tools.Camera;
import tools.Program;

public class TerrainQuadtree {

    private Node rootNode;

    public TerrainQuadtree(Camera camera) {
        VertexBufferObject vertexBufferObject = new VertexBufferObject();
        vertexBufferObject.allocate(generateRootPatch(), -1);

        rootNode = new Node(vertexBufferObject, camera, new Vector2f(0, 0), null, 0);
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

    private Vector2f[] generateRootPatch() {
        return new Vector2f[] {
                new Vector2f(0,0),
                new Vector2f(0.333f,0),
                new Vector2f(0.666f,0),
                new Vector2f(1,0),

                new Vector2f(0,0.333f),
                new Vector2f(0.333f,0.333f),
                new Vector2f(0.666f,0.333f),
                new Vector2f(1,0.333f),

                new Vector2f(0,0.666f),
                new Vector2f(0.333f,0.666f),
                new Vector2f(0.666f,0.666f),
                new Vector2f(1,0.666f),

                new Vector2f(0,1),
                new Vector2f(0.333f,1),
                new Vector2f(0.666f,1),
                new Vector2f(1,1)
        };
    }
}
