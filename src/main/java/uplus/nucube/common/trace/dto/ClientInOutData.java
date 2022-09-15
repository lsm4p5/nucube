package uplus.nucube.common.trace.dto;

import lombok.Data;

import java.util.HashMap;
import java.util.Map;


@Data
public class ClientInOutData {
    public Map<String, String> inputData = new HashMap<>();
    public Map<String, String> outputData = new HashMap<>();

    public Map<String, String> headerData = new HashMap<>();

    public void addInputData(String key, String value) {
        inputData.put( key, value );
    }
    public void addOutputData(String key, String value) {
        outputData.put( key, value );
    }
    public void addHeaderData(String key, String value) {
        headerData.put( key, value );
    }
}
