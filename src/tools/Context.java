package tools;

import core.Settings;
import core.tools.Log;
import core.view.Camera;
import org.lwjgl.Version;
import org.lwjgl.opengl.GL11;
import org.lwjgl.system.MemoryStack;
import tools.texture.ContextTexture;

import java.nio.FloatBuffer;

import static java.lang.Math.round;
import static org.lwjgl.glfw.GLFW.glfwGetMonitorContentScale;
import static org.lwjgl.opengl.GL11.glGetString;
import static org.lwjgl.opengl.GL20.GL_MAX_TEXTURE_IMAGE_UNITS;
import static org.lwjgl.opengl.GL20.GL_SHADING_LANGUAGE_VERSION;
import static org.lwjgl.opengl.GL43.GL_MAX_COMPUTE_WORK_GROUP_INVOCATIONS;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Context {

    private ContextTexture contextTexture;
    private Camera camera;

    private long monitor;
    private long max_texture_image_units;
    private long max_compute_work_group_invocations;

    private float scaleX, scaleY;

    public Context(long monitor) {
        this.monitor = monitor;
        this.camera = new Camera(Settings.WIDTH, Settings.HEIGHT);

        setScale();
    }

    public void initialize() {
        this.max_texture_image_units = GL11.glGetInteger(GL_MAX_TEXTURE_IMAGE_UNITS);
        this.max_compute_work_group_invocations = GL11.glGetInteger(GL_MAX_COMPUTE_WORK_GROUP_INVOCATIONS);

        this.verbose();

        this.contextTexture = new ContextTexture(this.max_texture_image_units);
    }

    private void verbose() {
        Log.v("OpenGL version: " + GL11.glGetString(GL11.GL_VERSION));
        Log.v("InkBoy " + Version.getVersion() + "! on shading language: " + glGetString(GL_SHADING_LANGUAGE_VERSION));
        Log.v("Max compute work group invocations: " + this.max_compute_work_group_invocations);
        Log.v("Max texture image units: " + this.max_texture_image_units);
    }

    public ContextTexture getContextTexture() {
        return contextTexture;
    }

    public Camera getCamera() {
        return camera;
    }

    public void setCamera(Camera camera) {
        this.camera = camera;
    }

    public float getScaleX() {
        return scaleX;
    }

    public float getScaleY() {
        return scaleY;
    }

    public void clear() {
        this.contextTexture.clear();
    }

    private void setScale() {
        MemoryStack memoryStack = stackPush();

        FloatBuffer scaleFloatX = memoryStack.mallocFloat(1);
        FloatBuffer scaleFloatY = memoryStack.mallocFloat(1);

        glfwGetMonitorContentScale(this.monitor, scaleFloatX, scaleFloatY);

        this.scaleX = scaleFloatY.get(0);
        this.scaleY = scaleFloatY.get(0);
    }
}
