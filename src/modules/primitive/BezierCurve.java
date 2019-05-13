package modules.primitive;

import core.features.VertexBufferObject;
import core.math.Vector2f;
import tools.Context;
import tools.Program;

import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

public class BezierCurve extends Program {

    private VertexBufferObject vertexBufferObject;

    public BezierCurve(Context context) {
        super(context, "/primitive/bezier", GL_STATIC_DRAW, null);

        setTessellationShader(16);

        vertexBufferObject = new VertexBufferObject();
        vertexBufferObject.allocate(generatePatch(), 16);
    }

    public Vector2f[] generatePatch(){
        // 16 vertices for each patch
        Vector2f[] vertices = new Vector2f[] {
                new Vector2f(0f, 0f),
                new Vector2f(0.3333f,0f),
                new Vector2f(0.6666f,0f),
                new Vector2f(1.0f,0f),
                new Vector2f(0f, 0.3333f),
                new Vector2f(0.3333f,0.3333f),
                new Vector2f(0.6666f,0.3333f),
                new Vector2f(1.0f,0.3333f),
                new Vector2f(0f, 0.6666f),
                new Vector2f(0.3333f,0.6666f),
                new Vector2f(0.6666f,0.6666f),
                new Vector2f(1.0f,0.6666f),
                new Vector2f(0f, 1.0f),
                new Vector2f(0.3333f,1.0f),
                new Vector2f(0.6666f,1.0f),
                new Vector2f(1.0f,1.0f)
        };

        for(Vector2f vec : vertices) {
            vec.add(-0.5f, -0.5f);
            vec.multiply(0.5f);
        }

        return vertices;
    }

    @Override
    public void createUniforms() {

    }

    @Override
    public void updateUniforms() {

    }

    @Override
    public void draw() {
        applySettings();

        vertexBufferObject.render();
    }
}
