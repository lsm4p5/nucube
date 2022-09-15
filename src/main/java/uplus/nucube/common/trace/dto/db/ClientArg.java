package uplus.nucube.common.trace.dto.db;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable
@Setter @Getter
public class ClientArg {

    String uuid;
    String ArgClassType;
    String ArgParaName;
    String ArgAnnotation;
    String ArgClassError;

    String argClassName;
    String argObjectValue;
}
