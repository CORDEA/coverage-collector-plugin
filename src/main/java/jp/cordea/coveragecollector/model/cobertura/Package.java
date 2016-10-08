package jp.cordea.coveragecollector.model.cobertura;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Yoshihiro Tanaka on 2016/10/08.
 */
@Root
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Package {

    @Attribute(name = "branch-rate")
    private float branchRate;

    @Attribute
    private float complexity;

    @Attribute(name = "line-rate")
    private float lineRate;

    @Attribute
    private String name;

    @ElementList
    private List<Class> classes;

}
