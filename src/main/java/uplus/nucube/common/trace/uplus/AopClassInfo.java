package uplus.nucube.common.trace.uplus;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.reflect.MethodSignature;
import uplus.nucube.common.trace.TraceStatus;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

import static uplus.nucube.common.trace.uplus.TraceCommonUtil.*;

@Data
@Slf4j
public class AopClassInfo {

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
    Object classObject; //classObject는 Bean임.
    List<InputMeta> inputMetas = new ArrayList<>();
    List<OutputMeta> outputMetas = new ArrayList<>();
    // Spring Bean 필드들이 저장되는 array
    List<ClassField> classFields = new ArrayList<>();
    // 임시로 사용하는 List
    Map<ClassField, Object> depthHandler = new HashMap<>();
    public void makeReturnInfo(Object object) {

        OutputMeta outputMeta = new OutputMeta( object, getUuid());
        makeClassFieldArray( object, object.getClass(), 0,outputMeta);

        /* 이어서 진행한다. */
        makeFieldDepth(1 ,outputMeta);
        beforeDepthHandling(outputMeta,1 );
        makeFieldDepth( 2,outputMeta );
        beforeDepthHandling(outputMeta,2 );
        makeFieldDepth( 3,outputMeta );
        beforeDepthHandling(outputMeta,3 );
    }
    public void makeInputInfo(ProceedingJoinPoint joinPoint) {

        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();
        Method method = signature.getMethod();

        Parameter[] parameters = method.getParameters();
        Object[] argsValues = joinPoint.getArgs();

        int i=0;

       for (Parameter parameter : parameters) {
           InputMeta inputMeta = new InputMeta(parameter,argsValues[i],getUuid());
           makeClassFieldArray( argsValues[i], parameter.getType(), 0 ,inputMeta);
           makeFieldDepth(1 ,inputMeta);
           beforeDepthHandling(inputMeta,1 );
           makeFieldDepth( 2,inputMeta );
           beforeDepthHandling(inputMeta,2 );
           makeFieldDepth( 3,inputMeta );
           beforeDepthHandling(inputMeta,3 );
           i++;
        }

    }

    public void makeBeanInfo(ProceedingJoinPoint joinPoint, TraceStatus status) {

        setUuid( status.getTraceId().getId() );
        setLevel( status.getTraceId().getLevel() );

        Object aThis = joinPoint.getThis();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Class<?> classType = signature.getDeclaringType();
        Class<?> returnType = signature.getReturnType();
        Method method = signature.getMethod();
        String methodName = method.getName();

        setFunctionClassAnnotation(TraceCommonUtil.getAnnotationString( classType ));
        setClassKinds( TraceCommonUtil.getClassKinds( classType ) );
        setFunctionClassName( classType.getTypeName() );
        setFunctionClassSimpleName( classType.getSimpleName() );
        setFunctionName( methodName );
        setClassObject( aThis );
        setReturnTypeName( returnType.getTypeName());

        ClassField functionClass = new ClassField();
        makeClassFieldArray( aThis, classType, 0,functionClass);
        makeFieldDepth(1 ,functionClass);
        beforeDepthHandling(functionClass,1);
        makeFieldDepth( 2,functionClass );
        beforeDepthHandling(functionClass,2);
        makeFieldDepth( 3,functionClass );
        beforeDepthHandling(functionClass,3);

    }


    public void printAopClassInfo() {
        log.info( "================================================ " );
        log.info( "printAopClassInfo [start] ========================== " );
        log.info("uuid={}", getUuid());
        log.info( "basePackage ={}", getBasePackage() );
        log.info("level ={}", getLevel());
        log.info("durationTimeMs ={}", getDurationTimeMs());
        log.info("classKinds ={}", getClassKinds());
        log.info("functionClassAnnotation ={}", getFunctionClassAnnotation());
        log.info( "functionClassName ={}", getFunctionClassName() );
        log.info( "returnTypeName ={}", getReturnTypeName() );
        log.info("functionName ={}",functionName);
        for (InputMeta inputMeta : inputMetas) {
           inputMeta.printAll();
        }
        for (OutputMeta outputMeta : outputMetas) {
            log.info( "outputMeat ={}", outputMeta );
        }
        log.info( "printAopClassInfo [end] ========================== " );
        log.info( "================================================ " );
    }

