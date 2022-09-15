package uplus.nucube.common.trace.uplus.controller;
import com.querydsl.core.types.dsl.BooleanExpression;

import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import uplus.nucube.common.trace.uplus.LocalDateParser;
import uplus.nucube.common.trace.uplus.TraceCommonUtil;
import uplus.nucube.common.trace.uplus.db.entity.*;
import uplus.nucube.common.trace.uplus.db.repository.TraceDataInfoEntityRepository;
import uplus.nucube.common.trace.uplus.db.repository.TraceEntityRepository;


import javax.persistence.EntityManager;

import java.time.LocalDateTime;

import java.util.*;

@Controller
@Slf4j
public class TraceController {

    private final TraceDataInfoEntityRepository traceDataInfoEntityRepository;
    private final TraceEntityRepository traceEntityRepository;

    private final EntityManager em;

    private final JPAQueryFactory query;


    public TraceController(TraceDataInfoEntityRepository traceDataInfoEntityRepository, TraceEntityRepository traceEntityRepository, EntityManager em) {
        this.traceDataInfoEntityRepository = traceDataInfoEntityRepository;
        this.traceEntityRepository = traceEntityRepository;
        this.em = em;
        query = new JPAQueryFactory( em );
    }

    @GetMapping("/trace/{id}/{level}/{function}")
    public String updateItemForm(@PathVariable("id") String id,
                                 @PathVariable("level") Integer level,
                                 @PathVariable("function") String function,
                                 Model model) {

        log.info( "id = {}, level={},function={}", id, level, function );
        QAopClassInfoEntity qAopClassInfo = QAopClassInfoEntity.aopClassInfoEntity;
        QInputMetaEntity qInputMeta = QInputMetaEntity.inputMetaEntity;
        QOutputMetaEntity qOutputMeta = QOutputMetaEntity.outputMetaEntity;

        AopClassInfoEntity aopClassInfoEntity = query.select( qAopClassInfo )
                .distinct()
                .from( qAopClassInfo )
                .leftJoin(qAopClassInfo.inputMetaEntities,qInputMeta)
                .fetchJoin()
                .leftJoin( qAopClassInfo.outputMetaEntities,qOutputMeta )
                .where( qAopClassInfo.uuid.eq( id ), qAopClassInfo.level.eq( level ), qAopClassInfo.functionName.eq( function ) )
                .fetchOne();

       //  aopClassInfoEntity.printAll();
        log.info("inputMeta = {}, outputMeta ={}, ClassField ={}", aopClassInfoEntity.getInputMetaEntities().size(),
                aopClassInfoEntity.getOutputMetaEntities().size(), aopClassInfoEntity.getClassFields().size());

        model.addAttribute( "singleAopInfo", aopClassInfoEntity );

        return "/trace/singleAopInfo";
    }


    @GetMapping("/trace")
    public String aopClassInfoGet( @ModelAttribute("traceSearch") TraceSearch traceSearch,Model model){

        QTraceEntity qTrace = QTraceEntity.traceEntity;
        QTraceDataInfoEntity qTraceDataInfo = QTraceDataInfoEntity.traceDataInfoEntity;



        log.info( "dateCompare. boolean ={}",dateCompare( traceSearch.getReqDate() ) );


        List<TraceEntity> traceEntities = query.select( qTrace )
                .distinct()
                .from( qTrace )
                .leftJoin( qTrace.traceDataInfoEntity, qTraceDataInfo )
                .fetchJoin()
                .where(uuidLike(traceSearch.uuid), domainNameLike(traceSearch.domainName),
                        viewIdLike(traceSearch.viewId),apiIdLike( traceSearch.apiId ),dateCompare( traceSearch.getReqDate()))
                .fetch();


        Set<TraceDataInfoEntity> imsi = new HashSet<>();
        for (TraceEntity traceEntity : traceEntities) {
            imsi.add(traceEntity.getTraceDataInfoEntity());

        }
        List<TraceDataInfoEntity> traceDataInfo = new ArrayList<>();
        for (TraceDataInfoEntity info : imsi) {
            traceDataInfo.add( info );
        }
        model.addAttribute( "traceDataInfo",traceDataInfo );

        return "/trace/AopMetaInfo";
    }

    @GetMapping("/traceold")
    public String aopClassInfoGetOld( @ModelAttribute("traceSearch") TraceSearch traceSearch,Model model){

        List<TraceEntity> traceEntities = traceEntityRepository.findTraceDataInfoByTraceEntity();
//        for (TraceDataInfoEntity traceDataInfoEntity : traceDataInfo) {
//            List<TraceEntity> traceEntities = traceDataInfoEntity.getTraceEntities();
//            for (TraceEntity traceEntity : traceEntities) {
//                traceEntity.getId();
//                traceEntity.printAll();
//            }
//        }

        Map<Object, String> map = new HashMap<>();
        Set<TraceDataInfoEntity> imsi = new HashSet<>();
        for (TraceEntity traceEntity : traceEntities) {
            imsi.add(traceEntity.getTraceDataInfoEntity());
            map.put( traceEntity.getTraceDataInfoEntity(), "entity" );
        }
        List<TraceDataInfoEntity> traceDataInfo = new ArrayList<>();
        for (Object o : map.keySet()) {
            traceDataInfo.add( (TraceDataInfoEntity)o );
        }
//        for (TraceDataInfoEntity info : imsi) {
//            traceDataInfo.add( info );
//        }
        model.addAttribute( "traceDataInfo",traceDataInfo );
        return "/trace/AopMetaInfo";
    }


    private BooleanExpression uuidLike(String uuid) {
        if (!StringUtils.hasText(uuid)) {
            return null;
        }
        return QTraceDataInfoEntity.traceDataInfoEntity.id.like(uuid);
    }
    private BooleanExpression domainNameLike(String domainName) {
        if (!StringUtils.hasText(domainName)) {
            return null;
        }
        return QTraceDataInfoEntity.traceDataInfoEntity.domainId.like(domainName);
    }
    private BooleanExpression viewIdLike(String viewId) {
        if (!StringUtils.hasText(viewId)) {
            return null;
        }
        return QTraceDataInfoEntity.traceDataInfoEntity.viewId.like(viewId);
    }
    private BooleanExpression apiIdLike(String apiId) {
        if (!StringUtils.hasText(apiId)) {
            return null;
        }
        return QTraceDataInfoEntity.traceDataInfoEntity.apiId.like(apiId);
    }

    private BooleanExpression dateCompare(String date) {
        log.info( "date = {}", date );
        LocalDateTime start;
        LocalDateTime end;
        String compare_date = TraceCommonUtil.dateCompareRet( date );

        log.info( "compare_date ={}", compare_date );
        LocalDateParser parser = new LocalDateParser( compare_date );
        start = parser.startDate();
        end = parser.endDate();
        return QTraceDataInfoEntity.traceDataInfoEntity.createdDate.between(start,end);
    }

}
