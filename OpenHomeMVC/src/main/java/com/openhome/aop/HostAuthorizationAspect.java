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
import com.openhome.dao.PlaceDAO;
import com.openhome.dao.PlaceDetailsDAO;
import com.openhome.data.Host;
import com.openhome.data.Place;
import com.openhome.exception.CustomException;
import com.openhome.session.SessionManager;

@Aspect
@Component
@Order(0)
public class HostAuthorizationAspect {

	@Autowired(required=true)
	PlaceDAO placeDao;
	
	@Autowired(required=true)
	SessionManager sessionManager;
	
	public void hasHostLogin(ProceedingJoinPoint joinPoint) throws CustomException {
			System.out.println(Arrays.toString(joinPoint.getArgs()));
			HttpSession httpSession = ArgsFinder.getHttpSession(joinPoint.getArgs());
			Model model = ArgsFinder.getModel(joinPoint.getArgs());
			Host host = sessionManager.getHost(httpSession);
			if(host == null) {
				throw new CustomException("No Host Login found in session.");
			}
			if(host.getUserDetails().verifiedEmail() == false) {
				throw new CustomException("Host is unverified.");
			}
	}
	
	public void hasPlaceHostLogin(ProceedingJoinPoint joinPoint) throws CustomException {
			hasHostLogin(joinPoint);
			Long placeId = ArgsFinder.findArg(joinPoint.getArgs(), Long.class);
			Model model = ArgsFinder.getModel(joinPoint.getArgs());
			
			HttpSession httpSession = ArgsFinder.getHttpSession(joinPoint.getArgs());
			
			Place s = placeDao.getOne(placeId);
			
			model.addAttribute("place", s);
			
			Host host = sessionManager.getHost(httpSession);
			
			if(s.getHost().getId().equals(host.getId()) == false) {
				throw new CustomException("Wrong Host.");
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
	
	@Around("@annotation(com.openhome.aop.helper.annotation.PlaceHostLoginRequired)")
	public Object rightHostLoginRequired(ProceedingJoinPoint joinPoint) throws Throwable {
		
		try {
			hasPlaceHostLogin(joinPoint);
			return joinPoint.proceed();
		} catch (Exception e) {
			e.printStackTrace();
			return ControllerHelper.popupMessageAndRedirect(e.getMessage(), "");
		}
		
	 }
	
	
}
