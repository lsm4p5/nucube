package uplus.nucube.common.trace.dto;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;

import org.aspectj.lang.reflect.MethodSignature;

import uplus.nucube.common.trace.TraceStatus;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

@Data
@Slf4j
public class TraceMethodDto {

    String uuid;
    String basePackage = "uplus.nucube";

    int level;

    long startTimeMs;

    long durationTimeMs;

    String classType;

    Set<String> argClassKinds = new HashSet<>();
    String methodName;

    String MethodReturnType;

    String[] MethodArguments;

    int parameterCount;

    Map<String, String> parameterType = new HashMap<>();

    Map<String, String> parameterValues = new HashMap<>();

    Map<String, String> argsValues = new HashMap<>();

    Map<String, String> args2Values = new HashMap<>();

    Map<String, String> args3Values = new HashMap<>();

    String returnType ;

    String returnType2;

    String returnType3;

    Map<String, String> returnParameter = new HashMap<>();
    Map<String, String> returnValues = new HashMap<>();

    Map<String, String> returnValues2 = new HashMap<>();

    Map<String, String> returnValues3 = new HashMap<>();

    Map<String,String> exceptions = new HashMap<>();

    Map<String, String> annotationMap = new HashMap<>();

    private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();

    public void addReturnParameter(String paraType, String paraName) {
        returnParameter.put( paraType, paraName );
    }

    public void addParameterType(String paraType, String paraName) {
        parameterType.put( paraType, paraName );
    }

    public void addParameterValues(String paraType, String paraValue) {
        parameterValues.put( paraType, paraValue );
    }

    public void addReturnValues(String returnType, String returnValue) {
        returnValues.put( returnType, returnValue );
    }

    public void addReturnValues2(String returnType, String returnValue) {
        returnValues2.put( returnType, returnValue );
    }

    public void addReturnValues3(String returnType, String returnValue) {
        returnValues3.put( returnType, returnValue );
    }

    public void addReturnException(String exceptionType, String exceptionMessage) {
        exceptions.put( exceptionType, exceptionMessage );
    }

    public void addArgsValues(String argType, String argName) {
        argsValues.put( argType, argName );
    }

    public void addArgs2Values(String argType, String argName) {
        args2Values.put( argType, argName );
    }
    public void addArgs3Values(String argType, String argName) {
        args3Values.put( argType, argName );
    }

    private Map<String,String> emergencyMessage = new HashMap<>();


    public void addEmergencyMessage(String argType, String argName) {
        emergencyMessage.put( argType, argName );
    }

    public void MakeMethodInfo(ProceedingJoinPoint joinPoint, TraceStatus status) {

        // Level을 남긴다.
       setUuid( status.getTraceId().getId() );
       setLevel( status.getTraceId().getLevel() );
       setStartTimeMs( status.getStartTimeMs() );

        //Method Type 저장
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        setClassType( signature.getDeclaringTypeName() );
        setMethodReturnType( String.valueOf(signature.getReturnType()));
        setMethodName( signature.getName() );
        setMethodArguments(signature.getParameterNames());
        Class aClass = signature.getDeclaringType();

        //클래스의 annotaion을 자져온다.
        Annotation[] annotations = aClass.getAnnotations();
        String s = annotations.toString();
        System.out.println( "s = " + s );

        for (Annotation annotation : annotations) {
            log.info( "annotation = {}", Arrays.stream( annotations ).toList() );
        }


        Method method = signature.getMethod();
        methodName = method.getName();
        Parameter[] parameters = method.getParameters();
        Object[] parameterValues = joinPoint.getArgs();
        int i=0;
        for (Parameter parameter : parameters) {
            if (parameter == null) {
             //   log.info( "parameter null" );
                continue;
            }
            addParameterType( parameter.getType().getName(),parameter.getName() );
            addParameterValues(parameter.getName(),parameterValues[i]+"");
            i++;
        }



        Object[] args = joinPoint.getArgs();

        if (args == null) {
            log.info("args == null ");

            setParameterCount( 0 );
        }else{
            setParameterCount( args.length );
        }

        for (Object arg : args) {
            if (arg == null) {
             //   log.info( "arg == null " );
                continue;
            }
            /**
             * ParameterName을 얻어온다.
             */
            String paraName = getParameterType().get( arg );
          //  log.info( "paraName = {} arg={}", paraName ,arg);
            FieldPrint(arg);
        }
    }

