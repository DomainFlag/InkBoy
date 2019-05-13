package modules.primitive;

import core.features.VertexBufferObject;
import core.math.Vector2f;
import tools.Context;
import tools.Program;

import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

public class Triangle extends Program {

    private VertexBufferObject vertexBufferObject;

    public Triangle(Context context) {
        super(context, "/primitive/triangle", GL_STATIC_DRAW, null);

        setTessellationShader(3);

        vertexBufferObject = new VertexBufferObject();
        vertexBufferObject.allocate(generatePatch(), 3);
    }

    public Vector2f[] generatePatch(){
        // 3 vertices for each patch
        Vector2f[] vertices = new Vector2f[] {
                new Vector2f(-0.5f, -0.5f),
                new Vector2f(0.5f,-0.5f),
                new Vector2f(0,0.5f)
        };

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
