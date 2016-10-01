package jp.cordea.coveragecollector.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.simpleframework.xml.Attribute;

import java.util.List;

/**
 * Created by Yoshihiro Tanaka on 2016/09/30.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class TestTotal {

    @Attribute
    private int tests;

    @Attribute
    private int skipped;

    @Attribute
    private int errors;

    @Attribute
    private int failures;

    @Attribute
    private float coverage;

    public static TestTotal fromTestSuites(List<TestSuite> testSuites) {
        int tests = 0;
        int failures = 0;
        int errors = 0;
        int skipped = 0;
        for (TestSuite testSuite : testSuites) {
            tests += testSuite.getTests();
            failures += testSuite.getFailures();
            errors += testSuite.getErrors();
            skipped += testSuite.getSkipped();
        }

        return new TestTotal(tests, skipped, errors, failures, (1f - ((float)(failures + errors + skipped) / tests)));
    }

}
