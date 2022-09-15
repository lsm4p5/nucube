package uplus.nucube.common.trace.dto;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import uplus.nucube.common.trace.TraceStatus;
import uplus.nucube.common.trace.dto.db.ClientArg;
import uplus.nucube.common.trace.dto.db.ClientEntity;
import uplus.nucube.common.trace.dto.db.ClientInput;
import uplus.nucube.common.trace.dto.db.ClientOutput;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@Data
@Slf4j
@NoArgsConstructor
@JsonSerialize
public class TraceInOutInfoOld {

    String uuid;

    String basePackage = "uplus.nucube";

    int level;

    long startTimeMs;
//
    long durationTimeMs;

    long callArgCount;

    String callClassType;

    String classAnnotation ;
    String callMethodName;
    String callReturnType;
    String callReturnTypeDepth1;

    public static final int Max_Collection_Size=10;
    public static final String Type_Name_Delimiter ="|";

    public static final String[] ArgReturnFlag={"ARG","RETURN"};


    // argClass(type.variable=String .object = object.value)
    public Map<String, Object> argClass = new HashMap<>();

    // objClass(FieldType.FieldName.FieldVariable = String, Object.value)
    public Map<String, Object> objClass_depth1 = new HashMap<>();

    public Map<String, Object> objClass_depth2 = new HashMap<>();

    public Map<String, Object> objClass_depth3 = new HashMap<>();

    public Map<String, Object> retClass_depth1 = new HashMap<>();

    public Map<String, Object> retClass_depth2 = new HashMap<>();

    public Map<String, Object> retClass_depth3 = new HashMap<>();

    public Map<String, String> exceptionClass = new HashMap<>();

    public Map<String, Object> argAnnotation = new HashMap<>();

    public Map<String, Object > fieldAnnotation = new HashMap<>();
    public Map<String, Object> returnAnnotation = new HashMap<>();

    public Map<Object, String> objectValue1 = new HashMap<>();

    public Map<Object, String> paraValue1 = new HashMap<>();

    public Map<Object, String> objectValue2 = new HashMap<>();

    public Map<Object, String> paraValue2 = new HashMap<>();

    public Map<Object, String> objectValue3 = new HashMap<>();

    public Map<Object, String> paraValue3 = new HashMap<>();
    public Map<Object, String> objectValue4 = new HashMap<>();

    public Map<Object, String> paraValue4 = new HashMap<>();

    private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

    public void addArgClass(String name, Object object) {
        argClass.put( name, object );
    }

    public void addClass_depth1(String name, Object object) {
        objClass_depth1.put( name, object );
    }

    public void addClass_depth2(String name, Object object) {
        objClass_depth2.put( name, object );
    }
    public void addClass_depth3(String name, Object object) {
        objClass_depth3.put( name, object );
    }

    public void addClassFirst(String name, Object object,String argReturnFlag) {
        if(argReturnFlag != null && argReturnFlag.equals(ArgReturnFlag[0] )){
            objClass_depth1.put("argFIRST-"+ name, object );
        }
        if(argReturnFlag != null && argReturnFlag.equals(ArgReturnFlag[1] )){
            retClass_depth1.put("retFIRST-"+ name, object );
        }
    }
    public void addClassSecond(String name, Object object,String argReturnFlag) {
        if(argReturnFlag != null && argReturnFlag.equals(ArgReturnFlag[0] )){
            objClass_depth2.put( "argSecond-" + name, object );
        }
        if(argReturnFlag != null && argReturnFlag.equals(ArgReturnFlag[1] )){
            retClass_depth2.put( "retSecond-" +name, object );
        }
    }
    public void addClassThird(String name, Object object,String argReturnFlag) {
        if(argReturnFlag != null && argReturnFlag.equals(ArgReturnFlag[0] )){
            objClass_depth3.put( "argThird-"+ name, object );
        }
        if(argReturnFlag != null && argReturnFlag.equals(ArgReturnFlag[1] )){
            retClass_depth3.put("retThird-" +  name, object );
        }
    }
    public void addRetClass_depth1(String name, Object object) {
        retClass_depth1.put( name, object );
    }

    public void addRetClass_depth2(String name, Object object) {
        retClass_depth2.put( name, object );
    }
    public void addRetClass_depth3(String name, Object object) {
        retClass_depth3.put( name, object );
    }


