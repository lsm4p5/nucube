package uplus.nucube.common.trace.aspect;


import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

import org.springframework.stereotype.Component;

import org.springframework.transaction.annotation.Transactional;
import uplus.nucube.common.trace.TraceStatus;

import uplus.nucube.common.trace.logtrace.LogTrace;
import uplus.nucube.common.trace.uplus.AopClassInfo;
import uplus.nucube.common.trace.uplus.InputMeta;
import uplus.nucube.common.trace.uplus.OutputMeta;
import uplus.nucube.common.trace.uplus.TraceCommonUtil;
import uplus.nucube.common.trace.uplus.db.entity.*;
import uplus.nucube.common.trace.uplus.db.repository.*;

import java.util.List;
import java.util.Optional;


@Aspect
@Slf4j
@Component
public class LogTraceAspect {

    private final LogTrace logTrace;

    private final ObjectMapper mapper;

    private final AopClassInfoEntityRepository aopClassInfoEntityRepository;
    private final InputMetaEntityRepository inputMetaEntityRepository;
    private final OutputMetaEntityRepository outputMetaEntityRepository;

    private final TraceDataInfoEntityRepository traceDataInfoEntityRepository;

    private final TraceEntityRepository traceEntityRepository;





    // 현재 BasePackage = uplus.nucube 임.
    private final String includePointcut = "execution(* uplus.nucube.*..*(..)) ";
    private final String excludePointcut = "!execution(* uplus.nucube.common.trace.*..*(..))";
    private final String excludePointcut2 = "!execution(* uplus.nucube.common.bean*.*..*(..))";



    public LogTraceAspect(LogTrace logTrace, ObjectMapper mapper,
                          InputMetaEntityRepository inputMetaEntityRepository,
                          OutputMetaEntityRepository outputMetaEntityRepository,
                          TraceDataInfoEntityRepository traceDataInfoEntityRepository,
                          TraceEntityRepository traceEntityRepository,
                          AopClassInfoEntityRepository aopClassInfoEntityRepository) {
        this.logTrace = logTrace;
        this.mapper = mapper;
        this.aopClassInfoEntityRepository = aopClassInfoEntityRepository;
        this.inputMetaEntityRepository=inputMetaEntityRepository;
        this.outputMetaEntityRepository=outputMetaEntityRepository;
        this.traceDataInfoEntityRepository = traceDataInfoEntityRepository;
        this.traceEntityRepository = traceEntityRepository;

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

        AopClassInfo aopClassInfo = new AopClassInfo();

        TraceStatus status = null;
        message = "[" + joinPoint.getSignature().toLongString() +"]";




        try {
            status = logTrace.begin( message );

            //AopClassInfo header & Arg 저장
            aopClassInfo.makeBeanInfo( joinPoint ,status);

            //AopClassInfo Input 저장
            aopClassInfo.makeInputInfo( joinPoint );

            proceed = joinPoint.proceed();
            if (proceed == null) {
            }else{

                Class<?> type = proceed.getClass();
                String typeName = proceed.getClass().getTypeName();
                String paraName = proceed.getClass().getName();



            }

            logTrace.end( status );

            return proceed;

        } catch (Exception e) {

           logTrace.exception( status,e );
           aopClassInfo.makeException( e.getClass() +"", e.getMessage() );
           throw  e ;

        }finally {

            finallyAction( aopClassInfo, status,proceed);

        }
    }

    @Transactional
    private void finallyAction(AopClassInfo aopClassInfo, TraceStatus status,Object proceed) {

        Long stopTimeMs = System.currentTimeMillis();
        long resultTimeMs = stopTimeMs - status.getStartTimeMs();
        aopClassInfo.setDurationTimeMs( resultTimeMs );

        // aopClassInfo 전체 출력
       // aopClassInfo.printAopClassInfo();
        AopClassInfoEntity infoEntity = new AopClassInfoEntity();
        infoEntity.makeInfoEntity( aopClassInfo );

        aopClassInfoEntityRepository.save( infoEntity );

        List<InputMeta> inputMetas = aopClassInfo.getInputMetas();
        for (InputMeta inputMeta : inputMetas) {
            InputMetaEntity inputMetaEntity = new InputMetaEntity();
            inputMetaEntity.makeInputEntity( inputMeta );
            // 연관관계 설정
            inputMetaEntity.changeAopClass(infoEntity);
            inputMetaEntityRepository.save( inputMetaEntity );

        }

        if (proceed != null) {

            aopClassInfo.makeReturnInfo( proceed );

            List<OutputMeta> outputMetas = aopClassInfo.getOutputMetas();
            for (OutputMeta outputMeta : outputMetas) {
                OutputMetaEntity outputMetaEntity = new OutputMetaEntity();
                outputMetaEntity.makeOutputEntity( outputMeta );
                //연관관계 설정
                outputMetaEntity.changeAopClass(infoEntity);
                outputMetaEntityRepository.save( outputMetaEntity );

            }
        }

        // traceEntity Data를 넣어준다.

        TraceEntity traceEntity = new TraceEntity();
        traceEntity.makeTraceEntity( infoEntity );

        Optional<TraceDataInfoEntity> byId = traceDataInfoEntityRepository.findById( traceEntity.getUuid() );
        if(byId.isEmpty()){
            TraceDataInfoEntity traceDataInfoEntity = new TraceDataInfoEntity();
            traceDataInfoEntity.setId( traceEntity.getUuid() );
            //화면ID, APIID, 도메인을 Setting한다.
            traceDataInfoEntity.setDomainId(TraceCommonUtil.makeUuid() );
            traceDataInfoEntity.setApiId( TraceCommonUtil.makeUuid() );
            traceDataInfoEntity.setViewId( TraceCommonUtil.makeUuid() );


            traceDataInfoEntity.getTraceEntities().add( traceEntity );
            traceEntity.setTraceDataInfoEntity( traceDataInfoEntity );
            traceDataInfoEntityRepository.save( traceDataInfoEntity );
            traceEntityRepository.save( traceEntity );

        }else{
            TraceDataInfoEntity traceDataInfoEntity = byId.get();
            traceDataInfoEntity.getTraceEntities().add(traceEntity);
            traceEntity.setTraceDataInfoEntity( traceDataInfoEntity );
            traceDataInfoEntityRepository.save(traceDataInfoEntity);
            traceEntityRepository.save( traceEntity );
        }

        // traceEntity Data 처리 close
    }


}

