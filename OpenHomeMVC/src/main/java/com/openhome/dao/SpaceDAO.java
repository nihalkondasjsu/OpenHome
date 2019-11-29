package com.openhome.dao;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.openhome.data.Space;

public interface SpaceDAO extends JpaRepository<Space,Long>{

	@Query("select count(*) from Space s left join s.host h where h.id = :hostId")
	public long getSpaceCountOfHost(
			@Param("hostId")Long hostId
			);
	
	@Query("select s from Space s left join s.spaceDetails sd where sd.id = :spaceDetailsId")
	public Space getSpaceBySpaceDetailsId(
			@Param("spaceDetailsId")Long spaceDetailsId
			);
	
	//@Query(value="select s.id from Space s , Space_Details sd where s.host_id = :hostId and s.space_details_id = sd.id and sd.name = :spaceName",nativeQuery=true)
	//public Long getSpaceByHostAndName(@Param("hostId")Long hostId,@Param("spaceName")String spaceName);

	@Query("select s.id from Space s left join s.spaceDetails sd where sd.name = :spaceName and s.host = (select h from Host h where h.id = :hostId)")
	public Long getSpaceByHostAndName(
			@Param("hostId")Long hostId,
			@Param("spaceName")String spaceName
			);
	
	@Query("select s from Space s where s.spaceDetails in (select sd from SpaceDetails sd where "+
			"sd.address in (select a from Address a where (a.latitude between :minLatitude and :maxLatitude) and (a.longitude between :minLongitude and :maxLongitude))) "+
			"and 0 = (select count(*) from Booking b where b.space = s and b.actualCheckOut >= :startDate and b.checkIn <= :endTime )")
	public List<Space> getSpacesByLocationAndDates(
			@Param("minLatitude")Double minLatitude,
			@Param("maxLatitude")Double maxLatitude,
			@Param("minLongitude")Double minLongitude,
			@Param("maxLongitude")Double maxLongitude,
			@Param("startDate")Long startDate,
			@Param("endTime")Long endTime
			);
	
	@Query("select s from Space s where s.spaceDetails in (select sd from SpaceDetails sd where "+
			"sd.address in (select a from Address a where a.zip = :zip)) "+
			"and 0 = (select count(*) from Booking b where b.space = s and b.actualCheckOut >= :startDate and b.checkIn <= :endTime )")
	public List<Space> getSpacesByZipAndDates(
			@Param("zip")String zip,
			@Param("startDate")Long startDate,
			@Param("endTime")Long endTime
			);

	@Query("select s from Space s where s.spaceDetails in (select sd from SpaceDetails sd where "+
			"(sd.weekdayRentPrice between :minPrice and :maxPrice ) and "+
			"sd.address in (select a from Address a where (a.latitude between :minLatitude and :maxLatitude) and (a.longitude between :minLongitude and :maxLongitude))) "+
			"and 0 = (select count(*) from Booking b where b.space = s and b.actualCheckOut >= :startDate and b.checkIn <= :endTime )")
	public List<Space> getSpacesByLocationAndDatesAndPrice(
			@Param("minLatitude")Double minLatitude,
			@Param("maxLatitude")Double maxLatitude,
			@Param("minLongitude")Double minLongitude,
			@Param("maxLongitude")Double maxLongitude,
			@Param("startDate")Long startDate,
			@Param("endTime")Long endTime,
			@Param("minPrice")Double minPrice,
			@Param("maxPrice")Double maxPrice
			);
	
	@Query("select s from Space s where s.spaceDetails in (select sd from SpaceDetails sd where "+
			"(sd.weekdayRentPrice between :minPrice and :maxPrice ) and "+
			"sd.address in (select a from Address a where a.zip = :zip)) "+
			"and 0 = (select count(*) from Booking b where b.space = s and b.actualCheckOut >= :startDate and b.checkIn <= :endTime )")
	public List<Space> getSpacesByZipAndDatesAndPrice(
			@Param("zip")String zip,
			@Param("startDate")Long startDate,
			@Param("endTime")Long endTime,
			@Param("minPrice")Double minPrice,
			@Param("maxPrice")Double maxPrice
			);
	
}
