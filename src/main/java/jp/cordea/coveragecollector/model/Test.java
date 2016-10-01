package jp.cordea.coveragecollector.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Yoshihiro Tanaka on 2016/09/29.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Test {

    @ElementList(entry = "testsuite")
    private List<TestSuite> testSuite;

    @Element
    private TestTotal testTotal;

}
