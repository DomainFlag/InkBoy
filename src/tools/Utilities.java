package tools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;

import static org.lwjgl.opengl.GL46.*;

public class Utilities {

    private static String readShader(File shaderFile) {
        try {
            FileReader fileReader = new FileReader(shaderFile);
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

    private static int createShader(File shaderFile, int shaderType) {
        int shader = glCreateShader(shaderType);
        glShaderSource(shader, readShader(shaderFile));
        glCompileShader(shader);

        int[] status = new int[]{0};
        glGetShaderiv(shader, GL_COMPILE_STATUS, status);

        if(status[0] == 1) {
            return shader;
        } else {
            String info = glGetShaderInfoLog(shader);
            Log.v(info);

            glDeleteShader(shader);
        }

        return -1;
    };

    public static File[] listShaders(String pathDirectorySourceName) {
        File file = new File("res/shaders/" + pathDirectorySourceName);
        return file.listFiles();
    }

    private static String getFileExtension(File file) {
        String name = file.getName();
        int lastIndexOf = name.lastIndexOf(".");
        if(lastIndexOf == -1) {
            return null;
        }

        return name.substring(lastIndexOf);
    }

    public static int createProgram(String pathSourceName) {
        int program = glCreateProgram();

        pathSourceName = pathSourceName.toLowerCase();

        File[] shaderFiles = listShaders(pathSourceName);
        for(File shaderFile : shaderFiles) {
            String extension = getFileExtension(shaderFile);
            if(extension != null) {
                int shaderType = SHADER_TYPES.get(extension);

                int shader = createShader(shaderFile, shaderType);
                glAttachShader(program, shader);
            }
        }

        glLinkProgram(program);

        int[] status = new int[]{0};
        glGetProgramiv(program, GL_LINK_STATUS, status);

        if(status[0] == 1) {
            return program;
        } else {
            String info = glGetProgramInfoLog(program);
            Log.v(info);

            glDeleteProgram(program);
        }

        return -1;
    }

    public static final HashMap<String, Integer> SHADER_TYPES;

    static {
        SHADER_TYPES = new HashMap<>();

        SHADER_TYPES.put(".vert", GL_VERTEX_SHADER);
        SHADER_TYPES.put(".geom", GL_GEOMETRY_SHADER);
        SHADER_TYPES.put(".frag", GL_FRAGMENT_SHADER);
        SHADER_TYPES.put(".tesc", GL_TESS_CONTROL_SHADER);
        SHADER_TYPES.put(".tese", GL_TESS_EVALUATION_SHADER);
    }
}