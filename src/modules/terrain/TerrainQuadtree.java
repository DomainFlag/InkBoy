package modules.terrain;

import core.math.Vector;
import core.math.Vector2f;
import tools.Context;
import tools.Program;

public class TerrainQuadtree {

    public static final int rootNodeCount = 4;

    private Node rootNode;

    public TerrainQuadtree(Context context) {
        rootNode = new Node(context);

        float span = 1.0f / (TerrainQuadtree.rootNodeCount * 2.0f);

        for(int g = 0; g < rootNodeCount; g++) {
            for(int h = 0; h < rootNodeCount; h++) {
                Vector location = new Vector2f((float) g / rootNodeCount, (float) h / rootNodeCount);
                Vector center = Vector.addition(location, new Vector2f(span, span));

                Node node = new Node(context, location, center, new Vector2f(g % 2, h % 2), 0);

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
