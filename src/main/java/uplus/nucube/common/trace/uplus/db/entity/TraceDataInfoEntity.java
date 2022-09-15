package uplus.nucube.common.trace.uplus.db.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.domain.Persistable;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class TraceDataInfoEntity implements Persistable<String>  {

    @Id
    @Column(name = "traceDataInfo_id")
    String id;

    String domainId;
    String viewId; //외부에서 setting 하여 준다. (도메인별로 유니크한 ID)
    String apiId;

    @CreatedDate
    private LocalDateTime createdDate;


    @OneToMany(mappedBy = "traceDataInfoEntity")
    List<TraceEntity> traceEntities = new ArrayList<>();




    @Override
    public boolean isNew() {

        return createdDate == null;
    }
}
