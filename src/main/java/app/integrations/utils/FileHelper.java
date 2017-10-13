package app.integrations.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.FileAlreadyExistsException;

public class FileHelper {

    public static File createFile(String absoluteFileName, boolean overwrite) throws IOException {
        File file = new File(absoluteFileName);
        file.getParentFile().mkdirs();
        if (!overwrite && file.exists()) {
            throw new FileAlreadyExistsException(absoluteFileName);
        }
        if (overwrite && file.exists()) {
            file.delete();
        }
        file.createNewFile();
        return file;
    }

    public static File createFile(String foldername, String filename, boolean overwrite) throws IOException {
        File folder = new File(foldername);
        if (!folder.exists()) {
            folder.mkdirs();
        }
        if (!folder.isDirectory()) {
            throw new FileNotFoundException("'" + folder.getAbsolutePath() + "' is not a directory");
        }
        File file = new File(folder, filename);
        if (!overwrite && file.exists()) {
            throw new FileAlreadyExistsException("'" + filename + "' in '" + foldername + "'");
        }
        if (overwrite && file.exists()) {
            file.delete();
        }
        file.createNewFile();
        return file;
    }

    public static File checkAndGetFile(String foldername, String filename) throws FileNotFoundException {
        File folder = new File(foldername);
        File file = new File(folder, filename);
        if (!file.exists() || file.isDirectory()) {
            throw new FileNotFoundException("'" + filename + "' in '" + foldername + "'");
        }
        return file;
    }

    public static File checkAndGetFile(String absoluteFileName) throws FileNotFoundException {
        File file = new File(absoluteFileName);
        if (!file.exists() || file.isDirectory()) {
            throw new FileNotFoundException(absoluteFileName);
        }
        return file;
    }
}
