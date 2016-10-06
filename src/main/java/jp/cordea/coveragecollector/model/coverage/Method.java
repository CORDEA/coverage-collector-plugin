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
@Root
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Method {

    @Attribute
    private String name;

    @Attribute
    private String desc;

    @Attribute
    private int line;

    @ElementList(inline = true)
    private List<Counter> counters;

}
