package core.features.light;

import core.features.light.lighting.Lighting;
import core.math.Vector;
import core.math.Vector3f;
import core.tools.Log;
import tools.Program;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Light {

    public static final int DIRECTIONAL_KEY = 0;

    public static final int POINT_KEY = 1;

    public static final int SPOT_KEY = 2;

    private Vector ambientLightColour = new Vector3f(0.3f, 0.3f, 0.3f);

    private Float specularPower = null;

    private Map<Integer, List<Lighting>> lightings;

    private String[] bindings = new String[] {
            "u_directional_light", "u_point_light", "u_spot_light"
    };

    public Light() {
        this.lightings = new HashMap<>();
    }

    public Light(String[] bindings) {
        this();

        this.setBindings(bindings);
    }

    public Vector getAmbientLightColour() {
        return ambientLightColour;
    }

    public float getSpecularPower() {
        return specularPower;
    }

    public Map<Integer, List<Lighting>> getLightings() {
        return lightings;
    }

    public String[] getBindings() {
        return bindings;
    }

    private boolean checkBinding(int type) {
        if(type < 0 || type >= 3) {
            throw new IllegalArgumentException("Illegal " + type + " binding type");
        }

        return true;
    }

    public void setAmbientLightColour(Vector ambientLightColour) {
        this.ambientLightColour = ambientLightColour;
    }

    public void setSpecularPower(float specularPower) {
        this.specularPower = specularPower;
    }

    public void setBindings(String[] bindings) {
        if(bindings.length == 3) {
            this.bindings = bindings;
        }
    }

    public void setBinding(String binding, int type) {
        if(checkBinding(type)) {
            this.bindings[type] = binding;
        }
    }

    public void setLightings(Map<Integer, List<Lighting>> lightings) {
        this.lightings = lightings;
    }

    public void setLighting(Lighting lighting) {
        int type = lighting.getType();

        if(checkBinding(type)) {
            if(lightings.containsKey(type)) {
                lightings.get(type).add(lighting);
            } else {
                List<Lighting> lights = new ArrayList<>();
                lights.add(lighting);

                lightings.put(type, lights);
            }
        }
    }

    public void createUniforms(Program program) {
        program.addUniform("u_ambient_light");

        if(specularPower != null)
            program.addUniform("u_specular_power");

        for(Map.Entry<Integer, List<Lighting>> light : lightings.entrySet()) {
            List<Lighting> lights = light.getValue();

            for(int g = 0; g < lights.size(); g++) {
                Lighting lighting = lights.get(g);

                lighting.createUniforms(program,  bindings[light.getKey()] + "[" + g + "]");
            }
        }
    }

    public void updateUniforms(Program program) {
        program.updateUniform("u_ambient_light", ambientLightColour);

        if(specularPower != null)
            program.updateUniform("u_specular_power", specularPower);

        for(Map.Entry<Integer, List<Lighting>> light : lightings.entrySet()) {
            List<Lighting> lights = light.getValue();

            for(int g = 0; g < lights.size(); g++) {
                Lighting lighting = lights.get(g);

                lighting.updateUniforms(program, bindings[light.getKey()] + "[" + g + "]");
            }
        }
    }
}
