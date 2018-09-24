package tools;

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

    public static void v(Float[] values) {
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

    // TODO() more...
}
