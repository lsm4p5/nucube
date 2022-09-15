package uplus.nucube.common.trace.data;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import uplus.nucube.common.trace.TraceStatus;
import uplus.nucube.common.trace.data.db.AopTraceEntity;
import uplus.nucube.common.trace.data.db.DynamicEntity;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@Data
@Slf4j
public class AopTraceData {


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

    Map<DynamicClass, String> dynamicClass = new HashMap<>();

    public Map<String, String> exceptionClass = new HashMap<>();
    public void addFunctionMap(DynamicClass dynamicObject, String value) {
        dynamicClass.put( dynamicObject, value );
    }

    public void addExceptionClass(String key, String value) {
        exceptionClass.put( key, value );
    }
    public void makeMethodInfo(ProceedingJoinPoint joinPoint, TraceStatus status) {
        makeHeader( status );
        makeFunctionInfo( joinPoint );
    }

    private void makeFunctionInfo(ProceedingJoinPoint joinPoint) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?> classType = signature.getDeclaringType();
        Class<?> returnType = signature.getReturnType();

        setFunctionClassType( classType+"" );
        setFunctionReturnType( returnType+"" );



        Method method = signature.getMethod();
        String methodName = method.getName();
        setFunctionMethod( methodName );
        Annotation[] annotations = classType.getAnnotations();
        for (Annotation annotation : annotations) {
            classAnnotation += annotation;
        }

        Parameter[] parameters = method.getParameters();
        Object[] argsValues = joinPoint.getArgs();
        setCallArgCount( argsValues.length );
        int i=0;
        for (Parameter parameter : parameters) {
            Class<?> type = parameter.getType();
            String typeName = parameter.getType().getName();
            String paraName = parameter.getName();
            Object object = argsValues[i];
          /**
          * 객체의 필드를 저장합니다.
          */
            DynamicClass dynamicObject = new DynamicClass();
            dynamicObject.makeDynamicClass(object,type,1);
            addFunctionMap( dynamicObject,typeName+"."+paraName);
          //  dynamicObject.printDynamicClass();
            i++;
        }

    }

    private void makeHeader(TraceStatus status) {
        setUuid( status.getTraceId().getId() );
        setLevel( status.getTraceId().getLevel() );
        setStartTimeMs( status.getStartTimeMs() );
    }

    public void printAopTraceData() {

        log.info("AopTraceData ======== start ============");
        log.info( "AopTraceData - uuid ={}", uuid );
        log.info( "AopTraceData - basePackage ={}", basePackage );
        log.info( "AopTraceData - level ={}", level );
        log.info( "AopTraceData - startTimeMs ={}", startTimeMs );
        log.info( "AopTraceData - durationTimeMs ={}", durationTimeMs );
        log.info( "AopTraceData - callArgCount ={}", callArgCount );
        log.info( "AopTraceData - functionReturnCount ={}", functionReturnCount );
        log.info( "AopTraceData - functionClassType ={}", functionClassType );
        log.info( "AopTraceData - functionMethod ={}", functionMethod );
        log.info( "AopTraceData - functionReturnType ={}", functionReturnType );
        log.info( "AopTraceData - functionReturnObject ={}", functionReturnObject );
        log.info( "AopTraceData - classAnnotation ={}", classAnnotation );

        for (DynamicClass aClass : dynamicClass.keySet()) {
            aClass.printDynamicClass();
        }
        for (String s : exceptionClass.keySet()) {
            log.info( "AopTraceData - exception ={} , value ={}", s, exceptionClass.get( s ) );
        }
        log.info("AopTraceData ======== end ============");
    }


    public AopTraceEntity makeAopTraceEntityData(DynamicClass dynamicClass) {
        AopTraceEntity traceEntity = new AopTraceEntity();
        traceEntity.setUuid( getUuid() );
        traceEntity.setBasePackage( getBasePackage() );
        traceEntity.setLevel( getLevel() );
        traceEntity.setStartTimeMs( getStartTimeMs() );
        traceEntity.setDurationTimeMs( getDurationTimeMs() );
        traceEntity.setCallArgCount( getCallArgCount() );
        traceEntity.setFunctionReturnCount( getFunctionReturnCount() );
        traceEntity.setFunctionClassType( getFunctionClassType() );
        traceEntity.setFunctionMethod( getFunctionMethod() );
        traceEntity.setFunctionReturnType( getFunctionReturnType() );
        traceEntity.setFunctionReturnObject( getFunctionReturnObject() );
        traceEntity.setClassAnnotation( getLimitString(getClassAnnotation()) );

        DynamicEntity dynamicEntity = new DynamicEntity();
        dynamicEntity.setClassType(getLimitString(dynamicClass.getClassType()+""));
        dynamicEntity.setClassObject( getLimitString(dynamicClass.getClassObject()+""));
        dynamicEntity.setTypeName( getLimitString(dynamicClass.getTypeName()));
        dynamicEntity.setTypeVariableName( getLimitString(dynamicClass.getTypeVariableName()) );
        dynamicEntity.setObjectValue( getLimitString(dynamicClass.getObjectValue()));
        dynamicEntity.setObjectDelimiter(dynamicClass.getObjectDelimiter());
        dynamicEntity.setObjectType(dynamicClass.getObjectType());
        dynamicEntity.setDynamicClassAnnotation( getLimitString(dynamicClass.getDynamicClassAnnotation()) );

        traceEntity.addDynamicEntity( dynamicEntity,getLimitString(dynamicEntity +"" ));

        Map<String, Object> fields = dynamicClass.getFields();
        for (String s : fields.keySet()) {
            traceEntity.addFields( s, fields.get(s)+"");
        }
        Map<String, String> fieldsAnnotation = dynamicClass.getFieldsAnnotation();
        for (String s : fieldsAnnotation.keySet()) {
            traceEntity.addFieldsAnnotation( s,fieldsAnnotation.get(s) );
        }
        return traceEntity;
    }

    public String getLimitString(String str) {
        if (str == null) {
            return str;
        }
        log.info( "str = {}, str.length()={}", str, str.length() );
        if (str.length() > 255) {
            return str.substring( 0, 255 );
        }
        return str;
    }
}
