package tools;

import core.math.Vector;
import core.math.Vector2f;
import core.math.Vector3f;

import java.io.*;
import java.nio.FloatBuffer;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Model {

    public HashMap<String, Material> materials = new HashMap<>();

    public List<Vector> vertices = new ArrayList<>();
    public List<Vector> textures = new ArrayList<>();
    public List<Vector> normals = new ArrayList<>();

    private List<Vector> verticesIndices = new ArrayList<>();
    private List<Vector> textureIndices = new ArrayList<>();
    private List<Vector> normalsIndices = new ArrayList<>();

    public static Model readModel(String pathName) {
        ModelContainer modelContainer = readDir("res/models/" + pathName);

        if(modelContainer != null && modelContainer.isValid()) {
            File objFile = modelContainer.obj;

            try {
                FileReader fileReader = new FileReader(objFile);
                BufferedReader bufferedReader = new BufferedReader(fileReader);

                Model model = new Model();
                model.readMtl(modelContainer.mtl);

                int textureIndex = -1;

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

                                model.verticesIndices.add(vec);

                                break;
                            }
                            case "vt" : {
                                Vector2f vec = new Vector2f(
                                        Float.valueOf(values[1]),
                                        Float.valueOf(values[2])
                                );

                                model.textureIndices.add(vec);

                                break;
                            }
                            case "vn" : {
                                Vector3f vec = new Vector3f(
                                        Float.valueOf(values[1]),
                                        Float.valueOf(values[2]),
                                        Float.valueOf(values[3])
                                );

                                model.normalsIndices.add(vec);

                                break;
                            }
                            case "f" : {
                                for(int g = 1; g <= 3; g++) {
                                    String[] split = values[g].split("/");

                                    Vector geometricVertex = model.verticesIndices.get(Integer.parseInt(split[0]) - 1);
                                    Vector textureVertex = model.textureIndices.get(Integer.parseInt(split[1]) - 1);
                                    Vector normalVertex = model.normalsIndices.get(Integer.parseInt(split[2]) - 1);

                                    Vector3f texture = new Vector3f(textureVertex.getX(), textureVertex.getY(), textureIndex);

                                    model.vertices.add(geometricVertex);
                                    model.textures.add(texture);
                                    model.normals.add(normalVertex);
                                }

                                break;
                            }
                            case "usemtl" : {
                                String materialName = values[1];

                                if(model.materials.containsKey(materialName)) {
                                    textureIndex = model.materials.get(materialName).index;
                                } else {
                                    textureIndex = -1;
                                }
                            }
                        }
                    }

                    line = bufferedReader.readLine();
                }

                fileReader.close();

                return model;
            } catch(IOException e) {
                throw new Error(e.toString());
            }
        }

        return null;
    }

    private void readMtl(File file) {
        try {
            FileReader fileReader = new FileReader(file);
            BufferedReader bufferedReader = new BufferedReader(fileReader);

            String materialName = null;
            int counter = 0;

            String line = bufferedReader.readLine();
            while(line != null) {
                String[] split = line.split(" ");

                if(split[0].equals("newmtl")) {
                    materialName = split[1];
                } else if(split[0].equals("map_Kd")) {
                    if(materialName != null) {
                        Material material = new Material(split[1], counter);
                        materials.put(materialName, material);

                        counter++;
                    }
                }

                line = bufferedReader.readLine();
            }
        } catch(IOException e) {
            Log.v(e.toString());
        }
    }

    private static ModelContainer readDir(String pathName) {
        File directory = new File(pathName);

        if(directory.isDirectory()) {
            ModelContainer modelContainer = new ModelContainer();

            File[] files = directory.listFiles();

            if(files != null) {
                for(File file : files) {
                    String fileName = file.getName();

                    if(fileName.endsWith(".png")) {
                        modelContainer.assets.add(file);
                    } else if(fileName.endsWith(".obj")) {
                        modelContainer.obj = file;
                    } else if(fileName.endsWith(".mtl")) {
                        modelContainer.mtl = file;
                    }
                }

                return modelContainer;
            }
        } else {
            throw new Error("The pathname specified is not a directory");
        }

        return null;
    }

    public static class Material {

        public String texture;
        public int index;

        public Material(String texture, int index) {
            this.texture = texture;
            this.index = index;
        }
    }

    private static class ModelContainer {

        private List<File> assets = new ArrayList<>();
        private File obj = null;
        private File mtl = null;

        boolean isValid() {
            return obj != null && mtl != null;
        }
    }
}



