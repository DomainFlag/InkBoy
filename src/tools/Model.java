package tools;

import core.features.light.PointLight;
import core.features.obj.Material;
import core.features.obj.Obj;
import core.math.Vector;
import core.math.Vector3f;
import core.math.Vector4f;

public abstract class Model extends Program {

    private Obj model = null;

    private PointLight pointLight;

    private String pathName;

    public Model(String pathProgram, int drawingType, int renderingType, String pathName) {
        super(pathProgram, drawingType, renderingType);

        this.pathName = pathName;

        this.pointLight = new PointLight(
                new Vector3f(1.0f, 1.0f, 1.0f),
                new Vector4f(0.0f, 0.0f, 2.0f, 1.0f),
                10.0f);

        this.pointLight.setAttenuation(
                new PointLight.Attenuation(1.0f, 0.05f, 0.05f)
        );
    }

    public void init() {
        if(pathName != null) {
            this.model = Obj.readModel(pathName);

            if(this.model != null) {
                this.model.print();
                parseModel(pathName);
            }
        }
    }

    private void parseModel(String pathName) {
        Log.v(model.vertices.size());

        loadDataV("a_position", model.vertices, 3);
        loadDataV("a_texture", model.textures, 3);
        loadDataV("a_normals", model.normals, 3);

        String pathModels = "models/" + pathName + "/";

        for(Material material : this.model.materials.values()) {
            for(Material.LightingColor lightingColor : material.colors) {
                if(lightingColor.index != -1) {
                    String name = "u_textures[" + lightingColor.index + "]";

                    addTexture(pathModels + lightingColor.texture, name, lightingColor.index, material.mode);
                }
            }
        }

        createPointLightUniform("u_point_light");
        updatePointLightUniform("u_point_light", this.pointLight);

        createMaterialUniform("u_materials");
        updateMaterialUniform("u_materials");
    }

    public void createPointLightUniform(String name) {
        addUniform(name + ".colour");
        addUniform(name + ".position");
        addUniform(name + ".intensity");
        addUniform(name + ".attenuation.constant");
        addUniform(name + ".attenuation.linear");
        addUniform(name + ".attenuation.exponent");
    }

    public void updatePointLightUniform(String name, PointLight pointLight) {
        updateUniform(name + ".colour", pointLight.getColor() );
        updateUniform(name + ".position", pointLight.getPosition());
        updateUniform(name + ".intensity", pointLight.getIntensity());

        PointLight.Attenuation att = pointLight.getAttenuation();

        updateUniform(name + ".attenuation.constant", att.getConstant());
        updateUniform(name + ".attenuation.linear", att.getLinear());
        updateUniform(name + ".attenuation.exponent", att.getExponent());
    }

    public void updateMaterialUniform(String name) {
        for(Material material : this.model.materials.values()) {

            for(int g = 0; g < material.colors.length; g++) {
                updateUniform(name + "[" + material.index + "].Kx[" + g + "].Kx", material.colors[g].color);
                updateUniform(name + "[" + material.index + "].Kx[" + g + "].Mx", material.colors[g].index);
            }

            updateUniform(name + "[" + material.index + "].Tf", material.Tf);
            updateUniform(name + "[" + material.index + "].Ni", material.Ni);
            updateUniform(name + "[" + material.index + "].Ns", material.Ns);
        }
    }

    public void createMaterialUniform(String name) {
        for(Material material : this.model.materials.values()) {

            for(int g = 0; g < material.colors.length; g++) {
                addUniform(name + "[" + material.index + "].Kx[" + g + "].Kx");
                addUniform(name + "[" + material.index + "].Kx[" + g + "].Mx");
            }

            addUniform(name + "[" + material.index + "].Tf");
            addUniform(name + "[" + material.index + "].Ni");
            addUniform(name + "[" + material.index + "].Ns");
        }
    }

    public abstract void updateUniforms();

    public abstract void draw();
}
