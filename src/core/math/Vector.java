package core.math;

import tools.Log;

public class Vector {

    private int size = 4;
    public float[] data;

    public Vector(int size) {
        data = new float[4];

        set(0, 0, 0, 1.0f);
    }

    public Vector(int size, int nb) {
        data = new float[4];

        set(nb, nb, nb, 1.0f);
    }

    public Vector(float x, float y, float z) {
        data = new float[4];

        set(x, y, z,1.0f);
    }

    public void print() {
        Log.v(this.data);
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

    public void min(Vector vector) {
        if(vector.size() != size())
            return;

        for(int it = 0; it < size(); it++)
            data[it] = Math.min(data[it], vector.get(it));
    }

    public void max(Vector vector) {
        if(vector.size() != size())
            return;

        for(int it = 0; it < size(); it++)
            data[it] = Math.max(data[it], vector.get(it));
    }

    public void divide(float division) {
        for(int it = 0; it < size; it++)
            data[it] /= division;
    }

    public float radius(Vector vector) {
        if(vector.size() != size())
            return 0.0f;

        float max = 0.0f;
        for(int it = 0; it < size(); it++) {
            max = Math.max(
                    max,
                    Math.abs(data[it] - vector.get(it))
            );
        }

        return max / 2.0f;
    }

    public Vector center(Vector vector) {
        if(vector.size() != size())
            return null;

        Vector vec = new Vector(3);
        for(int it = 0; it < size(); it++)
            vec.getData()[it] = (get(it) + vector.get(it)) / 2.0f;

        return vec;
    }

    public float distance(Vector vector) {
        if(vector.size() != size())
            return 0.0f;

        float dist = 0.0f;
        for(int it = 0; it < size(); it++)
            dist += Math.pow(data[it] - vector.get(it), 2.0f);

        return (float) Math.sqrt(dist);
    }

    public float get(int index) {
        return data[index];
    }

    public int size() {
        return size;
    }
}
