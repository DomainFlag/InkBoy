package modules;

import core.features.VertexBufferObject;
import core.math.Vector2f;
import core.tools.BufferTools;
import tools.Camera;
import tools.Program;

import static org.lwjgl.opengl.GL46.*;

public class Triangle extends Program {

    private VertexBufferObject vertexBufferObject;

    private Camera camera;

    private int size = 0;

    public Triangle(Camera camera) {
        super("Triangle", GL_STATIC_DRAW, GL_TRIANGLES, null);

        this.camera = camera;

        addSetting(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        addTexture("heightmap.bmp");

        setTessellationShaders(3);

        vertexBufferObject = new VertexBufferObject();
        vertexBufferObject.allocate(generatePatch(), 3);
    }

    public Vector2f[] generatePatch(){
        // 3 vertices for each patch
        Vector2f[] vertices = new Vector2f[] {
                new Vector2f(-0.5f, -0.5f),
                new Vector2f(-0.5f,0.5f),
                new Vector2f(0.5f,0.5f),

                new Vector2f(0.5f,0.5f),
                new Vector2f(0.5f,-0.5f),
                new Vector2f(-0.5f, -0.5f)
        };

        return vertices;
    }

    @Override
    public void render() {
        applySettings();

        vertexBufferObject.render();
    }
}
