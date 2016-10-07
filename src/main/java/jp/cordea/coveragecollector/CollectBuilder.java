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
import jp.cordea.coveragecollector.model.coverage.Counter;
import jp.cordea.coveragecollector.model.coverage.CounterType;
import jp.cordea.coveragecollector.model.coverage.Report;
import jp.cordea.coveragecollector.model.coverage.Summary;
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

    @Override
    public void perform(@Nonnull Run<?, ?> run, @Nonnull FilePath filePath, @Nonnull Launcher launcher, @Nonnull TaskListener taskListener) throws InterruptedException, IOException {
        Serializer serializer = new Persister();
        FileHelper fileHelper = new FileHelper();
        List<FilePath> filePaths = fileHelper.getFiles(filePath, path);
        if (filePaths == null) {
            taskListener.error("Directory is empty, or does not exist.");
            return;
        }
        List<TestSuite> testSuites = fileHelper.getXmlFiles(filePaths);
        if (testSuites.isEmpty()) {
            taskListener.error("Xml file does not exist.");
            return;
        }

        if (testSuites.size() != filePaths.size()) {
            taskListener.getLogger().println(
                    String.format("The number of files is %d. But the number of xml file is %d.", filePaths.size(), testSuites.size()));
        }

        TestTotal testTotal = TestTotal.fromTestSuites(testSuites);
        if (isMaster(run, taskListener)) {
            Test test = new Test(testSuites, testTotal);
            fileHelper.storeMasterFile(filePath, test);
            return;
        }

        List<TestSuite> masterTestSuites = null;
        TestTotal masterTestTotal = null;
        FilePath masterFile = fileHelper.getMasterFile(filePath);
        if (masterFile != null && masterFile.exists()) {
            try {
                Test test = serializer.read(Test.class, masterFile.read());
                masterTestSuites = test.getTestSuite();
                masterTestTotal = TestTotal.fromTestSuites(masterTestSuites);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        FilePath parentPath = fileHelper.getPluginDir(filePath);
        if (parentPath == null) {
            taskListener.error("Failed to directory operations.");
            return;
        }
        FilePath path = parentPath.child(OUTPUT_FILE);
        if (path.exists()) {
            path.delete();
        }
        String text = new TemplateLoader(testSuites, masterTestSuites, testTotal, masterTestTotal).load(template);
        path.write(text, "utf-8");
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
