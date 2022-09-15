package uplus.nucube.common.trace.uplus.db.repository;

import com.querydsl.core.annotations.QueryProjection;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uplus.nucube.common.trace.uplus.db.entity.TraceDataInfoEntity;

import java.util.List;
import java.util.Optional;

public interface TraceDataInfoEntityRepository extends JpaRepository<TraceDataInfoEntity,String> {

//    @EntityGraph(attributePaths = {"traceEntities"})
//    List<TraceDataInfoEntity> findByUuid(String uuid);

    @EntityGraph(attributePaths = {"traceEntities"})
    Optional<TraceDataInfoEntity> findById(String id);

    @EntityGraph(attributePaths = {"traceEntities"})
    List<TraceDataInfoEntity> findAll();

    @EntityGraph(attributePaths = {"traceEntities"})
    @Query("select t from TraceDataInfoEntity t  order by t.createdDate desc ")
    List<TraceDataInfoEntity> findTraceDataInfo(Pageable pageable);


}