    public void addArgAnnotation(String field, Object obj) {
        argAnnotation.put( field, obj );
    }



    public void addReturnAnnotation(String field, Object object) {
        returnAnnotation.put( field, object );
    }

    public void addExceptionClass(String name, String value) {
       exceptionClass.put( name, value );
    }

    public void addObjectValue1(Object object, String name) {
        objectValue1.put( object, name );
    }

    public void addParaValue1(Object object, String name) {
        paraValue1.put( object, name );
    }

    public void addObjectValue2(Object object, String name) {
        objectValue2.put( object, name );
    }

    public void addParaValue2(Object object, String name) {
        paraValue2.put( object, name );
    }

    public void addObjectValue3(Object object, String name) {
        objectValue3.put( object, name );
    }

    public void addParaValue3(Object object, String name) {
        paraValue3.put( object, name );
    }

    public void addObjectValue4(Object object, String name) {
        objectValue4.put( object, name );
    }

    public void addParaValue4(Object object, String name) {
        paraValue4.put( object, name );
    }

    public void addFieldAnnotation(String name , Object object) {
        fieldAnnotation.put( name, object );
    }
    public void callMethodInfo(ProceedingJoinPoint joinPoint, TraceStatus status) {

        // Level을 남긴다.
        setUuid( status.getTraceId().getId() );
        setLevel( status.getTraceId().getLevel() );
        setStartTimeMs( status.getStartTimeMs() );
        setClassAnnotation( "" );

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?> aClass = signature.getDeclaringType();
        Class<?> returnType = signature.getReturnType();
        String[] parameterNames = signature.getParameterNames();
        Annotation[] annotations = aClass.getAnnotations();


        Method method = signature.getMethod();
        String methodName = method.getName();
//        log.info("==================callMethod info[Start]==========================");
//        log.info("callClassType = {}", aClass );
//        log.info( "returnType = {}", returnType );

        for (Annotation annotation : annotations) {
            classAnnotation += annotation;
        }
//        log.info( "method = {}", method );
//        log.info( "methodName = {}", methodName );

        Parameter[] parameters = method.getParameters();
        Object[] argsValues = joinPoint.getArgs();

        setCallClassType( aClass +"" );
        setCallArgCount( argsValues.length );
        setCallMethodName( methodName );
        setCallReturnType(returnType+"");

        int i=0;
        for (Parameter parameter : parameters) {

            Class<?> type = parameter.getType();
            String typeName = parameter.getType().getName();
            String paraName = parameter.getName();
            Object object = argsValues[i];


       //     log.info("type = {}, typeName={},paraName={}", type, typeName,paraName);

            String type_name_value = typeName+ Type_Name_Delimiter + paraName;
            addArgClass( type_name_value, object );

            /**
             *  object와 para value를 저장한다.
             */
            addObjectValue1( object, typeName );
            addParaValue1( object, paraName );



            // Arg의 필드에 대한 annotation  처리한다.
            String annotation1 = getAnnotation(type,object,"ARG");

            makeDepthFirstArg(type,typeName,paraName, object,"ARG");

            i++;
        }
    }

