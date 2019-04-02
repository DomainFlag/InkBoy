package core.features.obj;

import core.math.Vector;
import core.math.Vector3f;
import tools.Log;

import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

public class Material {

    // ambient, diffuse, specular reflectivity,
    public static final String[] colorNames = new String[] { "Ka", "Kd", "Ks" };

    // lighting colors
    public LightingColor[] colors = new LightingColor[3];

    // transmission filter
    public Vector Tf;

    // optical density ~ index of refraction light bending
    public float Ni = 1.0f;

    // specular exponent, power intensity of reflection
    public float Ns = 1.5f;

    // illumination obj
    public int illum = 4;

    // material name
    public String name;

    // material index
    public int index = 0;

    // texture loading mode
    public int mode = GL_CLAMP_TO_EDGE;

    public Material() {
        for(int g = 0; g < 3; g++)
            colors[g] = new LightingColor();
    }

    public void print() {
        Log.v("Material name: " + name + " with index: " + index);

        for(Material.LightingColor lightingColor : colors) {
            lightingColor.print();
        }
    }

    public boolean isLightingColor(String value) {
        for(String colorName : colorNames) {
            if(value.endsWith(colorName))
                return true;
        }

        return false;
    }

    public int parseLightingColor(String values[], int index) {
        if(values.length != 0) {
            String lightingColorName = values[0].substring(values[0].length() - 2);

            int colorLightingIndex = -1;
            for(int g = 0; g < colorNames.length; g++) {
                if(colorNames[g].equals(lightingColorName)) {
                    colorLightingIndex = g;

                    break;
                }
            }

            if(colorLightingIndex != -1) {
                LightingColor lightingColor = colors[colorLightingIndex];

                if(values[0].startsWith("map_")) {
                    lightingColor.index = index;
                    lightingColor.texture = values[1];

                    return 1;
                } else {
                    lightingColor.color = Vector.parseLine(values, 1, 3);
                }
            }
        }

        return 0;
    }

    public static class LightingColor {

        // lighting color
        public Vector color = new Vector3f(1.0f, 1.0f, 1.0f);

        // color texture used for light component, -1 if not specified
        public int index = -1;

        // texture linked to color lighting
        public String texture = null;

        public void print() {
            Log.v("Index: " + index + " texture: " + texture + " color: ", color);
        }
    };
}
