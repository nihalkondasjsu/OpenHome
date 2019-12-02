package com.openhome.data;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.openhome.dao.helper.StringListConverter;

public class SpaceSearchQuery {

	private String keywords="";
	private String cityOrZip="";
	private String bookingStartDateTime="";
	private String bookingEndDateTime="";
	private List<String> sharingType=new ArrayList<String>();
	private List<String> propertyType=new ArrayList<String>();
	private Double minPrice=0.0;
	private Double maxPrice=10000.0;
	private Boolean internetAvailable=false;
	
	public String getKeywords() {
		if(keywords == null)
			return "";
		return keywords;
	}
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	
	public String getBookingStartDateTime() {
		if(bookingStartDateTime == null)
			throw new IllegalArgumentException("bookingStartDateTime not provided");
		return bookingStartDateTime;
	}
	public void setBookingStartDateTime(String bookingStartDateTime) {
		this.bookingStartDateTime = bookingStartDateTime;
	}
	public String getBookingEndDateTime() {
		if(bookingEndDateTime == null)
			throw new IllegalArgumentException("bookingEndDateTime not provided");
		return bookingEndDateTime;
	}

	public Date getBookingStartDateTimeObj() throws ParseException {
		return stringToDate(getBookingStartDateTime());
	}
	
	public Date getBookingEndDateTimeObj() throws ParseException {
		return stringToDate(getBookingEndDateTime());
	}
	
	public String getWeekDays() throws ParseException {
		List<String> weekdays = new ArrayList<String>();
		long start = getBookingStartDateTimeObj().getTime();
		long end = getBookingEndDateTimeObj().getTime();
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
	
	public void setBookingEndDateTime(String bookingEndDateTime) {
		this.bookingEndDateTime = bookingEndDateTime;
	}
	
	private Date stringToDate(String dateString) throws ParseException {
		String pattern = "yyyy-MM-dd";
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
		Date date = simpleDateFormat.parse(dateString);
		return date;
	}
	
	public List<String> getSharingType() {
		if(sharingType == null)
			return new ArrayList<String>();
		return sharingType;
	}
	public void setSharingType(List<String> sharingType) {
		this.sharingType = sharingType;
	}
	public List<String> getPropertyType() {
		if(propertyType == null)
			return new ArrayList<String>();
		return propertyType;
	}
	public void setPropertyType(List<String> propertyType) {
		this.propertyType = propertyType;
	}
	public Double getMinPrice() {
		if(minPrice == null)
			return 0.0;
		return minPrice;
	}
	public void setMinPrice(Double minPrice) {
		this.minPrice = minPrice;
	}
	public Double getMaxPrice() {
		if(maxPrice == null)
			return 100000.0;
		return maxPrice;
	}
	public void setMaxPrice(Double maxPrice) {
		this.maxPrice = maxPrice;
	}
	public Boolean getInternetAvailable() {
		if(internetAvailable == null)
			return false;
		return internetAvailable;
	}
	public void setInternetAvailable(Boolean internetAvailable) {
		this.internetAvailable = internetAvailable;
	}
	
	public boolean isCityQuery() {
		cityOrZip = cityOrZip.trim();
		if(cityOrZip.length() == 0)
			throw new IllegalArgumentException("No City or Zip provided");
		if(cityOrZip.matches("[0-9]+")) {
			return false;
		}
		return true;
	}
	
	public String getCityOrZip() {
		return cityOrZip;
	}
	public void setCityOrZip(String cityOrZip) {
		this.cityOrZip = cityOrZip;
	}
	
	public boolean suitableMatch(SpaceDetails spaceDetails) {
		if(getSharingType().size() > 0) {
			List<String> eliminateSharingType = new ArrayList<String>(Arrays.asList("Entire House","Shared Room","Private Room"));
			eliminateSharingType.removeAll(getSharingType());
			if(eliminateSharingType.contains(spaceDetails.getRoomType())) {
				System.out.println(spaceDetails.getName()+" fails at ST");
				return false;
			}
		}
		if(getPropertyType().size() > 0) {
			List<String> eliminatePropertyType = new ArrayList<String>(Arrays.asList("House","Apartment","Bed and breakfast"));
			eliminatePropertyType.removeAll(getPropertyType());
			if(eliminatePropertyType.contains(spaceDetails.getPropertyType())) {
				System.out.println(spaceDetails.getName()+" fails at PT");
				return false;
			}
		}
		String[] keywords = getKeywords().toLowerCase().split(" ");
		String description = spaceDetails.getDescription().toLowerCase();
		boolean temp = false;
		for (String keyword : keywords) {
			if(description.contains(keyword)) {
				temp = true;
				break;
			}
		}
		if(temp == false) {
			System.out.println(spaceDetails.getName()+" fails at KW");
			return false;
		}
		if(getInternetAvailable()) {
			if(spaceDetails.getFreeWiFi() == false) {
				System.out.println(spaceDetails.getName()+" fails at WF");
				return false;
			}
		}
		return true;
	}
	
}
