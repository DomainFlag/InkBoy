package core.features.obj;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ObjContainer {

    public List<File> assets = new ArrayList<>();
    public File obj = null;
    public File mtl = null;

    boolean isValid() {
        return obj != null && mtl != null;
    }
}
