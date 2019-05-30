package com.neuedu.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.springframework.stereotype.Component;

/**
 * 日志服务切面类
 */
@Component
@Aspect
public class LogAspect {

    //定义切入点表达式
    @Pointcut("execution(public * com.neuedu.service.impl.ProdcutServiceImpl.*(..))")
    public void pointcut(){}

    //要在service中的每个方法之前和之后执行
    //通知：前置通知
    @Before("pointcut()")
    public  void before(){
        System.out.println("=======before=======");
    }

    //后置通知
    @After("pointcut()")
    public  void after(){
        System.out.println("=======after=======");
    }

    //最终通知
    @AfterReturning("pointcut()")
    public  void afterReturning(){
        System.out.println("=======afterReturning=======");
    }

    //异常通知
    @AfterThrowing("pointcut()")
    public  void afterThrowing(){
        System.out.println("=======afterThrowing=======");
    }

    //环绕通知：在切入点执行之前之后都执行这个通知
    @Around("pointcut()")
    public Object arround(ProceedingJoinPoint proceedingJoinPoint){
        Object o =null;
        try {
            //执行方法前通知
            System.out.println("====arround====before====");
            //执行切入点匹配的方法，需要在环绕通知里执行环绕通知的方法
           o = proceedingJoinPoint.proceed();
           //执行方法后通知
           System.out.println("====arround====after====");
        } catch (Throwable throwable) {
            throwable.printStackTrace();
            //抛出异常通知:只有在执行方法的时候出现异常，才会通知
            System.out.println("====arround====thowing====");
        }
        //结束方法通知
        System.out.println("=====arround======afterReturning===");
        return o;
    }

}
