package jp.cordea.coveragecollector.model;

import lombok.Getter;
import lombok.ToString;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Created by Yoshihiro Tanaka on 2016/09/29.
 */
@Root(strict = false)
@Getter
public class TestCase {

    @Attribute
    private String name;

    @Attribute(name = "classname")
    private String className;

    @Attribute
    private float time;

}
