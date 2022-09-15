package uplus.nucube.common.trace.dto.db;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Embeddable;

@Embeddable
@Setter
@Getter
public class ClientTest {


    String uuid;
    int input_depth;
    String inputType1;
    String inputType2;
    String inputType3;
    String inputType4;

    String inputPara1;
    String inputPara2;
    String inputPara3;
    String inputPara4;
    String inputValue;
}
