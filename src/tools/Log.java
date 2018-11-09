package tools;

import core.math.MatrixCore;
import core.math.Vector;

import java.nio.FloatBuffer;
import java.util.ArrayList;

public class Log {

    public static void v(String message) {
        System.out.println(message);
    }

    public static void v(int value) {
        System.out.println(value);
    }

    public static void v(float value) {
        System.out.println(value);
    }

    public static void v(boolean value) {
        System.out.println(value);
    }

    public static void v(String message, float value) {
        Log.v(message + value);
    }

    public static void v(double value) {
        System.out.println(value);
    }

    public static void v(float[] values) {
        StringBuilder stringBuilder = new StringBuilder();

        for(Float value : values) {
            stringBuilder.append(value);
            stringBuilder.append(" ");
        }

        System.out.println(stringBuilder.toString());
    }

    public static void v(ArrayList<Float> values) {
        StringBuilder stringBuilder = new StringBuilder();

        for(Float value : values) {
            stringBuilder.append(value);
            stringBuilder.append(" ");
        }

        System.out.println(stringBuilder.toString());
    }

    public static void v(FloatBuffer values) {
        StringBuilder stringBuilder = new StringBuilder();

        for(int it = 0; it < values.capacity(); it++) {
            stringBuilder.append(values.get(it));
            stringBuilder.append(" ");
        }

        System.out.println(stringBuilder.toString());
    }

    public static void v(String message, Vector vector) {
        System.out.print(message);

        v(vector);
    }

    public static void v(MatrixCore matrix) {
        StringBuilder stringBuilder = new StringBuilder();

        for(int g = 0; g < matrix.getSize(); g++) {
            for(int h = 0; h < matrix.getSize(); h++) {
                stringBuilder.append(matrix.getData()[g*matrix.getSize() + h]);
                stringBuilder.append(" ");
            }
            stringBuilder.append("\n");
        }

        System.out.println(stringBuilder.toString());
    }

    public static void v(Vector vector) {
        StringBuilder stringBuilder = new StringBuilder();

        for(int g = 0; g < vector.getData().length; g++) {
            stringBuilder.append(vector.getData()[g]);
            stringBuilder.append(" ");
        }

        System.out.println(stringBuilder.toString());
    }
}
