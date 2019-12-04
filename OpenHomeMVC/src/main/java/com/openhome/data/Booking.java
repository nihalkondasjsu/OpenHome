package com.openhome.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Transient;

import org.springframework.stereotype.Component;

import com.openhome.dao.helper.StringListConverter;
import com.openhome.data.Transaction.TransactionNature;
import com.openhome.tam.TimeAdvancementManagement;

@Entity
@Component
public class Booking {
	
	public enum BookingState{
		Booked,CheckedIn,CheckedOut,GuestCancelled,HostCancelled,HostBlock
	}

	public static String CHECK_IN_TIME = "15:00";
	public static String CHECK_OUT_TIME = "11:00";
	
	@Id
	@GeneratedValue
	@Basic(optional = false)
	private Long id;
	
	@Column(nullable=false,updatable=false)
	private Date createdDate;
	
	@Transient
	private String checkInDateString;

	@Transient
	private String checkOutDateString;
	
	@Transient
	private String requiredDays;
	
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
		this.transactions = new ArrayList<Transaction>();
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
		if(transactions != null)
		Collections.sort(transactions,new Comparator<Transaction>() {

			@Override
			public int compare(Transaction o1, Transaction o2) {
				return o1.getCreatedDate().compareTo(o2.getCreatedDate());
			}
		});
		return transactions;
	}

	public void setTransactions(List<Transaction> transactions) {
		this.transactions = transactions;
	}

	public void addTransaction(Transaction transaction) {
		this.transactions.add(transaction);
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
	
	public String getRequiredDays() {
		return requiredDays;
	}

	public void setRequiredDays(String requiredDays) {
		this.requiredDays = requiredDays;
	}
	
	public String getCheckInDateString() {
		return checkInDateString;
	}

	public void setCheckInDateString(String checkInDateString) {
		this.checkInDateString = checkInDateString;
	}

	public String getCheckOutDateString() {
		return checkOutDateString;
	}

	public void setCheckOutDateString(String checkOutDateString) {
		this.checkOutDateString = checkOutDateString;
	}
	
	public void prepareForRegistration(Date createdDate,Space space,Guest guest) throws ParseException {
		this.createdDate = createdDate;
		String pattern = "yyyy-MM-dd HH:mm";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		this.checkIn = simpleDateFormat.parse(checkInDateString+" "+CHECK_IN_TIME).getTime();
		this.checkOut = simpleDateFormat.parse(checkOutDateString+" "+CHECK_OUT_TIME).getTime();
		if(this.checkIn<createdDate.getTime() || this.checkOut - this.checkIn < 20 * 60 * 60 * 1000) {
			throw new IllegalArgumentException("Invalid Booking");
		}
		this.requiredDays = weekDays();
		this.actualCheckIn = null;
		this.actualCheckOut = checkOut;
		this.bookingState = BookingState.Booked;
		this.space = space;
		this.transactions = new ArrayList<Transaction>();
		this.rating = null;
		this.guest = guest;
		this.weekdayRentPrice = this.space.getSpaceDetails().getWeekdayRentPrice();
		this.weekendRentPrice = this.space.getSpaceDetails().getWeekendRentPrice();
	}
	
	public void prepareForHostBlock(Date createdDate,Space space) throws ParseException {
		this.createdDate = createdDate;
		String pattern = "yyyy-MM-dd HH:mm";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		this.checkIn = simpleDateFormat.parse(checkInDateString+" "+CHECK_IN_TIME).getTime();
		this.checkOut = simpleDateFormat.parse(checkOutDateString+" "+CHECK_OUT_TIME).getTime();
		if(this.checkIn<createdDate.getTime() || this.checkOut - this.checkIn < 20 * 60 * 60 * 1000) {
			throw new IllegalArgumentException("Invalid Booking");
		}
		this.bookingState = BookingState.HostBlock;
		this.space = space;
		this.transactions = new ArrayList<Transaction>();
		this.rating = null;
		this.weekdayRentPrice = this.space.getSpaceDetails().getWeekdayRentPrice();
		this.weekendRentPrice = this.space.getSpaceDetails().getWeekendRentPrice();
	}
	
	public String weekDays() throws ParseException {
		List<String> weekdays = new ArrayList<String>();
		String pattern = "yyyy-MM-dd HH:mm";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		long start = simpleDateFormat.parse(checkInDateString+" 00:00").getTime();
		long end = simpleDateFormat.parse(checkOutDateString+" 00:00").getTime();
		String[] week = "Sunday;Monday;Tuesday;Wednesday;Thursday;Friday;Saturday".split(";");
		for (long i = start; i <= end; i+= 24*60*60*1000) {
			String weekS = week[new Date(i).getDay()];
			if(weekdays.contains(weekS) == false)
				weekdays.add(weekS);
			else break;
		}
		String res = "%"+StringListConverter.listToString(weekdays).replace(";;",";%;")+"%";
		System.out.println(res);
		return res;
	}

	@Override
	public String toString() {
		return String.format(
				"{ createdDate : %s , checkIn : %s , checkOut : %s , actualCheckIn : %s , actualCheckOut : %s , bookingState : %s , transactions: %s , weekdayRentPrice : %f , weekendRentPrice : %f }",
				createdDate,
				new Date(checkIn),
				new Date(checkOut),
				new Date(actualCheckIn == null ? 0 : actualCheckIn),
				new Date(actualCheckOut),
				bookingState,
				transactions,
				weekdayRentPrice,
				weekendRentPrice
				);
	}
}
