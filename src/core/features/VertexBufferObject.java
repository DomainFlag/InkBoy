package core.features;

import core.Settings;
import core.math.Vector;
import core.tools.BufferTools;
import tools.Log;

import static org.lwjgl.opengl.ARBVertexArrayObject.glBindVertexArray;
import static org.lwjgl.opengl.ARBVertexArrayObject.glGenVertexArrays;
import static org.lwjgl.opengl.GL15.*;
import static org.lwjgl.opengl.GL15C.GL_ARRAY_BUFFER;
import static org.lwjgl.opengl.GL20.glDisableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glEnableVertexAttribArray;
import static org.lwjgl.opengl.GL20.glVertexAttribPointer;
import static org.lwjgl.opengl.GL30.glDeleteVertexArrays;

public class VertexBufferObject {

    private int vertexBufferObject;
    private int vertexArrayObject;

    private int size = 0;

    public VertexBufferObject() {
        vertexBufferObject = glGenBuffers();
        vertexArrayObject = glGenVertexArrays();
    }

    public void allocate(Vector[] vertices) {
        this.size = vertices.length;

        glBindVertexArray(vertexArrayObject);

        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject);
        glBufferData(GL_ARRAY_BUFFER, BufferTools.createFlippedBuffer(vertices), GL_STATIC_DRAW);

        glVertexAttribPointer(0, vertices[0].size(), GL_FLOAT, false, 0, 0);

        glBindVertexArray(0);
    }

    public void allocate(float[] vertices, int size) {
        this.size = vertices.length / 2;

        glBindVertexArray(vertexArrayObject);

        glBindBuffer(GL_ARRAY_BUFFER, vertexBufferObject);
        glBufferData(GL_ARRAY_BUFFER, vertices, GL_STATIC_DRAW);

        glVertexAttribPointer(0, size, GL_FLOAT, false, 0, 0);

        glBindVertexArray(0);
    }

    public void render() {
        if(size != 0) {
            glBindVertexArray(vertexArrayObject);
            glEnableVertexAttribArray(0);

            glDrawArrays(Settings.TERRAIN_DRAWING_TYPE, 0, size);

            glDisableVertexAttribArray(0);
            glBindVertexArray(0);
        }
    }

    public void delete() {
        glBindVertexArray(vertexArrayObject);
        glDeleteBuffers(vertexBufferObject);
        glDeleteVertexArrays(vertexArrayObject);

        glBindVertexArray(0);
    }
}