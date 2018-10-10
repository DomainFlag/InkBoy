package modules;

import core.features.VertexBufferObject;
import core.math.Matrix;
import core.math.Vector;
import core.math.Vector3f;
import org.lwjgl.BufferUtils;
import sun.security.provider.certpath.Vertex;
import tools.Program;
import core.Settings;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.system.MemoryUtil.NULL;

public class Triangle extends Program {

    private VertexBufferObject vertexBufferObject1;
    private VertexBufferObject vertexBufferObject2;

    public Triangle() {
        super("Triangle", GL_STATIC_DRAW, GL_TRIANGLES, null);

        addSetting(GL_CULL_FACE);
        addSetting(GL_DEPTH_TEST);

        float[] vectors1 = new float[] {
                -1.0f, 1.0f, -1.0f,
                -1.0f, -0.5f, 0.5f,
                0.5f, 0.5f, 0.5f
        };

        vertexBufferObject1 = new VertexBufferObject();
        vertexBufferObject1.allocate(vectors1, 3);


        float[] vectors2 = new float[] {
                -1.0f, 0.25f, -1.0f,
                -1.0f, -0.5f, 1.0f,
                1.0f, 0.25f, 1.0f
        };

        vertexBufferObject2 = new VertexBufferObject();
        vertexBufferObject2.allocate(vectors2, 3);
    }

    @Override
    public void render() {
        applySettings();

        vertexBufferObject1.render();
        vertexBufferObject2.render();
    }
}
