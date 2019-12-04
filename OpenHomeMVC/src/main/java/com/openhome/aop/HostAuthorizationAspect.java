package com.openhome.aop;

import java.util.Arrays;

import javax.servlet.http.HttpSession;

import org.apache.catalina.session.StandardSessionFacade;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import com.openhome.aop.helper.ArgsFinder;
import com.openhome.controllers.helper.ControllerHelper;
import com.openhome.dao.SpaceDAO;
import com.openhome.dao.SpaceDetailsDAO;
import com.openhome.data.Host;
import com.openhome.data.Space;
import com.openhome.session.SessionManager;

@Aspect
@Component
@Order(0)
public class HostAuthorizationAspect {

	@Autowired
	SpaceDAO spaceDao;
	
	@Autowired
	SessionManager sessionManager;
	
	public void hasHostLogin(ProceedingJoinPoint joinPoint) throws IllegalAccessException {
			System.out.println(Arrays.toString(joinPoint.getArgs()));
			HttpSession httpSession = ArgsFinder.getHttpSession(joinPoint.getArgs());
			Model model = ArgsFinder.getModel(joinPoint.getArgs());
			Host host = sessionManager.getHost(httpSession);
			if(host == null) {
				throw new IllegalAccessException("No Host Login found in session.");
			}
			if(host.getUserDetails().verifiedEmail() == false) {
				throw new IllegalAccessException("Host is unverified.");
			}
	}
	
	public void hasSpaceHostLogin(ProceedingJoinPoint joinPoint) throws IllegalAccessException {
			hasHostLogin(joinPoint);
			Long spaceId = ArgsFinder.findArg(joinPoint.getArgs(), Long.class);
			Model model = ArgsFinder.getModel(joinPoint.getArgs());
			
			HttpSession httpSession = ArgsFinder.getHttpSession(joinPoint.getArgs());
			
			Space s = spaceDao.getOne(spaceId);
			
			model.addAttribute("space", s);
			
			Host host = sessionManager.getHost(httpSession);
			
			if(s.getHost().getId().equals(host.getId()) == false) {
				throw new IllegalAccessException("Wrong Host.");
			}
	}
	
	@Around("@annotation(com.openhome.aop.helper.annotation.HostLoginRequired)")
	public Object hostLoginRequired(ProceedingJoinPoint joinPoint) throws Throwable {
		
		try {
			hasHostLogin(joinPoint);
			return joinPoint.proceed();
		} catch (Exception e) {
			e.printStackTrace();
			return ControllerHelper.popupMessageAndRedirect(e.getMessage(), "");
		}
		
	 }
	
	@Around("@annotation(com.openhome.aop.helper.annotation.SpaceHostLoginRequired)")
	public Object rightHostLoginRequired(ProceedingJoinPoint joinPoint) throws Throwable {
		
		try {
			hasSpaceHostLogin(joinPoint);
			return joinPoint.proceed();
		} catch (Exception e) {
			e.printStackTrace();
			return ControllerHelper.popupMessageAndRedirect(e.getMessage(), "");
		}
		
	 }
	
	
}
