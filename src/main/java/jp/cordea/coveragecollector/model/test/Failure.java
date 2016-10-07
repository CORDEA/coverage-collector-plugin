package jp.cordea.coveragecollector.model.test;

import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Created by Yoshihiro Tanaka on 2016/10/05.
 */
@Root
public class Failure {

    @Attribute
    private String message;

    @Attribute
    private String type;

    private String text;
}
