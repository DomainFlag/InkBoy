package core.math;

public class Vector {

    private int size;

    public float[] data;

    public Vector(int size) {
        this.size = size;

        data = new float[size];

        set(0, 0, 0);
    }

    public void set(float x, float y, float z) {
        data[0] = x;
        data[1] = y;
        data[2] = z;
    }

    public void set(float x, float y, float z, float w) {
        data[0] = x;
        data[1] = y;
        data[2] = z;
        data[3] = w;
    }

    public float[] getData() {
        return data;
    }

    public int getSize() {
        return size;
    }
}