    public void makeDepthFirstArg(Class<?> type,String typeName, String paraName, Object object,String argReturnFlag) {

        if (object == null) {
            addClassFirst( "object=null " + getTypeNameValue( typeName, paraName ), null ,argReturnFlag);
            return ;
        }

        if(type.isPrimitive() || isWrapperType(type)){
            addClassFirst( "PRIMITIVE " + getTypeNameValue( typeName,paraName ), object ,argReturnFlag);
            addObjectValue2( object, typeName );
            addParaValue2( object, paraName );
            return;
        }

        /*
           Collection  10개 까지만 처리한다.
         */
        if (object instanceof Collection<?>) {
            Collection<Object> collect = (Collection<Object>) object;
            addClassFirst("LIST1 " + getTypeNameValue( typeName,paraName ), object,argReturnFlag );
            addObjectValue2( object, object.getClass().getTypeName() );
            addParaValue2( object, object.getClass().getName() );
            int i =0;
            for (Object o : collect) {
                if (i == 0) {
                    setCallReturnTypeDepth1( o.getClass().getTypeName() );
                    getAnnotation( o.getClass(), o, "Field" );
                    addObjectValue3( o, o.getClass().getTypeName() );
                    addParaValue3( o, o.getClass().getName() );

                }
                if (i == Max_Collection_Size) {
                    break;
                }
                String typeName2 = o.getClass().getTypeName();
                String paraName2 = o.getClass().getName();
                try {
//                    addObjectValue2( o, typeName2 );
//                    addParaValue2( o, paraName2 );
                    //makeDepthSecondArg( "List1-" +i+" "+o.getClass().getTypeName(),o.getClass().getName(), o, object);
                    makeDepthSecondArg( "List1-" +i+" "+typeName2,paraName2, o, object,argReturnFlag);
                    //log.info( "================ {} =={} ", o.getClass().getTypeName(), o.getClass().getName() );

                } catch (Exception e) {
                    log.info( " exception ={} ", e.getMessage() );
                }
                i++;

            }
            return;
        }else{
           // log.info( "객체 처리 =========------ {}", getTypeNameValue( typeName,paraName ) );
            getAnnotation( type, object, "Field" );
            addObjectValue2( object, typeName );
            addParaValue2( object, paraName);

            addClassFirst( getTypeNameValue( typeName,paraName ), object,argReturnFlag );
            makeDepthSecondArg( typeName,paraName,object,null,argReturnFlag);
        }



    }

    private String getTypeNameValue(String typeName, String paraName) {
        String uuid_seq = UUID.randomUUID().toString().substring( 0,8 );
        String type_name_value = typeName +Type_Name_Delimiter + paraName +Type_Name_Delimiter + uuid_seq;
        return type_name_value;
    }

    public void makeDepthSecondArg(String typeName, String paraName,Object object,Object before_object,String argReturnFlag) {

        if (object == null) {
            addClassSecond( "object=null " + getTypeNameValue( typeName,paraName ), null,argReturnFlag);
            return ;
        }
        for(Field field : object.getClass().getDeclaredFields()) {;
            Object obj;
            Class<?> type;
            String typeName2 ;
            String paraName2 ;


            try {
                typeName2 = field.getType().getTypeName() ;
                paraName2 = field.getName();
                try{
                    field.setAccessible(true);
                }catch(Exception field_exception){
                    log.info( "Field Access Deny = {}", field );
                    addClassSecond( object +"."+ getTypeNameValue( typeName2,paraName2 ), "ACCESS DENY",argReturnFlag);
                    addExceptionClass(object +"." + getTypeNameValue( typeName2,paraName2 ),field_exception.getMessage() );
                    addObjectValue3( object, typeName2 );
                    addParaValue3( object, paraName2);

                    continue;
                }
                obj = field.get( object );
                if (obj == null) {
                    addClassSecond( "Field-obj=null " + object +"." +getTypeNameValue( typeName2,paraName2 ), null,argReturnFlag);
                    addObjectValue3( object, typeName2 );
                    addParaValue3( object, paraName2);
                    continue;
                }
                type = obj.getClass();
                addObjectValue3( obj, typeName2 );
                addParaValue3( obj, paraName2);

                if(type.isPrimitive() || isWrapperType(type)){
//                    log.info( "isWrapperType(type){}========= {}",isWrapperType(type), type_name_value );
                    addClassSecond( "PRIMITIVE " + object+"." + getTypeNameValue( typeName2,paraName2 ), obj ,argReturnFlag);

                    continue;
                }

                if(obj instanceof Collection<?>){
                    addClassSecond("LIST2 "+ object + "." + getTypeNameValue( typeName2,paraName2 )+"", obj,argReturnFlag );
                    addObjectValue4( obj, typeName2 );
                    addParaValue4( obj, paraName2);
                    continue;
                }else{
                    // field가 객체인 경우는 추가로 다시갈지에 대해서는 추가 작업이 필요합니다.

                    addClassSecond( "OBJECT " +object +"." + getTypeNameValue( typeName2, paraName2 ), obj,argReturnFlag );
                    getAnnotation( obj.getClass(), obj, "Field" );
                    makeDepthThirdArg( typeName2,paraName2,obj,object,argReturnFlag);

                }
            } catch (Exception e) {
                log.info( "Eeception e ={}", e.getMessage());
                continue;
            }
        }
    }


