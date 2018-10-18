package modules;

import core.features.VertexBufferObject;
import core.math.Matrix;
import core.math.Vector;
import core.math.Vector2f;
import core.math.Vector3f;
import core.tools.BufferTools;
import org.lwjgl.BufferUtils;
import sun.security.provider.certpath.Vertex;
import tools.Camera;
import tools.Program;
import core.Settings;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.system.MemoryUtil.NULL;

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

        setTessellationShaders(4);

        vertexBufferObject = new VertexBufferObject();
        vertexBufferObject.allocate(BufferTools.createFloatArray(generatePatch()), 4, -1);
    }

    public Vector2f[] generatePatch(){
        // 6 vertices for each patch
        Vector2f[] vertices = new Vector2f[] {
                new Vector2f(-0.5f, -0.5f),
                new Vector2f(0.5f,-0.5f),
                new Vector2f(0.5f,0.5f),
                new Vector2f(-0.5f,0.5f)
        };

        return vertices;
    }

    @Override
    public void render() {
        applySettings();

        vertexBufferObject.render();
    }
}
