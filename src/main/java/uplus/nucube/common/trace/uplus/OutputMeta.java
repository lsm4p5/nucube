package uplus.nucube.common.trace.uplus;

import lombok.Data;

import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.List;

@Data
public class OutputMeta {
    String uuid;
    int outputKinds;// 1 - Primitive, 2 - Wrapper, 3 - Object , 4-Collect
    Class<?> classType;
    String classTypeName;
    String simpleTypeName;
    String variable;
    Object outputObject;
    List<ClassField> fields = new ArrayList<>();

    public OutputMeta(Object obj, String uuid) {
        this.uuid = uuid;
        this.classType = obj.getClass();
        this.classTypeName = obj.getClass().getTypeName();
        this.simpleTypeName = obj.getClass().getSimpleName();
        this.outputObject = obj;
        this.variable = obj.getClass().getName();
    }
}
