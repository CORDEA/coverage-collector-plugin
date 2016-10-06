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
public class SourceFile {

    @Attribute
    private String name;

    @ElementList(inline = true)
    private List<Line> lines;

    @ElementList(inline = true)
    private List<Counter> counters;

}
