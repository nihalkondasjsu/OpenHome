package com.openhome.data;

import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Enumerated;
import javax.persistence.EnumType;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.openhome.tam.TimeAdvancementManagement;

@Entity
@Component
public class Booking {
	
	enum BookingState{
		Booked,CheckedIn,CheckedOut,Cancelled
	}
	
	@Id
	@GeneratedValue
	@Basic(optional = false)
	private Long id;
	
	@Column(nullable=false,updatable=false)
	private Date createdDate;
	
	private Long checkIn;
	
	private Long checkOut;
	
	private Long actualCheckIn;
	
	private Long actualCheckOut;
	
	@Enumerated(EnumType.STRING)
	private BookingState bookingState;
	
	@ManyToOne(fetch = FetchType.LAZY)
	private Space space;
	
	@OneToMany(fetch=FetchType.LAZY,orphanRemoval=true,mappedBy = "booking")
	private List<Transaction> transactions;
	
	@OneToOne(orphanRemoval=true)
	private Rating rating;
	
	@ManyToOne
	private Guest guest;

	@Column(nullable = false)
	private Double weekdayRentPrice = 100.0;

	@Column(nullable = false)
	private Double weekendRentPrice = 140.0;
	
	public Booking() {
		// TODO Auto-generated constructor stub
		createdDate = new Date();
		bookingState = BookingState.Booked;
	}
	
	public Booking(TimeAdvancementManagement timeAdvancementManagement) {
		try {
			createdDate = timeAdvancementManagement.getCurrentDate();
		} catch (Exception e) {
			// TODO: handle exception
			createdDate = new Date();
		}
		bookingState = BookingState.Booked;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Date getCreatedDate() {
		return createdDate;
	}

	public void setCreatedDate(Date createdDate) {
		this.createdDate = createdDate;
	}

	public Long getCheckIn() {
		return checkIn;
	}
	
	public Date dateOfCheckIn() {
		return new Date(checkIn);
	}

	public void setCheckIn(Long checkIn) {
		this.checkIn = checkIn;
	}

	public Long getCheckOut() {
		return checkOut;
	}

	public Date dateOfCheckOut() {
		return new Date(checkOut);
	}
	
	public void setCheckOut(Long checkOut) {
		this.checkOut = checkOut;
	}

	public Space getSpace() {
		return space;
	}

	public void setSpace(Space space) {
		this.space = space;
	}

	public List<Transaction> getTransactions() {
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	public void addTransaction(Transaction transaction) {
		
	}
	
	public Rating getRating() {
		return rating;
	}

	public void setRating(Rating rating) {
		this.rating = rating;
	}

	public Guest getGuest() {
		return guest;
	}

	public void setGuest(Guest guest) {
		this.guest = guest;
	}
	
	public Double getWeekdayRentPrice() {
		return weekdayRentPrice;
	}
	
	public void setWeekdayRentPrice(Double weekdayRentPrice) {
		this.weekdayRentPrice = weekdayRentPrice;
	}
	
	public Double getWeekendRentPrice() {
		return weekendRentPrice;
	}
	
	public void setWeekendRentPrice(Double weekendRentPrice) {
		this.weekendRentPrice = weekendRentPrice;
	}
	
	public Long getActualCheckIn() {
		return actualCheckIn;
	}

	public void setActualCheckIn(Long actualCheckIn) {
		this.actualCheckIn = actualCheckIn;
	}

	public Long getActualCheckOut() {
		return actualCheckOut;
	}

	public void setActualCheckOut(Long actualCheckOut) {
		this.actualCheckOut = actualCheckOut;
	}
	
	public BookingState getBookingState() {
		return bookingState;
	}

	public void setBookingState(BookingState bookingState) {
		this.bookingState = bookingState;
	}

	public boolean bookingEnded() {
		return getBookingState().equals(BookingState.CheckedOut) || getBookingState().equals(BookingState.Cancelled) ;
	}
	
	public void processBooking(TimeAdvancementManagement timeAdvancementManagement) {
		if(bookingEnded()) {
			// booking is either checked-out or cancelled
			// in that case no need to process this booking
			return;
		}
		
		// booking is not finished
		
		Date currentTime = timeAdvancementManagement.getCurrentDate();
		
		if(getBookingState().equals(BookingState.CheckedIn)) {
			chargeGuestForPreviousDay(currentTime);
		}
		
		Date checkInTime = new Date(getCheckIn());
		Date checkOutTime = new Date(getCheckOut());
		
		if(currentTime.before(checkInTime)) {
			// nothing to process before the actual check-in time
			return;
		}
		// its time for the guest to check-in ( current time is after 3pm of first day )
		if(currentTime.before(checkOutTime)) {
			// checkInTime < currentTime < checkOutTime
			if(actualCheckIn == null) {
				// no record of guest check-in found
				if(currentTime.before(new Date(getCheckIn()+(12 * 60 * 60 * 1000)))) {
					// guest still has 12 hours to check-in
					return;
				}
				// guest has not checked in within the first 12 hours of their booking 
				initiateGuestNoShowProtocol();
				return;
			}
			// record of guest check-in found
			chargeGuestForPreviousDay(currentTime);
			return;
		}
		// currentTime is after check-out time
		initiateAutoCheckOutProtocol();
		return;
	}

	private void chargeGuestForPreviousDay(Date currentTime) {
		
	}

	private void initiateAutoCheckOutProtocol() {
		
	}

	private void initiateFullPaymentForTheDay() {
		
	}

	private void initiateGuestNoShowProtocol() {
		
	}

	public Double getPriceForDay(Date date) {
		return date.getDay() == 6 ? weekendRentPrice : weekdayRentPrice;
	}
	
	public Double getPriceForDays() {
		Double price = 0.0 ;
		
		Date start,end;
		
		start = new Date(checkIn);
		end = new Date(checkOut);
		
		start.setHours(0);end.setHours(0);
		
		start.setMinutes(0);end.setMinutes(0);
		
		start.setSeconds(0);end.setSeconds(0);
		
		long Start = start.getTime() , End = end.getTime();
		
		for (long i = Start; i <= End; i += 24*60*60*1000 ) {
			price += getPriceForDay(new Date(i));
		}
		
		return price;
	}
	
}
