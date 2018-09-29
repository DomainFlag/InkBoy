package modules;

import core.Settings;
import core.math.Matrix;
import core.math.MatrixCore;
import core.tools.SimplexNoise;
import org.lwjgl.BufferUtils;
import tools.Log;
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

    private static final int GRID_COMPLEXITY = 16;
    private static final float GRID_MIN = -6.0f;
    private static final float GRID_MAX = 6.0f;

    private static final float PARTITION = (GRID_MAX - GRID_MIN) / (GRID_COMPLEXITY - 1);

    private int gridLookingAtX = GRID_COMPLEXITY / 2;
    private int gridLookingAtY = GRID_COMPLEXITY / 2;

    private int gridLookingStep = 1;
    private float GRID_LOOKING_PARTITION_STEP = PARTITION * gridLookingStep;

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

        camera.rotationX((float) 0.0f);
        camera.translation(0.0f, 0.0f, 2.5f);
        addUniform("u_camera", camera.inverseMatrix());

        addUniform("u_model", model);

        projection.perspective((float) Math.PI/3, 1.0f, 0.00001f, 30.0f);
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

    public boolean checkCollision(float x, float y, float z) {
        int index = ((gridLookingAtY * (GRID_COMPLEXITY - 1)) + gridLookingAtX) * TILE_DIMENSIONS * TILE_VERTICES;

        Matrix newCamera = new Matrix(camera);
        newCamera.translate(x, y, z);
        Matrix inverseMatrix = newCamera.inverseMatrix();

        Vector vector = new Vector(3);
        for(int i = 0; i < TILE_VERTICES * TILE_DIMENSIONS; i += TILE_DIMENSIONS) {
            vector.set(
                    floatBuffer.get(index + i),
                    floatBuffer.get(index + (i+1)),
                    floatBuffer.get(index + (i+2))
            );

            Vector cameraPoint = inverseMatrix.multiplyVector(vector);
            Vector projectedPoint = projection.multiplyVector(cameraPoint);

            projectedPoint.divide(projectedPoint.data[3]);

            if((projectedPoint.data[2] < -1.0f || projectedPoint.data[2] > 1.0f))
                return false;
        }

        return true;
    }

    public void updateTile(float x, float y, float z) {
        if(checkCollision(x, y, z)) {
            camera.translate(x, y, z);

            updateUniform("u_camera", camera.inverseMatrix());
        };
    }

    @Override
    public void keyCallback(int key, int action) {
        switch(key) {
            case GLFW_KEY_W : {
                if(gridLookingAtY >= gridLookingStep) {
                    gridLookingAtY -= gridLookingStep;
                    updateTile(0, GRID_LOOKING_PARTITION_STEP, 0);
                }
                break;
            }
            case GLFW_KEY_A : {
                if(gridLookingAtX >= gridLookingStep) {
                    gridLookingAtX -= gridLookingStep;
                    updateTile(-GRID_LOOKING_PARTITION_STEP, 0, 0);
                };
                break;
            }
            case GLFW_KEY_S : {
                if(gridLookingAtY < GRID_COMPLEXITY - gridLookingStep - 1) {
                    gridLookingAtY += gridLookingStep;
                    updateTile(0, -GRID_LOOKING_PARTITION_STEP, 0);
                };
                break;
            }
            case GLFW_KEY_D : {
                if(gridLookingAtX < GRID_COMPLEXITY - gridLookingStep - 1) {
                    gridLookingAtX += gridLookingStep;
                    updateTile(GRID_LOOKING_PARTITION_STEP, 0, 0);
                }
                break;
            }
        }
    }

    @Override
    public void scrollCallback(double xoffset, double yoffset) {
        if(yoffset > 0.0f) {
            updateTile(0, 0, -GRID_LOOKING_PARTITION_STEP);
        } else {
            updateTile(0, 0, GRID_LOOKING_PARTITION_STEP);
        }
    }
}
