package uplus.nucube.common.trace.logtrace;


import uplus.nucube.common.trace.TraceStatus;

public interface LogTrace {

    TraceStatus begin(String message);
    void end(TraceStatus status);
    void exception(TraceStatus status, Exception e);

    void end_message(TraceStatus status, String message);

    void exception_message(TraceStatus status,String message, Exception e);
}
