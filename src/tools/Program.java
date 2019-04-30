package tools;

import core.math.Matrix;
import core.math.Vector;
import core.tools.Log;
import core.view.Camera;
import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.FloatBuffer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class Program {

    private static final Pattern pattern = Pattern.compile("\\w+_(.*)");

    private HashMap<String, Integer> attributes = new HashMap<>();
    private HashMap<String, Integer> uniforms = new HashMap<>();

    private HashMap<String, Integer> buffers = new HashMap<>();

    private Set<Integer> parameters = new HashSet<>();

    private Camera camera;

    private Context context;

    public int program;

    private int drawingType = GL_STATIC_DRAW;
    public int renderingType = GL_TRIANGLES;
    public int nb;

    public Program(Context context, String pathProgram, Integer drawingType, Integer renderingType) {
        this.context = context;
        this.program = Utilities.createProgram(pathProgram);

        if(drawingType != null)
            this.drawingType = drawingType;

        if(renderingType != null)
            this.renderingType = renderingType;

        glUseProgram(this.program);
        getContext().getContextTexture().bindProgram(this.program);
    }

    public Camera getCamera() {
        return camera;
    }

    public Context getContext() {
        return context;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public void setCount(int nbElements, int count) {
        switch(renderingType) {
            case GL_TRIANGLES : {
                nb = nbElements / count;
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

    public void loadData(String attributeName, Vector[] data, int count) {
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(data.length * count);
        for(int it = 0; it < data.length; it++) {
            Vector vector = data[it];

            floatBuffer.put(vector.getData(), 0, vector.size());
        }

        floatBuffer.flip();

        addAttribute(attributeName, floatBuffer, count);
    }

    private void loadData(String attributeName, List<Float> data, int count) {
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(data.size());
        for(int it = 0; it < data.size(); it++)
            floatBuffer.put(data.get(it));

        floatBuffer.flip();

        addAttribute(attributeName, floatBuffer, count);
    }

    public void loadDataV(String attributeName, List<Vector> data, int count) {
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(data.size() * count);
        for(int g = 0; g < data.size(); g++) {
            Vector vector = data.get(g);

            floatBuffer.put(vector.getData(), 0, vector.size());
        }

        floatBuffer.flip();

        addAttribute(attributeName, floatBuffer, count);
    }

    public void updateDataV(String attributeName, List<Vector> data, int count) {
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(data.size() * count);
        for(int g = 0; g < data.size(); g++) {
            Vector vector = data.get(g);

            floatBuffer.put(vector.getData(), 0, vector.size());
        }

        floatBuffer.flip();

        updateAttribute(attributeName, floatBuffer, count);
    }

    private void bindBuffer(String bufferName, FloatBuffer vertices) {
        int buffer = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, buffer);
        glBufferData(GL_ARRAY_BUFFER, vertices, drawingType);

        if(!buffers.containsKey(bufferName)) {
            buffers.put(bufferName, buffer);
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

    public void setTessellationShader(int verticesPerPatch) {
        int[] parameters = new int[1];
        glGetIntegerv(GL_MAX_PATCH_VERTICES, parameters);
        glPatchParameteri(GL_PATCH_VERTICES, verticesPerPatch);

        Log.v("Max supported patch vertices " + parameters[0]);
    }

    public void addAttribute(String name) {
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
        }
    }

    public void addAttribute(String name, FloatBuffer vertices, int count) {
        // add attribute
        this.addAttribute(name);

        // update attribute
        this.updateAttribute(name, vertices, count);
    }

    public void updateAttribute(String name, FloatBuffer vertices, int count) {
        if(!attributes.containsKey(name)) {
            Log.v("Unknown attribute name: " + name);

            return;
        }

        this.setCount(vertices.capacity(), count);

        int attribute = attributes.get(name);

        glEnableVertexAttribArray(attribute);

        bindBuffer(attribute + "Buffer", vertices);

        glVertexAttribPointer(attribute, count, GL_FLOAT, false, 0, NULL);
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

    public void addUniform(String name, Vector vector) {
        int uniform = checkUniform(name);
        if(uniform != -1) {
            assignUniformValues(uniform, vector.getData());
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

    public void free() {
        removeSettings();

        for(Map.Entry<String, Integer> attribute : attributes.entrySet()) {
            glDisableVertexAttribArray(attribute.getValue());
        }
    }

    public void useProgram() {
        glUseProgram(program);
    }

    public void render() {
        useProgram();
        applySettings();

        draw();

        updateUniforms();
        removeSettings();
    }

    public void clear() {}

    public abstract void createUniforms();

    public abstract void updateUniforms();

    public abstract void draw();
}