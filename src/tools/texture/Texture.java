package tools.texture;

import core.tools.Log;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;

public class Texture {

    private static final String RESOURCE_LOCATION = "./res/";

    private Dimension dimension = new Dimension();

    private int channels;

    private int textureName;

    public Texture() {
        this.textureName = glGenTextures();
    }

    public Texture(int width, int height) {
        this();

        dimension.setWidth(width);
        dimension.setHeight(height);
    }

    public int getTextureName() {
        return textureName;
    }

    public int getChannels() {
        return channels;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public ByteBuffer fetchTexture(String pathSourceName) {
        try(MemoryStack stack = MemoryStack.stackGet()) {
            IntBuffer bufferWidth = stack.mallocInt(1);
            IntBuffer bufferHeight = stack.mallocInt(1);
            IntBuffer bufferChannels = stack.mallocInt(1);

            stbi_set_flip_vertically_on_load(true);
            ByteBuffer image = stbi_load(RESOURCE_LOCATION + pathSourceName, bufferWidth, bufferHeight, bufferChannels, 4);
            if(image == null) {
                handleError(pathSourceName);
            }

            this.dimension.setWidth(bufferWidth.get());
            this.dimension.setHeight(bufferHeight.get());
            this.channels = bufferChannels.get();

            if(getDimension().width == 0 || getDimension().height == 0) {
                handleError(pathSourceName);
            }

            return image;
        }
    }

    private void handleError(String pathSourceName) {
        throw new RuntimeException("Failed to load a texture file!"
                + System.lineSeparator() + stbi_failure_reason() + " " + pathSourceName);
    }

    public static class Dimension {

        private int width;

        private int height;

        private void setWidth(int width) {
            this.width = width;
        }

        private void setHeight(int height) {
            this.height = height;
        }

        public int getWidth() {
            return width;
        }

        public int getHeight() {
            return height;
        }
    }
}
