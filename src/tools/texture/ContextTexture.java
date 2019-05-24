package tools.texture;

import core.tools.Log;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import tools.Context;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL12.GL_CLAMP_TO_EDGE;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.GL_MAX_TEXTURE_IMAGE_UNITS;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.stb.STBTruetype.stbtt_BakeFontBitmap;

public class ContextTexture {

    private HashMap<String, Texture> textures = new HashMap<>();
    private Set<Integer> textureIndices;

    private Integer program = null;

    public ContextTexture(long max_texture_image_units) {
        this.textureIndices = new HashSet<>();

        for(int g = 0; g < max_texture_image_units; g++) {
            this.textureIndices.add(g);
        }
    }

    public HashMap<String, Texture> getTextures() {
        return textures;
    }

    public void bindProgram(int program) {
        this.program = program;
    }

    public Texture getTexture(String pathSourceName) {
        return textures.get(pathSourceName);
    }

    public Texture addTexture(String pathSourceName, String uniformName, int textureUnitIndex, int mode) {
        Texture texture = new Texture();
        ByteBuffer image = texture.fetchTexture(pathSourceName);

        this.addTexture(texture, image, uniformName, textureUnitIndex, mode);
        this.textures.put(pathSourceName, texture);

        return texture;
    }

    public Texture addTexture(ByteBuffer image, String pathSourceName, String uniformName, int textureUnitIndex, int width, int height, int mode) {
        Texture texture = new Texture(width, height);

        this.addTexture(texture, image, uniformName, textureUnitIndex, mode);
        this.textures.put(pathSourceName, texture);

        return texture;
    }

    private void addTexture(Texture texture, ByteBuffer image, String uniformName, int textureUnitIndex, int mode) {
        Texture.Dimension dimension = texture.getDimension();

        int imageLoc = glGetUniformLocation(program, uniformName);
        if(imageLoc == -1) {
            throw new Error("Null pointer exception while getting uniform for " + uniformName);
        }

        glUniform1i(imageLoc, textureUnitIndex);
        glActiveTexture(GL_TEXTURE0 + textureUnitIndex);

        reserveTextureIndex(textureUnitIndex);

        glBindTexture(GL_TEXTURE_2D, texture.getTextureName());
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexImage2D(GL_TEXTURE_2D, 0, texture.getType(), dimension.getWidth(), dimension.getHeight(),
                0, texture.getType(), GL_UNSIGNED_BYTE, image);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, mode);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, mode);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR_MIPMAP_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glGenerateMipmap(GL_TEXTURE_2D);
    }

    public void bindTexture(Texture texture, String uniformName, int textureUnitIndex) {
        int imageLoc = glGetUniformLocation(program, uniformName);

        glUniform1i(imageLoc, textureUnitIndex);
        glActiveTexture(GL_TEXTURE0 + textureUnitIndex);

        this.reserveTextureIndex(textureUnitIndex);

        glBindTexture(GL_TEXTURE_2D, texture.getTextureName());
    }

    public Integer generateTextureIndex() {
        if(!this.textureIndices.isEmpty()) {
            return this.textureIndices.iterator().next();
        }

        return null;
    }

    private boolean reserveTextureIndex(Integer textureUnitIndex) {
        if(textureUnitIndex != null) {
            return this.textureIndices.remove(textureUnitIndex);
        }

        return false;
    }

    public void generateTexture(int index) {
        int uniformTextureLoc = glGetUniformLocation(program, "u_texture[" + index + "]");

        glUniform1i(uniformTextureLoc, index);
        glActiveTexture(GL_TEXTURE0 + index);
    }

    public void clear() {
        for(Texture texture : textures.values()) {
            texture.removeTexture();
        }
    }
}
