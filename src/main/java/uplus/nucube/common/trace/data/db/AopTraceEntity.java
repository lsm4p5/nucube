package uplus.nucube.common.trace.data.db;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import uplus.nucube.common.trace.data.DynamicClass;
import uplus.nucube.common.trace.dto.db.ClientArg;
import uplus.nucube.common.trace.dto.db.ClientInput;
import uplus.nucube.common.trace.dto.db.ClientOutput;
import uplus.nucube.common.trace.dto.db.ClientTest;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

//@Entity
@Getter @Setter
@NoArgsConstructor
public class AopTraceEntity {

    @Id
    @GeneratedValue
    @Column(name="aopTrace_id")
    Long id;
    String uuid;
    String basePackage = "uplus.nucube";
    int level;
    long startTimeMs;
    long durationTimeMs;
    long callArgCount;
    int functionReturnCount ;
    String functionClassType;
    String functionMethod;
    String functionReturnType;
    String functionReturnObject;
    String classAnnotation ="";

    @ElementCollection
    @CollectionTable(name = "dynamicEntity", joinColumns = @JoinColumn(name = "aopTrace_id"))
    Map<DynamicEntity, String> dynamicEntity = new HashMap<>();

    @ElementCollection
    @CollectionTable(name="exceptionClass",joinColumns = @JoinColumn(name="aopTrace_id"))
    public Map<String, String> exceptionClass = new HashMap<>();

    @ElementCollection
    @CollectionTable(name="fields",joinColumns = @JoinColumn(name="aopTrace_id"))
    Map<String, String> fields = new HashMap<>();

    public void addFields(String key, String value) {
        fields.put( key, value );
    }

    public void addDynamicEntity(DynamicEntity entity, String value) {
        dynamicEntity.put( entity, value );
    }

    @ElementCollection
    @CollectionTable(name="fieldsAnnotation",joinColumns = @JoinColumn(name="aopTrace_id"))
    Map<String, String> fieldsAnnotation = new HashMap<>();

    public void addFieldsAnnotation(String key, String value) {
        fieldsAnnotation.put( key, value );
    }
}
