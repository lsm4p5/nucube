package uplus.nucube.common.trace.advice;

import lombok.extern.slf4j.Slf4j;
import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;
import uplus.nucube.common.trace.TraceStatus;
import uplus.nucube.common.trace.logtrace.LogTrace;

import java.lang.reflect.Method;

@Slf4j
public class LogTraceAdvice implements MethodInterceptor {

    private final LogTrace logTrace;

    public LogTraceAdvice(LogTrace logTrace) {
        this.logTrace = logTrace;
    }

    @Override
    public Object invoke(MethodInvocation invocation) throws Throwable {

        TraceStatus status = null;
        try {
            Method method = invocation.getMethod();
            String message = method.getDeclaringClass().getSimpleName()+"."+method.getName()+"()";
            log.info( "message={},proxyFactory", message );
            status = logTrace.begin( message );
            // 실제 로직 호출
            Object result = invocation.proceed();

            logTrace.end( status );

            return result;

        } catch (Exception e) {
            logTrace.exception( status ,e);
            throw e;
        }
    }
}