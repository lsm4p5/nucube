package uplus.nucube.common.trace.aspect;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import uplus.nucube.common.trace.TraceStatus;
import uplus.nucube.common.trace.data.AopTraceData;
import uplus.nucube.common.trace.data.DynamicClass;
import uplus.nucube.common.trace.data.db.AopTraceEntity;

import uplus.nucube.common.trace.logtrace.LogTrace;

import java.util.Collection;
import java.util.Map;


@Aspect
@Slf4j
//@Component
public class LogTraceAspectOld2 {

    private final LogTrace logTrace;

    private final ObjectMapper mapper;

//    private final AopTraceEntityRepository aopTraceEntityRepository;




    // 현재 BasePackage = uplus.nucube 임.
    private final String includePointcut = "execution(* uplus.nucube.*..*(..)) ";
    private final String excludePointcut = "!execution(* uplus.nucube.common.trace.*..*(..))";
    private final String excludePointcut2 = "!execution(* uplus.nucube.common.bean*.*..*(..))";



    public LogTraceAspectOld2(LogTrace logTrace, ObjectMapper mapper) {
      //      , AopTraceEntityRepository aopTraceEntityRepository) {
        this.logTrace = logTrace;
        this.mapper = mapper;
       // this.aopTraceEntityRepository = aopTraceEntityRepository;
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

        AopTraceData aopTraceData = new AopTraceData();

        TraceStatus status = null;
        message = "[" + joinPoint.getSignature().toLongString() +"]";



        try {

        //    log.info("Aop start ------");
            status = logTrace.begin( message );

            aopTraceData.makeMethodInfo(joinPoint, status);

            proceed = joinPoint.proceed();

            if (proceed == null) {
            }else{

                Class<?> type = proceed.getClass();
                String typeName = proceed.getClass().getTypeName();
                String paraName = proceed.getClass().getName();
                DynamicClass dynamicObject = new DynamicClass();
                dynamicObject.makeDynamicClass(proceed,type,2);
                aopTraceData.addFunctionMap( dynamicObject,typeName+"."+paraName);

                if(proceed instanceof Collection<?>) {
                    Collection<Object> collect = (Collection<Object>) proceed;
                    aopTraceData.setFunctionReturnCount( collect.size() );
                }
               // dynamicObject.printDynamicClass();
            }

            logTrace.end( status );

            return proceed;

        } catch (Exception e) {

           logTrace.exception( status,e );
           aopTraceData.addExceptionClass(e.getClass().toString(), e.getMessage());
           throw  e ;

        }finally {
            Long stopTimeMs = System.currentTimeMillis();
            long resultTimeMs = stopTimeMs - status.getStartTimeMs();
            aopTraceData.setDurationTimeMs( resultTimeMs );
            aopTraceData.printAopTraceData();
           // log.info( "aopTraceData = {}", aopTraceData );
            String mapperString = mapper.writeValueAsString( aopTraceData );
            log.info( "aopTraceData-mapping = {}", mapperString );

            try {
                //AopTraceEntity를 만든다.
                Map<DynamicClass, String> dynamicClass = aopTraceData.getDynamicClass();
                for (DynamicClass aClass : dynamicClass.keySet()) {
                    AopTraceEntity traceEntity = aopTraceData.makeAopTraceEntityData( aClass );
               //     aopTraceEntityRepository.save( traceEntity );
                }
            } catch (Exception err) {
                log.info( "Exception err = {}", err.getMessage() );
            }
        }
    }



}

