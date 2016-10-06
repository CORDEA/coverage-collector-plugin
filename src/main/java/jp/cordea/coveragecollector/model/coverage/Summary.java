package jp.cordea.coveragecollector.model.coverage;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * Created by Yoshihiro Tanaka on 2016/10/06.
 */
@Getter
@AllArgsConstructor
public class Summary {

    private CounterType type;

    private int missed;

    private int covered;

    private float coverage;

    public static Summary fromCounter(Counter counter) {
        return new Summary(counter.getType(), counter.getMissed(), counter.getCovered(), counter.calcCoverage());
    }

}
