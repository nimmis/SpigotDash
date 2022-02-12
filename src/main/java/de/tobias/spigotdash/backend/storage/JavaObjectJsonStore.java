package de.tobias.spigotdash.backend.storage;

import com.google.gson.Gson;
import de.tobias.spigotdash.backend.logging.fieldLogger;
import de.tobias.spigotdash.backend.logging.globalLogger;
import de.tobias.spigotdash.main;
import org.apache.commons.io.FileUtils;

import java.io.File;

public class JavaObjectJsonStore {

    private Object obj;
    private Class obj_class;
    private Gson gson;

    private File file;
    private fieldLogger thisLogger;

    public JavaObjectJsonStore(Class t, File file) {
        this.obj = null;
        this.obj_class = t;
        this.file = file;
        this.gson = main.GLOBAL_GSON;
        this.thisLogger = new fieldLogger("JavaObjStore", file.getName(), globalLogger.constructed);
    }

    public Object getObject() {
        return obj_class.cast(this.obj);
    }

    public boolean loadOrCreate() {
        thisLogger.INFO("Reading File...", 0);
        if(file.exists()) {
            thisLogger.INFO("File exists! Reading...", 10);
            try {
                obj = gson.fromJson(FileUtils.readFileToString(file, main.GLOBAL_CHARSET), obj_class);
                thisLogger.INFO("Read successfully", 0);
                return true;
            } catch (Exception ex) {
                thisLogger.ERROREXEP("Cannot read the existing JSON File: ", ex, 0);
                return createFile();
            }
        } else {
            thisLogger.WARNING("File not found", 0);
            return createFile();
        }
    }

    public boolean createFile() {
        thisLogger.WARNING("Creating new default File", 0);
        Object newDefault;

        try {
            newDefault = obj_class.getConstructors()[0].newInstance();
            thisLogger.INFO("Constructed empty Object", 20);
            FileUtils.write(this.file, main.GLOBAL_GSON.toJson(newDefault, obj_class), main.GLOBAL_CHARSET);
            thisLogger.INFO("Wrote the Object to File", 20);
            thisLogger.INFO("File successfully created", 0);
            this.obj = newDefault;
            return true;
        } catch (Exception ex) {
            thisLogger.ERROREXEP("Cannot create new File: ", ex, 0);
            return false;
        }
    }
}
