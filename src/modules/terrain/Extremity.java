package modules.terrain;

import core.math.Vector;
import tools.Log;

public class Extremity {

    private Vector[] extremities;

    public Extremity(Vector leftUpCorner, Vector rightBottomCorner) {
        extremities = new Vector[] {
                leftUpCorner,
                rightBottomCorner
        };
    }

    public float[] data() {
        Vector leftUpCorner = extremities[0];
        Vector rightBottomCorner = extremities[1];

        return new float[] {
                leftUpCorner.get(0), 0.0f,  leftUpCorner.get(1),
                leftUpCorner.get(0), 0.0f,  rightBottomCorner.get(1),
                rightBottomCorner.get(0), 0.0f,  rightBottomCorner.get(1),
                rightBottomCorner.get(0), 0.0f,  rightBottomCorner.get(1),
                rightBottomCorner.get(0), 0.0f,  leftUpCorner.get(1),
                leftUpCorner.get(0), 0.0f,  leftUpCorner.get(1),
        };
    }

    public Extremity extract(int i, int j) {
        Vector leftUpperCorner = extremities[0];
        Vector rightBottomCorner = extremities[1];

        Vector dist = Vector.subtractValues(rightBottomCorner, leftUpperCorner).multiply(0.5f);

        if(i == 0 && j == 0) {
            // Left Up Corner
            return new Extremity(
                    new Vector(leftUpperCorner.get(0), leftUpperCorner.get(1)),
                    Vector.subtractValues(rightBottomCorner, dist)
            );
        } else if(i == 0 && j == 1) {
            // Right Up Corner
            return new Extremity(
                    new Vector(leftUpperCorner.get(0) + dist.get(0), leftUpperCorner.get(1)),
                    new Vector(rightBottomCorner.get(0), leftUpperCorner.get(1)  + dist.get(1))
            );
        } else if(i == 1 && j == 0) {
            // Left Bottom Corner
            return new Extremity(
                    new Vector(leftUpperCorner.get(0), leftUpperCorner.get(1)  + dist.get(1)),
                    new Vector(rightBottomCorner.get(0) + dist.get(0), leftUpperCorner.get(1))
            );
        } else {
            // Right Bottom Corner
            return new Extremity(
                    Vector.addValues(leftUpperCorner, dist),
                    new Vector(rightBottomCorner.get(0), rightBottomCorner.get(1))
            );
        }
    }
}
