package uplus.nucube.common.login.config;



import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Configuration;
import uplus.nucube.common.login.filter.LogFilter;
import uplus.nucube.common.login.filter.LoginCheckFilter;

import javax.servlet.Filter;

@Configuration
public class WebConfig  {

  //  @Bean
    public FilterRegistrationBean logFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter( new LogFilter() );
        filterRegistrationBean.setOrder( 1 );
        filterRegistrationBean.addUrlPatterns( "/*" ); //모든 URL

        return filterRegistrationBean;
    }

 //   @Bean
    public FilterRegistrationBean loginCHeckFilter() {
        FilterRegistrationBean<Filter> filterRegistrationBean = new FilterRegistrationBean<>();
        filterRegistrationBean.setFilter( new LoginCheckFilter() );
        filterRegistrationBean.setOrder( 2 );
        filterRegistrationBean.addUrlPatterns( "/*" ); //모든 URL

        return filterRegistrationBean;
    }
}
