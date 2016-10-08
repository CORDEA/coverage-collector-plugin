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
public class Line {

    @Attribute
    private boolean branch;

    @Attribute
    private long hits;

    @Attribute
    private int number;

    @Attribute(name = "condition-coverage")
    private String conditionCoverage;

    @ElementList(required = false)
    private List<Condition> conditions;

}