    public  void makeClassFieldArray(Object obj, Class<?> type, int depth,Object functionClass) {

        InputMeta inputMeta=null;
        OutputMeta outputMeta = null;
        if (functionClass instanceof InputMeta) {
            inputMeta = (InputMeta) functionClass;
        }
        if (functionClass instanceof OutputMeta) {
            outputMeta  = (OutputMeta) functionClass;
        }
        if (depth != 0) {
            log.info( "depth 입력값은 0만 허용 합니다." );
            return;
        }

        if (type.isPrimitive() || isWrapperType( type )) {
            Class<?> field_type;
            String field_typeName;

            ClassField field2 = new ClassField();
            field2.setSuper_uuid( getUuid() );

            if (depth == 0) {
                field2.setUuid( TraceCommonUtil.makeUuid() );
                field2.setSuper_uuid( getUuid() );
            }
            field_type = type;
            field_typeName = type.getTypeName();
            field2.setFieldTypeFullName( type.getTypeName());
            field2.setDepth( depth );
            field2.setNext_depth( depth );
            field2.setFieldType( field_type );
            field2.setFieldAnnotation( getAnnotationString( field_type ) );
            field2.setFieldTypeName( field_typeName );
            field2.setSimpleFieldTypeName( field_type.getSimpleName() );
            field2.setFieldVariable( field_type.getName() );
            field2.setFieldKind( TraceCommonUtil.getFieldKind( field_type, obj ) );
            field2.setFieldObject( obj );
            if (functionClass instanceof ClassField) {
                field2.setFieldVariable( ((ClassField) functionClass).getFieldVariable() );
                getClassFields().add( field2 );
            }
            if (functionClass instanceof InputMeta) {
                field2.setFieldVariable( ((InputMeta) functionClass).getVariable() );
                inputMeta.getFields().add( field2 );
            }
            if (functionClass instanceof OutputMeta) {
                field2.setFieldVariable( ((OutputMeta) functionClass).getVariable() );
                outputMeta.getFields().add( field2 );
            }

            if (functionClass instanceof InputMeta) {;
                getInputMetas().add( inputMeta );
            }
            if (functionClass instanceof OutputMeta) {
                getOutputMetas().add( outputMeta );
            }
            return;
        }

       if(ListHandling( obj, type, depth, functionClass, inputMeta, outputMeta )){
            // true 이면 for 인입안된다
            if (functionClass instanceof InputMeta) {;
                getInputMetas().add( inputMeta );
            }
            if (functionClass instanceof OutputMeta) {
                getOutputMetas().add( outputMeta );
            }
            return;
        }else{
            if(obj != null && obj instanceof Collection<?>){
                ListHandlingObject( obj, type, depth, functionClass, inputMeta, outputMeta );
                if (functionClass instanceof InputMeta) {;
                    getInputMetas().add( inputMeta );
                }
                if (functionClass instanceof OutputMeta) {
                    getOutputMetas().add( outputMeta );
                }
                return;
            }
        }
         //List 처리 End
        for(Field field :type.getDeclaredFields()) {
            Object field_object;
            Class<?> field_type;
            String field_typeName;

            ClassField field1 = new ClassField();
            field1.setSuper_uuid( getUuid() );

            if (depth == 0) {
                field1.setUuid( TraceCommonUtil.makeUuid() );
                field1.setSuper_uuid( getUuid() );
            }
            try {
                field_type = field.getType();
                field_typeName = field.getType().getTypeName() ;

                field1.setFieldTypeFullName( type.getTypeName() +"." + field.getType().getTypeName() );

                field1.setDepth( depth );
                field1.setNext_depth( depth );
                field1.setFieldType( field_type );
                field1.setFieldAnnotation( getAnnotationString( field_type ) );
                field1.setFieldTypeName( field_typeName );
                field1.setSimpleFieldTypeName( field_type.getSimpleName() );
                field1.setFieldVariable( field.getName() );


                try{
                    field.setAccessible(true);
                }catch(Exception field_exception){
                    field1.setFieldKind( TraceCommonUtil.getFieldKind(field_type, null) );
                    field1.setFieldObject( "No-access" );
                    if (functionClass instanceof ClassField) {
                        getClassFields().add( field1 );
                    }
                    if (functionClass instanceof InputMeta) {;
                        inputMeta.getFields().add( field1 );
                    }
                    if (functionClass instanceof OutputMeta) {
                        outputMeta.getFields().add( field1 );
                    }
                    continue;
                }

                if (obj == null) {
                    field1.setFieldKind( TraceCommonUtil.getFieldKind(field_type, null) );
                    if (functionClass instanceof ClassField) {
                        getClassFields().add( field1 );
                    }
                    if (functionClass instanceof InputMeta) {;
                        inputMeta.getFields().add( field1 );
                    }
                    if (functionClass instanceof OutputMeta) {
                        outputMeta.getFields().add( field1 );
                    }
                    continue;
                }
                field_object = field.get( obj );
                field1.setFieldKind( TraceCommonUtil.getFieldKind(field_type, field_object) );
                field1.setFieldObject( field_object );

                if(field_object != null){
                    if(field_object instanceof Collection<?>){
                        if(((Collection<?>) field_object).size() ==0){
                        }else{
                            field1.setNext_depth( depth + 1 );
                        }
                    }else if(field_type.isPrimitive()){

                    }else if(isWrapperType(field_type)){

                    }else{
                        field1.setNext_depth( depth + 1 );
                    }
                }else{
                    log.info("field_object가 null이면 다음으로 진행하지 않는다.");
                }
            } catch (Exception e) {
                log.info( "Exception e ={}", e.getMessage() );
            }
            // listTypeAdd( functionClass, field1 );
            if (functionClass instanceof ClassField) {
                getClassFields().add( field1 );
            }
            if (functionClass instanceof InputMeta) {;
                inputMeta.getFields().add( field1 );
            }
            if (functionClass instanceof OutputMeta) {
                outputMeta.getFields().add( field1 );
            }

        }
        if (functionClass instanceof InputMeta) {;
            getInputMetas().add( inputMeta );
        }
        if (functionClass instanceof OutputMeta) {
            getOutputMetas().add( outputMeta );
        }
    }

