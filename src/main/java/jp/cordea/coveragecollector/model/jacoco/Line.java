package jp.cordea.coveragecollector.model.jacoco;

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
public class Line {

    @Attribute
    private int nr;

    @Attribute
    private int mi;

    @Attribute
    private int ci;

    @Attribute
    private int mb;

    @Attribute
    private int cb;

}
