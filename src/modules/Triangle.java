package modules;

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
                        -1.0f, 1.0f, 0.0f,
                        -1.0f, -1.0f, 0.0f,
                        1.0f, 1.0f, 0.0f
                });
        floatBuffer.flip();

        addAttribute("a_position", floatBuffer);
    }

    @Override
    public void keyCallback(int key, int action) {

    }

    @Override
    public void scrollCallback(double xoffset, double yoffset) {

    }
}
