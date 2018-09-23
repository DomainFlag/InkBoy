package tools;

import org.lwjgl.BufferUtils;

import static org.lwjgl.opengl.GL46.*;
import static org.lwjgl.system.MemoryUtil.NULL;

import java.nio.FloatBuffer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Program {

    private static final Pattern pattern = Pattern.compile("\\w+_(.*)");

    private HashMap<String, Integer> attributes = new HashMap<>();
    private HashMap<String, Integer> uniforms = new HashMap<>();
    private HashMap<String, Integer> textures = new HashMap<>();

    private HashMap<String, Integer> buffers = new HashMap<>();

    private Set<Integer> parameters = new HashSet<>();

    private FloatBuffer mModelMatrix;
    private FloatBuffer mCameraMatrix;

    private String mode;

    private int program;

    private int drawingType;
    private int renderingType;

    private int nb;

    public Program(String mode, int drawingType, int renderingType) {
        this.program = Utilities.createProgram("triangle");
        glUseProgram(program);

        this.mode = mode;

        this.drawingType = drawingType | GL_STATIC_DRAW;
        this.renderingType = renderingType | GL_TRIANGLES;
    }

    public Program(String mode, int drawingType, int renderingType, String model) {
        this(mode, drawingType, renderingType);

        loadModel(model);
    }

    private int getCount(int nbElements) {
        switch(renderingType) {
            case GL_TRIANGLES : return nbElements / 3;
            default: return 0;
        }
    }

    public void addSetting(int parameter) {
        parameters.add(parameter);
    }

    private void applySettings() {
        for(int parameter : parameters) {
            glEnable(parameter);
        }
    }

    private void removeSettings() {
        for(int parameter : parameters) {
            glDisable(parameter);
        }
    }

    private void loadModel(String model) {
        ObjReader objReader = new ObjReader();
        objReader.readDir(model);

        addAttribute("a_position", objReader.getGeometricVertices());
    }

    private void createCameraMatrix() {
//        Matrix4f matrix4f = new Matrix4f(
//                0.0f, 0.0f, 0.0f, 0.0f,
//                0.0f, 0.0f, 0.0f, 0.0f,
//                0.0f, 0.0f, 0.0f, 0.0f,
//                0.0f, 0.0f, -1.0f, 0.0f
//        );
//
//        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(16);
//        matrix4f.get(floatBuffer);
//
//        mCameraMatrix = floatBuffer;
    }

    private void createBuffer(String bufferName, ArrayList<Float> vertices) {
        nb = getCount(vertices.size());

        FloatBuffer bufferData = BufferUtils.createFloatBuffer(vertices.size());
        for(Float vertex : vertices)
            bufferData.put(vertex);
        bufferData.flip();

        int buffer = glGenBuffers();
        glBindBuffer(GL_ARRAY_BUFFER, buffer);
        glBufferData(GL_ARRAY_BUFFER, bufferData, drawingType);

        if(!buffers.containsKey(bufferName)) {
            buffers.put(bufferName, buffer);
        } else {
            Log.v(buffer);
        }
    }

    public void addAttribute(String name, ArrayList<Float> vertices) {
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
            Log.v("Check the shader, the location is inappropriate");

            return;
        }

        createBuffer(matcher.group(1) + "Buffer", vertices);

        glEnableVertexAttribArray(attribute);
        glVertexAttribPointer(attribute, 3, GL_FLOAT, false, 0, NULL);
    }

    public void addUniform(String name) {
        if(uniforms.containsKey(name)) {
            Log.v("Unfortunately you used this uniform before: " + name);
        }

        int uniform = glGetUniformLocation(program, name);

        if(uniform != -1) {
            uniforms.put(name, uniform);
        }
    }

    // TODO() later
    public void addTexture(String name) {
    }

    public void free() {
        removeSettings();

        for(Map.Entry<String, Integer> attribute : attributes.entrySet()) {
            glDisableVertexAttribArray(attribute.getValue());
        }
    }

    public void render() {
        applySettings();

        glDrawArrays(renderingType, 0, nb);
    }
}
