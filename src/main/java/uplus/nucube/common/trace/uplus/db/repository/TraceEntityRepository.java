package uplus.nucube.common.trace.uplus.db.repository;

import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import uplus.nucube.common.trace.uplus.db.entity.TraceDataInfoEntity;
import uplus.nucube.common.trace.uplus.db.entity.TraceEntity;

import java.util.List;

public interface TraceEntityRepository extends JpaRepository<TraceEntity,Long> {

    @Query("select distinct t from TraceEntity t join fetch t.traceDataInfoEntity m ")
    List<TraceEntity> findTraceDataInfoByTraceEntity();


}