    private boolean ListHandling(Object obj, Class<?> type, int depth, Object functionClass, InputMeta inputMeta, OutputMeta outputMeta) {

        if(type.getDeclaredFields().length == 0){
            ClassField field1 = new ClassField();
            field1.setSuper_uuid( getUuid() );

            if (depth == 0) {
                field1.setUuid( TraceCommonUtil.makeUuid() );
                field1.setSuper_uuid( getUuid() );
            }
            field1.setFieldTypeFullName( type.getTypeName());

            field1.setDepth( depth );
            field1.setNext_depth( depth );
            field1.setFieldType( type );
            field1.setFieldAnnotation( getAnnotationString( type ) );
            field1.setFieldTypeName( type.getTypeName() );
            field1.setSimpleFieldTypeName( type.getSimpleName() );
            field1.setFieldVariable( type.getName() );
            field1.setFieldKind( TraceCommonUtil.getFieldKind( type, obj ) );
            field1.setFieldObject( obj );
            //매우 중요
            if (obj != null ) {
                if(obj instanceof Collection<?>){
                    if(((Collection<?>) obj).size() != 0){
                        field1.setNext_depth( depth +1 );
                    }
                }
            }
            if (functionClass instanceof ClassField) {
                getClassFields().add( field1 );
            }
            if (functionClass instanceof InputMeta) {;
                inputMeta.getFields().add( field1 );
            }
            if (functionClass instanceof OutputMeta) {
                outputMeta.getFields().add( field1 );
            }
            return true;
        }
        else{
            return false;
        }
    }

