package jp.cordea.coveragecollector.model.test;

import lombok.Getter;
import lombok.ToString;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Yoshihiro Tanaka on 2016/09/29.
 */
@Root(strict = false)
@Getter
@ToString
public class TestSuite {

    @Attribute
    private String name;

    @Attribute
    private int tests;

    @Attribute
    private int skipped;

    @Attribute
    private int errors;

    @Attribute
    private int failures;

    @Attribute
    private String timestamp;

    @Attribute(name = "hostname")
    private String hostName;

    @Attribute
    private float time;

    @ElementList(entry = "testcase", inline = true)
    private List<TestCase> testCase;

}
