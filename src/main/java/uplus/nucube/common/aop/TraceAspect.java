package uplus.nucube.common.aop;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.stereotype.Component;

@Slf4j
@Aspect
@Component
public class TraceAspect {

    @Before("@annotation(trace)")
    public void doTrace(JoinPoint joinPoint,Trace trace) {

        Object[] args = joinPoint.getArgs();
        log.info("[trace] {} args={}",joinPoint.getSignature(), args);
    }
}