    public void FieldPrint(Object obj) {
        int entity_exist = 0;
        if (obj == null) {
            return;
        }
        if(isWrapperType(obj.getClass())){
            addArgsValues( obj.getClass() +"."+ obj,obj+"");
            return ;
        }
        if (obj instanceof Collection<?>) {
            Collection<Object> collect = (Collection<Object>) obj;
            for (Object o : collect) {
                try {

                    FieldPrintDepth2( o, obj );

                } catch (Exception e) {
                    log.info( "[chekcArgs] exception ={} ", e );
                }

            }
            return;
        }
        int i =0;
        for(Field field : obj.getClass().getDeclaredFields()) {
            boolean setAccessibleFlag = true;
            field.setAccessible(true);
            String annotation = Arrays.toString( field.getAnnotations() );

            if (annotation != null) {
                if(annotation.contains( "fetch=EAGER" )){
                    addEmergencyMessage( "Level-1, Arg ","Entity 설정 오류 - Lazy로 바꾸세요" );
                }
                if (annotation.contains( "javax.persistence" )) {
                    entity_exist = 1;
                }
            }
            try {
                if((field.get(obj) != null) && isWrapperType(field.get(obj).getClass())){
                    addArgsValues(obj+"." + field.getName(),field.get(obj)+"");
                }else{
                 //   log.info( "==== ARG depth2 before =={}-{}", field.get( obj ), obj );
                    FieldPrintDepth2(field.get(obj),obj);
                }
            } catch (IllegalAccessException e) {
                log.info( "에러 발생 {}", e );
                // throw new RuntimeException( e );
            }
            i++;
        }

        if (entity_exist == 1) {
            String data =  obj.getClass() + "=Entity";
            if(!argClassKinds.contains(data)) {
                argClassKinds.add( data );
            }
        }
    }

    public void FieldPrintDepth2(Object obj, Object before_arg)  {
        int entity_exist = 0;
        if (obj == null) {
            return;
        }
        if(isWrapperType(obj.getClass())){
            if((before_arg !=null) && isWrapperType(before_arg.getClass())){
                addArgs2Values(before_arg+"." +obj,obj +"");

            } else{
                addArgs2Values(obj.getClass()+"."+obj,obj+"");
            }
            return ;
        }
        if (obj instanceof Collection<?>) {
          //  log.info( "[checkArgs_FieldObject] Collection -> Collection은 reflection을 하지 않습니다." );
            return ;
        }

        if(!obj.getClass().toString().contains(basePackage)){
            return;
        }
        for(Field field : obj.getClass().getDeclaredFields()) {

            field.setAccessible(true);
            String annotation = Arrays.toString( field.getAnnotations() );
            if (annotation != null) {
                if(annotation.contains( "fetch=EAGER" )){
                    addEmergencyMessage( "Level-2, Arg ","Entity 설정 오류 - Lazy로 바꾸세요" );
                }
                if (annotation.contains( "javax.persistence" )) {
                    entity_exist = 2;
                }
            }

            try {
                if (before_arg != null) {
                    addArgs2Values(before_arg+"." +obj+"."+field.getName(),field.get(obj)+"");
                }else {
                    addArgs2Values(obj+"."+field.getName(),field.get(obj)+"");
                }
            } catch (Exception ee) {
                log.info( "Exception ee={}", ee );
            }
        }

    }

    public void FieldPrintReturn(Object obj) {
        int entity_exist = 0;
        if (obj == null) {
            return;
        }
        if(isWrapperType(obj.getClass())){
         //   log.info( "return isWrapperType = {}-{}", obj.getClass(), obj );
            addReturnValues( obj.getClass() +"."+ obj,obj+"");
            return ;
        }
        if (obj instanceof Collection<?>) {
            Collection<Object> collect = (Collection<Object>) obj;
            for (Object o : collect) {
                try {

                    FieldPrintReturnDepth2( o, obj );

                } catch (Exception e) {
                    log.info( "[chekcArgs] exception ={} ", e );
                }

            }
           return;
        }
        if(!obj.getClass().toString().contains(basePackage)){
          return;
        }

       // setReturnType( obj..getType().getSimpleName() );
        for(Field field : obj.getClass().getDeclaredFields()) {
            boolean setAccessibleFlag = true;
            field.setAccessible(true);

            String annotation = Arrays.toString( field.getAnnotations() );

            if (annotation != null) {
                if(annotation.contains( "fetch=EAGER" )){
                    addEmergencyMessage( "Level-1, Return ","Entity 설정 오류 - Lazy로 바꾸세요" );
                }
                if (annotation.contains( "javax.persistence" )) {
                    entity_exist = 1;
                }
            }
            try {
                if((field.get(obj) != null) && isWrapperType(field.get(obj).getClass())){
                    addReturnValues(obj+"." + field.getName(),field.get(obj)+"");
                }else{
                //    log.info( "==== depth2 before =={}-{}", field.get( obj ), obj );
                    FieldPrintReturnDepth2(field.get(obj),obj);
                }
            } catch (IllegalAccessException e) {
                log.info( "에러 발생 {}", e );
               // throw new RuntimeException( e );
            }
        }
        if (entity_exist == 1) {
            String data =  obj.getClass() + "=Entity";
            if(!argClassKinds.contains(data)) {
                argClassKinds.add( data );
            }
        }
    }

