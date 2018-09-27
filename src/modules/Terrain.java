package modules;

import core.Settings;
import core.math.Matrix;
import core.math.MatrixCore;
import core.tools.SimplexNoise;
import org.lwjgl.BufferUtils;
import tools.Program;

import java.nio.FloatBuffer;
import java.util.LinkedList;
import core.math.Vector;

import static org.lwjgl.glfw.GLFW.*;
import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL15.GL_DYNAMIC_DRAW;

public class Terrain extends Program {

    private static final int TILE_VERTICES = 6;
    private static final int TILE_DIMENSIONS = 3;

    private static final float PLAIN_SPEED = 0.2f;

    private static final int GRID_COMPLEXITY = 300;
    private static final float GRID_MIN = -1.0f;
    private static final float GRID_MAX = 1.0f;

    private static final float PARTITION = (GRID_MAX - GRID_MIN) / (GRID_COMPLEXITY - 1) * 5;

    private int gridLookingAtX = GRID_COMPLEXITY / 2;
    private int gridLookingAtY = GRID_COMPLEXITY / 2;

    private int gridLookingStep = 3;

    private FloatBuffer floatBuffer;

    private Matrix camera = new Matrix(4);
    private Matrix model = new Matrix(4);
    private Matrix projection = new Matrix(4);

    public Terrain() {
        super("Terrain", GL_DYNAMIC_DRAW, GL_TRIANGLES, null);

        addSetting(GL_CULL_FACE);
        addSetting(GL_DEPTH_TEST);

        init();
    }

    private void init() {
        floatBuffer = createTerrain();

        if(floatBuffer != null)
            addAttribute("a_position", floatBuffer);

        camera.rotationX(45);
        camera.translation(0.0f, 0.0f, 0.0f);
        camera.inverseMatrix();
        addUniform("u_camera", camera);

        addUniform("u_model", model);

        projection.perspective((float) Math.PI/3, 1.0f, 0.001f, 30.0f);
        addUniform("u_projection", projection);

        // HSV colors that are normalized to [0, 1]
        float[][] gradients = {
                new float[] {120.0f/360.0f, 76.0f/100.0f, 55.0f/100.0f},
                new float[] {26.0f/360.0f, 36.0f/100.0f, 65.0f/100.0f},
                new float[] {0.0f/360.0f, 2.0f/100.0f, 100.0f/100.0f}
        };

        addUniform("u_gradients", gradients);
        addUniform("u_threshold", 0.7f);
    }

    private FloatBuffer createTerrain() {
        if(GRID_COMPLEXITY < 2)
            return null;

        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(
                (GRID_COMPLEXITY - 1) * (GRID_COMPLEXITY - 1)* TILE_DIMENSIONS * TILE_VERTICES);

        LinkedList<Vector> linkedList = new LinkedList<>();
        for(int g = 0; g < GRID_COMPLEXITY; g++) {
            Vector vector = getVertex(0, g, PARTITION);

            linkedList.add(vector);
        }

        for(int g = 1; g < GRID_COMPLEXITY; g++) {
            Vector lowerLeftVertex = getVertex(g, 0, PARTITION);
            linkedList.add(lowerLeftVertex);

            for(int h = 1; h < GRID_COMPLEXITY; h++) {
                Vector upperLeftVertex = linkedList.getFirst();
                linkedList.removeFirst();

                Vector upperRightVertex = linkedList.getFirst();

                Vector lowerRightVertex = getVertex(g, h, PARTITION);

                fillBuffer(
                        floatBuffer,
                        upperLeftVertex,
                        upperRightVertex,
                        lowerLeftVertex,
                        lowerRightVertex);

                linkedList.add(lowerRightVertex);

                lowerLeftVertex = lowerRightVertex;
            }

            linkedList.removeFirst();
        }

        floatBuffer.flip();

        return floatBuffer;
    };

