package uplus.nucube.common.trace.uplus.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import uplus.nucube.common.trace.uplus.ClassField;
import uplus.nucube.common.trace.uplus.TraceCommonUtil;

import javax.persistence.Embeddable;

@Getter @Setter
@Slf4j
@NoArgsConstructor
@Embeddable
public class ClassFieldEntity {

    int depth;
    String uuid;
    String super_uuid;
    int next_depth;
    int fieldKind; // 1 - Primitive, 2 - Wrapper, 3 - Object , 4-Collect

    String fieldAnnotation;
    String fieldTypeName;

    String fieldTypeFullName;
    String simpleFieldTypeName;
    String fieldVariable;
    String fieldObject;

    public ClassFieldEntity makeFieldEntity(ClassField classField) {

        ClassFieldEntity classFieldEntity = new ClassFieldEntity();
        classFieldEntity.setDepth( classField.getDepth() );
        classFieldEntity.setUuid( classField.getUuid() );
        classFieldEntity.setSuper_uuid( classField.getSuper_uuid() );
        classFieldEntity.setNext_depth( classField.getNext_depth() );
        classFieldEntity.setFieldKind( classField.getFieldKind() );
        classFieldEntity.setFieldAnnotation( TraceCommonUtil.getLimitString( classField.getFieldAnnotation() ) );
        classFieldEntity.setFieldTypeName( classField.getFieldTypeName() );
        classFieldEntity.setFieldTypeFullName( classField.getFieldTypeFullName() );
        classFieldEntity.setSimpleFieldTypeName( classField.getSimpleFieldTypeName() );
        classFieldEntity.setFieldVariable( classField.getFieldVariable() );
        classFieldEntity.setFieldObject( TraceCommonUtil.getLimitString(classField.getFieldObject()+"" ));
        return classFieldEntity;
    }

    public void printAll() {
        log.info("ClassField Print -----------------");
        log.info( "depth ={}, next_depth={}", depth ,next_depth);
        log.info( "uuid ={}", uuid );
        log.info( "super_uuid ={}", super_uuid );
        log.info( "fieldKind ={}", fieldKind );
        log.info( "fieldAnnotation ={}", fieldAnnotation );
        log.info( "fieldTypeName ={}", fieldTypeName );
        log.info( "fieldTypeFullName ={}", fieldTypeFullName );
        log.info( "simpleFieldTypeName ={}", simpleFieldTypeName );
        log.info( "fieldVariable ={}", fieldVariable );
        log.info( "fieldObject ={}", fieldObject );
        log.info("ClassField Print -----------------");
    }

}
