package core.math;

import tools.Log;

public class MatrixCore {

    private int size;

    public float[] data;

    public MatrixCore() {}

    public MatrixCore(int size) {
        this.size = size;

        data = new float[size*size];

        for(int g = 0; g < size; g++)
            data[g*size+g] = 1.0f;
    }

    public void print() {
        Log.v(this);
    }

    public int getSize() {
        return size;
    }

    public float[] getData() {
        return data;
    }

    public Matrix add(Matrix mat) {
        Matrix result = new Matrix(mat.getSize());

        for(int g = 0; g < size; g++) {
            for(int h = 0; h < size; h++) {
                result.data[g*4+h] = 0;

                for(int i = 0; i < 4; i++) {
                    result.data[g*4+h] += data[g*4+i]*mat.data[i*4+h];
                }
            }
        }

        return result;
    }

    public Vector multiplyVector(Vector vec) {
        Vector res = new Vector(vec.size());

        for(int h = 0; h < size; h++) {
            res.data[h] = 0.0f;
            for(int i = 0; i < size; i++) {
                res.data[h] += data[i*size + h] * vec.data[i];
            }
        }

        return res;
    }

    public Matrix transposeMatrix() {
        Matrix res = new Matrix(size);

        for(int g = 0; g < size; g++) {
            for(int h = 0; h < size; h++) {
                res.data[g*4+h] = data[h*4+g];
            }
        }

        return res;
    }

