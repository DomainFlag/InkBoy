package modules.terrain;

import core.Settings;
import core.features.VertexBufferObject;
import core.math.*;
import core.tools.Log;
import core.view.Camera;
import tools.Program;

import java.util.ArrayList;
import java.util.List;


public class Node {

    static VertexBufferObject vertexBufferObject1 = new VertexBufferObject();
    static VertexBufferObject vertexBufferObject2 = new VertexBufferObject();

    static {
        vertexBufferObject1.allocate(generateRootPatch1(), 3);
        vertexBufferObject2.allocate(generateRootPatch2(), 3);
    }

    private static Vector2f[] generateRootPatch1() {
        // 3 vertices for each patch
        return new Vector2f[] {
                new Vector2f(0.0f, 0.0f),
                new Vector2f(1.0f,1.0f),
                new Vector2f(0.0f,1.0f),

                new Vector2f(1.0f, 1.0f),
                new Vector2f(0.0f,0.0f),
                new Vector2f(1.0f,0.0f)
        };
    }

    private static Vector2f[] generateRootPatch2() {
        // 3 vertices for each patch
        return new Vector2f[] {
                new Vector2f(0.0f, 1.0f),
                new Vector2f(0.0f,0.0f),
                new Vector2f(1.0f,0.0f),

                new Vector2f(1.0f, 0.0f),
                new Vector2f(1.0f,1.0f),
                new Vector2f(0.0f,1.0f)
        };
    }

    private static final int rootNodes = 2;

    private int lod = -1;
    // distance of current tile span^2 = A(node)
    private double span;
	private List<Node> children = new ArrayList<>();
	private Camera camera;
	private Vector index;
	private Vector worldLocation;
	private Vector location;
	private Vector center;
	private double cameraDist;

	public Node(Camera camera) {
	    this.camera = camera;
    }

	public Node(Camera camera, Vector location, Vector center, Vector index, int lod) {
	    this(camera);

	    this.index = index;
	    this.location = location;
        this.span = 1.0f / (TerrainQuadtree.rootNodeCount * Math.pow(2.0f, lod));
        this.center = center;
        this.lod = lod;
        this.worldLocation = computeWorldPosition();
        this.cameraDist = getCameraDist();

        addNodes();
	}

	private Vector computeWorldPosition() {
	    Vector4f loc = new Vector4f((float) (location.get(0) + span / 2.0f) * Settings.Terrain.SCALE_XZ, 0,
                (float) (location.get(1) + span / 2.0f) * Settings.Terrain.SCALE_XZ, 1.0f);

        return camera.getCamera().multiplyVector(loc);
    }

    public List<Node> getChildren() {
        return children;
    }

    private void addNodes() {
        if(cameraDist < Settings.Terrain.TERRAIN_THRESHOLDS[lod]) {
            addNodes(lod);
        } else if(lod > 0) {
            removeNodes();
        }
    }

    private float getCameraDist() {
	    return Vector.distanceVectors(worldLocation);
    }

    public void updateNode() {
        this.worldLocation = computeWorldPosition();
        this.cameraDist = getCameraDist();

        addNodes();
    }

    private void removeNodes() {
	    children.clear();
    }

    private void addNodes(int lod) {
	    if(children.size() != 0) {
            for(Node child : children)
                child.updateNode();
        } else {
	        // Parent node center
	        Vector center = getCenterLocation();

            for(int g = 0; g < rootNodes; g++) {
                for(int h = 0; h < rootNodes; h++) {
                    Vector vec = Vector.addition(location, new Vector2f((float) (h *  span / 2.0f), (float) (g * span  / 2.0f)));

                    Node node = new Node(camera, vec, center, new Vector2f(g, h), lod + 1);

                    children.add(node);
                }
            }
        }
    }

    private Vector getCenterLocation() {
        return Vector.addition(location, new Vector2f((float) span / 2.0f, (float) span  / 2.0f));
    }

    private void updateUniforms(Program program) {
	    program.updateUniform("u_center", center);
	    program.updateUniform("u_location", location);
	    program.updateUniform("u_index", index);
        program.updateUniform("u_span", span);
        program.updateUniform("u_lod", lod);
    }

	public void render(Program program) {
        for(Node child : children)
            child.render(program);

	    if(children.size() == 0) {
            updateUniforms(program);

            if(index.get(0) == 0 && index.get(1) == 0 ||
                    index.get(0) == 1 && index.get(1) == 1)
                vertexBufferObject1.render();
            else vertexBufferObject2.render();
        }
    }
}
