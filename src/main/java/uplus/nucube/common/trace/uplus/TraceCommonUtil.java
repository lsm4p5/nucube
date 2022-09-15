package uplus.nucube.common.trace.uplus;
import lombok.Getter;
import org.springframework.util.StringUtils;

import java.lang.annotation.Annotation;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class TraceCommonUtil {

    public static final Set<Class<?>> WRAPPER_TYPES = getWrapperTypes();
    public static final int STRING_LIMIT = 255;
    public static String[] getAnnotationArray(Class<?> classType) {
        Annotation[] annotations = classType.getAnnotations();
        int i = 0;
        String[] anno = new String[annotations.length];
        for (Annotation annotation : annotations) {
            anno[i] = annotation +"";
            i++;
        }
        return anno;
    }
    public static String getAnnotationString(Class<?> classType) {
        String anno ="";
        Annotation[] annotations = classType.getAnnotations();
        for (Annotation annotation : annotations) {
            anno = annotation +"";
        }
        return anno;
    }
    public static String getClassKinds(Class<?> classType) {

        String kinds = "";
        String anno="";
        Annotation[] annotations = classType.getAnnotations();
        for (Annotation annotation : annotations) {
           anno += annotation;
        }
        if(anno.contains( "Controller" )){
            kinds +="Controller,";
        }else if(anno.contains("Repository")){
            kinds += "Reporsitory,";
        }else if(anno.contains("Service")){
            kinds = "Service,";
        }else if(anno.contains("Component")){
            kinds += "Component,";
        }else{
            kinds += "";
        }
        return kinds;
    }
    public static String getLimitString(String str) {
        if (str == null) {
            return null;
        }
        String ret;
        if (str.length() > STRING_LIMIT) {
            ret = str.substring( 0, STRING_LIMIT );
        }else{
            ret = str;
        }
        return ret;
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
    public static int getFieldKind(Class<?> field_type, Object field_object) {

        if (field_type == null) {
            return 0;
        }
        else if(field_type.isPrimitive()){
            return 1;
        }
        else if(isWrapperType( field_type )){
            return 2;
        }
        else if (field_object == null || field_type == null) {
            return 0;
        }
        else if(field_object instanceof Collection<?>){
            return 4;
        }else{
            return 3;
        }

    }
    public static String makeUuid(){
        return UUID.randomUUID().toString().substring( 0,8);
    }

    public static String lastIndexOf(String inStr) {
        if (inStr == null) {
            return null;
        }
        String str =  inStr;
        int pos = str.lastIndexOf( "." );
        return str.substring( pos + 1 );
    }

    public static String dateCompareRet(String date) {
        String localDateTimeFormat = LocalDateTime.now().format( DateTimeFormatter.ofPattern( "yyyyMMdd" ));
        if (!StringUtils.hasText(date)) {
            return localDateTimeFormat;
        }
        if(!digitOnlyString(date)){
            return localDateTimeFormat;
        }
        return date;
    }

    // length가 8이고 숫자로만 구성되어야 함.
    public static boolean digitOnlyString(String s) {
        char tmp;
        if (s.length()==8){
            for (int i =0; i<s.length(); i++){
                tmp = s.charAt(i);
                if(Character.isDigit(tmp)==false){
                    return false;}
            }
        }
        else return false;
        return true;
    }



}
