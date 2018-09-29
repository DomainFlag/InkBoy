package modules;

import core.math.Matrix;
import org.lwjgl.BufferUtils;
import tools.Program;
import core.Settings;

import java.nio.FloatBuffer;
import java.util.ArrayList;

import static org.lwjgl.opengl.GL46.*;

public class Triangle extends Program {

    public Triangle() {
        super("Triangle", GL_STATIC_DRAW, GL_TRIANGLES, null);

        addSetting(GL_CULL_FACE);
        addSetting(GL_DEPTH_TEST);

        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(9);
        floatBuffer.put(
                new float[] {
                        -3.0f, 3.0f, -15.0f,
                        -1.0f, -0.5f, 0.5f,
                        0.5f, 0.5f, 0.5f
                });
        floatBuffer.flip();

        addAttribute("a_position", floatBuffer);
        Matrix matrix = new Matrix(2);
        matrix.data[1] = 1.0f;
        matrix.data[0] = 0.0f;
        matrix.data[2] = 0.0f;
        matrix.data[3] = 0.0f;
        addUniform("u_matrix", matrix);
    }

    @Override
    public void keyCallback(int key, int action) {

    }

    @Override
    public void scrollCallback(double xoffset, double yoffset) {

    }
}
