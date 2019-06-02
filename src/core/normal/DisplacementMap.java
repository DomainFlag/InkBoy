package core.normal;

import core.tools.Log;
import org.lwjgl.opengl.GL11;
import tools.Context;
import tools.Program;
import tools.texture.Texture;

import static org.lwjgl.opengl.GL11.glFinish;
import static org.lwjgl.opengl.GL11C.GL_TEXTURE_2D;
import static org.lwjgl.opengl.GL15.GL_WRITE_ONLY;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL42.glBindImageTexture;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.opengl.GL43.glDispatchCompute;

public class DisplacementMap extends Program {

    private static final String pathProgram = "nmapping";

    private static final float strength = 48.0f;
    private static final int invocations = 32;

    private Texture texture;

    private Texture normalMap;

    private int size;

    public DisplacementMap(Context context, Texture texture) {
        super(context, pathProgram, null, null);

        this.texture = texture;
        this.normalMap = new Texture();
        this.size = texture.getDimension().getWidth();

        getContext().getContextTexture().bindTexture(this.texture, "u_height_map", 0);
        getContext().getContextTexture().bindTexture(this.normalMap, "u_normal_map", 1);

        glTexStorage2D(GL11.GL_TEXTURE_2D, (int) (Math.log(size) / Math.log(2)), GL_RGBA32F, size, size);

        createUniforms();
    }

    public Texture getTexture() {
        return texture;
    }

    public Texture getNormalMap() {
        return normalMap;
    }

    public float getStrength() {
        return strength;
    }

    @Override
    public void createUniforms() {
        addUniform("u_size");
        addUniform("u_normal_strength");
    }

    @Override
    public void updateUniforms() {
        updateUniform("u_size", this.size);
        updateUniform("u_normal_strength", strength);
    }

    @Override
    public void draw() {
        updateUniforms();

        glBindImageTexture(1, normalMap.getTextureName(), 0, false, 0, GL_WRITE_ONLY, GL_RGBA32F);
        glDispatchCompute(this.size / invocations,this.size / invocations,1);
        glFinish();
    }
}