    public void FieldPrintReturnDepth2(Object obj, Object before_arg) {

        if (obj == null) {
            return;
        }

        //  log.info("[checkArgs_FieldObject] checkArgs_FieldObject instantanceof Conllection ={}", arg instanceof Collection<?> );
        if(isWrapperType(obj.getClass())){
            if((before_arg !=null) && isWrapperType(before_arg.getClass())){
                addReturnValues2(before_arg+"." +obj,obj +"");

            } else{
                addReturnValues2(obj.getClass()+"."+obj,obj+"");
            }
            return ;
        }

        if (obj instanceof Collection<?>) {
          //  log.info( "[checkArgs_FieldObject] Collection -> Collection은 reflection을 하지 않습니다." );
            return ;
        }

        if(!obj.getClass().toString().contains(basePackage)){
           return;
        }

        int entity_exist = 0;
        for(Field field : obj.getClass().getDeclaredFields()) {

            field.setAccessible(true);
            String annotation = Arrays.toString( field.getAnnotations() );

            if (annotation != null) {
                if(annotation.contains( "fetch=EAGER" )){
                    addEmergencyMessage( "Level-1, Return " ,"Entity 설정 오류 - Lazy로 바꾸세요" );
                }
                if (annotation.contains( "javax.persistence" )) {
                    entity_exist = 2;
                }
            }

            setReturnType2(field.getType().getSimpleName() );

            try {
                if (before_arg != null) {
                    addReturnValues2(before_arg+"." +obj+"."+field.getName(),field.get(obj)+"");
                }else {
                    addReturnValues2(obj+"."+field.getName(),field.get(obj)+"");
                }
            } catch (Exception ee) {
                log.info( "Exception ee={}", ee );
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

    public void fieldPrint_detail(Field f, Object obj, boolean setAccessibleFlag) {
        if (f == null || obj == null) {
            return;
        }
       Field field = f;
       log.info("[fieldPrint] => =====================================");
       log.info("field class ={}" , field.getType().getSimpleName());
       log.info("field annotation = {} ", Arrays.toString(field.getAnnotations()));
       log.info("field decareclass = {}" + field.getDeclaringClass().toString());
       log.info( "field name ={} " , field.getName() );
       field.setAccessible(setAccessibleFlag);
        try {
            log.info("field value ={}", field.get(obj) );
        } catch (IllegalAccessException e) {
            log.info( "Exception {}", e );
           // throw new RuntimeException( e );
        }
        log.info("[fieldPrint] <== ===================================");

    }

    public void tracePrintAll() {
        log.info( "==============================" );
        log.info( "uuid = {}", uuid );
        log.info( "basePackage = {}", basePackage );
        log.info( "leevel = {}", level );
        log.info( "startTimeMs = {}", startTimeMs );
        log.info( "durationTimeMs = {}", durationTimeMs );
        log.info( "classType = {}", classType );
        argClassKinds.stream().forEach( (a) -> log.info( "argClassKinds ={}", a ) );
        log.info( "methodName = {}", methodName );
        log.info( "methodReturnType = {}", getMethodReturnType() );
        log.info( "methodArguments ={}", getMethodArguments().toString() );
        String[] methodArguments = getMethodArguments();
        for (String methodArgument : methodArguments) {
            log.info( "methodArgument={}", methodArgument );
        }
        log.info( "parameterCount = {}", parameterCount );
        parameterType.forEach( (k, v) -> {
            log.info( "parameterType = [{},{}]", k, v );
        } );
        parameterValues.forEach( (k, v) -> {
            log.info( "parameterValues = [{},{}]", k, v );
        } );
        argsValues.forEach( (k, v) -> {
            log.info( "argsValues = [{},{}]", k, v );
        } );
        args2Values.forEach( (k, v) -> {
            log.info( "args2Values = [{},{}]", k, v );
        } );
        log.info( "returnType = {}", returnType );
        log.info( "returnType2 = {}", returnType2 );
        returnParameter.forEach( (k, v) -> {
            log.info( "returnParameter = [{},{}]", k, v );
        } );
        returnValues.forEach( (k, v) -> {
            log.info( "returnValues = [{},{}]", k, v );
        } );
        returnValues2.forEach( (k, v) -> {
            log.info( "returnValues2 = [{},{}]", k, v );
        } );
        exceptions.forEach( (k, v) -> {
            log.info( "exceptions = [{},{}]", k, v );
        } );
        annotationMap.forEach( (k, v) -> {
            log.info( "annotationMap = [{},{}]", k, v );
        } );
        log.info( "==============================" );
    }

}
