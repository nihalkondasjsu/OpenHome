package com.openhome.aop;

import java.util.Arrays;

import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.openhome.aop.helper.ArgsFinder;
import com.openhome.session.SessionManager;

import org.apache.catalina.session.StandardSessionFacade;

@Aspect
@Component
public class UserAuthorizationAspect {

	@Autowired
	SessionManager sessionManager;
	
	@Around("@annotation(com.openhome.aop.helper.annotation.UserLoginRequired)")
	public Object userLoginRequired(ProceedingJoinPoint joinPoint) throws Throwable {
		
		try {
			System.out.println(Arrays.toString(joinPoint.getArgs()));
			HttpSession httpSession = ArgsFinder.getHttpSession(joinPoint.getArgs());
			if(httpSession == null)
				System.out.println("HttpSession Not found.");
			if(sessionManager.hasUserLogin(httpSession))
				return joinPoint.proceed();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "index";
		
	 }
	
}