    public void makeDepthThirdArg(String typeName, String paraName,Object object,Object before_object,String argReturnFlag) {

        if (object == null) {
            addClassThird( "object=null " + before_object +"." + getTypeNameValue( typeName,paraName ), null,argReturnFlag);
            return ;
        }
        for(Field field : object.getClass().getDeclaredFields()) {;
            Object obj;
            Class<?> type;
            String typeName2 ;
            String paraName2 ;
            try {
                typeName2 = field.getType().getTypeName() ;
                paraName2 = field.getName();
                try{
                    field.setAccessible(true);
                }catch(Exception field_exception){
                    log.info( "Field Access Deny = {}", field );
                    addObjectValue3( object, typeName2 );
                    addParaValue3( object, paraName2);
                    addClassThird( before_object +"." + object +"." + getTypeNameValue( typeName2,paraName2 ), "ACCESS DENY",argReturnFlag);
                    addExceptionClass("Third- " + before_object +"." + object +"." + getTypeNameValue( typeName2,paraName2 ),field_exception.getMessage() );
                    continue;
                }
                obj = field.get( object );
                addObjectValue4( obj, typeName2 );
                addParaValue4( obj, paraName2);
                if (obj == null) {
                    addClassThird( "Field-obj=null " +before_object +"." + object +"." +getTypeNameValue( typeName2,paraName2 ), null,argReturnFlag);

                    continue;
                }
                type = obj.getClass();

                if(type.isPrimitive() || isWrapperType(type)){
//                    log.info( "isWrapperType(type){}========= {}",isWrapperType(type), type_name_value );
                    addClassThird( "PRIMITIVE " + before_object +"." + object+"." + getTypeNameValue( typeName2,paraName2 ), obj ,argReturnFlag);

                    continue;
                }

                if(obj instanceof Collection<?>){
                    addClassThird("LIST3 "+ before_object +"." +object + "." + getTypeNameValue( typeName2,paraName2 )+"", obj,argReturnFlag );

                    continue;
                }else{
                    // field가 객체인 경우는 추가로 다시갈지에 대해서는 추가 작업이 필요합니다.
                    addClassThird( "OBJECT "+ before_object +"." + object +"." + getTypeNameValue( typeName2, paraName2 ), obj ,argReturnFlag);
                    getAnnotation( obj.getClass(), obj, "Field" );

                }
            } catch (Exception e) {
                log.info( "Eeception e ={}", e.getMessage());
                continue;
            }
        }
    }

    public static boolean isWrapperType(Class<?> clazz)
    {
        if (clazz == null) {
            return false;
        }
        return WRAPPER_TYPES.contains(clazz);
    }

    private static Set<Class<?>> getWrapperTypes()
    {
        Set<Class<?>> ret = new HashSet<Class<?>>();
        ret.add(Boolean.class);
        ret.add(Character.class);
        ret.add(Byte.class);
        ret.add(Short.class);
        ret.add(Integer.class);
        ret.add(Long.class);
        ret.add(Float.class);
        ret.add(Double.class);
        ret.add(Void.class);
        ret.add(String.class);
        return ret;
    }

    public String lastIndexOf(String inStr) {
        String str =  inStr;
        int pos = str.lastIndexOf( "." );
        return str.substring( pos + 1 );
    }

