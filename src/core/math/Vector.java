package core.math;

import org.lwjgl.BufferUtils;
import tools.Log;

import java.nio.FloatBuffer;

public class Vector {

    private int size = 4;
    public float[] data;

    public Vector(int size) {
        data = new float[size];

        switch(size) {
            case 4 :  {
                this.size = 4;
                set(0, 0, 0, 1.0f);
                break;
            }
            case 3 :  {
                this.size = 3;
                set(0, 0, 0);
                break;
            }
            case 2 :  {
                this.size = 2;
                set(0, 0);
                break;
            }
        }
    }

    public Vector(int size, int nb) {
        data = new float[4];

        set(nb, nb, nb, 1.0f);
    }

    public Vector(float x, float y) {
        data = new float[2];
        size = 2;

        set(x, y);
    }

    public Vector(float x, float y, float z) {
        data = new float[4];
        size = 3;

        set(x, y, z,1.0f);
    }

    public void print() {
        Log.v(this.data);
    }

    public void set(float x, float y) {
        data[0] = x;
        data[1] = y;
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

    public static FloatBuffer createFloatBuffer(Vector[] vectors) {
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(vectors.length * vectors[0].size());
        for(Vector vector : vectors) {
            for(int it = 0; it < vector.size(); it++)
                floatBuffer.put(vector.get(it));
        }

        floatBuffer.flip();

        return floatBuffer;
    }

    public void add(float x, float y) {
        if(size != 2)
            return;

        data[0] += x;
        data[1] += y;
    }

    public void add(Vector vector) {
        add(vector.get(0), vector.get(1));
    }

    public Vector add(float adder) {
        add(adder, adder);

        return this;
    }

    public Vector substitute(float value) {
        for(int it = 0; it < size(); it++) {
            data[it] -= value;
        }

        return this;
    }

    public Vector multiply(Vector vector) {
        for(int it = 0; it < size(); it++) {
            data[it] *=  vector.get(it);
        }

        return this;
    }

    public Vector multiply(float scalar) {
        for(int it = 0; it < size(); it++) {
            data[it] *= scalar;
        }

        return this;
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

    public static Vector subtractValues(Vector a, Vector b) {
        Vector res;
        switch(a.size()) {
            case 3 : {
                res = new Vector(3);

                res.set(
                        a.data[0]-b.data[0],
                        a.data[1]-b.data[1],
                        a.data[2]-b.data[2]
                );

                break;
            }
            case 2 : {
                res = new Vector(2);

                res.set(
                        a.data[0]-b.data[0],
                        a.data[1]-b.data[1]
                );

                break;
            }
            default: res = null;
        }

        return res;
    }

    public static Vector addValues(Vector a, Vector b) {
        Vector res;
        switch(a.size) {
            case 3 : {
                res = new Vector(3);

                res.set(
                        a.data[0] + b.data[0],
                        a.data[1] + b.data[1],
                        a.data[2] + b.data[2]
                );

                break;
            }
            case 2 : {
                res = new Vector(2);

                res.set(
                        a.data[0] + b.data[0],
                        a.data[1] + b.data[1]
                );

                break;
            }
            default: res = null;
        }

        return res;
    }

    public float get(int index) {
        return data[index];
    }

    public int size() {
        return size;
    }
}
