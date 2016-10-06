package jp.cordea.coveragecollector.model.coverage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Created by Yoshihiro Tanaka on 2016/10/05.
 */
@Root
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Counter {

    @Attribute
    private String type;

    @Attribute
    private int missed;

    @Attribute
    private int covered;

    @Attribute(required = false)
    private float coverage;

    public CounterType getType() {
        return CounterType.valueOf(type);
    }

    public float calcCoverage() {
        coverage = (covered / (float) (missed + covered));
        return coverage;
    }

}