    private void ListHandlingObject(Object obj, Class<?> type, int depth, Object functionClass, InputMeta inputMeta, OutputMeta outputMeta) {

            ClassField field1 = new ClassField();
            field1.setSuper_uuid( getUuid() );

            if (depth == 0) {
                field1.setUuid( TraceCommonUtil.makeUuid() );
                field1.setSuper_uuid( getUuid() );
            }
            field1.setFieldTypeFullName( type.getTypeName());

            field1.setDepth( depth );
            field1.setNext_depth( depth );
            field1.setFieldType( type );
            field1.setFieldAnnotation( getAnnotationString( type ) );
            field1.setFieldTypeName( type.getTypeName() );
            field1.setSimpleFieldTypeName( type.getSimpleName() );
            field1.setFieldVariable( type.getName() );
            field1.setFieldKind( TraceCommonUtil.getFieldKind( type, obj ) );
            field1.setFieldObject( obj );
            //매우 중요
            if (obj != null ) {
                if(obj instanceof Collection<?>){
                    if(((Collection<?>) obj).size() != 0){
                        field1.setNext_depth( depth +1 );
                    }
                }
            }
            if (functionClass instanceof ClassField) {
                getClassFields().add( field1 );
            }
            if (functionClass instanceof InputMeta) {;
                inputMeta.getFields().add( field1 );
            }
            if (functionClass instanceof OutputMeta) {
                outputMeta.getFields().add( field1 );
            }

    }


    public void makeFieldDepth(int depth,Object functionClass) {

        if (depth == 0) {
            log.info( "depth = 1 이상이어야 합니다" );
            return;
        }
        List<ClassField> fields = new ArrayList<>();
        if(functionClass instanceof ClassField ) {
            for (ClassField classField : getClassFields()) {
                fields.add( classField );
            }
        }
        if(functionClass instanceof InputMeta ) {
            for (ClassField classField : ((InputMeta) functionClass).getFields()) {
                 fields.add( classField );
            }
        }
        if(functionClass instanceof OutputMeta ) {
            for (ClassField classField : ((OutputMeta) functionClass).getFields()) {
                fields.add( classField );
            }
        }
        for (ClassField field_in : fields) {
            if(field_in.getNext_depth() == depth && (field_in.getDepth() == depth -1)) {
                Class<?> type = field_in.getFieldType();
                Object obj = field_in.getFieldObject();
                if(obj instanceof  Collection<?> ){
                    Collection<Object> collect = (Collection<Object>) obj;
                    for (Object o : collect) {
                        Class<?> oType = o.getClass();
                        makeFieldDepthAction( field_in , o, oType, depth,functionClass,1 );
                    }
                }else if(type.isPrimitive()){
                    log.info("depth != 0 이 아니면 이곳으로 들어오면 안됨.");
                }else if(isWrapperType( type )){
                    log.info("depth != 0 이 아니면 이곳으로 들어오면 안됨.");
                }else{
                    makeFieldDepthAction( field_in ,obj, type, depth,functionClass,0 );
                }
            }
        }

    }

    private boolean ListHandlingAction(Object obj, Class<?> type, int depth, Object functionClass) {

        if(type.getDeclaredFields().length == 0){

            ClassField field1 = new ClassField();
            field1.setSuper_uuid( getUuid() );
            if (depth == 0) {
                field1.setUuid(TraceCommonUtil.makeUuid());
                field1.setSuper_uuid( getUuid() );
            }
            field1.setFieldTypeFullName( type.getTypeName());
            field1.setDepth( depth );
            field1.setNext_depth( depth );
            field1.setFieldType( type );
            field1.setFieldAnnotation( getAnnotationString( type ) );
            field1.setFieldTypeName( type.getTypeName() );
            field1.setSimpleFieldTypeName( type.getSimpleName() );
            field1.setFieldVariable( type.getName() );
            field1.setFieldKind( TraceCommonUtil.getFieldKind( type, obj ) );
            field1.setFieldObject( obj );
            //매우 중요
            if (obj != null ) {
                if(obj instanceof Collection<?>){
                    if(((Collection<?>) obj).size() != 0){
                        field1.setNext_depth( depth +1 );
                    }
                }
            }
            if (functionClass instanceof ClassField) {
                getClassFields().add( field1 );
            }
            if (functionClass instanceof InputMeta) {
                InputMeta im = (InputMeta) functionClass;
                im.getFields().add(field1);
            }
            if (functionClass instanceof OutputMeta) {
                OutputMeta op = (OutputMeta) functionClass;
                op.getFields().add(field1);
            }
            return false;
        }else{
            return false;
        }
    }

