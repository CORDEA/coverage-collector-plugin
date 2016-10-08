package jp.cordea.coveragecollector;

import hudson.Extension;
import hudson.FilePath;
import hudson.Launcher;
import hudson.model.AbstractProject;
import hudson.model.Result;
import hudson.model.Run;
import hudson.model.TaskListener;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;
import hudson.util.ListBoxModel;
import jenkins.tasks.SimpleBuildStep;
import jp.cordea.coveragecollector.model.cobertura.Coverage;
import jp.cordea.coveragecollector.model.jacoco.Counter;
import jp.cordea.coveragecollector.model.jacoco.CounterType;
import jp.cordea.coveragecollector.model.jacoco.Report;
import jp.cordea.coveragecollector.model.jacoco.Summary;
import jp.cordea.coveragecollector.model.test.TestCase;
import jp.cordea.coveragecollector.model.test.TestSuite;
import lombok.Getter;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;
import org.simpleframework.xml.Serializer;
import org.simpleframework.xml.core.Persister;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class CollectBuilder extends Recorder implements SimpleBuildStep {

    private static final String OUTPUT_FILE = "cov_result.txt";

    @Getter
    private final String coverageFilePath;

    @Getter
    private final String junitDirPath;

    @Getter
    private final String counterType;

    @Getter
    private final String branch;

    @Getter
    private final String template;

    @DataBoundConstructor
    public CollectBuilder(String coverageFilePath, String junitDirPath, String counterType, String branch, String template) {
        this.coverageFilePath = coverageFilePath;
        this.junitDirPath = junitDirPath;
        this.counterType = counterType;
        this.branch = branch;
        this.template = template;
    }

    private boolean isMaster(Run run, TaskListener listener) {
        try {
            String branchName = run.getEnvironment(listener).get("GIT_BRANCH");
            if (branch.startsWith("*/")) {
                branchName = branchName.replaceFirst("[\\w-]+/", "*/");
            }
            return branchName.equals(branch);
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }

    private List<TestSuite> readTestResult(TaskListener taskListener, FileHelper fileHelper, FilePath filePath) {
        List<TestSuite> testSuites = new ArrayList<>();
        if (junitDirPath == null || junitDirPath.isEmpty()) {
            taskListener.getLogger().println("JUnit results does not exist.");
        } else {
            List<FilePath> filePaths = fileHelper.getFiles(filePath, junitDirPath);
            if (filePaths == null) {
                taskListener.error("Directory is empty, or does not exist.");
                return testSuites;
            }
            testSuites = fileHelper.getXmlFiles(filePaths);
            if (testSuites.isEmpty()) {
                taskListener.error("Xml file does not exist.");
                return testSuites;
            }

            if (testSuites.size() != filePaths.size()) {
                taskListener.getLogger().println(
                        String.format("The number of files is %d. But the number of xml file is %d.", filePaths.size(), testSuites.size()));
            }
        }
        return testSuites;
    }

    private List<TestCase> getFailureTestCases(List<TestSuite> testSuites) {
        List<TestCase> testCases = new ArrayList<>();
        for (TestSuite testSuite : testSuites) {
            for (TestCase testCase : testSuite.getTestCase()) {
                if (testCase.getFailure() != null) {
                    testCases.add(testCase);
                }
            }
        }
        return testCases;
    }

    private void loadTemplate(@Nonnull FilePath parentPath, @Nullable Report report, @Nullable Report masterReport) throws IOException, InterruptedException {
        FilePath path = parentPath.child(OUTPUT_FILE);
        if (path.exists()) {
            path.delete();
        }

        Summary summary = null;
        if (report != null) {
            summary = Summary.fromCounter(report.getCounterByType(CounterType.valueOf(counterType)));
        }
        Summary masterSummary = null;
        if (masterReport != null) {
            masterSummary = Summary.fromCounter(masterReport.getCounterByType(CounterType.valueOf(counterType)));
        }

        String text = new TemplateLoader(report, summary, masterReport, masterSummary).load(template);
        path.write(text, "utf-8");
    }

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener taskListener) throws InterruptedException, IOException {
        Serializer serializer = new Persister();
        FileHelper fileHelper = new FileHelper();

        if (run.getResult() != Result.SUCCESS) {
            List<TestSuite> testSuites = readTestResult(taskListener, fileHelper, filePath);
            if (testSuites != null) {
                List<TestCase> testCases = getFailureTestCases(testSuites);
                if (testCases.size() > 0) {
                    taskListener.getLogger().println("Maybe fails the test. Load the failed test case.");
                    FilePath parentPath = fileHelper.getPluginDir(filePath);
                    if (parentPath == null) {
                        taskListener.error("Failed to directory operations.");
                        return;
                    }
                    FilePath path = parentPath.child(OUTPUT_FILE);
                    if (path.exists()) {
                        path.delete();
                    }
                    String text = new TemplateLoader(testCases).load(template);
                    path.write(text, "utf-8");
                }
            }
            return;
        }

        FilePath file = filePath.child(coverageFilePath);
        if (!file.exists()) {
            taskListener.error("Coverage report file is not found.");
            return;
        }

        Report report = fileHelper.getJacocoReport(file);
        Coverage coverage = fileHelper.getCoberturaReport(file);

        if (report == null && coverage == null) {
            taskListener.error("Coverage report file is not found.");
        }

        if (report != null) {
            for (Counter counter : report.getCounters()) {
                counter.calcCoverage();
            }
        }

        if (isMaster(run, taskListener)) {
            fileHelper.storeMasterFile(filePath, report, coverage);
            return;
        }

        FilePath masterFile = fileHelper.getMasterFile(filePath);
        FilePath parentPath = fileHelper.getPluginDir(filePath);

        if (parentPath == null) {
            taskListener.error("Failed to directory operations.");
            return;
        }

        if (report != null) {
            Report masterReport = null;
            if (masterFile.exists()) {
                try {
                    masterReport = serializer.read(Report.class, masterFile.read());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            loadTemplate(parentPath, report, masterReport);
        }
        if (coverage != null) {
            Coverage masterCoverage = null;
            if (masterFile.exists()) {
                try {
                    masterCoverage = serializer.read(Coverage.class, masterFile.read());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            FilePath path = parentPath.child(OUTPUT_FILE);
            if (path.exists()) {
                path.delete();
            }

            String text = new TemplateLoader(coverage, masterCoverage).load(template);
            path.write(text, "utf-8");
        }
    }

    @Override
    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.NONE;
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        public FormValidation doCheckTemplate(@QueryParameter String value) throws IOException, ServletException {
            if (value.length() == 0) {
                return FormValidation.error("Please set a template.");
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckCoverageFilePath(@QueryParameter String value) throws IOException, ServletException {
            if (value.length() == 0) {
                return FormValidation.error("Please set a xml file.");
            }
            if (!value.endsWith(".xml")) {
                return FormValidation.error("Please set a xml file.");
            }
            return FormValidation.ok();
        }

        public FormValidation doCheckJunitDirPath(@QueryParameter String value) throws IOException, ServletException {
            return FormValidation.ok();
        }

        public FormValidation doCheckBranch(@QueryParameter String value) throws IOException, ServletException {
            if (value.length() == 0) {
                return FormValidation.error("Please set a branch name.");
            }
            if (!value.contains("/")) {
                return FormValidation.warning("Perhaps, branch name is wrong. For example, */master.");
            }
            return FormValidation.ok();
        }

        public ListBoxModel doFillCounterTypeItems() {
            ListBoxModel items = new ListBoxModel();
            for (CounterType type : CounterType.values()) {
                items.add(type.name());
            }
            return items;
        }

        public String getDisplayName() {
            return "Output coverage data";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            return super.configure(req, formData);
        }
    }
}
