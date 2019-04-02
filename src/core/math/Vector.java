package core.math;

import tools.Log;

public class Vector {

    private float[] data;
    private int size;

    public Vector() {}

    public Vector(int size) {
        initData(size);
    }

    public void setData(float[] data) {
        this.data = data;
    }

    public float[] getData() {
        return data;
    }

    public void initData(int size) {
        this.data = new float[size];

        this.size = size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getSize() {
        return size;
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

    public float get(int index) {
        return data[index];
    }

    public float getX() {
        return data[0];
    }

    public float getY() {
        return data[1];
    }

    public void set(int index, float value) {
        data[index] = value;
    }

    public Vector subtract(float value) {
        for(int it = 0; it < size(); it++) {
            data[it] -= value;
        }

        return this;
    }

    public Vector multiply(Vector vector) {
        for(int it = 0; it < size(); it++) {
            data[it] *= vector.get(it);
        }

        return this;
    }

    public Vector multiply(float scalar) {
        for(int it = 0; it < size(); it++) {
            data[it] *= scalar;
        }

        return this;
    }

    public static Vector parseLine(String line[], int offset, int len) {
        Vector vector = new Vector(len);

        for(int g = 0; g < len; g++) {
            vector.set(g, Float.parseFloat(line[g + offset]));
        }

        return vector;
    }

    public static Vector multiply(Vector vector, float scalar) {
        Vector res = new Vector(vector.getSize());

        for(int it = 0; it < vector.getSize(); it++) {
            res.set(it, vector.get(it) * scalar);
        }

        return res;
    }

    public static Vector center(Vector vector1, Vector vector2) {
        Vector vector = Vector.subtractValues(vector2, vector1);
        vector.multiply(0.5f);

        return Vector.subtractValues(vector2, vector);
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

    public int size() {
        return size;
    }

    public static Vector subtractValues(Vector a, Vector b) {
        if(a.size != b.size)
            throw new Error("Error while subtracting values, unequal sizes of vectors");

        Vector res = new Vector();
        res.initData(a.size);

        for(int it = 0; it < a.size; it++) {
            float value = a.get(it) - b.get(it);

            res.set(it, value);
        }

        return res;
    }

    public static Vector addition(Vector a, Vector b) {
        if(a.size != b.size)
            throw new Error("Error while adding values, unequal size of vectors");

        Vector res = new Vector(a.size);

        for(int it = 0; it < a.size; it++) {
            float value = a.get(it) + b.get(it);

            res.set(it, value);
        }

        return res;
    }

    public static Vector cross(Vector a, Vector b) {
        if(a.size != b.size && a.size != 3)
            throw new Error("Error while doing the cross of 2 vectors, unequal size of vectors");

        Vector res = new Vector(a.size);

        res.set(
                a.get(1)*b.get(2)-a.get(2)*b.get(1),
                a.get(2)*b.get(0)-a.get(0)*b.get(2),
                a.get(0)*b.get(1)-a.get(1)*b.get(0)
        );

        return res;
    }

    public static float dot(Vector a, Vector b) {
        float value = 0.0f;

        for(int it = 0; it < a.size; it++)
            value += a.get(it) * b.get(it);

        return value;
    }

    public static Vector4f fromEuler(float x, float y, float z) {
        Vector4f res = new Vector4f();

        float halfToRad = (float) (0.5 * Math.PI / 180.0);

        x *= halfToRad;
        y *= halfToRad;
        z *= halfToRad;

        float sx = (float) Math.sin(x);
        float cx = (float) Math.cos(x);
        float sy = (float) Math.sin(y);
        float cy = (float) Math.cos(y);
        float sz = (float) Math.sin(z);
        float cz = (float) Math.cos(z);

        res.set(0, sx * cy * cz - cx * sy * sz);
        res.set(1, cx * sy * cz + sx * cy * sz);
        res.set(2, cx * cy * sz - sx * sy * cz);
        res.set(3, cx * cy * cz + sx * sy * sz);

        return res;
    }

    public static Vector4f fromEuler(Vector vector) {
        return fromEuler(vector.get(0), vector.get(1), vector.get(2));
    }

    public static Vector normalize(Vector v) {
        Vector res = new Vector(v.size);

        float magnitude = 0.0f;
        for(int it = 0; it < v.size(); it++)
            magnitude += v.get(it) * v.get(it);

        magnitude = (float) Math.sqrt(magnitude);

        if(magnitude > 0.0001) {
            for(int it = 0; it < v.size(); it++)
                res.set(it, v.get(it) / magnitude);
        };

        return res;
    }

    public static float angle(Vector a, Vector b) {
        float cosine = dot(normalize(a), normalize(b));
        if(cosine > 1.0) {
            return 0;
        } else if(cosine < -1.0) {
            return (float) Math.PI;
        } else {
            return (float) Math.acos(cosine);
        }
    }

    public static float distanceVectors(Vector a, Vector b) {
        float x = b.data[0] - a.data[0];
        float y = b.data[1] - a.data[1];
        float z = b.data[2] - a.data[2];

        return (float) Math.sqrt(x*x + y*y + z*z);
    }

    public static float distanceVectors(Vector a) {
        return (float) Math.sqrt(a.data[0]*a.data[0] + a.data[1]*a.data[1] + a.data[2]*a.data[2]);
    }

    public Vector normalize() {
        if(size == 4) {
            for(int g = 0; g < 3; g++)
                data[g] /= data[3];
        }

        return this;
    }
}
