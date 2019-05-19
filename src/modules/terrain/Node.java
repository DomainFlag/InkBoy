package modules.terrain;

import core.Settings;
import core.features.VertexBufferObject;
import core.math.*;
import core.tools.Log;
import tools.Context;
import tools.Program;
import tools.texture.Texture;

import java.util.ArrayList;
import java.util.List;

public class Node {

    static VertexBufferObject vertexBufferObject1 = new VertexBufferObject();
    static VertexBufferObject vertexBufferObject2 = new VertexBufferObject();

    static {
        vertexBufferObject1.allocate(generateRootPatch1(), 4);
        vertexBufferObject2.allocate(generateRootPatch2(), 4);
    }

    private static Vector2f[] generateRootPatch1() {
        // 4 vertices for each patch
        return new Vector2f[] {
                new Vector2f(0, 0),
                new Vector2f(1.0f,0),
                new Vector2f(0,1.0f),
                new Vector2f(1.0f,1.0f)
        };

    }

    private static Vector2f[] generateRootPatch2() {
        // 4 vertices for each patch
        return new Vector2f[] {
                new Vector2f(0,1.0f),
                new Vector2f(0, 0),
                new Vector2f(1.0f,1.0f),
                new Vector2f(1.0f,0)
        };
    }

    private static final int rootNodes = 2;

    private int lod = -1;
    
    // node size
    private double span;
	private List<Node> children = new ArrayList<>();
	private Context context;
	private Vector index;
	private Vector worldLocation;
	private Vector location;
	private Vector parentCenter;
	private Vector center;
	private float height;
	private double cameraDist;

	public Node(Context context) {
	    this.context = context;
    }

	public Node(Context context, Vector location, Vector parentCenter, Vector index, int lod) {
	    this(context);

	    this.index = index;
	    this.location = location;
        this.span = 1.0f / (TerrainQuadtree.rootNodeCount * Math.pow(2.0f, lod));
        this.parentCenter = parentCenter;
        this.lod = lod;
        this.center = computeCenterLocation();
        this.height = computeWorldHeight();
        this.worldLocation = computeWorldPosition();
        this.cameraDist = getCameraDist();

        addNodes();
	}
	
	private float computeWorldHeight() {
        Texture texture = this.context.getContextTexture().getTexture("heightmaps/heightmap.bmp");
        
        return texture.getHeight(this.center.get(0), this.center.get(1), Settings.Terrain.MIN_HEIGHT, Settings.Terrain.MAX_HEIGHT);
    }

	private Vector computeWorldPosition() {
	    Vector4f loc = new Vector4f(this.center.get(0) * Settings.Terrain.SCALE_XZ, this.height,
                this.center.get(1) * Settings.Terrain.SCALE_XZ, 1.0f);

        return context.getCamera().getCamera().multiplyVector(loc);
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
	    return Vector.distanceVectors(this.worldLocation);
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
            for(int g = 0; g < rootNodes; g++) {
                for(int h = 0; h < rootNodes; h++) {
                    Vector vec = Vector.addition(location, new Vector2f((float) (h * span / 2.0f), (float) (g * span / 2.0f)));

                    Node node = new Node(context, vec, center, new Vector2f(g, h), lod + 1);

                    children.add(node);
                }
            }
        }
    }

    private Vector computeCenterLocation() {
        return Vector.addition(location, new Vector2f((float) span / 2.0f, (float) span  / 2.0f));
    }

    private void updateUniforms(Program program) {
	    program.updateUniform("u_center", parentCenter);
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

            if(index.get(0) == 0 && index.get(1) == 0 || index.get(0) == 1 && index.get(1) == 1)
                vertexBufferObject2.render();
            else vertexBufferObject1.render();
        }
    }
}
