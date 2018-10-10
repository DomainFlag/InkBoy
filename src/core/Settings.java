package core;

import com.sun.javafx.geom.Vec4f;

import static org.lwjgl.opengl.GL46.*;

public class Settings {

    public static final Vec4f CLEAR_COLOR = new Vec4f(1.0f, 1.0f, 1.0f, 1.0f);

    public static final String PRODUCTION_MODE = "PRODUCTION_MODE";
    public static final String TESTING_MODE = "TESTING_MODE";

    public static final String TEXTURE_DEFAULT = "PNG";

    public static final float Z_FAR = 15.0f;
    public static final float Z_NEAR = 0.0001f;

    public static final int TERRAIN_DRAWING_TYPE = GL_TRIANGLES;

    public static final float[] TERRAIN_THRESHOLDS;

    static {
        TERRAIN_THRESHOLDS = new float[TERRAIN_THRESHOLDS_ENUM.values().length];
        for(int it = 0; it < TERRAIN_THRESHOLDS_ENUM.values().length; it++) {
            TERRAIN_THRESHOLDS[it] = TERRAIN_THRESHOLDS_ENUM.values()[it].ordinal();
        }
    }

    public enum TERRAIN_THRESHOLDS_ENUM {
        lod1_range(1750),
        lod2_range(874),
        lod3_range(386),
        lod4_range(192),
        lod5_range(100),
        lod6_range(50),
        lod7_range(20),
        lod8_range(5);

        TERRAIN_THRESHOLDS_ENUM(int value) {}
    }
}
