package tools.texture;

import core.tools.Log;
import org.lwjgl.system.MemoryStack;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import static org.lwjgl.opengl.GL11.GL_RGB;
import static org.lwjgl.opengl.GL11.GL_RGBA;
import static org.lwjgl.opengl.GL11.glGenTextures;
import static org.lwjgl.stb.STBImage.stbi_failure_reason;
import static org.lwjgl.stb.STBImage.stbi_load;
import static org.lwjgl.stb.STBImage.stbi_set_flip_vertically_on_load;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Texture {

    private static final int MAX_COLOUR = 255 * 255 * 255;

    private static final String RESOURCE_LOCATION = "./res/";

    private Dimension dimension = new Dimension();
    private ByteBuffer byteBuffer = null;

    private int channels;
    private int type = GL_RGB;
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

    public int getType() {
        return type;
    }

    public ByteBuffer getByteBuffer() {
        return byteBuffer;
    }

    public Dimension getDimension() {
        return dimension;
    }

    public ByteBuffer fetchTexture(String pathSourceName) {
        try(MemoryStack stack = stackPush()) {
            IntBuffer bufferWidth = stack.mallocInt(1);
            IntBuffer bufferHeight = stack.mallocInt(1);
            IntBuffer bufferChannels = stack.mallocInt(1);

            stbi_set_flip_vertically_on_load(true);
            ByteBuffer image = stbi_load(RESOURCE_LOCATION + pathSourceName, bufferWidth, bufferHeight, bufferChannels, 0);
            if(image == null) {
                handleError(pathSourceName);
            }

            this.dimension.setWidth(bufferWidth.get(0));
            this.dimension.setHeight(bufferHeight.get(0));
            this.channels = bufferChannels.get(0);
            this.type = computeType(this.channels);
            this.byteBuffer = image;

            if(getDimension().width == 0 || getDimension().height == 0) {
                handleError(pathSourceName);
            }

            return image;
        }
    }

    public float getHeight(int x, int z, int minHeight, int maxHeight) {
        ByteBuffer byteBuffer = getByteBuffer();
        int width = getDimension().getWidth();
        int channels = getChannels();

        byte r = byteBuffer.get(x * channels + z * channels * width);
        byte g = byteBuffer.get(x * channels + z * channels * width + 1);
        byte b = byteBuffer.get(x * channels + z * channels * width + 2);

        int argb = ((0xFF & r) << 16) | ((0xFF & g) << 8) | (0xFF & b);

        if(channels == 4) {
            byte  a = byteBuffer.get(x * channels + z * channels * width + 3);

            argb = ((0xFF & a) << 24) | argb;
        }

        return minHeight + Math.abs(maxHeight - minHeight) * ((float) argb / (float) MAX_COLOUR);
    }

    public float getHeight(float x, float z, int minHeight, int maxHeight) {
        ByteBuffer byteBuffer = getByteBuffer();
        int size = getDimension().getWidth();

        // texel offset
        float offset = 1.0f / size;

        int x0 = (int) Math.floor(x * size) - 1;
        int x1 = x0 + 1;
        int z0 = (int) Math.floor(z * size) - 1;
        int z1 = z0 + 1;

        int height00 = (0xFF & byteBuffer.get(x0 * channels + z0 * channels * size));
        int height01 = (0xFF & byteBuffer.get(x0 * channels + z1 * channels * size));
        int height10 = (0xFF & byteBuffer.get(x1 * channels + z0 * channels * size));
        int height11 = (0xFF & byteBuffer.get(x1 * channels + z1 * channels * size));

        float weightX = (x - (x0 * offset)) / offset;
        float weightZ = (z - (z0 * offset)) / offset;

        float heightX0 = height00 + (height10 - height00) * weightX;
        float heightX1 = height01 + (height11 - height01) * weightX;

        float height = (heightX0 + (heightX1 - heightX0) * weightZ) / 255.0f;

        return minHeight + Math.abs(maxHeight - minHeight) * height;
    }

    private int computeType(int channels) {
        if(channels == 3) {
            return GL_RGB;
        } else {
            return GL_RGBA;
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