    /**
     * Instead of (v0, v1, v3) & (v3, v2, v0)
     * We use (v0, v3, v1) & (v3, v0, v2) to prevent culling of front facing faces
     * @param floatBuffer
     * @param upperLeftVertex v0
     * @param upperRightVertex v1
     * @param lowerLeftVertex v2
     * @param lowerRightVertex v3
     */
    private void fillBuffer(
            FloatBuffer floatBuffer,
            Vector upperLeftVertex,
            Vector upperRightVertex,
            Vector lowerLeftVertex,
            Vector lowerRightVertex) {
        fillVector(floatBuffer, upperLeftVertex);
        fillVector(floatBuffer, lowerRightVertex);
        fillVector(floatBuffer, upperRightVertex);
        fillVector(floatBuffer, lowerRightVertex);
        fillVector(floatBuffer, upperLeftVertex);
        fillVector(floatBuffer, lowerLeftVertex);
    }

    private void fillVector(FloatBuffer floatBuffer, Vector vector) {
        floatBuffer.put(vector.getData()[0]);
        floatBuffer.put(vector.getData()[1]);
        floatBuffer.put(vector.getData()[2]);
    }

    private Vector getVertex(float x, float y, float partition) {
        x = GRID_MIN + (x * partition);
        y = GRID_MIN + (y * partition);

        float z = (float) SimplexNoise.noise(x, y);
        
        return new Vector(x, y, z);
    };

    public FloatBuffer getFloatBuffer() {
        return floatBuffer;
    }

    public boolean checkCollision() {
        Vector center;
        float radius;

        int index = gridLookingAtX * gridLookingAtY * TILE_DIMENSIONS * TILE_VERTICES;

        Vector minCoordinates = new Vector(3);
        Vector maxCoordinates = new Vector(3);

        Vector vector = new Vector(3);
        for(int i = 0; i < TILE_VERTICES; i += TILE_DIMENSIONS) {
            vector.set(
                    floatBuffer.get(index + i),
                    floatBuffer.get(index + i+1),
                    floatBuffer.get(index + i+2)
            );

            minCoordinates.min(vector);
            maxCoordinates.max(vector);
        }

        center = minCoordinates.center(maxCoordinates);
        radius = minCoordinates.radius(maxCoordinates);

        Vector pointCameraSpace = camera.multiplyVector(center);

        return (new Vector(3)).distance(pointCameraSpace) > radius && pointCameraSpace.get(3) >= 0.0f;
    }

    public void updateTile(float x, float y, float z) {
        if(checkCollision()) {
            camera.translate(x, y, z);

            updateUniform("u_camera", camera);
        };
    }

    @Override
    public void keyCallback(int key, int action) {
        switch(key) {
            case GLFW_KEY_W : {
                if(gridLookingAtY < GRID_COMPLEXITY - gridLookingStep) {
                    gridLookingAtY += gridLookingStep;
                    updateTile(0, PARTITION, 0);
                }
                break;
            }
            case GLFW_KEY_A : {
                if(gridLookingAtX < GRID_COMPLEXITY - gridLookingStep) {
                    gridLookingAtX += gridLookingStep;
                    updateTile(PARTITION, 0, 0);
                }
                break;
            }
            case GLFW_KEY_S : {
                if(gridLookingAtY > gridLookingStep) {
                    gridLookingAtY -= gridLookingStep;
                    updateTile(0, -PARTITION, 0);
                };
                break;
            }
            case GLFW_KEY_D : {
                if(gridLookingAtX > gridLookingStep) {
                    gridLookingAtX -= gridLookingStep;
                    updateTile(-PARTITION, 0, 0);
                };
                break;
            }
        }
    }

    @Override
    public void scrollCallback(double xoffset, double yoffset) {
        if(yoffset > 0.0f) {
            updateTile(0, 0, PARTITION);
        } else {
            updateTile(0, 0, -PARTITION);
        }

        updateUniform("u_camera", camera);
    }
}
