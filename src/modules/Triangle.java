package modules;

import tools.Program;
import core.Settings;

import java.util.ArrayList;

import static org.lwjgl.opengl.GL46.*;

public class Triangle extends Program {

    public Triangle() {
        super(Settings.PRODUCTION_MODE, GL_STATIC_DRAW, GL_TRIANGLES, "cube");

        addSetting(GL_CULL_FACE);
        addSetting(GL_DEPTH_TEST);
    }
}
