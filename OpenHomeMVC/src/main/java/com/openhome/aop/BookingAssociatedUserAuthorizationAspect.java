package com.openhome.aop;

import javax.servlet.http.HttpSession;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import com.openhome.aop.helper.ArgsFinder;
import com.openhome.dao.BookingDAO;
import com.openhome.data.Booking;
import com.openhome.session.SessionManager;

@Aspect
@Component
@Order(0)
public class BookingAssociatedUserAuthorizationAspect {

	@Autowired
	BookingDAO bookingDao;
	
	@Autowired
	SessionManager sessionManager;

	@Around("@annotation(com.openhome.aop.helper.annotation.BookingAssociatedUserLoginRequired)")
	public Object rightUserLoginRequired(ProceedingJoinPoint joinPoint) throws Throwable {
		
		try {
			HttpSession httpSession = ArgsFinder.getHttpSession(joinPoint.getArgs());
			if(sessionManager.hasUserLogin(httpSession) == false) {
				throw new IllegalAccessError("Login Required");
			}

			Long bookingId = ArgsFinder.findArg(joinPoint.getArgs(), Long.class);
			Booking booking = bookingDao.getOne(bookingId);
			Long guestId = sessionManager.getGuestId(httpSession);
			if(guestId != null) {
				System.out.println(booking.getGuest().getId() +" | "+guestId);
				if(booking.getGuest().getId().equals(guestId) == false) {
					throw new IllegalAccessError("Associated Login Required");
				}
			}else {
				if(booking.getSpace().getHost().getId().equals(sessionManager.getHostId(httpSession)) == false) {
					throw new IllegalAccessError("Associated Login Required");
				}
			}
			return joinPoint.proceed();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "index";
		
	 }
	

	@Around("@annotation(com.openhome.aop.helper.annotation.BookingAssociatedGuestLoginRequired)")
	public Object rightGuestLoginRequired(ProceedingJoinPoint joinPoint) throws Throwable {
		
		try {
			HttpSession httpSession = ArgsFinder.getHttpSession(joinPoint.getArgs());
			if(sessionManager.hasUserLogin(httpSession) == false) {
				throw new IllegalAccessError("Login Required");
			}

			Long bookingId = ArgsFinder.findArg(joinPoint.getArgs(), Long.class);
			Booking booking = bookingDao.getOne(bookingId);
			Long guestId = sessionManager.getGuestId(httpSession);
			if(guestId != null) {
				System.out.println(booking.getGuest().getId() +" | "+guestId);
				if(booking.getGuest().getId().equals(guestId) == false) {
					throw new IllegalAccessError("Associated Login Required");
				}
			}else {
				throw new IllegalAccessError("Associated Login Required");
			}
			return joinPoint.proceed();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return "index";
		
	 }
	
	
}
