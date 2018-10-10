package core.tools;

import core.math.Vector;
import org.lwjgl.BufferUtils;

import java.nio.FloatBuffer;

public class BufferTools {

    public static FloatBuffer createFlippedBuffer(Vector[] vectors) {
        FloatBuffer floatBuffer = BufferUtils.createFloatBuffer(vectors.length * vectors[0].size());
        for(Vector vector : vectors) {
            for(int it = 0; it < vector.size(); it++)
                floatBuffer.put(vector.get(it));
        }

        floatBuffer.flip();

        return floatBuffer;
    }
}
