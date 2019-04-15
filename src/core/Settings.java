package core;
import core.math.Vector4f;

import java.util.HashMap;
import java.util.Map;

import static org.lwjgl.opengl.GL46.*;

public class Settings {

    public static final Vector4f CLEAR_COLOR = new Vector4f(0.2f, 0.2f, 0.2f, 1.0f);

    public static final String PRODUCTION_MODE = "PRODUCTION_MODE";
    public static final String TESTING_MODE = "TESTING_MODE";

    public static final float Z_FAR = 30000.0f;
    public static final float Z_NEAR = 0.0001f;

    public static final int TERRAIN_DRAWING_TYPE = GL_PATCHES;

    public static final float SCALE_XZ = 5000;

    public static final float[] TERRAIN_THRESHOLDS = new float[] {
            4250,
            2550,
            1350,
            910,
            630,
            420,
            330,
            275,
            100,
            0,
    };
}
