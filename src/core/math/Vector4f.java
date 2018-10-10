package core.math;

public class Vector4f extends Vector {

    public Vector4f() {
        super(4);

        set(0.0f, 0.0f, 0.0f, 1.0f);
    }

    public Vector4f(float x, float y, float z, float w) {
        super(4);

        set(x, y, z, w);
    }

    public void set(float x, float y, float z, float w) {
        set(0, x);
        set(1, y);
        set(2, z);
        set(3, w);
    }

    public void add(float x, float y, float z, float w) {
        float[] data = getData();

        data[0] += x;
        data[1] += y;
        data[2] += z;
        data[3] += w;
    }
}
