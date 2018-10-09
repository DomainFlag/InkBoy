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

    public void projection(float x, float y, float z) {
        data[0] = 2.0f/x;
        data[5] = -2.0f/y;
        data[10] = 1.0f/z;
        data[12] = -1.0f;
        data[13] = 1.0f;
        data[15] = 1.0f;
    }

    public void perspective(float fieldOfView, float aspect, float near, float far) {
        float f = (float) Math.tan(Math.PI*0.5 - 0.5*fieldOfView);
        float rangeInv = (float) 1.0 / (near-far);

        data[0] = f/aspect;
        data[5] = f;
        data[10] = (near+far)*rangeInv;
        data[11] = -1;
        data[14] = near*far*rangeInv*2;
        data[15] = 0;
    }

    void lookAt(Vector cameraPosition, Vector target, Vector up) {
        Vector zAxis = normalize(Vector.subtractValues(cameraPosition, target));
        Vector xAxis = cross(up, zAxis);
        Vector yAxis = cross(zAxis, xAxis);

        data[0] = xAxis.data[0];
        data[1] = xAxis.data[1];
        data[2] = xAxis.data[2];
        data[4] = yAxis.data[0];
        data[5] = yAxis.data[1];
        data[6] = yAxis.data[2];
        data[8] = zAxis.data[0];
        data[9] = zAxis.data[1];
        data[10] = zAxis.data[2];
        data[12] = cameraPosition.data[0];
        data[13] = cameraPosition.data[1];
        data[14] = cameraPosition.data[2];
        data[15] = 1;
    }


    void fromQuat(Vector quaternion) {
        float x = quaternion.data[0], y = quaternion.data[1], z = quaternion.data[2], w = quaternion.data[3];
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
        data[0] = 1 - yy - zz;
        data[1] = yx + wz;
        data[2] = zx - wy;
        data[3] = 0;
        data[4] = yx - wz;
        data[5] = 1 - xx - zz;
        data[6] = zy + wx;
        data[7] = 0;
        data[8] = zx + wy;
        data[9] = zy - wx;
        data[10] = 1 - xx - yy;
        data[11] = 0;
        data[12] = 0;
        data[13] = 0;
        data[14] = 0;
        data[15] = 1;
    }

    Vector quaternion() {
        Vector res = new Vector(4);

        res.data[0] = 0;
        res.data[1] = 0;
        res.data[2] = 0;
        res.data[3] = 1;

        return res;
    }


    Vector fromEuler(float x, float y, float z) {
        Vector res = new Vector(4);

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

        res.data[0] = sx * cy * cz - cx * sy * sz;
        res.data[1] = cx * sy * cz + sx * cy * sz;
        res.data[2] = cx * cy * sz - sx * sy * cz;
        res.data[3] = cx * cy * cz + sx * sy * sz;

        return res;
    }

    public Vector transformQuat(Vector vec, Vector quaternion) {
        Vector res = new Vector(3);

        float x = vec.data[0], y = vec.data[1], z = vec.data[2];
        float qx = quaternion.data[0], qy = quaternion.data[1], qz = quaternion.data[2], qw = quaternion.data[3];

        float ix = qw * x + qy * z - qz * y;
        float iy = qw * y + qz * x - qx * z;
        float iz = qw * z + qx * y - qy * x;
        float iw = -qx * x - qy * y - qz * z;

        res.data[0] = ix * qw + iw * -qx + iy * -qz - iz * -qy;
        res.data[1] = iy * qw + iw * -qy + iz * -qx - ix * -qz;
        res.data[2] = iz * qw + iw * -qz + ix * -qy - iy * -qx;

        return res;
    }
}
