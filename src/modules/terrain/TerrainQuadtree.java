package modules.terrain;

import core.math.Vector2f;
import core.math.Vector4f;
import core.tools.Log;
import core.view.Camera;
import tools.Program;

public class TerrainQuadtree {

    public static final int rootNodeCount = 4;

    private Node rootNode;

    public TerrainQuadtree(Camera camera) {
        rootNode = new Node(camera);

        for(int g = 0; g < rootNodeCount; g++) {
            for(int h = 0; h < rootNodeCount; h++) {
                Vector2f location = new Vector2f((float) g / rootNodeCount, (float) h / rootNodeCount);
                Node node = new Node(camera, location, location, null, 0);

                rootNode.getChildren().add(node);
            }
        }
    }

    public Node getRootNode() {
        return rootNode;
    }

    public void updateTree() {
        for(Node node : rootNode.getChildren()) {
            node.updateNode();
        }
    }

    public void render(Program program) {
        for(Node child : rootNode.getChildren()) {
            child.render(program);
        }
    }
}
