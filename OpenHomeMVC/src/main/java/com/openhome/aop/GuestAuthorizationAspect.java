package com.openhome.aop;

import java.util.Arrays;

import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import com.openhome.aop.helper.ArgsFinder;
import com.openhome.dao.SpaceDAO;
import com.openhome.data.Guest;
import com.openhome.data.Host;
import com.openhome.data.Space;
import com.openhome.session.SessionManager;

@Aspect
@Component
@Order(0)
public class GuestAuthorizationAspect {

	@Autowired
	SpaceDAO spaceDao;
	
	@Autowired
	SessionManager sessionManager;
	
	public boolean hasGuestLogin(ProceedingJoinPoint joinPoint) {
		try {
			System.out.println(Arrays.toString(joinPoint.getArgs()));
			HttpSession httpSession = ArgsFinder.getHttpSession(joinPoint.getArgs());
			Model model = ArgsFinder.getModel(joinPoint.getArgs());
			Guest guest = sessionManager.getGuest(httpSession);
			if(guest == null) {
				model.addAttribute("errorMessage", "No Guest Login found in session.");
				return false;
			}
			if(guest.getUserDetails().verifiedEmail() == false) {
				model.addAttribute("errorMessage", "Guest is unverified.");
				return false;
			}
			System.out.println("============>Has Guest Login");
			return true;
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return false;
	}
	
//	public boolean hasSpaceHostLogin(ProceedingJoinPoint joinPoint) {
//		if(hasGuestLogin(joinPoint) == false)
//			return false;
//		try {
//			Long spaceId = ArgsFinder.findArg(joinPoint.getArgs(), Long.class);
//			Model model = ArgsFinder.getModel(joinPoint.getArgs());
//			
//			HttpSession httpSession = ArgsFinder.getHttpSession(joinPoint.getArgs());
//			
//			Space s = spaceDao.getOne(spaceId);
//			
//			model.addAttribute("space", s);
//			
//			Host host = sessionManager.getHost(httpSession);
//			
//			if(s.getHost().getId() != host.getId()) {
//				model.addAttribute("errorMessage", "Wrong Host.");
//				return false;
//			}
//			
//			return true;
//		} catch (Exception e) {
//			
//		}
//		return false;
//	}
	
	@Around("@annotation(com.openhome.aop.helper.annotation.GuestLoginRequired)")
	public Object hostLoginRequired(ProceedingJoinPoint joinPoint) throws Throwable {
		
		try {
			if(hasGuestLogin(joinPoint))
				return joinPoint.proceed();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "index";
		
	 }
	
//	@Around("@annotation(com.openhome.aop.helper.annotation.SpaceHostLoginRequired)")
//	public Object rightHostLoginRequired(ProceedingJoinPoint joinPoint) throws Throwable {
//		
//		try {
//			if(hasSpaceHostLogin(joinPoint))
//				return joinPoint.proceed();
//		} catch (Exception e) {
//			e.printStackTrace();
//		}
//		
//		return "index";
//		
//	 }
	
}
