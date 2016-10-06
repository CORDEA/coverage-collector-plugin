package jp.cordea.coveragecollector.model.coverage;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.ElementList;
import org.simpleframework.xml.Root;

import java.util.List;

/**
 * Created by Yoshihiro Tanaka on 2016/10/05.
 */
@Root(strict = false)
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Report {

    @Attribute
    private String name;

    @ElementList(inline = true)
    private List<Package> packages;

    @ElementList(inline = true)
    private List<Counter> counters;

    public Counter getCounterByType(CounterType type) {
        for (Counter c : counters) {
            if (c.getType() == type) {
                return c;
            }
        }
        return null;
    }

}
