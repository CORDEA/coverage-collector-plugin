package jp.cordea.coveragecollector.model.test;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.simpleframework.xml.Element;
import org.simpleframework.xml.ElementList;

import java.util.List;

/**
 * Created by Yoshihiro Tanaka on 2016/09/29.
 */
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Test {

    @ElementList(entry = "testcase")
    private List<TestCase> testCases;

    @Element
    private boolean isError;

}
