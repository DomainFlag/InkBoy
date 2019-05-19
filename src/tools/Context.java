package tools;

import core.view.Camera;
import org.lwjgl.system.MemoryStack;
import tools.texture.ContextTexture;

import java.nio.FloatBuffer;

import static java.lang.Math.round;
import static org.lwjgl.glfw.GLFW.glfwGetMonitorContentScale;
import static org.lwjgl.system.MemoryStack.stackPush;

public class Context {

    private ContextTexture contextTexture = new ContextTexture();
    private Camera camera;

    private long monitor;

    private float scaleX, scaleY;

    public Context(long monitor) {
        this.monitor = monitor;

        setScale();
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

    private void setScale() {
        MemoryStack memoryStack = stackPush();

        FloatBuffer scaleFloatX = memoryStack.mallocFloat(1);
        FloatBuffer scaleFloatY = memoryStack.mallocFloat(1);

        glfwGetMonitorContentScale(this.monitor, scaleFloatX, scaleFloatY);

        this.scaleX = scaleFloatY.get(0);
        this.scaleY = scaleFloatY.get(0);
    }
}
