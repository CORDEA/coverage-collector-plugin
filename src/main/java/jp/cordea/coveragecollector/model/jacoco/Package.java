package jp.cordea.coveragecollector.model.jacoco;

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
public class Package {

    @Attribute
    private String name;

    @ElementList(inline = true)
    private List<Class> classes;

    @ElementList(entry = "sourcefile", name = "sourcefiles", inline = true, type = SourceFile.class)
    private List<SourceFile> sourceFiles;

    @ElementList(inline = true)
    private List<Counter> counters;

}
