package modules.terrain;

import core.Settings;
import core.math.Vector;
import core.math.Vector2f;
import core.math.Vector3f;
import core.math.Vector4f;
import tools.Log;

public class Extremity {

    private Vector leftBottomCorner;
    private Vector rightUpCorner;

    private Vector scaledLeftBottomCorner;
    private Vector scaledRightUpCorner;

    public Extremity(Vector2f leftBottomCorner, Vector2f rightUpCorner) {
        this.leftBottomCorner = leftBottomCorner;
        this.rightUpCorner = rightUpCorner;

        scale();
    }

    public Extremity(Vector leftBottomCorner, Vector rightUpCorner) {
        this.leftBottomCorner = leftBottomCorner;
        this.rightUpCorner = rightUpCorner;

        scale();
    }

    private void scale() {
        scaledLeftBottomCorner = Vector.multiply(leftBottomCorner, Settings.SCALE_XZ);
        scaledRightUpCorner = Vector.multiply(rightUpCorner, Settings.SCALE_XZ);
    }

    public float[] extractTrianglesData() {
        return new float[] {
                scaledLeftBottomCorner.get(0), 0.0f,  scaledLeftBottomCorner.get(1),
                scaledLeftBottomCorner.get(0), 0.0f,  scaledRightUpCorner.get(1),
                scaledRightUpCorner.get(0), 0.0f,  scaledRightUpCorner.get(1),
                scaledRightUpCorner.get(0), 0.0f,  scaledRightUpCorner.get(1),
                scaledRightUpCorner.get(0), 0.0f,  scaledLeftBottomCorner.get(1),
                scaledLeftBottomCorner.get(0), 0.0f,  scaledLeftBottomCorner.get(1),
        };
    }

    public float[] extractLinesData() {
        return new float[] {
                scaledLeftBottomCorner.get(0), 0.0f,  scaledLeftBottomCorner.get(1),
                scaledLeftBottomCorner.get(0), 0.0f,  scaledRightUpCorner.get(1),
                scaledLeftBottomCorner.get(0), 0.0f,  scaledRightUpCorner.get(1),
                scaledRightUpCorner.get(0), 0.0f,  scaledRightUpCorner.get(1),
                scaledRightUpCorner.get(0), 0.0f,  scaledRightUpCorner.get(1),
                scaledLeftBottomCorner.get(0), 0.0f,  scaledLeftBottomCorner.get(1),
                scaledLeftBottomCorner.get(0), 0.0f,  scaledLeftBottomCorner.get(1),
                scaledRightUpCorner.get(0), 0.0f,  scaledLeftBottomCorner.get(1),
                scaledRightUpCorner.get(0), 0.0f,  scaledLeftBottomCorner.get(1),
                scaledRightUpCorner.get(0), 0.0f,  scaledRightUpCorner.get(1)
        };
    }

    public Vector getLocation() {
        Vector center = Vector.center(scaledLeftBottomCorner, scaledRightUpCorner);

        return new Vector4f(center.getX(), 0, center.getY(), 1.0f);
    };

    public Extremity extract(int i, int j) {
        Vector dist = Vector.subtractValues(rightUpCorner, leftBottomCorner);
        if(dist != null)
            dist.multiply(0.5f);
        else return null;

        if(i == 0 && j == 0) {
            // Left Bottom Corner
            return new Extremity(
                    new Vector2f(leftBottomCorner),
                    Vector.addition(leftBottomCorner, dist)
            );
        } else if(i == 0 && j == 1) {
            // Right Bottom Corner
            return new Extremity(
                    new Vector2f(leftBottomCorner.getX() + dist.getX(), leftBottomCorner.getY()),
                    new Vector2f(rightUpCorner.getX(), rightUpCorner.getY() - dist.getY())
            );
        } else if(i == 1 && j == 0) {
            // Left Up Corner
            return new Extremity(
                    new Vector2f(leftBottomCorner.getX(), leftBottomCorner.getY()  + dist.getY()),
                    new Vector2f(rightUpCorner.getX() - dist.getX(), rightUpCorner.getY())
            );
        } else {
            // Right Bottom Corner
            return new Extremity(
                    Vector.subtractValues(rightUpCorner, dist),
                    new Vector2f(rightUpCorner)
            );
        }
    }
}
