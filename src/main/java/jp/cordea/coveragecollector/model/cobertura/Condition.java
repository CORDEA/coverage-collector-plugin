package jp.cordea.coveragecollector.model.cobertura;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.simpleframework.xml.Attribute;
import org.simpleframework.xml.Root;

/**
 * Created by Yoshihiro Tanaka on 2016/10/08.
 */
@Root
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class Condition {

    @Attribute
    private String coverage;

    @Attribute
    private int number;

    @Attribute
    private String type;

}
