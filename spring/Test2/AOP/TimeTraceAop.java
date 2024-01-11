package jaeyong.Test2.AOP;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class TimeTraceAop {

    @Around("execution(* jaeyong.Test2..*(..))") //모두 적용
    //@Around("execution(* hello.hellospring.service.*(..))") 서비스 하위 파일들만 적용
    public Object execute(ProceedingJoinPoint joinPoint) throws Throwable{
        long start = System.currentTimeMillis();
        System.out.println("Start: " + joinPoint.toString());

        try{
            return joinPoint.proceed();
        }finally{
            long finish = System.currentTimeMillis();
            long timeMs = finish - start;
            System.out.println("Finish: " + joinPoint.toString() +" "+ timeMs +  "ms");
        }
    }
}

