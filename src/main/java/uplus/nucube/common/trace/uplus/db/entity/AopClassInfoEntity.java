package uplus.nucube.common.trace.uplus.db.entity;

import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import uplus.nucube.common.trace.uplus.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

import static uplus.nucube.common.trace.uplus.TraceCommonUtil.*;


@Slf4j
@Entity
@Getter @Setter
public class AopClassInfoEntity {

    @Id
    @GeneratedValue
    @Column(name="aopInfo_id")
    Long id;

    String uuid;
    String basePackage ="uplus.nucube";
    int level;
    long durationTimeMs;

    String functionClassAnnotation;

    String classKinds;
    String functionClassName;
    String functionClassSimpleName;
    String functionName;

    String returnTypeName;

    String exceptionClass;

    String exceptionMessage;

    String classObject; //classObject는 Bean임.

    @OneToMany(mappedBy = "aopClassInfoEntity")
    List<InputMetaEntity> inputMetaEntities = new ArrayList<>();

    @OneToMany(mappedBy = "aopClassInfoEntity")
    List<OutputMetaEntity> outputMetaEntities = new ArrayList<>();


    // Spring Bean 필드들이 저장되는 array
    @ElementCollection
    @CollectionTable(name = "classFields", joinColumns = @JoinColumn(name = "aopInfo_id"))
    List<ClassFieldEntity> classFields = new ArrayList<>();

    public void makeInfoEntity(AopClassInfo info) {

       setUuid( info.getUuid() );
       setBasePackage( info.getBasePackage() );
       setLevel( info.getLevel() );
       setDurationTimeMs( info.getDurationTimeMs() );
       setFunctionClassAnnotation( getLimitString( info.getFunctionClassAnnotation()) );
       setClassKinds( info.getClassKinds() );
       setFunctionClassName( info.getFunctionClassName() );
       setFunctionClassSimpleName( info.getFunctionClassSimpleName() );
       setFunctionName( info.getFunctionName() );
       setReturnTypeName( info.getReturnTypeName() );
       setClassObject( getLimitString( info.getClassObject() +"" ));
       setExceptionClass( getLimitString( info.getExceptionClass() ) );
       setExceptionMessage( getLimitString( info.getExceptionMessage() ) );

        List<ClassField> fields = info.getClassFields();

        for (ClassField field : fields) {
            ClassFieldEntity ent = new ClassFieldEntity();
            ClassFieldEntity classFieldEntity = ent.makeFieldEntity( field );
            getClassFields().add( classFieldEntity );
        }
//        log.info( "ClassField =====================================" );
//        for (ClassFieldEntity classField : getClassFields()) {
//            log.info( "classField = {}", classField.getSuper_uuid() );
//        }
//        log.info( "ClassField =====================================" );
    }

    public void printAll() {
        log.info( "id = {}", id );
        log.info( "uuid = {}", uuid );
        log.info( "level = {}", level );
        log.info( "functionClassAnnotation = {}", functionClassAnnotation );
        log.info( "durationTimeMs = {}", durationTimeMs );
        log.info( "classKinds = {}", classKinds );
        log.info( "functionClassName = {}", functionClassName );
        log.info( "functionClassSimpleName = {}", functionClassSimpleName );
        log.info( "functionName = {}", functionName );
        log.info( "returnTypeName = {}", returnTypeName );
        log.info( "exceptionClass = {}", exceptionClass );
        log.info( "classObject = {}", classObject );

        for (InputMetaEntity inputMetaEntity : inputMetaEntities) {
            inputMetaEntity.printAll();
        }

        for (OutputMetaEntity outputMetaEntity : outputMetaEntities) {
            outputMetaEntity.printAll();
        }

        for (ClassFieldEntity classField : classFields) {
            classField.printAll();
        }
    }

}
