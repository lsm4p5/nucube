package uplus.nucube.common.trace.uplus.db.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import javax.persistence.Embeddable;

@Data
@NoArgsConstructor
@Embeddable
@Slf4j
public class TraceArg {

    String argClass;
    String simpleArgClass;
    String argVariable;
    String argObject;
    String simpleArgObject;

    public void print() {
        log.info( "simpleArgClass ={}", simpleArgClass );
    }
}
