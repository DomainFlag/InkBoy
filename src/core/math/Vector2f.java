package core.math;

public class Vector2f extends Vector {

    public Vector2f() {
        super(2);

        set(0.0f, 0.0f);
    }

    public Vector2f(float x, float y) {
        super(2);

        set(x, y);
    }

    public Vector2f(Vector vector) {
        super(2);

        set(vector.get(0), vector.get(1));
    }

    public void set(float x, float y) {
        set(0, x);
        set(1, y);
    }

    public void add(float x, float y) {
        float[] data = getData();

        data[0] += x;
        data[1] += y;
    }
}
