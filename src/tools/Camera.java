package tools;

import core.math.Matrix;

public class Camera {
    private Matrix camera = new Matrix(4);
    private Matrix model = new Matrix(4);
    private Matrix projection = new Matrix(4);
    private Matrix world  = new Matrix(4);

    public Camera() {
        camera.rotationX((float) 0.0f);
        camera.translation(0.0f, 0.0f, 2.5f);
        projection.perspective((float) Math.PI/3, 1.0f, 0.00001f, 30.0f);
    }

    public void setCamera(Matrix camera) {
        this.camera = camera;
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
        return camera;
    }

    public Matrix getWorld() {
        return world;
    }
}