    public Matrix inverseMatrix() {
        Matrix matrix = new Matrix(size);

        float m00 = data[0 * 4 + 0];
        float m01 = data[0 * 4 + 1];
        float m02 = data[0 * 4 + 2];
        float m03 = data[0 * 4 + 3];
        float m10 = data[1 * 4 + 0];
        float m11 = data[1 * 4 + 1];
        float m12 = data[1 * 4 + 2];
        float m13 = data[1 * 4 + 3];
        float m20 = data[2 * 4 + 0];
        float m21 = data[2 * 4 + 1];
        float m22 = data[2 * 4 + 2];
        float m23 = data[2 * 4 + 3];
        float m30 = data[3 * 4 + 0];
        float m31 = data[3 * 4 + 1];
        float m32 = data[3 * 4 + 2];
        float m33 = data[3 * 4 + 3];
        float tmp_0  = m22 * m33;
        float tmp_1  = m32 * m23;
        float tmp_2  = m12 * m33;
        float tmp_3  = m32 * m13;
        float tmp_4  = m12 * m23;
        float tmp_5  = m22 * m13;
        m11 = data[1 * 4 + 1];
        m12 = data[1 * 4 + 2];
        m13 = data[1 * 4 + 3];
        m20 = data[2 * 4 + 0];
        m21 = data[2 * 4 + 1];
        m22 = data[2 * 4 + 2];
        m23 = data[2 * 4 + 3];
        m30 = data[3 * 4 + 0];
        m31 = data[3 * 4 + 1];
        m32 = data[3 * 4 + 2];
        m33 = data[3 * 4 + 3];
        tmp_0  = m22 * m33;
        tmp_1  = m32 * m23;
        tmp_2  = m12 * m33;
        tmp_3  = m32 * m13;
        tmp_4  = m12 * m23;
        tmp_5  = m22 * m13;
        float tmp_6  = m02 * m33;
        float tmp_7  = m32 * m03;
        float tmp_8  = m02 * m23;
        float tmp_9  = m22 * m03;
        float tmp_10 = m02 * m13;
        float tmp_11 = m12 * m03;
        float tmp_12 = m20 * m31;
        float tmp_13 = m30 * m21;
        float tmp_14 = m10 * m31;
        float tmp_15 = m30 * m11;
        float tmp_16 = m10 * m21;
        float tmp_17 = m20 * m11;
        float tmp_18 = m00 * m31;
        float tmp_19 = m30 * m01;
        float tmp_20 = m00 * m21;
        float tmp_21 = m20 * m01;
        float tmp_22 = m00 * m11;
        float tmp_23 = m10 * m01;

        float t0 = (tmp_0 * m11 + tmp_3 * m21 + tmp_4 * m31) -
                (tmp_1 * m11 + tmp_2 * m21 + tmp_5 * m31);
        float t1 = (tmp_1 * m01 + tmp_6 * m21 + tmp_9 * m31) -
                (tmp_0 * m01 + tmp_7 * m21 + tmp_8 * m31);
        float t2 = (tmp_2 * m01 + tmp_7 * m11 + tmp_10 * m31) -
                (tmp_3 * m01 + tmp_6 * m11 + tmp_11 * m31);
        float t3 = (tmp_5 * m01 + tmp_8 * m11 + tmp_11 * m21) -
                (tmp_4 * m01 + tmp_9 * m11 + tmp_10 * m21);

        float d = (float) 1.0 / (m00 * t0 + m10 * t1 + m20 * t2 + m30 * t3);

        matrix.data[0] = d*t0;
        matrix.data[1] = d*t1;
        matrix.data[2] = d*t2;
        matrix.data[3] = d*t3;

        matrix.data[4] = d * ((tmp_1 * m10 + tmp_2 * m20 + tmp_5 * m30) -
                (tmp_0 * m10 + tmp_3 * m20 + tmp_4 * m30));
        matrix.data[5] = d * ((tmp_0 * m00 + tmp_7 * m20 + tmp_8 * m30) -
                (tmp_1 * m00 + tmp_6 * m20 + tmp_9 * m30));
        matrix.data[6] = d * ((tmp_3 * m00 + tmp_6 * m10 + tmp_11 * m30) -
                (tmp_2 * m00 + tmp_7 * m10 + tmp_10 * m30));
        matrix.data[7] = d * ((tmp_4 * m00 + tmp_9 * m10 + tmp_10 * m20) -
                (tmp_5 * m00 + tmp_8 * m10 + tmp_11 * m20));

        matrix.data[8] = d * ((tmp_12 * m13 + tmp_15 * m23 + tmp_16 * m33) -
                (tmp_13 * m13 + tmp_14 * m23 + tmp_17 * m33));
        matrix.data[9] = d * ((tmp_13 * m03 + tmp_18 * m23 + tmp_21 * m33) -
                (tmp_12 * m03 + tmp_19 * m23 + tmp_20 * m33));
        matrix.data[10] = d * ((tmp_14 * m03 + tmp_19 * m13 + tmp_22 * m33) -
                (tmp_15 * m03 + tmp_18 * m13 + tmp_23 * m33));
        matrix.data[11] = d * ((tmp_17 * m03 + tmp_20 * m13 + tmp_23 * m23) -
                (tmp_16 * m03 + tmp_21 * m13 + tmp_22 * m23));


        matrix.data[12] = d * ((tmp_14 * m22 + tmp_17 * m32 + tmp_13 * m12) -
                (tmp_16 * m32 + tmp_12 * m12 + tmp_15 * m22));
        matrix.data[13] = d * ((tmp_20 * m32 + tmp_12 * m02 + tmp_19 * m22) -
                (tmp_18 * m22 + tmp_21 * m32 + tmp_13 * m02));
        matrix.data[14] = d * ((tmp_18 * m12 + tmp_23 * m32 + tmp_15 * m02) -
                (tmp_22 * m32 + tmp_14 * m02 + tmp_19 * m12));
        matrix.data[15] = d * ((tmp_22 * m22 + tmp_16 * m02 + tmp_21 * m12) -
                (tmp_20 * m12 + tmp_23 * m22 + tmp_17 * m02));

        return matrix;
    }



    public Vector cross(Vector a, Vector b) {
        Vector res = new Vector(3);
        
        res.set(
            a.data[1]*b.data[2]-a.data[2]*b.data[1],
                    a.data[2]*b.data[0]-a.data[0]*b.data[2],
                    a.data[0]*b.data[1]-a.data[1]*b.data[0]
        );
        
        return res;
    }

    public Vector addValues(Vector a, Vector b) {
        Vector res = new Vector(3);
        
        res.set(
                a.data[0]+b.data[0],
                    a.data[1]+b.data[1],
                    a.data[2]+b.data[2]
        );

        return res;
    }

    public float dot(Vector a, Vector b) {
        return a.data[0] * b.data[0] + a.data[1] * b.data[1] + a.data[2] * b.data[2];
    }

    public Vector normalize(Vector v) {
        Vector res = new Vector(3);
        
        float length = (float) Math.sqrt(v.data[0]*v.data[0]+v.data[1]*v.data[1]+v.data[2]*v.data[2]);

        if(length > 0.0001) {
            res.set(
                    v.data[0]/length,
                    v.data[1]/length,
                    v.data[2]/length);
        };

        return res;
    }

    public float angle(Vector a, Vector b) {
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
}
