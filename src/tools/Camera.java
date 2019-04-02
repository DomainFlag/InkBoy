package tools;

import core.Settings;
import core.math.*;
import org.lwjgl.glfw.GLFWVidMode;

import static org.lwjgl.glfw.GLFW.*;

public class Camera {

    private static final float ROTATION = 0.4f;
    private static final float SPEED_RATIO = 0.02f;
    private boolean freeze = false;
    private float speed = 0;

    private Vector3f rotation = new Vector3f(0, 0, 0);
    private Vector2f currentRotation = new Vector2f();
    private Vector3f translation = new Vector3f(0, 0, 0);

    private Matrix model = new Matrix(4);
    private Matrix projection = new Matrix(4);
    private Matrix world  = new Matrix(4);

    private float width;
    private float height;

    public Camera(float width, float height) {
        this.width = width;
        this.height = height;

        projection.perspective((float) Math.PI/3, (float) width / height, Settings.Z_NEAR, Settings.Z_FAR);
    }

    public void setModel(Matrix model) {
        this.model = model;
    }

    public void setProjection(Matrix projection) {
        this.projection = projection;
    }

    public void setWorld(Matrix world) {
        this.world = world;
    }

    public Matrix getProjection() {
        return projection;
    }

    public Matrix getModel() {
        return model;
    }

    public Matrix getCamera() {
        Matrix transl = new Matrix(4);
        transl.translate(0, 0, 1.5f);
        transl.translate(translation);

        Vector4f quaternion = Vector.fromEuler(rotation);
        Matrix camera = Matrix.fromQuat(quaternion);

        return camera.multiply(transl).inverseMatrix();
    }

    public Matrix getWorld() {
        return world;
    }

    public Vector getRealWorldTranslation() {
        Vector4f quaternion = Vector.fromEuler(rotation);

        return Matrix.transformQuat(new Vector4f(0, 0, -speed, 1.0f), quaternion);
    }

    public Matrix getViewMatrix() {
        Matrix transl = new Matrix(4);
        transl.translate(0, 1.0f, 2.5f);
        transl.translate(translation);

        Vector4f quaternion = Vector.fromEuler(rotation);
        Matrix camera = Matrix.fromQuat(quaternion);

        return camera.multiply(transl);
    }

    public void keyCallback(int key, int action) {
        switch(key) {
            case GLFW_KEY_W : {
                speed += SPEED_RATIO;
                break;
            }
            case GLFW_KEY_A : {
                break;
            }
            case GLFW_KEY_S : {
                speed -= SPEED_RATIO;
                break;
            }
            case GLFW_KEY_D : {
                break;
            }
            case GLFW_KEY_P : {
                Log.v(freeze);
                freeze = !freeze;
                break;
            }
        }
    }

    public void change() {
        if(!freeze) {
            rotation.add(currentRotation.get(0), currentRotation.get(1), 0.0f);
            translation.add(getRealWorldTranslation());
        }
    }

    public void scrollCallback(double xoffset, double yoffset) {
        if(yoffset > 0.0f) {
            // Do something
        } else {
            // Do something
        }
    }

    public void cursorPosCallback(double xpos, double ypos) {
        float width = this.width / 2.0f;
        float height = this.height / 2.0f;

        float x = (float) (xpos - width) / width;
        float y = (float) (ypos - height) / height;

        currentRotation.set(- y * ROTATION, - x * ROTATION);
    }
}