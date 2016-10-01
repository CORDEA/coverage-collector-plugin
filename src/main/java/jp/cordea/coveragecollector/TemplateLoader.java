package jp.cordea.coveragecollector;

import com.mitchellbosecke.pebble.PebbleEngine;
import com.mitchellbosecke.pebble.error.PebbleException;
import com.mitchellbosecke.pebble.loader.StringLoader;
import com.mitchellbosecke.pebble.template.PebbleTemplate;
import jp.cordea.coveragecollector.model.TestSuite;
import jp.cordea.coveragecollector.model.TestTotal;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Yoshihiro Tanaka on 2016/09/30.
 */
public class TemplateLoader {

    private List<TestSuite> testSuites;
    private List<TestSuite> masterTestSuites;
    private TestTotal testTotal;
    private TestTotal masterTestTotal;

    public TemplateLoader(List<TestSuite> testSuites, List<TestSuite> masterTestSuites, TestTotal testTotal, TestTotal masterTestTotal) {
        this.testSuites = testSuites;
        this.masterTestSuites = masterTestSuites;
        this.testTotal = testTotal;
        this.masterTestTotal = masterTestTotal;
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
        context.put("testSuites", testSuites);
        context.put("testTotal", testTotal);
        if (masterTestSuites != null) {
            context.put("masterTestSuites", masterTestSuites);
            context.put("masterTestTotal", masterTestTotal);
        }

        try {
            template.evaluate(writer, context);
            return writer.toString();
        } catch (PebbleException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

}
