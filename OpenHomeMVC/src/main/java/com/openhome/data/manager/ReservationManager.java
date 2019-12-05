package com.openhome.data.manager;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.openhome.aop.helper.annotation.Debug;
import com.openhome.data.Reservation;
import com.openhome.data.Reservation.ReservationState;
import com.openhome.data.Transaction;
import com.openhome.data.Transaction.TransactionNature;
import com.openhome.data.UserDetails;
import com.openhome.mailer.Mailer;

@Component
public class ReservationManager {

	private static final Double SERVICE_CHARGE = 1.00;
	private static final Long MS_24_HOURS = 24*60*60*1000l;
	private static final Long MS_12_HOURS = 12*60*60*1000l;
	private static final Long MS_4_HOURS = 4*60*60*1000l;

	private static final Long CHECK_IN_TIME = 15*60*60*1000l;//3 pm
	private static final Long CHECK_OUT_TIME = 11*60*60*1000l;//11 am

	@Autowired
	public Mailer mailer;
	
	public Reservation reservation;
	
	public ReservationManager() {
		
	}

	public Reservation getReservation() {
		return reservation;
	}

	@Debug
	public void setReservation(Reservation reservation) {
		this.reservation = reservation;
	}
	
	@Debug
	public void guestCancelReservation(Date currentTime) {
		for (int i = 0 ; i < 2 ; i++) {
			//chargeGuestFeeForDay(currentTime, new Date(currentTime.getTime()+(i*MS_24_HOURS)));
		}
	}
	
	@Debug
	public void hostCancelReservation(Date currentTime) {
		for (int i = getReservation().getReservationState() == ReservationState.CheckedIn ? 1 : 0 ; i < 7 ; i++) {
			//chargeHostFeeForDay(currentTime, new Date(currentTime.getTime()+(i*MS_24_HOURS)));
		}
	}
	
	@Debug
	public boolean compareDMY(Date a, Date b) {
		return a.getDate() == b.getDate() && a.getMonth() == b.getMonth() && a.getYear() == b.getYear();
	}
	
	@Debug
	public boolean reservationEnded() {
		return !(getReservation().getReservationState().equals(ReservationState.Booked) || getReservation().getReservationState().equals(ReservationState.CheckedIn));
	}
	
	@Debug
	public Double getGuestCancellationFeeForDay(Date currentDate,Date date) {
		System.out.println(date.getTime() - currentDate.getTime() +" | " + MS_24_HOURS);
		if(date.getTime() - currentDate.getTime() >= MS_24_HOURS) {
			System.out.println("cancelling 24 hours ahead");
			return 0.0;
		}
		return getPriceForDay(date) * 0.30 ;
	}
	
	private double getPriceForDay(Date date) {
		date.setHours(0);date.setMinutes(0);date.setSeconds(0);
		
		Date checkInDate = new Date(getReservation().getCheckIn());

		checkInDate.setHours(0);checkInDate.setMinutes(0);checkInDate.setSeconds(0);
		
		Date checkOutDate = new Date(getReservation().getCheckOut());
		
		checkOutDate.setHours(0);checkOutDate.setMinutes(0);checkOutDate.setSeconds(0);
		
		if(date.after(checkOutDate) || date.getTime() == checkOutDate.getTime()) {
			//date being asked for price is the last day(morning) of the reservation (unchargeable) or after the last day
			return 0;
		}
		
		if(date.before(checkInDate)) {
			//date being asked for price is before the reservation begins
			return 0;
		}
		
		return (date.getDay() == 5 || date.getDay() == 6) ? getReservation().getWeekendRentPrice() : getReservation().getWeekdayRentPrice();
	}

	@Debug
	public Double getHostCancellationFeeForDay(Date currentDate,Date date) {
		if(date.getTime() - currentDate.getTime() >= 7*MS_24_HOURS) {
			return 0.0;
		}
		return getPriceForDay(date) * 0.15 ;
	}
	
