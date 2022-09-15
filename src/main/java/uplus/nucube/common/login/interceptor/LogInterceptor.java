package uplus.nucube.common.login.interceptor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.UUID;

@Slf4j
public class LogInterceptor implements HandlerInterceptor {
    public static final String LOG_ID = "logId";

    @Override
    public boolean preHandle(HttpServletRequest request,
                             HttpServletResponse response,
                             Object handler) throws Exception {

        String requestURI = request.getRequestURI();

        String uuid = UUID.randomUUID().toString();
        request.setAttribute(LOG_ID, uuid);

        log.info("\n===== Interceptor preHandle[PreStart] [{}][{}][{}][{}]", uuid, request.getDispatcherType(), requestURI, handler);
        return true;
    }

    @Override
    public void postHandle(HttpServletRequest request,
                           HttpServletResponse response,
                           Object handler,
                           ModelAndView modelAndView) throws Exception {
        log.info("\n===== Interceptor[Post End] - postHandle [{}]", modelAndView);
    }

    @Override
    public void afterCompletion(HttpServletRequest request,
                                HttpServletResponse response,
                                Object handler,
                                Exception ex) throws Exception {
        String requestURI = request.getRequestURI();
        String logId = (String)request.getAttribute(LOG_ID);
        log.info("\n===== Interceptor[END] afterCompletion [{}][{}][{}]", logId, request.getDispatcherType(), requestURI);
        if (ex != null) {
            log.error("\n===== Interceptor afterCompletion error!!", ex);
        }
    }
}
