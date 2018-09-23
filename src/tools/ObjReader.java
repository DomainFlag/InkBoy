package tools;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ObjReader {

    private static HashMap<Integer, Pattern> patterns;

    static {
        patterns = new HashMap<>();

        patterns.put(Types.MODEL_MATERIAL_KEY, Pattern.compile("^[^\\.]+.*\\.png"));
        patterns.put(Types.MODEL_OBJ_KEY, Pattern.compile("^[^\\.]+.*\\.obj"));
        patterns.put(Types.MODEL_MTL_KEY, Pattern.compile("^[^\\.]+.*\\.mtl"));
    }

    private File obj;
    private File mtl;

    private HashSet<String> textures;
    private HashMap<String, String> materials;

    Pattern slicer = Pattern.compile("[\\s:]");

    private ArrayList<Float> geometricVertixIndices = new ArrayList<>();
    private ArrayList<Float> textureVertixIndices = new ArrayList<>();
    private ArrayList<Float> normalVertixIndices = new ArrayList<>();

    private ArrayList<Float> geometricVertices = new ArrayList<>();
    private ArrayList<Float> textureVertices = new ArrayList<>();
    private ArrayList<Float> normalVertices= new ArrayList<>();

    public ObjReader() {

    }

    private void readObj(File file) {
        try {
            FileInputStream fileInputStream = new FileInputStream(file);

            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line = bufferedReader.readLine();
            while(line != null) {
                String[] data = line.split("[\\s\\\\/]");

                if(data[0].equals("v")) {
                    geometricVertixIndices.add(Float.valueOf(data[1]));
                    geometricVertixIndices.add(Float.valueOf(data[2]));
                    geometricVertixIndices.add(Float.valueOf(data[3]));
                } else if (data[0].equals("vt")) {
                    textureVertixIndices.add(Float.valueOf(data[1]));
                    textureVertixIndices.add(Float.valueOf(data[2]));
                } else if (data[0].equals("vn")) {
                    normalVertixIndices.add(Float.valueOf(data[1]));
                    normalVertixIndices.add(Float.valueOf(data[2]));
                    normalVertixIndices.add(Float.valueOf(data[3]));
                } else if (data[0].equals("f")) {
                    for (int i = 1; i < 10; i += 3) {
                        int geometricIndex = (Integer.valueOf(data[i]) - 1) * 3;
                        int textureIndex = (Integer.valueOf(data[i + 1]) - 1) * 2;
                        int normalIndex = (Integer.valueOf(data[i + 2]) - 1) * 3;

                        for (int h = 0; h < 3; h++) {
                            Float geometricVertix = geometricVertixIndices.get(geometricIndex+h);
                            geometricVertices.add(geometricVertix);

                            Float normalVertix = normalVertixIndices.get(normalIndex+h);
                            normalVertices.add(normalVertix);
                        }

                        for (int h = 0; h < 2; h++) {
                            Float textureVertix = textureVertixIndices.get(textureIndex+h);
                            textureVertices.add(textureVertix);
                        }
                    }
                }

                line = bufferedReader.readLine();
            }
        } catch (FileNotFoundException e) {
            Log.v(e.toString());
        } catch (IOException e) {
            Log.v(e.toString());
        }
    }

    private void readMtl(File file) {
        String material = null;

        try {
            FileInputStream fileInputStream = new FileInputStream(file);

            InputStreamReader inputStreamReader = new InputStreamReader(fileInputStream);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);

            String line = bufferedReader.readLine();
            while(line != null) {
                String[] components = line.split("[\\w:]");

                if(components[0].equals("newmtl")) {
                    material = components[1];
                } else if(components[0].equals("map_Kd")) {
                    if(material != null) {
                        materials.put(material, components[0]);
                    }
                }

                line = bufferedReader.readLine();
            }

        } catch(FileNotFoundException e) {
            Log.v(e.toString());
        } catch(IOException e) {
            Log.v(e.toString());
        }
    }

    public void readDir(String pathname) {
        File file = new File("res/models/" + pathname);
        File[] files = file.listFiles();

        if(files == null)
            return;

        for(File f : files) {

            for(Map.Entry<Integer, Pattern> patternEntry : patterns.entrySet()) {
                Pattern pattern = patternEntry.getValue();

                Matcher matcher = pattern.matcher(f.getName());

                if(matcher.find()) {
                    String matched = matcher.group(0);

                    Integer type = patternEntry.getKey();

                    switch(type) {
                        case Types.MODEL_MATERIAL_KEY : {
                            textures.add(matched);
                            break;
                        }
                        case Types.MODEL_MTL_KEY : {
                            mtl = f;
                            break;
                        }
                        case Types.MODEL_OBJ_KEY : {
                            obj = f;
                            break;
                        }
                        default : {
                            Log.v("Unknown file: " + f.getName());
                        }
                    }
                }
            }
        }

        readMtl(mtl);
        readObj(obj);
    }

    public ArrayList<Float> getGeometricVertices() {
        return geometricVertices;
    }

    public ArrayList<Float> getNormalVertices() {
        return normalVertices;
    }

    public ArrayList<Float> getTextureVertices() {
        return textureVertices;
    }

    private class Types {
        private static final int MODEL_MATERIAL_KEY = 20;
        private static final int MODEL_MTL_KEY = 21;
        private static final int MODEL_OBJ_KEY = 22;
    }
}
