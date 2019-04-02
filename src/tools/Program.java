package tools;

import core.math.Matrix;
import core.math.Vector;
import org.lwjgl.BufferUtils;
import org.lwjgl.system.MemoryStack;

import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.stb.STBImage.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.ByteBuffer;
import java.nio.FloatBuffer;
import java.nio.IntBuffer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Program {

    private static final Pattern pattern = Pattern.compile("\\w+_(.*)");

    private HashMap<String, Integer> attributes = new HashMap<>();
    private HashMap<String, Integer> uniforms = new HashMap<>();
    private HashMap<String, Integer> textures = new HashMap<>();

    private HashMap<String, Integer> buffers = new HashMap<>();

    private Set<Integer> parameters = new HashSet<>();

    private Camera camera;

    public int program;

    private int drawingType = GL_STATIC_DRAW;
    public int renderingType = GL_TRIANGLES;
    public int nb;

    public Program(String pathProgram, Integer drawingType, Integer renderingType) {
        this.program = Utilities.createProgram(pathProgram);

        if(drawingType != null)
            this.drawingType = drawingType;

        if(renderingType != null)
            this.renderingType = renderingType;

        glUseProgram(this.program);
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setCount(int nbElements) {
        switch(renderingType) {
            case GL_TRIANGLES : {
                nb = nbElements / 3;
                break;
            }
            case GL_LINES : {
                nb = nbElements / 2;
                break;
            }
            default: {
                throw new Error("Unknown drawing type");
            }
        }
    }

    public void addSetting(int parameter) {
        parameters.add(parameter);
    }

    public void applySettings() {
        for(int parameter : parameters) {
            glEnable(parameter);
        }
    }

    public void removeSettings() {
        for(int parameter : parameters) {
            glDisable(parameter);
        }
    }

    public void loadData(String attributeName, Vector[] data) {
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(data.length * 2);
        for(int it = 0; it < data.length; it++) {
            Vector vector = data[it];

            floatBuffer.put(vector.getData(), 0, vector.size());
        }

        floatBuffer.flip();

        addAttribute(attributeName, floatBuffer);
    }

    private void loadData(String attributeName, List<Float> data) {
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(data.size());
        for(int it = 0; it < data.size(); it++)
            floatBuffer.put(data.get(it));

        addAttribute(attributeName, floatBuffer);
    }

    public void loadDataV(String attributeName, List<Vector> data, int count) {
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(data.size() * count);
        for(int g = 0; g < data.size(); g++) {
            Vector vector = data.get(g);

            floatBuffer.put(vector.getData(), 0, vector.size());
        }

        floatBuffer.flip();

        addAttribute(attributeName, floatBuffer);
    }

    private void bindBuffer(String bufferName, FloatBuffer vertices) {
        int buffer = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, buffer);
        glBufferData(GL_ARRAY_BUFFER, vertices, drawingType);

        if(!buffers.containsKey(bufferName)) {
            buffers.put(bufferName, buffer);
        } else {
            Log.v(buffer);
        }
    }

    public static int createBoundBuffer(FloatBuffer vertices) {
        int buffer = glGenBuffers();

        vertices.flip();

        glBindBuffer(GL_ARRAY_BUFFER, buffer);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);

        return buffer;
    }

    public static int createBoundBuffer(float[] vertices) {
        int buffer = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, buffer);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_DYNAMIC_DRAW);

        return buffer;
    }

    public void setTessellationShaders(int verticesPerPatch) {
        int[] parameters = new int[1];
        glGetIntegerv(GL_MAX_PATCH_VERTICES, parameters);
        glPatchParameteri(GL_PATCH_VERTICES, verticesPerPatch);

        Log.v("Max supported patch vertices " + parameters[0]);
    }

    public void addAttribute(String name, FloatBuffer vertices) {
        if(name.equals("a_position")) {
            setCount(vertices.capacity());
        }

        if(attributes.containsKey(name)) {
            Log.v("Unfortunately you used this attribute before: " + name);
        }

        Matcher matcher = pattern.matcher(name);
        if(!matcher.find()) {
            Log.v("Attribute location is inappropriate, it should be of type a_, found: " + name);

            return;
        }

        int attribute = glGetAttribLocation(program, name);

        if(attribute != -1) {
            attributes.put(name, attribute);
        } else {
            Log.v("Check the shader, the location is inappropriate: " + name);

            return;
        }

        glEnableVertexAttribArray(attribute);
        bindBuffer(matcher.group(1) + "Buffer", vertices);

        glVertexAttribPointer(attribute, 3, GL_FLOAT, false, 0, NULL);
    }

    public int checkUniform(String name) {
        if(uniforms.containsKey(name)) {
            Log.v("Unfortunately you used this uniform before: " + name);
        }

        Matcher matcher = pattern.matcher(name);
        if(!matcher.find()) {
            Log.v("Uniform location is inappropriate, it should be of type u_, found: " + name);

            return -1;
        }

        int uniform = glGetUniformLocation(program, name);

        if(uniform != -1) {
            uniforms.put(name, uniform);

            return uniform;
        } else {
            Log.v("Check the shader, the location is inappropriate: " + name);
        }

        return -1;
    }

    public void assignUniformValues(int uniform, float[] values) {
        switch(values.length) {
            case 4 : {
                glUniform4fv(uniform, values);
                break;
            }
            case 3 : {
                glUniform3fv(uniform, values);
                break;
            }
            case 2 : {
                glUniform2fv(uniform, values);
                break;
            }
            case 1 : {
                glUniform1fv(uniform, values);
                break;
            }
        }
    }

    public void addUniform(String name) {
        int uniform = checkUniform(name);
    }

    public void addUniform(String name, float[] values) {
        int uniform = checkUniform(name);
        if(uniform != -1) {
            assignUniformValues(uniform, values);
        }
    }

    public void addUniform(String name, float value) {
        int uniform = checkUniform(name);
        if(uniform != -1) {
            glUniform1f(uniform, value);
        }
    }

    public void addUniform(String name, float[][] values) {
        for(int it = 0; it < values.length; it++) {
            int uniform = checkUniform(name + "[" + it + "]");
            if(uniform != -1) {
                assignUniformValues(uniform, values[it]);
            }
        }
    }

    public void addUniform(String name, Matrix matrix) {
        int uniform = checkUniform(name);
        if(uniform != -1) {
            switch(matrix.getSize()) {
                case 2 : {
                    glUniformMatrix2fv(uniform, false, matrix.getData());
                    break;
                }
                case 4 : {
                    glUniformMatrix4fv(uniform, false, matrix.getData());
                }
            }
        }
    }

    public void updateUniform(String name, Matrix matrix) {
        if(uniforms.containsKey(name)) {
            int uniform = uniforms.get(name);

            glUniformMatrix4fv(uniform, false, matrix.getData());
        } else addUniform(name, matrix);
    }

    public int getUniform(String name) {
        if(uniforms.containsKey(name))
            return uniforms.get(name);
        else {
            throw new Error("Null pointer exception while getting uniform for " + name);
        }
    }

    public void updateUniform(String name, float value) {
        int uniform = getUniform(name);

        glUniform1f(uniform, value);
    }

    public void updateUniform(String name, double value) {
        int uniform = getUniform(name);

        glUniform1f(uniform, (float) value);
    }

    public void updateUniform(String name, Vector vector) {
        int uniform = getUniform(name);
        switch(vector.size()) {
            case 2 : {
                glUniform2fv(uniform, vector.getData());
                break;
            }
            case 3 : {
                glUniform3fv(uniform, vector.getData());
                break;
            }
            case 4 : {
                glUniform4fv(uniform, vector.getData());
                break;
            }
            default : {
                throw new Error("Undefined");
            }
        }
    }

    public void updateUniform(String name, int value) {
        int uniform = uniforms.get(name);

        glUniform1i(uniform, value);
    }

    public void addTexture(String pathSourceName, String name, int textureUnitIndex, int mode) {
        int imageLoc = glGetUniformLocation(program, name);
        glUniform1i(imageLoc, textureUnitIndex);
        glActiveTexture(GL_TEXTURE0 + textureUnitIndex);

        int tex = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, tex);

        MemoryStack stack = MemoryStack.stackGet();
        IntBuffer w = stack.mallocInt(1);
        IntBuffer h = stack.mallocInt(1);
        IntBuffer comp = stack.mallocInt(1);


        stbi_set_flip_vertically_on_load(true);
        ByteBuffer image = stbi_load("./res/" + pathSourceName, w, h, comp, 4);
        if(image == null) {
            throw new RuntimeException("Failed to load a texture file!"
                    + System.lineSeparator() + stbi_failure_reason() + " " + pathSourceName);
        }

        int width = w.get();
        int height = h.get();

        if(width == 0 || height == 0) {
            throw new RuntimeException("Failed to load a texture file!"
                    + System.lineSeparator() + stbi_failure_reason() + " " + pathSourceName);
        }

        glPixelStorei(GL_UNPACK_ALIGNMENT, 1);

        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA, width, height, 0, GL_RGBA, GL_UNSIGNED_BYTE, image);

        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, mode);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, mode);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);

        glGenerateMipmap(GL_TEXTURE_2D);

        stbi_image_free(image);
    }

    public void generateTexture(int index) {
        int uniformTextureLoc = glGetUniformLocation(program, "u_texture[" + index + "]");
        glUniform1i(uniformTextureLoc, index);
        glActiveTexture(GL_TEXTURE0 + index);
    }

//    public void spawnTextures(String[] textures) {
//        for(int it = 0; it < textures.length; it++) {
//            generateTexture(it);
//            addTexture(textures[it]);
//        }
//    }

    public void free() {
        removeSettings();

        for(Map.Entry<String, Integer> attribute : attributes.entrySet()) {
            glDisableVertexAttribArray(attribute.getValue());
        }
    }

    public void render() {
        glUseProgram(program);
        applySettings();

        draw();

        updateUniforms();
        removeSettings();
    }

    public abstract void updateUniforms();
    public abstract void draw();
}
