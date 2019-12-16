package com.openhome.dao;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.openhome.data.Place;

public interface PlaceDAO extends JpaRepository<Place,Long>{

	@Query("select count(*) from Place s left join s.host h where h.id = :hostId")
	public long getPlaceCountOfHost(
			@Param("hostId")Long hostId
			);
	
	@Query("select s from Place s left join s.placeDetails sd where sd.id = :placeDetailsId")
	public Place getPlaceByPlaceDetailsId(
			@Param("placeDetailsId")Long placeDetailsId
			);
	
	//@Query(value="select s.id from Place s , Place_Details sd where s.host_id = :hostId and s.place_details_id = sd.id and sd.name = :placeName",nativeQuery=true)
	//public Long getPlaceByHostAndName(@Param("hostId")Long hostId,@Param("placeName")String placeName);

	@Query("select s.id from Place s left join s.placeDetails sd where sd.name = :placeName and s.host = (select h from Host h where h.id = :hostId)")
	public Long getPlaceByHostAndName(
			@Param("hostId")Long hostId,
			@Param("placeName")String placeName
			);
	
	/*
	
	@Query("select s from Place s where s.placeDetails in (select sd from PlaceDetails sd where "+
			"sd.address in (select a from Address a where (a.latitude between :minLatitude and :maxLatitude) and (a.longitude between :minLongitude and :maxLongitude))) "+
			"and 0 = (select count(*) from Reservation b where b.place = s and b.actualCheckOut >= :startDate and b.checkIn <= :endTime )")
	public List<Place> getPlacesByLocationAndDates(
			@Param("minLatitude")Double minLatitude,
			@Param("maxLatitude")Double maxLatitude,
			@Param("minLongitude")Double minLongitude,
			@Param("maxLongitude")Double maxLongitude,
			@Param("startDate")Long startDate,
			@Param("endTime")Long endTime
			);
	
	@Query("select s from Place s where s.placeDetails in (select sd from PlaceDetails sd where "+
			"sd.address in (select a from Address a where a.zip = :zip)) "+
			"and 0 = (select count(*) from Reservation b where b.place = s and b.actualCheckOut >= :startDate and b.checkIn <= :endTime )")
	public List<Place> getPlacesByZipAndDates(
			@Param("zip")String zip,
			@Param("startDate")Long startDate,
			@Param("endTime")Long endTime
			);
	
	@Query("select s from Place s where s.placeDetails in (select sd from PlaceDetails sd where "+
			"sd.address in (select a from Address a where a.city = :city)) "+
			"and 0 = (select count(*) from Reservation b where b.place = s and b.actualCheckOut >= :startDate and b.checkIn <= :endTime )")
	public List<Place> getPlacesByCityAndDates(
			@Param("city")String city,
			@Param("startDate")Long startDate,
			@Param("endTime")Long endTime
			);

	@Query("select s from Place s where s.placeDetails in (select sd from PlaceDetails sd where "+
			"(sd.weekdayRentPrice between :minPrice and :maxPrice ) and "+
			"sd.address in (select a from Address a where (a.latitude between :minLatitude and :maxLatitude) and (a.longitude between :minLongitude and :maxLongitude))) "+
			"and 0 = (select count(*) from Reservation b where b.place = s and b.actualCheckOut >= :startDate and b.checkIn <= :endTime )")
	public List<Place> getPlacesByLocationAndDatesAndPrice(
			@Param("minLatitude")Double minLatitude,
			@Param("maxLatitude")Double maxLatitude,
			@Param("minLongitude")Double minLongitude,
			@Param("maxLongitude")Double maxLongitude,
			@Param("startDate")Long startDate,
			@Param("endTime")Long endTime,
			@Param("minPrice")Double minPrice,
			@Param("maxPrice")Double maxPrice
			);

	*/
	
	@Query("select s from Place s where s.permanentlyUnavailable != true AND s.placeDetails in (select sd from PlaceDetails sd where "+
			"(sd.availableWeekDaysString like :requiredWeekDays) and "+
			"(sd.weekdayRentPrice between :minPrice and :maxPrice ) and "+
			"sd.address in (select a from Address a where a.zip = :zip)) "+
			"and 0 = (select count(*) from Reservation b where b.place = s and b.actualCheckOut >= :startDate and b.checkIn <= :endTime )")
	public List<Place> getPlacesByZipAndDatesAndPrice(
			@Param("zip")String zip,
			@Param("startDate")Long startDate,
			@Param("endTime")Long endTime,
			@Param("minPrice")Double minPrice,
			@Param("maxPrice")Double maxPrice,
			@Param("requiredWeekDays")String requiredWeekDays
			);
	
	@Query("select s from Place s where s.permanentlyUnavailable != true AND s.placeDetails in (select sd from PlaceDetails sd where "+
			"(sd.availableWeekDaysString like :requiredWeekDays) and "+
			"(sd.weekdayRentPrice between :minPrice and :maxPrice ) and "+
			"sd.address in (select a from Address a where a.city = :city)) "+
			"and 0 = (select count(*) from Reservation b where b.place = s and b.actualCheckOut >= :startDate and b.checkIn <= :endTime )")
	public List<Place> getPlacesByCityAndDatesAndPrice(
			@Param("city")String city,
			@Param("startDate")Long startDate,
			@Param("endTime")Long endTime,
			@Param("minPrice")Double minPrice,
			@Param("maxPrice")Double maxPrice,
			@Param("requiredWeekDays")String requiredWeekDays
			);
	
//	@Query("select count(s) from Place s where s.permanentlyUnavailable != true AND s.id = :placeId and s.placeDetails in (select sd from PlaceDetails sd where "+
//			"(sd.availableWeekDaysString like :requiredWeekDays) )"+
//			"and 0 = (select count(*) from Reservation b where b.place = s and b.actualCheckOut >= :startDate and b.checkIn <= :endTime )")
//	public Integer getSpecifiPlacesCountByDates(
//			@Param("placeId")Long placeId,
//			@Param("startDate")Long startDate,
//			@Param("endTime")Long endTime,
//			@Param("requiredWeekDays")String requiredWeekDays
//			);
	
	
	
//	@Query("select s from Place s where s.placeDetails = (select sd from PlaceDetails sd where )")
//	public Integer getReservationsOfPlaceByTime(@Param("placeId")Long placeId,@Param("checkIn")Long checkIn,@Param("checkOut")Long checkOut);
	
	
}
