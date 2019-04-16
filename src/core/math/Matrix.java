package core.math;

public class Matrix extends MatrixCore {

    public Matrix(int size) {
        super(size);
    }

    public Matrix(Matrix matrix) {
        super(matrix.getSize());

        for(int it = 0; it < matrix.getData().length; it++)
            data[it] = matrix.getData()[it];
    }
    
    public void translation(float x, float y, float z) {
        data[12] = x;
        data[13] = y;
        data[14] = z;
    }

    public void translate(float x, float y, float z) {
        data[12] += x;
        data[13] += y;
        data[14] += z;
    }

    public void translate(float translation) {
        translate(translation, translation, translation);
    }

    public void translate(Vector translation) {
        translate(translation.get(0), translation.get(1), translation.get(2));
    }
    
    public void rotationX(float rotation) {
        float c = (float) Math.cos(rotation);
        float s = (float) Math.sin(rotation);
        data[5] = c;
        data[6] = s;
        data[9] = -s;
        data[10] = c;
    }

    public void rotationY(float rotation) {
        float c = (float) Math.cos(rotation);
        float s = (float) Math.sin(rotation);
        data[0] = c;
        data[2] = -s;
        data[8] = s;
        data[10] = c;
    }

    public void rotationZ(float rotation) {
        float c = (float) Math.cos(rotation);
        float s = (float) Math.sin(rotation);
        data[0] = c;
        data[1] = s;
        data[4] = -s;
        data[5] = c;
    }

    public void scaling(float x, float y, float z) {
        data[0] = x;
        data[5] = y;
        data[10] = z;
        data[15] = 1;
    }

    public void scaling(float scale) {
        scaling(scale, scale, scale);
    }

    public void scaling(Vector scalar) {
        scaling(scalar.get(0), scalar.get(1), scalar.get(2));
    }

    public void projection(float width, float height, float depth) {
        data[0] = 2.0f / width;
        data[5] = -2.0f / height;
        data[10] = 2.0f / depth;
        data[12] = -1.0f;
        data[13] = 1.0f;
        data[14] = 1.0f;
    }

    public void perspective(float fieldOfView, float aspect, float near, float far) {
        float f = (float) Math.tan(Math.PI * 0.5 - 0.5 * fieldOfView);
        float rangeInv = (float) 1.0 / (near-far);

        data[0] = f / aspect;
        data[5] = f;
        data[10] = (near + far) * rangeInv;
        data[11] = -1;
        data[14] = near * far * rangeInv * 2;
        data[15] = 0;
    }

    public void orthographic(float left, float right, float bottom, float top, float near, float far) {
        data[0] = 2 / (right - left);
        data[5] = 2 / (top - bottom);
        data[10] = -2 / (far - near);
        data[12] = - (right + left) / (right - left);
        data[13] = - (top + bottom) / (top - bottom);
        data[14] = - (far + near) / (far - near);
        data[15] = 1;
    }

    void lookAt(Vector cameraPosition, Vector target, Vector up) {
        Vector zAxis = Vector.normalize(Vector.subtractValues(cameraPosition, target));
        Vector xAxis = Vector.cross(up, zAxis);
        Vector yAxis = Vector.cross(zAxis, xAxis);

        data[0] = xAxis.get(0);
        data[1] = xAxis.get(1);
        data[2] = xAxis.get(2);
        data[4] = yAxis.get(0);
        data[5] = yAxis.get(1);
        data[6] = yAxis.get(2);
        data[8] = zAxis.get(0);
        data[9] = zAxis.get(1);
        data[10] = zAxis.get(2);
        data[12] = cameraPosition.get(0);
        data[13] = cameraPosition.get(1);
        data[14] = cameraPosition.get(2);
        data[15] = 1;
    }

    public static Matrix fromQuat(Vector quaternion) {
        Matrix matrix = new Matrix(4);

        float x = quaternion.get(0), y = quaternion.get(1), z = quaternion.get(2), w = quaternion.get(3);
        float x2 = x + x;
        float y2 = y + y;
        float z2 = z + z;
        float xx = x * x2;
        float yx = y * x2;
        float yy = y * y2;
        float zx = z * x2;
        float zy = z * y2;
        float zz = z * z2;
        float wx = w * x2;
        float wy = w * y2;
        float wz = w * z2;

        matrix.getData()[0] = 1 - yy - zz;
        matrix.getData()[1] = yx + wz;
        matrix.getData()[2] = zx - wy;
        matrix.getData()[3] = 0;
        matrix.getData()[4] = yx - wz;
        matrix.getData()[5] = 1 - xx - zz;
        matrix.getData()[6] = zy + wx;
        matrix.getData()[7] = 0;
        matrix.getData()[8] = zx + wy;
        matrix.getData()[9] = zy - wx;
        matrix.getData()[10] = 1 - xx - yy;
        matrix.getData()[11] = 0;
        matrix.getData()[12] = 0;
        matrix.getData()[13] = 0;
        matrix.getData()[14] = 0;
        matrix.getData()[15] = 1;

        return matrix;
    }

    Vector quaternion() {
        Vector res = new Vector(4);

        res.set(0, 0);
        res.set(1, 0);
        res.set(2, 0);
        res.set(3, 1);

        return res;
    }

    public static Vector transformQuat(Vector vec, Vector quaternion) {
        Vector res = new Vector(3);

        float x = vec.get(0), y = vec.get(1), z = vec.get(2);
        float qx = quaternion.get(0), qy = quaternion.get(1), qz = quaternion.get(2), qw = quaternion.get(3);

        float ix = qw * x + qy * z - qz * y;
        float iy = qw * y + qz * x - qx * z;
        float iz = qw * z + qx * y - qy * x;
        float iw = -qx * x - qy * y - qz * z;

        res.set(0, ix * qw + iw * -qx + iy * -qz - iz * -qy);
        res.set(1, iy * qw + iw * -qy + iz * -qx - ix * -qz);
        res.set(2, iz * qw + iw * -qz + ix * -qy - iy * -qx);

        return res;
    }
}
