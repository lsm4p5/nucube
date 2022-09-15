package uplus.nucube.basictest;

import org.junit.jupiter.api.Test;
import uplus.nucube.domain.Member;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class BasicTest {

    @Test
    void basicTest() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException {
        Member member = new Member("name1",null);
        Object obj = member;

        Class c = member.getClass();



        Method[] m = c.getMethods();
        for (Method method : m) {
            System.out.println( "method = " + method );
            if(method.getName().toString().startsWith("get" )) {
                String name = member.getName();
                System.out.println( "method.getName() = " + method.getName());
                Method method1 = c.getMethod( method.getName() );
               // Object invoke = method1.invoke( obj, null );
                Object invoke = method1.invoke( obj );
                System.out.println( "invoke = " + invoke );
            }
        }
        Field[] f = c.getFields();
        for (Field field : f) {
            System.out.println( "field = " + field );
        }
        Constructor[] cs = c.getConstructors();
        for (Constructor constructor : cs) {
            System.out.println( "constructor = " + constructor );
        }
        Class[] inter = c.getInterfaces();
        for (Class aClass : inter) {
            System.out.println( "aClass = " + aClass );
        }
        Class superClass = c.getSuperclass();
        System.out.println( "superClass = " + superClass );


    }
}
