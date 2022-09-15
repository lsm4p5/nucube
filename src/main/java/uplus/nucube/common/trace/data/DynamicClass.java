package uplus.nucube.common.trace.data;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;

@Data
@Slf4j
public class DynamicClass {


    private static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();
    public static final String Type_Name_Delimiter ="|";
    Class<?> classType;
    Object classObject;
    String typeName;
    String typeVariableName;
    String objectValue;

    int objectDelimiter ;  // 1이면 arg, 2이면 retrun

    int objectType ;  // 1이면 primitive, 2이면 Wrapper 3이면 일반 객체 , 4이면 컬렉션

    String dynamicClassAnnotation = "";

    /* 필드 Name, 필드 value */
    Map<String, Object> fields = new HashMap<>();
    Map<String, String> fieldsAnnotation = new HashMap<>();
    public void addFields(String key, Object value){
        fields.put( key, value );
    }

    public void addFieldsAnnotation(String key, String value) {
        fieldsAnnotation.put( key, value );
    }

    public void makeDynamicClass(Object obj, Class<?> type,int objectDelimiter){
        if( type == null ){
            log.info( "Dynamic class create fail" );
        }
        this.objectDelimiter = objectDelimiter ; //1 or 2 임.
        this.classObject= obj;
        this.classType = type;
        this.typeName = type.getTypeName();
        this.typeVariableName = type.getName();
        Annotation[] annotations = type.getAnnotations();
        for (Annotation annotation : annotations) {
            dynamicClassAnnotation += annotation;
        }

        if(type.isPrimitive()){
            objectType = 1;
        }else if(isWrapperType(type)){
            objectType =2;
        }else {
            // obj가 null이면 필드를 구하지 않음.
            if (obj != null) {
                objectValue = obj +"";
                if(obj instanceof Collection<?>){
                    Collection<Object> collect = (Collection<Object>) obj;
                    int i=0;
                    for (Object o : collect) {
                        log.info( "makeField ={}", o );
                        makeField( o, o.getClass() , obj);
                        if (i == 0) {
                            Annotation[] annotations1 = o.getClass().getAnnotations();
                            String fieldAnnotation = "";
                            for (Annotation annotation : annotations1) {
                                fieldAnnotation += annotation;
                            }
                            if (fieldAnnotation.length() > 0) {
                                addFieldsAnnotation(type.getTypeName()+"."+ type.getName()+"'"+ getFieldAnnotationName( o.getClass().getTypeName(), o.getClass().getName() ), fieldAnnotation );
                            }
                        }
                        i++;
                    }
                    objectType = 4;
                }else{
                    makeField( obj, type,null );
                    objectType = 3;
                }
            }

        }
    }

    public void makeField(Object obj, Class<?> type,Object before_obj) {
        // obj == null 이면 필드 정보를 만들지 않음.
        String before_type;

        // before_obj != before_obj는 컬렉션임.
        if (before_obj != null) {
            before_type = before_obj.getClass().getTypeName() +".";
        }
        else {
            before_type = "";
            String fieldAnnotation = "";
            Annotation[] annotations = type.getAnnotations();
            for (Annotation annotation : annotations) {
                fieldAnnotation += annotation;
            }
            if (fieldAnnotation.length() > 0) {
                addFieldsAnnotation( before_type + getFieldAnnotationName( type.getTypeName(),type.getName() ), fieldAnnotation );
            }
        }

        if (obj == null) {
            addFields( before_type + getFieldName( type.getTypeName(),type.getName() ), "make-object=null");
            return;
        }
        String typeName = type.getTypeName();
        String paraName = type.getName();
        for(Field field :type.getDeclaredFields()) {
            Object field_object;
            Class<?> field_type;
            String field_typeName;
            String field_paraName;


            try {
                field_type = field.getType();
                field_typeName = field.getType().getTypeName() ;
                field_paraName = field.getName();
                try{
                    field.setAccessible(true);
                }catch(Exception field_exception){
                    addFields( before_type + typeName +"." + getFieldName( field_typeName,field_paraName ), "NOT_ACCESS" );
                    continue;
                }
                // fieldAnnotation 추가 시작
                String fieldAnno = "";
                Annotation[] annotations = field_type.getAnnotations();
                for (Annotation annotation : annotations) {
                    fieldAnno += annotation;
                }
                if (fieldAnno.length() > 0) {
                    addFieldsAnnotation( before_type + typeName +"." + getFieldAnnotationName( field_typeName,field_paraName ), fieldAnno );
                }
                // fieldAnnotation 추가 종료

                field_object = field.get( obj );

                if(field_object instanceof  Collection<?> ){
                    log.info("makeField-field_object Collection ={} ",field_object);
                    if(field_object != null && ((Collection<?>) field_object).size() ==0){
                        log.info("makeField-field_object Collection ={} ",((Collection<?>) field_object).size());
                        addFields( before_type + typeName +"." + getFieldName( field_typeName,field_paraName ), field_object );
                    }
                    //Start
                    Collection<Object> collect = (Collection<Object>) field_object;

                    for (Object o : collect) {
                        log.info("makeField-field_object Collection o ={} ",o);
                        Class<?> oType = o.getClass();
                        String oTypeName = oType.getTypeName();
                        String oParaName = oType.getName();
                        if (o == null) {
                            addFields(before_type +typeName +"." + field_typeName+"." + getFieldName( oTypeName,oParaName ),"o=null" );
                        }else if (oType.isPrimitive() || isWrapperType( oType )) {
                            addFields(before_type+field_typeName+"." + getFieldName( oTypeName,oParaName ),o);
                        }else if(o instanceof Collection<?>){
                            //2번재도 Collection 인경우에는 어떻게 그냥 필드만 추가 해준다.
                            log.info("2번째도 Collection이네요..");
                            addFields(before_type+typeName +"." +field_typeName+"." + getFieldName( oTypeName,oParaName ),o);
                        }else{
                            makeField_Normal(o, oType,oTypeName, oParaName, field_object,2);
                        }

                    }
                    //End

                }else{
                    // Primitive, WrapperType인경우
                    if(field_type.isPrimitive() || isWrapperType(field_type)){
                       // log.info("type is primitive, isWrapperTyp임");
                        addFields( before_type+typeName +"." +getFieldName( field_typeName,field_paraName ), field_object );

                    }
                    else{
                        //일반 객체인 경우임. interface 인경우도 구분할수 있을까?
                       makeField_Normal(field_object, field_type,field_typeName, field_paraName, obj,1);
                    }
                }
            } catch (Exception e) {
                log.info( "Eeception e ={}", e.getMessage());

            }
        }
        ///////////////////////////////////////////////////////////
    }

