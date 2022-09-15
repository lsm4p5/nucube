package uplus.nucube.common.trace.uplus;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

@Data
@Slf4j
public class InputMeta {
    String uuid;
    int inputKinds;// 1 - Primitive, 2 - Wrapper, 3 - Object , 4-Collect
    Class<?> classType;
    String classTypeName;
    String simpleTypeName;
    String variable;
    Object inputObject;
    public List<ClassField> fields = new ArrayList<>();

    public InputMeta(Parameter parameter,Object obj, String uuid) {
        this.uuid = uuid;
        this.classType = parameter.getType();
        this.classTypeName = parameter.getType().getTypeName();
        this.simpleTypeName = parameter.getType().getSimpleName();
        this.variable = parameter.getName();
        this.inputObject = obj;
    }

    public void addFields(ClassField field) {
        fields.add( field );
    }

    public void printAll() {

        log.info("uuid = {}", uuid);
        log.info("classType = {}", classType);
        log.info("classTypeName = {}", classTypeName);
        log.info("simpleTypeName = {}", simpleTypeName);
        log.info("variable = {}", variable);
        log.info("inputObject = {}", inputObject);
        for (ClassField field : fields) {
            field.printAll();
        }
    }
}