    public void printAll() {
        log.info( "=============Print ALL Start=================" );
        log.info( "uuid = {}", uuid );
        log.info( "basePackage = {}", basePackage );
        log.info( "level = {}", level );
        log.info( "startTimeMs = {}", startTimeMs );
        log.info( "durationTimeMs = {}", durationTimeMs );
        log.info( "classType = {}", getCallClassType() );
        log.info("callArgCount={}", getCallArgCount());
        log.info( "methodName = {}", getCallMethodName() );
        log.info( "methodReturnType = {}", getCallReturnType() );
        log.info( "classAnnotation = {}", getClassAnnotation() );
        argClass.forEach( (k,v)->{log.info("argClass = [{}, {}]",k,v);} );
        objClass_depth1.forEach( (k,v)->{log.info("objClass_depth1 = [{}, {}]",k,v);} );
        objClass_depth2.forEach( (k,v)->{log.info("objClass_depth2 = [{}, {}]",k,v);} );
        objClass_depth3.forEach( (k,v)->{log.info("objClass_depth3 = [{}, {}]",k,v);} );
        retClass_depth1.forEach( (k,v)->{log.info("retClass_depth1 = [{}, {}]",k,v);} );
        retClass_depth2.forEach( (k,v)->{log.info("retClass_depth2 = [{}, {}]",k,v);} );
        argAnnotation.forEach( (k,v)->{log.info("argAnnotation = [{}, {}]",k,v);} );
        returnAnnotation.forEach( (k,v)->{log.info("returnAnnotation = [{}, {}]",k,v);} );
        exceptionClass.forEach( (k,v)->{log.info("exceptionClass = [{}, {}]",k,v);} );
        log.info( "=============Print ALL End=================" );
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
    public String getAnnotation(Class<?> type,Object object,String delimiter) {
        if (object == null) {
            Annotation[] annotations = type.getAnnotations();
            String annotationValue="";
            for (Annotation annotation : annotations) {
                annotationValue += annotation;
            }
            if(!annotationValue.isEmpty()){
                if(delimiter != null && delimiter.equals(ArgReturnFlag[0])){
                    addArgAnnotation( object.getClass().getTypeName() + "." + annotationValue, null );
                }
                if(delimiter != null && delimiter.equals(ArgReturnFlag[1])){
                    addReturnAnnotation( object.getClass().getTypeName() + "." + annotationValue, null );

                }
                addFieldAnnotation( object.getClass().getTypeName() + "." + annotationValue, null  );
                return getLimitString( annotationValue );
            }
            log.info( "===================================" );
            return null;
        }
        String annotationValue="";
        if(object instanceof Collection<?>) {
            Collection<Object> collect = (Collection<Object>) object;
            for (Object o : collect) {
                Annotation[] annotations = o.getClass().getAnnotations();
                for (Annotation annotation : annotations) {
                    annotationValue += annotation;
                }
                if(!annotationValue.isEmpty()){
                    if(delimiter != null && delimiter.equals(ArgReturnFlag[0])) {
                        addArgAnnotation( object.getClass().getTypeName() + "." + annotationValue, o );
                    }
                    else if(delimiter != null && delimiter.equals(ArgReturnFlag[1])) {
                        addReturnAnnotation( object.getClass().getTypeName() + "." + annotationValue, o );
                    }
                    else{
                        addFieldAnnotation( object.getClass().getTypeName()+"." + annotationValue , o );
                    }
                    break;
                }
            }
        } else {
            Annotation[] annotations = object.getClass().getAnnotations();
            for (Annotation annotation : annotations) {
                annotationValue += annotation;
            }
            if(!annotationValue.isEmpty()){
                if(delimiter != null && delimiter.equals(ArgReturnFlag[0])) {
                    addArgAnnotation( object.getClass().getTypeName() + "." + annotationValue, object );
                }
                else if(delimiter != null && delimiter.equals(ArgReturnFlag[1])) {
                    addReturnAnnotation( object.getClass().getTypeName() + "." + annotationValue, object );
                }
                else{
                    addFieldAnnotation( object.getClass().getTypeName()+"." + annotationValue , object );
                }
            }
        }
        if(annotationValue.isEmpty()){
            return null;
        }
        else{
            return getLimitString( annotationValue );
        }
    }


    public ClientEntity makeClientData(ObjectMapper objectMapper) {

        /* In/Out Data를 출력한다. */

        ClientInOutData client= new ClientInOutData();



        ClientEntity clientEntity = new ClientEntity();

        clientEntity.setUuid(getUuid() );
        clientEntity.setBasePackage(getBasePackage() );
        clientEntity.setLevel(getLevel());
        clientEntity.setStartTimeMs(getStartTimeMs());
        clientEntity.setCallArgCount(getCallArgCount());
        clientEntity.setCallClassType(getCallClassType());
        clientEntity.setCallMethodName( getCallMethodName() );
        clientEntity.setCallReturnType( getCallReturnType() );
        clientEntity.setCallReturnTypeDepth1( getCallReturnTypeDepth1() );
        String annotation = getClassAnnotation();
        String beanInfo ="";
        if (annotation != null) {
            clientEntity.setClassAnnotation( getLimitString(getClassAnnotation()) );
            if(annotation.contains("Controller")){
                beanInfo += "Controller|";
            }
            if(annotation.contains("Service")){
                beanInfo += "Service|";
            }
            if(annotation.contains("Repository")){
                beanInfo += "Repository|";
            }
            if(annotation.contains("Configuration")){
                beanInfo += "Configuration|";
            }
            if(annotation.contains("Transactional")){
                beanInfo += "Transactional|";
            }
            clientEntity.setBeanInfo( beanInfo );
        }


        /**
         *  Client Arg 테이블에 넣어준다.
         */
        Map<String, Object> args = getArgClass();
        for (String s : args.keySet()) {
            ClientArg arg = new ClientArg();
            arg.setUuid( getUuid() );

            getObjectValue1().forEach( (k,v)->{
                if(k.equals( args.get(s) )){
                    arg.setArgClassType( v+"" );
                }
            } );
            getParaValue1().forEach( (k,v)->{
                if(k.equals( args.get(s) )){
                    arg.setArgParaName( v+"" );
                }
            } );


            //arg.setArgClassType(  );
            String ArgClassType;
           // String ArgAnnotation;
            arg.setArgAnnotation( getAnnotation(args.get(s).getClass(),args.get(s),"ARG") );
           // String ArgClassError;
            arg.setArgClassName( s );
            arg.setArgObjectValue( args.get(s) +"" );
            clientEntity.getClientArgs().add( arg );
        }

        /* Input Data를 테이블에 넣어준다*/


//        String inputType1;
//        String inputType2;
//        String Para1;
//        String Para2;
//        String FieldError;
//        String fieldAnnotation;

        Map<String, Object> classDepth1 = getObjClass_depth1();
        for (String s : classDepth1.keySet()) {
            ClientInput clientInput = new ClientInput();
            clientInput.setUuid( getUuid() );
            clientInput.setInput_depth( 1 );
            clientInput.setInputName( s );
            /**/
            getObjectValue2().forEach( (k,v)->{
                if(k.equals( classDepth1.get(s) )){
                    clientInput.setInputType1( v+"" );
                }
            } );
            getParaValue2().forEach( (k,v)->{
                if(k.equals( classDepth1.get(s) )){
                    clientInput.setPara1( v+"" );
                }
            } );

            clientInput.setInputValue( classDepth1.get(s) +"" );
            clientEntity.getClientInputs().add( clientInput );
        }
        Map<String, Object> classDepth2 = getObjClass_depth2();
        for (String s : classDepth2.keySet()) {
            ClientInput clientInput = new ClientInput();
            clientInput.setUuid( getUuid() );
            clientInput.setInput_depth( 2 );
            clientInput.setInputName( s );

            /**/
            getObjectValue3().forEach( (k,v)->{
                if(k.equals( classDepth1.get(s) )){
                    clientInput.setInputType2( v+"" );
                }
            } );
            getParaValue3().forEach( (k,v)->{
                if(k.equals( classDepth1.get(s) )){
                    clientInput.setPara2( v+"" );
                }
            } );

            clientInput.setInputValue( classDepth1.get(s) +"" );
            clientEntity.getClientInputs().add( clientInput );
        }

        /* Return  Data를 테이블에 넣어준다*/
        Map<String, Object> retDepth1 = getRetClass_depth1();
        for (String s : retDepth1.keySet()) {
            ClientOutput clientOutput = new ClientOutput();
            clientOutput.setUuid( getUuid() );
            clientOutput.setOutput_depth( 1 );
            clientOutput.setOutputName( s );
            clientOutput.setOutputValue( retDepth1.get(s) +"" );
            clientEntity.getClientOutputs().add( clientOutput );
        }
        Map<String, Object> retDepth2 = getRetClass_depth2();
        for (String s : retDepth2.keySet()) {
            ClientOutput clientOutput = new ClientOutput();
            clientOutput.setUuid( getUuid() );
            clientOutput.setOutput_depth( 2 );
            clientOutput.setOutputName( s );
            clientOutput.setOutputValue( retDepth2.get(s) +"" );
            clientEntity.getClientOutputs().add( clientOutput );
        }



        try {
            String s = objectMapper.writeValueAsString( client );
            log.info( "Mapper Client = {}", s );
        } catch (JsonProcessingException e) {
            log.info( "Exception mapper = {}", e );
        }

        return clientEntity;
    }

    public void postSend() {

    }

}


