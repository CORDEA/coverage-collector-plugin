package jp.cordea.coveragecollector;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import jp.cordea.coveragecollector.model.cobertura.Coverage;
import jp.cordea.coveragecollector.model.jacoco.Report;
import jp.cordea.coveragecollector.model.jacoco.Summary;
import jp.cordea.coveragecollector.model.test.TestCase;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yoshihiro Tanaka on 2016/09/30.
 */
public class TemplateLoader {

    private Report report;

    private Summary summary;

    private Report masterReport;

    private Summary masterSummary;

    private Coverage coverage;

    private Coverage masterCoverage;

    private List<TestCase> failureTestCases;

    public TemplateLoader(Report report, Summary summary, Report masterReport, Summary masterSummary) {
        this.report = report;
        this.summary = summary;
        this.masterReport = masterReport;
        this.masterSummary = masterSummary;
    }

    public TemplateLoader(Coverage coverage, Coverage masterCoverage) {
        this.coverage = coverage;
        this.masterCoverage = masterCoverage;
    }

    public TemplateLoader(List<TestCase> failureTestCases) {
        this.failureTestCases = failureTestCases;
    }

    public String load(String templateString) {
        PebbleEngine engine = new PebbleEngine.Builder().loader(new StringLoader()).build();
        PebbleTemplate template;
        try {
            template = engine.getTemplate(templateString);
        } catch (PebbleException e) {
            return null;
        }
        StringWriter writer = new StringWriter();

        Map<String, Object> context = new HashMap<>();
        if (report != null) {
            context.put("report", report);
            context.put("summary", summary);
        }
        if (masterReport != null) {
            context.put("masterReport", masterReport);
            context.put("masterSummary", masterSummary);
        }
        if (coverage != null) {
            context.put("coverage", coverage);
        }
        if (masterCoverage != null) {
            context.put("masterCoverage", masterCoverage);
        }
        if (failureTestCases != null) {
            context.put("failureTestCases", failureTestCases);
        }
        boolean failure = false;
        if (report == null && masterReport == null) {
            failure = true;
        }
        context.put("failure", failure);

        try {
            template.evaluate(writer, context);
            return writer.toString();
        } catch (PebbleException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
