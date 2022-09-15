package uplus.nucube.common.trace.dto.db;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable
@Setter
@Getter
public class ClientInput {


    String uuid;
    int input_depth;
    String inputType1;
    String inputType2;
    String Para1;
    String Para2;
    String FieldError;
    String fieldAnnotation;


    String inputName;
    String inputValue;
}