	@Debug
	public void performGuestCheckIn(Date currentDate) throws IllegalAccessException {
		
		processReservation(currentDate);
		
		if(getReservation().getReservationState() != ReservationState.Booked) {
			throw new IllegalAccessException("Check In not Allowed");
		}
		
		if(currentDate.before(new Date(getReservation().getCheckIn()))) {
			throw new IllegalAccessException("It is too early to Check In");
		}
	
		getReservation().setActualCheckIn(currentDate.getTime());
		
		getReservation().setReservationState(ReservationState.CheckedIn);
	}
	
	@Debug
	public void performGuestCheckOut(Date currentDate) throws IllegalAccessException {
		
		processReservation(currentDate);
		
		if(getReservation().getReservationState() != ReservationState.CheckedIn) {
			throw new IllegalAccessException("Check Out is not Allowed");
		}
		
		if(currentDate.after(new Date(getReservation().getCheckOut()))) {
			throw new IllegalAccessException("It is too late to Check Out");
		}

		//chargeGuestForDay(currentDate, currentDate);
	
		if(currentDate.before(new Date(getReservation().getCheckOut()-MS_24_HOURS))) {
			//early checkout
			//chargeGuestFeeForDay(currentDate, new Date(currentDate.getTime()+MS_24_HOURS));
		}
		
		getReservation().setActualCheckOut(currentDate.getTime());
		
		getReservation().setReservationState(ReservationState.CheckedOut);
		
	}
	
	@Debug
	public void performGuestCancel(Date currentDate) throws IllegalAccessException {
		
		processReservation(currentDate);
		
		if(getReservation().getReservationState()!=ReservationState.Booked) {
			throw new IllegalAccessException("Cancellation is not Allowed.");
		}
		
		guestCancelReservation(currentDate);
		
		getReservation().setActualCheckOut(currentDate.getTime());

		getReservation().setReservationState(ReservationState.GuestCancelled);

	}
	
	@Debug
	public void performHostCancel(Date currentDate) throws IllegalAccessException {
		processReservation(currentDate);
		
		if(reservationEnded()) {
			throw new IllegalAccessException("Cancellation is not Allowed.");
		}
		
		hostCancelReservation(currentDate);
		
		//getReservation().setActualCheckOut(currentDate.getTime());
		getReservation().setReservationState(ReservationState.HostCancelled);
	}

	@Debug
	public void processReservation(Date currentDate) {
		if(reservationEnded()) {
			// reservation is either checked-out or cancelled
			// in that case no need to process this reservation
			System.out.println("Reservation Ended");
			return;
		}
		
		// reservation is not finished
		
		Date checkInTime = new Date(getReservation().getCheckIn());
		Date checkOutTime = new Date(getReservation().getCheckOut());
		
		if(currentDate.before(checkInTime)) {
			// nothing to process before the actual check-in time
			System.out.println("Before check in");
			return;
		}
		
		// its time for the guest to check-in ( current time is after 3pm of first day )
		// checkInTime < currentTime < checkOutTime
		if(getReservation().getActualCheckIn() == null) {
			// no record of guest check-in found
			if(currentDate.before(new Date(getReservation().getCheckIn()+MS_12_HOURS))) {
				// guest still has 12 hours to check-in
				System.out.println("Check in day first 12 hours");
				return;
			}
			// guest has not checked in within the first 12 hours of their reservation 
			performGuestNoShowProtocol(currentDate);
			System.out.println("Guest no show");
			return;
		}
		
		if(currentDate.after(checkOutTime)) {
			// currentTime is after check-out time
			performGuestAutoCheckOut(currentDate);
			System.out.println("Guest Auto checkout");
			return;
		}else {
			// record of guest check-in found
			System.out.println("Guest charge");
			return;
		}
		
	}

	@Debug
	private void performGuestNoShowProtocol(Date currentTime) {
		
	}
	

	@Debug
	private void performGuestAutoCheckOut(Date currentTime) {
		
	}
}
