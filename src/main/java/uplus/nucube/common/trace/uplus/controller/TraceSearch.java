package uplus.nucube.common.trace.uplus.controller;

import lombok.Data;

@Data
public class TraceSearch {

    String uuid;
    String methodName;
    String viewId;
    String apiId;
    String domainName;

    String reqDate;
}
