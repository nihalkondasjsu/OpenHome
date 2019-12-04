package com.openhome.aop;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class DebugAspect {

	@Around("@annotation(com.openhome.aop.helper.annotation.Debug)")
	public Object around(ProceedingJoinPoint proceedingJoinPoint) throws Throwable {
		System.out.println("starting "+proceedingJoinPoint.toShortString()+" with "+Arrays.toString(proceedingJoinPoint.getArgs()));
		Object obj = proceedingJoinPoint.proceed();
		System.out.println("finished "+proceedingJoinPoint.toShortString()+" with "+obj);
		return obj;
	}
	
}
