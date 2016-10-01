package jp.cordea.coveragecollector;

import hudson.FilePath;
import jp.cordea.coveragecollector.model.Test;
import jp.cordea.coveragecollector.model.TestSuite;
import lombok.NonNull;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Yoshihiro Tanaka on 2016/09/30.
 */
public class FileHelper {

    private static final String SYSTEM_FILE = "cov_recent.xml";

    private Serializer serializer;

    public FileHelper() {
        serializer = new Persister();
    }

    public String getPluginDirPath() {
        String path = Configuration.get().getPluginFileDir();
        if (path == null) {
            path = "./";
        }
        return path;
    }

    public FilePath getPluginDir(FilePath filePath) {
        String path = getPluginDirPath();
        FilePath dir = filePath.child(path);
        try {
            if (!dir.exists()) {
                dir.mkdirs();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return null;
        }

        return dir;
    }

    public FilePath getMasterFile(FilePath filePath) {
        FilePath covDir = getPluginDir(filePath);
        if (covDir == null) {
            return null;
        }
        return covDir.child(SYSTEM_FILE);
    }

    public boolean storeMasterFile(FilePath filePath, Test test) {
        FilePath covFile = getMasterFile(filePath);
        if (covFile == null) {
            return false;
        }
        try {
            if (covFile.exists()) {
                covFile.delete();
            }
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return false;
        }
        try {
            serializer.write(test, covFile.write());
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public List<FilePath> getFiles(FilePath filePath, String path) {
        FilePath dir = filePath.child(path);
        List<FilePath> files = null;
        try {
            if (!dir.isDirectory()) {
                return null;
            }
            files = dir.list();
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return files;
    }

    public List<TestSuite> getXmlFiles(@NonNull  List<FilePath> filePaths) {
        List<TestSuite> testSuites = new ArrayList<>();
        for (FilePath filePath : filePaths) {
            if (filePath.getName().endsWith(".xml")) {
                try {
                    TestSuite testSuite = serializer.read(TestSuite.class, filePath.read());
                    testSuites.add(testSuite);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return testSuites;
    }

}
