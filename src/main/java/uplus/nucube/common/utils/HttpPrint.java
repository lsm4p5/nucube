package uplus.nucube.common.utils;

import lombok.extern.slf4j.Slf4j;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

@Slf4j
public class HttpPrint {
    public static void printStartLine(HttpServletRequest request) {

        log.info( "--- REQUEST-LINE - start ---" );
        log.info( "request.getMethod() = {}", request.getMethod() );
        log.info( "request.getProtocol() = {}", request.getProtocol() );
        log.info( "request.getScheme() = {}", request.getScheme() );
        // http://locahost:8080/request-header
        log.info( "request.getRequestURL = {}", request.getRequestURL() );
        //usernane=hi
        log.info( "request.getRequestURI = {}", request.getRequestURI() );
        log.info( "request.getQueryString = {}", request.getQueryString() );
        log.info( "request.getRemoteHost() = {}", request.getRemoteHost() );
        log.info( "request.isSecure() = {}", request.isSecure() );
        log.info( "--- REQUEST-LINE - end ---" );
    }

    public static void printHeaders(HttpServletRequest request) {
        log.info( "--- Headers - start ---" );
//        Enumeration<String> headerNames = request.getHeaderNames();
//        while (headerNames.hasMoreElements()) {
//            String headerName = headerNames.nextElement();
//            log.info( "headerName : {} " , headerName);
//        }
        request.getHeaderNames()
                .asIterator()
                .forEachRemaining( s -> log.info( "headerName: ={}", s ) );
        log.info( "--- Headers - end ---" );
    }

    public static void printHeaderUtils(HttpServletRequest request) {
        log.info("--- Header 편의 조회 start ---");
        log.info("[Host 편의 조회]");
        log.info("request.getServerName() = {}" , request.getServerName()); //Host 헤더
        log.info("request.getServerPort() = {}" , request.getServerPort()); //Host 헤더


        log.info("[Accept-Language 편의 조회]");
        request.getLocales().asIterator()
                .forEachRemaining(locale -> log.info("locale = {}" , locale));
        log.info("request.getLocale() = {}" , request.getLocale());


        log.info("[cookie 편의 조회]");
        if (request.getCookies() != null) {
            for (Cookie cookie : request.getCookies()) {
                log.info(cookie.getName() , ": " , cookie.getValue());
            }
        }


        log.info("[Content 편의 조회]");
        log.info("request.getContentType() = {}" , request.getContentType());
        log.info("request.getContentLength() = {}" , request.getContentLength());
        log.info("request.getCharacterEncoding() = {}" , request.getCharacterEncoding());

        log.info("--- Header 편의 조회 end ---");

    }

    //기타 정보
    private static void printEtc(HttpServletRequest request) {
        log.info("--- 기타 조회 start ---");

        log.info("[Remote 정보]");
        log.info("request.getRemoteHost() = {}" , request.getRemoteHost()); //
        log.info("request.getRemoteAddr() = {}" , request.getRemoteAddr()); //
        log.info("request.getRemotePort() = {}" , request.getRemotePort()); //


        log.info("[Local 정보]");
        log.info("request.getLocalName() = {}" , request.getLocalName()); //
        log.info("request.getLocalAddr() = {}" , request.getLocalAddr()); //
        log.info("request.getLocalPort() = {}" , request.getLocalPort()); //

        log.info("--- 기타 조회 end ---");

    }
}
