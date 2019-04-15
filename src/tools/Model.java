package tools;

import core.features.obj.Material;
import core.features.obj.Obj;

public abstract class Model extends Program {

    private Obj model = null;

    private String pathName;

    public Model(String pathProgram, int drawingType, int renderingType, String pathName) {
        super(pathProgram, drawingType, renderingType);

        this.pathName = pathName;
    }

    public void init() {
        if(pathName != null) {
            this.model = Obj.readModel(pathName);

            if(this.model != null) {
                parseModel(pathName);
            }
        }
    }

    private void parseModel(String pathName) {
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

        createMaterialUniform("u_materials");
        updateMaterialUniform("u_materials");
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