    private void ListHandlingActionObject(Object obj, Class<?> type, int depth, Object functionClass) {

            ClassField field1 = new ClassField();
            field1.setSuper_uuid( getUuid() );

            if (depth == 0) {
                field1.setUuid( TraceCommonUtil.makeUuid() );
                field1.setSuper_uuid( getUuid() );
            }
            field1.setFieldTypeFullName( type.getTypeName());

            field1.setDepth( depth );
            field1.setNext_depth( depth );
            field1.setFieldType( type );
            field1.setFieldAnnotation( getAnnotationString( type ) );
            field1.setFieldTypeName( type.getTypeName() );
            field1.setSimpleFieldTypeName( type.getSimpleName() );
            field1.setFieldVariable( type.getName() );
            field1.setFieldKind( TraceCommonUtil.getFieldKind( type, obj ) );
            field1.setFieldObject( obj );
            //매우 중요
            if (obj != null ) {
                if(obj instanceof Collection<?>){
                    if(((Collection<?>) obj).size() != 0){
                        field1.setNext_depth( depth +1 );
                    }
                }
            }
            if (functionClass instanceof ClassField) {
                getClassFields().add( field1 );
            }
            if (functionClass instanceof InputMeta) {
                InputMeta im = (InputMeta) functionClass;
                im.getFields().add(field1);
            }
            if (functionClass instanceof OutputMeta) {
                OutputMeta op = (OutputMeta) functionClass;
                op.getFields().add(field1);
            }
    }
    public void makeFieldDepthAction(ClassField inClass,Object obj, Class<?> type,int depth,Object functionClass,int listFlag){

        List<ClassField> existList = new ArrayList<>();
        if(functionClass instanceof ClassField ) {
            for (ClassField classField : getClassFields()) {
                existList.add( classField );
            }
        }
        else if(functionClass instanceof InputMeta ) {
            for (ClassField classField : ((InputMeta) functionClass).getFields()) {
                existList.add( classField );
            }
        }
        else if(functionClass instanceof OutputMeta ) {
            for (ClassField classField : ((OutputMeta) functionClass).getFields()) {
                // classField.setSuper_uuid( getUuid() );
                existList.add( classField );
            }
        }else{
            log.info( "지원하지 않는 Class입니다. functionClass ={} ", functionClass );
            return;
        }

        if(ListHandlingAction(obj, type, depth, functionClass )){
            return;
        }else{
            if(obj != null && obj instanceof Collection<?>){
                ListHandlingActionObject(obj, type, depth, functionClass );
                return;
            }

        }
        //List 처리 End
        for(Field field :type.getDeclaredFields()) {
            Object field_object;
            Class<?> field_type;
            String field_typeName;
            String beforeFullName = null;

            ClassField field1 = new ClassField();

            field1.setUuid( TraceCommonUtil.makeUuid() );
            for (ClassField classField : existList) {
                if(classField.getUuid().equals(inClass.getUuid())){
                    field1.setSuper_uuid( inClass.getSuper_uuid() );
                    if(listFlag == 0) {
                        beforeFullName = inClass.getFieldTypeFullName();
                    }else{
                        beforeFullName = inClass.getFieldTypeFullName() + "." + obj.getClass().getTypeName() ;
                    }

                    //이전 depth를 맞추기 위한 데이타를 List에 Add해준다.
                    beforeDepthFind(classField,functionClass,depth);
                    break;
                }
            }
            if (beforeFullName == null) {
                log.info("존재하지 않는 ClassField 입니다.");
                return;
            }

            try {
                field_type = field.getType();
                field_typeName = field.getType().getTypeName() ;
                field1.setDepth( depth );
                field1.setNext_depth( depth );
                field1.setFieldType( field_type );
                field1.setFieldAnnotation( getAnnotationString( field_type ) );
                field1.setFieldTypeName( field_typeName );
                field1.setSimpleFieldTypeName( field_type.getSimpleName() );
                field1.setFieldVariable( field.getName() );
                field1.setFieldTypeFullName( beforeFullName +"." + field.getType().getTypeName() );
                try{
                    field.setAccessible(true);
                }catch(Exception field_exception){
                    field1.setFieldKind( TraceCommonUtil.getFieldKind(field_type, null) );
                    field1.setFieldObject( "No-access" );
                    fieldAdd( functionClass, field1 );
                    continue;
                }

                if (obj == null) {
                    field1.setFieldKind( TraceCommonUtil.getFieldKind(field_type, null) );
                    fieldAdd( functionClass, field1 );
                    continue;
                }
                field_object = field.get( obj );
                field1.setFieldKind( TraceCommonUtil.getFieldKind(field_type, field_object) );
                field1.setFieldObject( field_object );

                if(field_object != null){
                    if(field_object instanceof  Collection<?> ){
                        if(((Collection<?>) field_object).size() ==0){
                            fieldAdd( functionClass, field1 );
                        //    log.info( "size=0, functionClass ={}, field1 ={} ", functionClass.getClass(), field1 );
                        }else{
                            field1.setDepth( depth );
                            field1.setNext_depth( depth + 1 );
                            fieldAdd( functionClass, field1 );
                        }
                    }else if(field_type.isPrimitive()){
                 //       log.info( "isPrimitive add" );
                        fieldAdd( functionClass, field1 );
                    }else if(isWrapperType(field_type)){
                 //       log.info( "WrapperType add" );
                        fieldAdd( functionClass, field1 );
                    }else{

                        field1.setDepth( depth );
                        field1.setNext_depth( depth + 1 );

                        fieldAdd( functionClass, field1 );

                    }
                }else{
                    fieldAdd( functionClass, field1 );
                }
            } catch (Exception e) {
                log.info( "Exception e ={}", e.getMessage() );
            }

        }
    }

