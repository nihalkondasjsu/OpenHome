package com.openhome.aop;

import java.util.Arrays;

import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.openhome.aop.helper.ArgsFinder;
import com.openhome.dao.GuestDAO;
import com.openhome.dao.HostDAO;
import com.openhome.session.SessionManager;

@Aspect
@Component
public class UserAuthorizationAspect {

	@Autowired(required=true)
	SessionManager sessionManager;

	@Autowired(required=true)
	HostDAO hostDao;
	
	@Autowired(required=true)
	GuestDAO guestDao;
	
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
	
	@Around("@annotation(com.openhome.aop.helper.annotation.ValidHostId)")
	public Object validHostId(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			if(hostDao.getOne(ArgsFinder.findArg(joinPoint.getArgs(), Long.class)) != null) {
				return joinPoint.proceed();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "index";
	}
	
	@Around("@annotation(com.openhome.aop.helper.annotation.ValidGuestId)")
	public Object validGuestId(ProceedingJoinPoint joinPoint) throws Throwable {
		try {
			if(guestDao.getOne(ArgsFinder.findArg(joinPoint.getArgs(), Long.class)) != null) {
				return joinPoint.proceed();
			}
		} catch (Exception e) {
			// TODO: handle exception
		}
		return "index";
	}
	
}
