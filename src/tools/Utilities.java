package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import static org.lwjgl.opengl.GL46.*;

public class Utilities {

    // TODO(0)

    private static String readShader(String pathName) {
        File file = new File(pathName);
        try {
            FileReader fileReader = new FileReader("res/shaders/" + file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            StringBuilder stringBuilder = new StringBuilder();

            String line = bufferedReader.readLine();
            while(line != null) {
                stringBuilder.append(line);
                stringBuilder.append("\n");
                line = bufferedReader.readLine();
            }

            return stringBuilder.toString();
        } catch(IOException e) {
            System.out.println(e.toString());
        }

        return null;
    };

    private static String resolvePath(String pathSourceName, int shaderType) {
        if(pathSourceName == null)
            return null;

        String extension;
        if(shaderType == GL_VERTEX_SHADER)
            extension = ".vert";
        else if(shaderType == GL_FRAGMENT_SHADER)
            extension = ".frag";
        else return null;

        return pathSourceName + "/" + pathSourceName + extension;
    }

    private static int createShader(String pathSourceName, int shaderType) {
        String path = resolvePath(pathSourceName, shaderType);
        if(path == null)
            return -1;

        int shader = glCreateShader(shaderType);
        glShaderSource(shader, readShader(path));
        glCompileShader(shader);

        int[] status = new int[]{0};
        glGetShaderiv(shader, GL_COMPILE_STATUS, status);

        if(status[0] == 1) {
            return shader;
        } else {
            String info = glGetShaderInfoLog(shader);
            System.out.println(info);

            glDeleteShader(shader);
        }

        return -1;
    };

    public static int createProgram(String pathSourceName) {
        int program = glCreateProgram();

        glAttachShader(program, createShader(pathSourceName, GL_VERTEX_SHADER));
        glAttachShader(program, createShader(pathSourceName, GL_FRAGMENT_SHADER));
        glLinkProgram(program);

        int[] status = new int[]{0};
        glGetProgramiv(program, GL_LINK_STATUS, status);

        if(status[0] == 1) {
            return program;
        } else {
            String info = glGetProgramInfoLog(program);
            System.out.println(info);

            glDeleteProgram(program);
        }

        return -1;
    }
}