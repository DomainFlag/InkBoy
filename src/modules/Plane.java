package modules;

import core.math.Vector;
import core.math.Vector3f;
import core.view.Camera;
import tools.Context;
import tools.Program;

import java.util.ArrayList;
import java.util.List;

import static org.lwjgl.opengl.GL46.*;

public class Plane extends Program {

    public Plane(Context context) {
        super(context,"Plane", GL_STATIC_DRAW, GL_TRIANGLES);

        addSetting(GL_DEPTH_TEST);

        int offset = 2;

        List<Vector> vectorList = new ArrayList<>();
        for(int g = -100; g < 100; g += offset) {
            for(int h = -100; h < 100; h += offset) {
                vectorList.add(new Vector3f(g, -100, h));
                vectorList.add(new Vector3f(g + offset, -10, h));
                vectorList.add(new Vector3f(g + offset, -10, h + offset));
                vectorList.add(new Vector3f(g + offset, -10, h + offset));
                vectorList.add(new Vector3f(g, -10, h + offset));
                vectorList.add(new Vector3f(g, -10, h));
            }
        }

        loadDataV("a_position_a", vectorList, 3);

        createUniforms();
    }

    @Override
    public void createUniforms() {
        addUniform("u_camera", getContext().getCamera().getCamera());
        addUniform("u_projection", getContext().getCamera().getProjection());
    }

    @Override
    public void updateUniforms() {
        updateUniform("u_camera", getContext().getCamera().getCamera());
        updateUniform("u_projection", getContext().getCamera().getProjection());
    }

    @Override
    public void draw() {
        glDrawArrays(renderingType, 0, nb);
    }
}