    private boolean beforeDepthFind(ClassField classField, Object functionClass, int depth) {
        ClassField updateClassField = classField;
        int  beforeDepth = classField.getDepth();
        int beforeNext_depth = classField.getNext_depth();

        if (beforeDepth != beforeNext_depth && beforeNext_depth == depth) {
            getDepthHandler().put( updateClassField,updateClassField );
        }
        return true;

    }

    public void beforeDepthHandling(Object functionClass, int depth) {
        Map<ClassField, Object> depthHandler1 = getDepthHandler();

       if(functionClass instanceof ClassField){
           Object find = depthHandler1.get( functionClass );
           ClassField findClassField = (ClassField) find;
           ClassField classField = (ClassField) functionClass;

           if(find!=null) {
               findClassField.setDepth( depth );
               getClassFields().remove(classField);
               getClassFields().add( findClassField );
           }
       }
       if(functionClass instanceof InputMeta){
           InputMeta inputMeta = (InputMeta) functionClass;
           List<ClassField> fields = inputMeta.getFields();
           for (ClassField field : fields) {
               Object find= depthHandler1.get( field );
               ClassField findField = (ClassField) find;
               if(find!=null )  {
                   findField.setDepth( depth );
                   inputMeta.getFields().remove( field );
                   inputMeta.getFields().add( findField );
                   break;
               }
           }

       }
       if (functionClass instanceof OutputMeta) {
            OutputMeta outputMeta = (OutputMeta) functionClass;
            List<ClassField> fields = outputMeta.getFields();
            for (ClassField field : fields) {
                Object find= depthHandler1.get( field );
                ClassField findField = (ClassField) find;
                if(find!=null) {
                     findField.setDepth( depth );
                    outputMeta.getFields().remove( field );
                    outputMeta.getFields().add( findField );
                    break;
                }
            }
        }

    }
    public void fieldAdd(Object functionClass, ClassField field) {
        if (functionClass instanceof ClassField) {
            getClassFields().add( field );
        }
        if (functionClass instanceof InputMeta) {
            InputMeta im = (InputMeta) functionClass;
            im.getFields().add(field);
           // log.info( "여기를 또 들어온다. ==================" );
        }
        if (functionClass instanceof OutputMeta) {
            OutputMeta op = (OutputMeta) functionClass;
            op.getFields().add(field);
        }
    }

    public void makeException(String exceptionClass, String exceptionMessage) {
        if (exceptionClass == null || exceptionClass.isEmpty()) {
            this.exceptionClass = exceptionClass;
        }else{
            this.exceptionClass +="," + exceptionClass;
        }

        if (exceptionMessage == null || exceptionMessage.isEmpty()) {
            this.exceptionMessage = exceptionMessage;
        }else{
            this.exceptionMessage +="," + exceptionMessage;
        }
    }

}
