package tools.texture.material;

import core.tools.Log;
import tools.Program;
import tools.texture.ContextTexture;
import tools.texture.Texture;

import java.util.HashMap;

import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL11.GL_REPEAT;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;

public class MaterialTexture {

    private static final String[] types = new String[] {
            "diffuse", "displacement", "normal"
    };

    private HashMap<String, Texture> materials = new HashMap<>();

    private String material;
    private float verticalScale;
    private float horizontalScale;

    public MaterialTexture(String material, float verticalScale, float horizontalScale) {
        this.material = material;
        this.verticalScale = verticalScale;
        this.horizontalScale = horizontalScale;
    }

    public HashMap<String, Texture> getMaterials() {
        return materials;
    }

    public void setMaterials(HashMap<String, Texture> materials) {
        this.materials = materials;
    }

    public void setMaterials(ContextTexture contextTexture, Program program, String uniformName) {
        for(String type : types) {
            Integer textureUnitIndex = contextTexture.generateTextureIndex();
            Texture texture = contextTexture.addTexture(this.material + "_" + type + ".jpg",
                    uniformName + "." + type, textureUnitIndex, GL_LINEAR);

            this.materials.put(type, texture);
        }

        this.createUniforms(program, uniformName);
    }

    public void createUniforms(Program program, String uniformName) {
        program.addUniform(uniformName + ".vertical_scale", this.verticalScale);
        program.addUniform(uniformName + ".horizontal_scale", this.horizontalScale);
    }

    public static void setMaterialTextures(ContextTexture contextTexture, Program program, MaterialTexture[] materialTextures, String uniformName) {
        for(int g = 0; g < materialTextures.length; g++) {
            MaterialTexture materialTexture = materialTextures[g];

            materialTexture.setMaterials(contextTexture, program, uniformName + "[" + g + "]");
        }

        program.addUniform("u_materials_length", materialTextures.length);
    }
}
