package uplus.nucube.common.trace.aspect;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;
import uplus.nucube.common.trace.TraceStatus;
import uplus.nucube.common.trace.dto.TraceInOutInfo;
import uplus.nucube.common.trace.dto.TraceMethodDto;
import uplus.nucube.common.trace.dto.db.ClientEntity;

import uplus.nucube.common.trace.logtrace.LogTrace;


@Aspect
@Slf4j
public class LogTraceAspectOld {

    private final LogTrace logTrace;

    private final ObjectMapper mapper;





    // 현재 BasePackage = uplus.nucube 임.
    private final String includePointcut = "execution(* uplus.nucube.*..*(..)) ";
    private final String excludePointcut = "!execution(* uplus.nucube.common.trace.*..*(..))";
    private final String excludePointcut2 = "!execution(* uplus.nucube.common.bean*.*..*(..))";



    public LogTraceAspectOld(LogTrace logTrace, ObjectMapper mapper) {
        this.logTrace = logTrace;
        this.mapper = mapper;
   }


    @Pointcut("execution(* *..*" + "Service.*(..))")
    public void allService() {}

    @Pointcut(includePointcut)
    public void setIncludePointcut() {}

    @Pointcut(excludePointcut)
    public void setExcludePointcut() {}

    @Pointcut(excludePointcut2)
    public void setExcludePointcut2() {}




    @Around("setIncludePointcut() && setExcludePointcut() && setExcludePointcut2()")
    public Object doExceptionCatch(ProceedingJoinPoint joinPoint) throws Throwable {
        String methodName = "" ;
        String message;
        Object proceed = null;

        TraceInOutInfo traceInOutInfo = new TraceInOutInfo();
        TraceMethodDto traceMethodDto = new TraceMethodDto();

        TraceStatus status = null;
        message = "[" + joinPoint.getSignature().toLongString() +"]";



        try {

        //    log.info("Aop start ------");
            status = logTrace.begin( message );

            traceInOutInfo.callMethodInfo(joinPoint, status);

            proceed = joinPoint.proceed();
       //     log.info("Aop end------");

            if (proceed == null) {
            }else{

                String returnAnnotation = traceInOutInfo.getAnnotation( proceed.getClass(),proceed, "RETURN" );
                Class<?> type = proceed.getClass();
                String typeName = proceed.getClass().getTypeName();
                String paraName = proceed.getClass().getName();
                traceInOutInfo.makeDepthFirstArg(type,typeName,paraName, proceed,"RETURN");
            }

            logTrace.end( status );

            return proceed;

        } catch (Exception e) {


           traceInOutInfo.addExceptionClass( e.getClass().toString(), e.getMessage() );
           logTrace.exception( status,e );
           throw  e ;

        }finally {
            Long stopTimeMs = System.currentTimeMillis();
            long resultTimeMs = stopTimeMs - status.getStartTimeMs();
            traceInOutInfo.setDurationTimeMs( resultTimeMs );
            //traceInOutInfo.printAll();
            ClientEntity clientEntity = traceInOutInfo.makeClientData( mapper );
            System.out.println( "clientEntity = " + clientEntity );
            try {

//                EntityManagerFactory factory = Persistence.createEntityManagerFactory(PERSISTENCE_UNIT_NAME);
//                EntityManager em = factory.createEntityManager();
//                System.out.println( "em = " + em );
//                em.getTransaction().begin();
//                em.persist( clientEntity );
//                em.getTransaction().commit();
//                em.flush();
//                em.clear();

            } catch (Exception ee) {
                log.info( "DB 저장 오류 ee = {}", ee.getMessage() );
            }

        }
    }



}

