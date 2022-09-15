package uplus.nucube.common.beanpostprocessor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static java.lang.reflect.Modifier.isFinal;

@Slf4j
public class BeanFindMemberVariable implements BeanPostProcessor {

    private final String basePackage = "uplus.nucube";
    /**
     * ServletAppliation은 springBoot 시작 클래스
     */
    private final String[] excludePackage = {
             "org.springframework",
             "ServletApplication",
             "com.fasterxml.jackson",
             "common.trace",
             "EntityManager",
             "$$EnhancerBySpringCGLIB$$"};

    @Autowired
    private Environment environment;


    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {

        List<Annotation> annotations = new ArrayList<>();
        Field[] allFields = getAllFieldsNotMemberVariable( bean,annotations );
        for (Field beanField : allFields) {
            log.info( " ==============================================" );
            log.error( "Field=[{}],bean=[{}] beanName=[{}]",beanField,bean.getClass(),beanName);
            log.info( "annotation size = {}", annotations.size() );
            for (Annotation annotation : annotations) {
                log.info( "annotation = {}", annotation.annotationType().toString() );
            }
        }
        return bean;
    }

    // 상위 클래스까지 탐색하여 얻어 온다.
    public Field[] getAllFieldsNotMemberVariable(Object obj, List<Annotation> annotations) {

        Class<?> cls = obj.getClass();

        Annotation[] annotations1 = cls.getAnnotations();
        for (Annotation annotation : annotations1) {
            annotations.add( annotation );
        }

        List<Object> accum = new LinkedList<Object>();

        while ( cls != null ) {
            Field[] f = cls.getDeclaredFields();
            for (Field field : f) {
                if (getFieldNotMemberVariable( field )) {
                    //필드의 제외 조건이 true 이면 리스트에 추가 하지 않음
                    continue;
                }
                accum.add( field );
            }
            cls = cls.getSuperclass();
        }

        return (Field[])accum.toArray(new Field[accum.size()]);
    }

    public boolean getFieldNotMemberVariable(Field field) {

        int modify = field.getModifiers();
        if(isFinal(modify)) {
            return true;
        }
        if(!field.toString().contains(basePackage)){
            return true;
        }
        for (String s : excludePackage) {
            if(field.toString().contains(s)){
                return true;
            }
        }

        return false;
    }
}


/**
 static boolean isPublic(int mod)
 static boolean isPrivate(int mod)
 static boolean isProtected(int mod)
 static boolean isStatic(int mod)
 static boolean isFinal(int mod)
 static boolean isSynchronized(int mod)
 static boolean isVolatile(int mod)
 static boolean isNative(int mod)
 static boolean isInterface(int mod)
 static boolean isTransient(int mod)
 static boolean isAbstract(int mod)
 static boolean isStrict(int mod)
 */