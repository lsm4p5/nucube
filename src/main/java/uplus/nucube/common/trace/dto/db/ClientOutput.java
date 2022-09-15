package uplus.nucube.common.trace.dto.db;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable
@Setter
@Getter
public class ClientOutput {

    String uuid;
    int output_depth;
    String outputType;
    String outputType2;
    String outputName;
    String outputValue;
}
