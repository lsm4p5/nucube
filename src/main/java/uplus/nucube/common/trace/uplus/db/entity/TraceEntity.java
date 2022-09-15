package uplus.nucube.common.trace.uplus.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import uplus.nucube.common.trace.uplus.TraceCommonUtil;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Slf4j
public class TraceEntity {

    @Id
    @GeneratedValue
    @Column(name = "traceEntity_id")
    Long id;

    int Level;
    String className;
    String singleClassName;
    String FunctionName;
    String uuid;
    String annotation;

    String classKinds;
    long durationTimeMs;
    String returnType;
    String exceptionClass;
    String exceptionMessage;
    String ClassObject;
    String outputObject;

    int argSize ;


    @ElementCollection
    @CollectionTable(name = "traceArg", joinColumns = @JoinColumn(name = "traceEntity_id"))
    public List<TraceArg> args = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="traceDataInfo_id")
    private TraceDataInfoEntity traceDataInfoEntity;

    public void makeTraceEntity(AopClassInfoEntity aopClassInfoEntity) {
        setLevel( aopClassInfoEntity.getLevel() );
        setClassName( aopClassInfoEntity.getFunctionClassName() );
        setSingleClassName( aopClassInfoEntity.getFunctionClassSimpleName() );
        setFunctionName( aopClassInfoEntity.getFunctionName() );
        setUuid( aopClassInfoEntity.getUuid() );
        setAnnotation( aopClassInfoEntity.getFunctionClassAnnotation() );
        setDurationTimeMs( aopClassInfoEntity.getDurationTimeMs() );
        setReturnType( TraceCommonUtil.lastIndexOf(aopClassInfoEntity.getReturnTypeName()) );
        setExceptionClass( TraceCommonUtil.lastIndexOf(aopClassInfoEntity.getExceptionClass()) );
        setExceptionMessage( aopClassInfoEntity.getExceptionMessage() );
        setClassObject( TraceCommonUtil.lastIndexOf(aopClassInfoEntity.getClassObject()) );
        setClassKinds( aopClassInfoEntity.getClassKinds() );

        List<InputMetaEntity> inputMetaEntities = aopClassInfoEntity.getInputMetaEntities();
        log.info( "============================={}",inputMetaEntities.size() );
        for (InputMetaEntity inputMetaEntity : inputMetaEntities) {
            log.info( "inputMetaEntity. = {}", inputMetaEntity.getInputObject() );
            if (inputMetaEntity.getUuid().equals( this.getUuid() )) {
                //Arg를 setting하여 준다.
                TraceArg arg = new TraceArg();
                arg.setArgClass( inputMetaEntity.getClassTypeName() );
                arg.setArgVariable( inputMetaEntity.getVariable() );
                arg.setArgObject( TraceCommonUtil.lastIndexOf(inputMetaEntity.getInputObject()) );
                arg.setSimpleArgClass( inputMetaEntity.getSimpleTypeName() );
                arg.setSimpleArgObject( TraceCommonUtil.lastIndexOf(inputMetaEntity.getInputObject()) );
                getArgs().add(arg);
                log.info( "++++++++++++++++++++++++++++++" );
                for (TraceArg traceArg : getArgs()) {
                    traceArg.print();
                }
                log.info( "++++++++++++++++++++++++++++++" );
               // break;
            }
        }

        setArgSize( getArgs().size());

        log.info( "=============================" );
        List<OutputMetaEntity> outputMetaEntities = aopClassInfoEntity.getOutputMetaEntities();
        for (OutputMetaEntity outputMetaEntity : outputMetaEntities) {
            if(outputMetaEntity.getUuid().equals( this.getUuid() )){
                setOutputObject( outputMetaEntity.getOutputObject() );
              //  break;
            }
        }
    }

    public void printAll() {

        log.info("id = {}", id);
        log.info("Level = {}", Level);
        log.info("className = {}", className);
        log.info("singleClassName = {}", singleClassName);
        log.info("FunctionName = {}", FunctionName);
        log.info("uuid = {}", uuid);
        log.info("annotation = {}", annotation);
        log.info("durationTimeMs = {}", durationTimeMs);
        log.info("returnType = {}", returnType);
        log.info("exceptionClass = {}", exceptionClass);
        log.info("exceptionMessage = {}", exceptionMessage);
        log.info("ClassObject = {}", ClassObject);
        log.info("outputObject = {}", outputObject);
        for (TraceArg arg : args) {
            log.info( "arg.getArgObject() ={}", arg.getArgObject() );
            log.info( "arg.getArgClass() ={}", arg.getArgClass() );
            log.info( "arg.getArgVariable() ={}", arg.getArgVariable() );
        }

    }


}
