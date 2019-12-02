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
	
	public boolean hasHostLogin(ProceedingJoinPoint joinPoint) {
		try {
			System.out.println(Arrays.toString(joinPoint.getArgs()));
			HttpSession httpSession = ArgsFinder.getHttpSession(joinPoint.getArgs());
			Model model = ArgsFinder.getModel(joinPoint.getArgs());
			Host host = sessionManager.getHost(httpSession);
			if(host == null) {
				model.addAttribute("errorMessage", "No Host Login found in session.");
				return false;
			}
			if(host.getUserDetails().verifiedEmail() == false) {
				model.addAttribute("errorMessage", "Host is unverified.");
				return false;
			}
			return true;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return false;
	}
	
	public boolean hasSpaceHostLogin(ProceedingJoinPoint joinPoint) {
		if(hasHostLogin(joinPoint) == false)
			return false;
		try {
			Long spaceId = ArgsFinder.findArg(joinPoint.getArgs(), Long.class);
			Model model = ArgsFinder.getModel(joinPoint.getArgs());
			
			HttpSession httpSession = ArgsFinder.getHttpSession(joinPoint.getArgs());
			
			Space s = spaceDao.getOne(spaceId);
			
			model.addAttribute("space", s);
			
			Host host = sessionManager.getHost(httpSession);
			
			if(s.getHost().getId() != host.getId()) {
				model.addAttribute("errorMessage", "Wrong Host.");
				return false;
			}
			
			return true;
		} catch (Exception e) {
			
		}
		return false;
	}
	
	@Around("@annotation(com.openhome.aop.helper.annotation.HostLoginRequired)")
	public Object hostLoginRequired(ProceedingJoinPoint joinPoint) throws Throwable {
		
		try {
			if(hasHostLogin(joinPoint))
				return joinPoint.proceed();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "index";
		
	 }
	
	@Around("@annotation(com.openhome.aop.helper.annotation.SpaceHostLoginRequired)")
	public Object rightHostLoginRequired(ProceedingJoinPoint joinPoint) throws Throwable {
		
		try {
			if(hasSpaceHostLogin(joinPoint))
				return joinPoint.proceed();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "index";
		
	 }
	
	
}
