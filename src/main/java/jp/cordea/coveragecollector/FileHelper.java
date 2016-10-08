package jp.cordea.coveragecollector;

import hudson.FilePath;
import jp.cordea.coveragecollector.model.cobertura.Coverage;
import jp.cordea.coveragecollector.model.jacoco.Report;
import jp.cordea.coveragecollector.model.test.TestSuite;
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

    public boolean storeMasterFile(FilePath filePath, Report report, Coverage coverage) {
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
            if (report != null) {
                serializer.write(report, covFile.write());
            }
            if (coverage != null) {
                serializer.write(coverage, covFile.write());
            }
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

    public Report getJacocoReport(@NonNull FilePath filePath) {
        Report report = null;
        try {
            String xml = ignoreDoctype(filePath.readToString());
            report = serializer.read(Report.class, xml);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return report;
    }

    public Coverage getCoberturaReport(@NonNull FilePath filePath) {
        Coverage coverage = null;
        try {
            coverage = serializer.read(Coverage.class, filePath.read());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return coverage;
    }

    public List<TestSuite> getXmlFiles(@NonNull List<FilePath> filePaths) {
        List<TestSuite> testSuites = new ArrayList<>();
        for (FilePath filePath : filePaths) {
            if (filePath.getName().endsWith(".xml")) {
                try {
                    String xml = ignoreDoctype(filePath.readToString());
                    TestSuite testSuite = serializer.read(TestSuite.class, xml);
                    testSuites.add(testSuite);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        return testSuites;
    }

    private String ignoreDoctype(String xml) {
        return xml.replaceAll("<!DOCTYPE \\w+ PUBLIC \"[^<>]+\" \"[^<>]+\">", "");
    }


}
