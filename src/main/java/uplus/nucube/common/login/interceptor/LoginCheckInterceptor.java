package uplus.nucube.common.login.interceptor;



import lombok.extern.slf4j.Slf4j;
import org.springframework.web.servlet.HandlerInterceptor;
import uplus.nucube.common.login.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        String requestURI = request.getRequestURI();
        log.info( "인증 체크 인터셊터 실행 {}", requestURI );

        HttpSession session = request.getSession( false );

        if (session == null || session.getAttribute( SessionConst.LOGIN_MEMBER ) == null) {
            log.info( "미인증 사용자 요청" );
            response.sendRedirect( "login?redireectURL=" + requestURI );
            return false;
        }

        return true;
    }
}
