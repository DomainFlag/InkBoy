package tools.texture;

import core.tools.Log;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.util.HashMap;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL11.GL_LINEAR;
import static org.lwjgl.opengl.GL13.GL_TEXTURE0;
import static org.lwjgl.opengl.GL13.glActiveTexture;
import static org.lwjgl.opengl.GL20.glGetUniformLocation;
import static org.lwjgl.opengl.GL20.glUniform1i;
import static org.lwjgl.opengl.GL30.GL_RGBA32F;
import static org.lwjgl.opengl.GL30.glGenerateMipmap;
import static org.lwjgl.opengl.GL42.glTexStorage2D;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.stb.STBTruetype.stbtt_BakeFontBitmap;

public class ContextTexture {

    private HashMap<String, Texture> textures = new HashMap<>();

    private Integer program = null;

    public ContextTexture() {}

    public HashMap<String, Texture> getTextures() {
        return textures;
    }

    public void bindProgram(int program) {
        this.program = program;
    }

    public Texture addTexture(String pathSourceName, String uniformName, int textureUnitIndex, int mode) {
        Texture texture = new Texture();
        ByteBuffer image = texture.fetchTexture(pathSourceName);

        addTexture(texture, image, uniformName, textureUnitIndex, GL_RGBA, mode);

        stbi_image_free(image);

        textures.put(pathSourceName, texture);

        return texture;
    }

    public Texture addTexture(ByteBuffer image, String pathSourceName, String uniformName, int textureUnitIndex, int width, int height, int type, int mode) {
        Texture texture = new Texture(width, height);
        addTexture(texture, image, uniformName, textureUnitIndex, type, mode);

        textures.put(pathSourceName, texture);

        return texture;
    }

    private void addTexture(Texture texture, ByteBuffer image, String uniformName, int textureUnitIndex, int type, int mode) {
        Texture.Dimension dimension = texture.getDimension();

        int imageLoc = glGetUniformLocation(program, uniformName);

        glUniform1i(imageLoc, textureUnitIndex);
        glActiveTexture(GL_TEXTURE0 + textureUnitIndex);

        glBindTexture(GL_TEXTURE_2D, texture.getTextureName());
        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);
        glTexImage2D(GL_TEXTURE_2D, 0, type, dimension.getWidth(), dimension.getHeight(), 0, type, GL_UNSIGNED_BYTE, image);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, mode);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, mode);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glGenerateMipmap(GL_TEXTURE_2D);
    }

    public void bindTexture(Texture texture, String uniformName, int textureUnitIndex) {
        int imageLoc = glGetUniformLocation(program, uniformName);

        glUniform1i(imageLoc, textureUnitIndex);
        glActiveTexture(GL_TEXTURE0 + textureUnitIndex);

        glBindTexture(GL_TEXTURE_2D, texture.getTextureName());
    }

    public void bindTexture(Texture texture, String uniformName, int textureUnitIndex, int size) {
        int imageLoc = glGetUniformLocation(program, uniformName);

        glUniform1i(imageLoc, textureUnitIndex);
        glActiveTexture(GL_TEXTURE0 + textureUnitIndex);

        glBindTexture(GL_TEXTURE_2D, texture.getTextureName());

        glTexStorage2D(GL_TEXTURE_2D, (int) (Math.log(size) / Math.log(2)), GL_RGBA32F, size, size);
    }

    public void generateTexture(int index) {
        int uniformTextureLoc = glGetUniformLocation(program, "u_texture[" + index + "]");

        glUniform1i(uniformTextureLoc, index);
        glActiveTexture(GL_TEXTURE0 + index);
    }
}
