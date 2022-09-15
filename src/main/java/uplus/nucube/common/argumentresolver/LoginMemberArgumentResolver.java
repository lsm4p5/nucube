package uplus.nucube.common.argumentresolver;


import lombok.extern.slf4j.Slf4j;
import org.springframework.core.MethodParameter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import uplus.nucube.common.login.SessionConst;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Slf4j
public class LoginMemberArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter parameter) {

        log.info("supportsParameter 실행 ");

        boolean hasLoginAnnotation = parameter.hasParameterAnnotation( Login.class );
        boolean assignableFrom = MemberLogin.class.isAssignableFrom( parameter.getParameterType() );

        return hasLoginAnnotation && assignableFrom;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter,
                                  ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest,
                                  WebDataBinderFactory binderFactory) throws Exception {
        log.info( "resolveArgument 실행" );

        HttpServletRequest request = (HttpServletRequest)webRequest.getNativeRequest();
        HttpSession session = request.getSession( false );

        if(session==null){
            return null;
        }
        Object member = session.getAttribute( SessionConst.LOGIN_MEMBER );

        return member;
    }
}