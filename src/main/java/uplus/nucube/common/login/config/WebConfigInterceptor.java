package uplus.nucube.common.login.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import uplus.nucube.common.argumentresolver.LoginMemberArgumentResolver;
import uplus.nucube.common.login.interceptor.LogInterceptor;

import java.util.List;

@Configuration
public class WebConfigInterceptor implements WebMvcConfigurer {

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
//        registry.addInterceptor( new LogInterceptor() )
//                .order( 1 )
//                .addPathPatterns( "/**" )
//                .excludePathPatterns( "/css/**", "/*.ico", "/error" );

//        registry.addInterceptor( new LoginCheckInterceptor() )
//                .order( 2 )
//                .addPathPatterns( "/**" )
//                .excludePathPatterns("/","/login","/logout","/css/**", "/*.ico", "/error" );
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add( new LoginMemberArgumentResolver() );
    }

}
