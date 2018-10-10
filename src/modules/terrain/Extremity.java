package modules.terrain;

import core.math.Vector;
import core.math.Vector2f;
import tools.Log;

public class Extremity {

    private Vector leftBottomCorner;
    private Vector rightUpCorner;

    public Extremity(Vector2f leftBottomCorner, Vector2f rightUpCorner) {
        this.leftBottomCorner = leftBottomCorner;
        this.rightUpCorner = rightUpCorner;
    }

    public Extremity(Vector leftBottomCorner, Vector rightUpCorner) {
        this.leftBottomCorner = leftBottomCorner;
        this.rightUpCorner = rightUpCorner;
    }

    public float[] extractTrianglesData() {
        return new float[] {
                leftBottomCorner.get(0), 0.0f,  leftBottomCorner.get(1),
                leftBottomCorner.get(0), 0.0f,  rightUpCorner.get(1),
                rightUpCorner.get(0), 0.0f,  rightUpCorner.get(1),
                rightUpCorner.get(0), 0.0f,  rightUpCorner.get(1),
                rightUpCorner.get(0), 0.0f,  leftBottomCorner.get(1),
                leftBottomCorner.get(0), 0.0f,  leftBottomCorner.get(1),
        };
    }

    public float[] extractLinesData() {
        return new float[] {
                leftBottomCorner.get(0), 0.0f,  leftBottomCorner.get(1),
                leftBottomCorner.get(0), 0.0f,  rightUpCorner.get(1),
                leftBottomCorner.get(0), 0.0f,  rightUpCorner.get(1),
                rightUpCorner.get(0), 0.0f,  rightUpCorner.get(1),
                rightUpCorner.get(0), 0.0f,  rightUpCorner.get(1),
                leftBottomCorner.get(0), 0.0f,  leftBottomCorner.get(1),
                leftBottomCorner.get(0), 0.0f,  leftBottomCorner.get(1),
                rightUpCorner.get(0), 0.0f,  leftBottomCorner.get(1),
                rightUpCorner.get(0), 0.0f,  leftBottomCorner.get(1),
                rightUpCorner.get(0), 0.0f,  rightUpCorner.get(1)
        };
    }

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
