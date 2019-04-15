package core.features.obj;

import core.math.Vector;
import core.math.Vector2f;
import core.math.Vector3f;
import tools.Log;

import java.io.*;
import java.util.*;

import static org.lwjgl.opengl.GL11.GL_REPEAT;

public class Obj {

    public HashMap<String, Material> materials = new HashMap<>();

    public List<Vector> vertices = new ArrayList<>();
    public List<Vector> textures = new ArrayList<>();
    public List<Vector> normals = new ArrayList<>();

    private List<Vector> verticesIndices = new ArrayList<>();
    private List<Vector> textureIndices = new ArrayList<>();
    private List<Vector> normalsIndices = new ArrayList<>();

    public static Obj readModel(String pathName) {
        ObjContainer objContainer = readDir("res/models/" + pathName);

        if(objContainer != null && objContainer.isValid()) {
            File objFile = objContainer.obj;

            try {
                FileReader fileReader = new FileReader(objFile);
                BufferedReader bufferedReader = new BufferedReader(fileReader);

                Obj obj = new Obj();
                obj.readMtl(objContainer.mtl);

                Material material = null;

                String line = bufferedReader.readLine();
                while(line != null) {
                    String[] values = line.split(" ");

                    if(values.length > 0 ) {
                        switch(values[0]) {
                            case "v" : {
                                Vector3f vec = new Vector3f(
                                    Float.valueOf(values[1]),
                                    Float.valueOf(values[2]),
                                    Float.valueOf(values[3])
                                );

                                obj.verticesIndices.add(vec);

                                break;
                            }
                            case "vt" : {
                                Vector2f vec = new Vector2f(
                                        Float.valueOf(values[1]),
                                        Float.valueOf(values[2])
                                );

                                obj.textureIndices.add(vec);

                                break;
                            }
                            case "vn" : {
                                Vector3f vec = new Vector3f(
                                        Float.valueOf(values[1]),
                                        Float.valueOf(values[2]),
                                        Float.valueOf(values[3])
                                );

                                obj.normalsIndices.add(vec);

                                break;
                            }
                            case "f" : {
                                for(int g = 1; g <= 3; g++) {
                                    String[] split = values[g].split("/");

                                    Vector geometricVertex = obj.verticesIndices.get(Integer.parseInt(split[0]) - 1);
                                    Vector textureVertex = obj.textureIndices.get(Integer.parseInt(split[1]) - 1);
                                    Vector normalVertex = obj.normalsIndices.get(Integer.parseInt(split[2]) - 1);

                                    if(textureVertex.getX() < 0.0 || textureVertex.getX() > 1.0 ||
                                            textureVertex.getY() < 0.0 || textureVertex.getY() > 1.0) {
                                        material.mode = GL_REPEAT;
                                    }

                                    Vector3f texture = new Vector3f(textureVertex.getX(), textureVertex.getY(), material.index);

                                    obj.vertices.add(geometricVertex);
                                    obj.textures.add(texture);
                                    obj.normals.add(normalVertex);
                                }

                                break;
                            }
                            case "usemtl" : {
                                values = values[1].split(":");

                                String materialName = values[values.length - 1];
                                material = obj.materials.getOrDefault(materialName, null);
                            }
                        }
                    }

                    line = bufferedReader.readLine();
                }

                fileReader.close();

                return obj;
            } catch(IOException e) {
                throw new Error(e.toString());
            }
        }

        return null;
    }

    public void print() {
        for(Map.Entry<String, Material> entry : materials.entrySet()) {
            Log.v(entry.getKey());

            entry.getValue().print();
        }
    }

    private void readMtl(File file) {
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            Material material = null;
            int index = 0;
            int counter = 0;

            String line = bufferedReader.readLine();
            while(line != null) {
                String[] split = line.split(" ");

                switch(split[0]) {
                    case "newmtl" : {
                        if(material != null) {
                            materials.put(material.name, material);
                        }

                        material = new Material();
                        material.name = split[1];
                        material.index = index++;

                        break;
                    }
                    case "Tf" : {
                        material.Tf = Vector.parseLine(split, 1, 3);

                        break;
                    }
                    case "Ni" : {
                        material.Ni = Float.parseFloat(split[1]);

                        break;
                    }
                    case "Ns" : {
                        material.Ns = Float.parseFloat(split[1]);

                        break;
                    }
                    default : {
                        if(material.isLightingColor(split[0])) {
                            counter += material.parseLightingColor(split, counter);
                        }
                    }
                }

                line = bufferedReader.readLine();
            }

            if(material != null) {
                materials.put(material.name, material);
            }
        } catch(IOException e) {
            Log.v(e.toString());
        }
    }

    private static ObjContainer readDir(String pathName) {
        File directory = new File(pathName);

        if(directory.isDirectory()) {
            ObjContainer objContainer = new ObjContainer();

            File[] files = directory.listFiles();

            if(files != null) {
                for(File file : files) {
                    String fileName = file.getName();

                    if(fileName.endsWith(".png")) {
                        objContainer.assets.add(file);
                    } else if(fileName.endsWith(".obj")) {
                        objContainer.obj = file;
                    } else if(fileName.endsWith(".mtl")) {
                        objContainer.mtl = file;
                    }
                }

                return objContainer;
            }
        } else {
            throw new Error("The pathname specified is not a directory");
        }

        return null;
    }
}