    public void makeField_Normal(Object obj,Class<?> type,String typeName, String paraName, Object before_obj, int before_seq) {

        String before_type ="";
        if(before_seq == 1 ){
            before_type = before_obj.getClass().getTypeName() +".";
        }else{
            before_type = classType +"." +  before_obj.getClass().getTypeName() +".";
        }

        if (obj == null) {
            addFields( before_type + getFieldName( typeName,paraName ), "Normal obj=null" );
            return;
        }

        for(Field field :type.getDeclaredFields()) {
            Object field_object;
            Class<?> field_type;
            String field_typeName;
            String field_paraName;

            try {
                field_type = field.getType();
                field_typeName = field.getType().getTypeName() ;
                field_paraName = field.getName();
                try{
                    field.setAccessible(true);
                }catch(Exception field_exception){
                    addFields(before_type + typeName +"." +getFieldName( field_typeName,field_paraName ), "NOT_ACCESS" );
                    continue;
                }
                field_object = field.get( obj );

                if(field_object instanceof  Collection<?> ){
                  log.info("makeField_Normal-field_object Collection ={} ",field_object);
                  // 객체가 Collection을 가지고 있는 경우는 ?
                  addFields( before_type + typeName +"." +getFieldName( field_typeName,field_paraName ), field_object );


                }else{
                    // Primitive, WrapperType인경우
                    if(field_type.isPrimitive() || isWrapperType(field_type)){
                      //  log.info("type is primitive, isWrapperTyp임");
                        addFields( before_type + typeName +"." +getFieldName( field_typeName,field_paraName ), field_object );

                    }
                    else{

                        addFields( before_type + typeName +"." +getFieldName( field_typeName,field_paraName ), field_object );
                        //일반 객체인 경우임. interface 인경우도 구분할수 있을까?
                        // 더이상 진행하지 않는다. 진행을 한다면 고려할것이 많다.

                    }
                }
            } catch (Exception e) {
                log.info( "Eeception e ={}", e.getMessage());

            }
        }
    }



    public void printDynamicClass() {
        log.info( "[start] ===============" );
        log.info( "classType ={}", classType );
        log.info( "classObject = {}", classObject );
        log.info( "typeName ={}", typeName );
        log.info("typeVariableName = {}",typeVariableName);
        log.info( "objectDelimiter = {}", objectDelimiter );
        log.info( "objectValue = {} ", objectValue );
        log.info( "objectType = {}", objectType );
        log.info( "dynamicClassAnnotation = {}", dynamicClassAnnotation );
        Map<String, Object> fields1 = getFields();
        for (String s : fields1.keySet()) {
            log.info( "fields key ={}, value={}", s, fields1.get( s ) );
        }
        Map<String, String> fieldsAnno = getFieldsAnnotation();
        for (String s : fieldsAnno.keySet()) {
            log.info( "fields annotation key ={}, value={}", s, fieldsAnno.get( s ) );
        }
        log.info( "[end]================" );

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

    public String lastIndexOf(String inStr,String delimiiter) {
        if (inStr == null) {
            return null;
        }
        String str =  inStr;
        int pos = str.lastIndexOf( delimiiter );
        return str.substring( pos + 1 );
    }

    private String getTypeNameValue(String typeName, String paraName) {
        String uuid_seq = UUID.randomUUID().toString().substring( 0,8 );
        String typeName_paraName = typeName +Type_Name_Delimiter + paraName +Type_Name_Delimiter + uuid_seq;
        return typeName_paraName;
    }

    private String getFieldName(String typeName, String paraName) {
        return typeName +"." + paraName +"|" +UUID.randomUUID().toString().substring( 0,8 ) ;
    }

    private String getFieldAnnotationName(String typeName, String paraName) {
        return typeName +"." + paraName  ;
    }

}
