package uplus.nucube.common.trace.data.db;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embeddable;
import javax.persistence.JoinColumn;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Embeddable
@Data
@NoArgsConstructor
public class DynamicEntity {
    String classType;
    String classObject;
    String typeName;
    String typeVariableName;
    String objectValue;
    int objectDelimiter ;  // 1이면 arg, 2이면 retrun
    int objectType ;  // 1이면 primitive, 2이면 Wrapper 3이면 일반 객체 , 4이면 컬렉션
    String dynamicClassAnnotation = "";
}
