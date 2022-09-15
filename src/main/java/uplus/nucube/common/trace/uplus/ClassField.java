package uplus.nucube.common.trace.uplus;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;


@Data
@Slf4j
@NoArgsConstructor
public class ClassField {

    private final static int MAX_COLLECTION_SIZE = 5;


    int depth;
    String uuid;

    String super_uuid;
    int next_depth;
    int fieldKind; // 1 - Primitive, 2 - Wrapper, 3 - Object , 4-Collect
    Class<?> fieldType;
    String fieldAnnotation;
    String fieldTypeName;

    String fieldTypeFullName;
    String simpleFieldTypeName;
    String fieldVariable;
    Object fieldObject;


    public void printAll() {
        log.info( "ClassField [start] =====================" );
        log.info( "depth ={}", depth );
        log.info( "uuid ={}", uuid );
        log.info( "super_uuid ={}", super_uuid );
        log.info( "next_depth ={}", next_depth );
        log.info( "fieldKind ={}", fieldKind );
        log.info( "fieldType ={}", fieldType );
        log.info( "fieldAnnotation ={}", fieldAnnotation );
        log.info( "fieldTypeName ={}", fieldTypeName );
        log.info( "fieldTypeFullName ={}", fieldTypeFullName );
        log.info( "simpleFieldTypeName ={}", simpleFieldTypeName );
        log.info( "fieldVariable ={}", fieldVariable );
        log.info( "fieldObject ={}", fieldObject );
        log.info( "ClassField [end] =====================" );
    }
}
