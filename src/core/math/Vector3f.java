package core.math;

public class Vector3f extends Vector {

    public Vector3f() {
        super(3);

        set(0.0f, 0.0f, 0.0f);
    }

    public Vector3f(Vector vector) {
        super(3);

        set(vector.get(0), vector.get(1), vector.get(2));
    }

    public Vector3f(float x, float y, float z) {
        super(3);

        set(x, y, z);
    }

    public void set(float x, float y, float w) {
        set(0, x);
        set(1, y);
        set(2, w);
    }

    public void add(float x, float y, float z) {
        float[] data = getData();

        data[0] += x;
        data[1] += y;
        data[2] += z;
    }

    public void add(Vector vector) {
        float[] data = getData();

        for(int it = 0; it < size(); it++)
            data[it] += vector.get(it);
    }
}
