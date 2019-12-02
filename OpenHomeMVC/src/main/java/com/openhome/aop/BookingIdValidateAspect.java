package com.openhome.aop;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.ui.Model;

import com.openhome.aop.helper.ArgsFinder;
import com.openhome.dao.BookingDAO;
import com.openhome.data.Booking;

@Aspect
@Component
@Order(1)
public class BookingIdValidateAspect {

	@Autowired
	BookingDAO bookingDao;
	
	@Around("@annotation(com.openhome.aop.helper.annotation.ValidBookingId)")
	public Object rightBookingId(ProceedingJoinPoint joinPoint) throws Throwable {
		System.out.println(joinPoint.toLongString());
		System.out.println(Arrays.toString(joinPoint.getArgs()));
		try {
			Long bookingId = ArgsFinder.findArg(joinPoint.getArgs(), Long.class);
			Model model = ArgsFinder.getModel(joinPoint.getArgs());
			if(bookingId == null) {
				model.addAttribute("errorMessage", "No BookingId provided.");
				return false;
			}
			Booking b = bookingDao.getOne(bookingId);
			if(b == null) {
				model.addAttribute("errorMessage", "Invalid Booking Id.");
				return false;
			}
			System.out.println("============>Booking Id is Valid");
			return joinPoint.proceed();
		} catch (Exception e) {
			System.out.println(e.toString());
		}
		
		return "index";
		
	 }
	
}
