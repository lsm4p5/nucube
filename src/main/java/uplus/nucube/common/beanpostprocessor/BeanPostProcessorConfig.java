package uplus.nucube.common.beanpostprocessor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.Advisor;
import org.springframework.aop.support.DefaultPointcutAdvisor;
import org.springframework.aop.support.NameMatchMethodPointcut;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import uplus.nucube.common.trace.advice.LogTraceAdvice;
import uplus.nucube.common.trace.logtrace.LogTrace;


@Slf4j
@Configuration
public class BeanPostProcessorConfig {

//    @Bean
//    public PackageLogTracePostProcessor packageLogTracePostProcessor(LogTrace logTrace){
//        return new PackageLogTracePostProcessor( "uplus.nucube", getAdvisor(logTrace) );
//    }

    @Bean
    public BeanFindMemberVariable beanFindMemberVariable(){
        return new BeanFindMemberVariable();
    }

    private Advisor getAdvisor(LogTrace logTrace) {
        //pointcut
        NameMatchMethodPointcut pointcut = new NameMatchMethodPointcut();
        pointcut.setMappedNames("request*","order*","save*");

        //advice
        LogTraceAdvice advice = new LogTraceAdvice( logTrace );

        return new DefaultPointcutAdvisor( pointcut, advice );
    }
}