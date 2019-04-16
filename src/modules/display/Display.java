package modules.display;

import core.Settings;
import core.features.text.Text;
import core.math.*;
import core.view.Camera;
import tools.Context;
import tools.Program;

import java.util.Set;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;

public class Display extends Program {

    private Matrix orthographic = new Matrix(4);

    private Vector color = new Vector4f(0.015f, 0.01f, 0.014f, 1.0f);

    private Text text;

    public Display(Context context, Camera camera) {
        super("text", GL_DYNAMIC_DRAW, GL_TRIANGLES);

        addSetting(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);

        this.setContext(context);
        this.orthographic.orthographic(0, Settings.WIDTH, Settings.HEIGHT, 0, 1, -1);

        this.text = new Text(context, "./res/fonts/sans-serif/WorkSans-Medium.ttf",32, "Circle of Life");
        this.text.init(this);

        addUniforms();
    }

    public void addUniforms() {
        addUniform("u_projection", orthographic);
        addUniform("u_color", color);
    }

    @Override
    public void updateUniforms() {
        updateUniform("u_projection", orthographic);
        updateUniform("u_color", color);
    }

    @Override
    public void draw() {
        this.text.renderText(this, Settings.WIDTH / 2, Settings.HEIGHT / 2);

        glDrawArrays(renderingType, 0, nb);
    }

    @Override
    public void clear() {
        this.text.free();
    }
}
