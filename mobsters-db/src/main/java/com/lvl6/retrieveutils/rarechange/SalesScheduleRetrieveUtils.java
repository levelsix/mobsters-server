package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.db.jooq.generated.tables.daos.SalesScheduleConfigDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.SalesScheduleConfigPojo;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;
import com.lvl6.utils.TimeUtils;

@Component
@DependsOn("gameServer")
public class SalesScheduleRetrieveUtils {

	@Autowired
	protected SalesScheduleConfigDao sscDao;

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static Map<Integer, SalesScheduleConfigPojo> salesScheduleIdsToSalesSchedules;

	private static final String TABLE_NAME = DBConstants.TABLE_SALES_SCHEDULE_CONFIG;

	public Map<Integer, SalesScheduleConfigPojo> getSalesScheduleIdsToSalesSchedules() {
		log.debug("retrieving all sales packs data map");
		if (salesScheduleIdsToSalesSchedules == null) {
			setStaticSalesScheduleIdsToSalesSchedules();
		}
		return salesScheduleIdsToSalesSchedules;
	}

	public Map<Integer, SalesScheduleConfigPojo> getSalesSchedulesForSalesScheduleIds(
			List<Integer> salesScheduleIds) {
		if (salesScheduleIdsToSalesSchedules == null) {
			setStaticSalesScheduleIdsToSalesSchedules();
		}
		Map<Integer, SalesScheduleConfigPojo> returnValue = new HashMap<Integer, SalesScheduleConfigPojo>();
		for (int id : salesScheduleIds) {
			SalesScheduleConfigPojo aPack = salesScheduleIdsToSalesSchedules.get(id);
			returnValue.put(id, aPack);
		}

		return returnValue;
	}


	public SalesScheduleConfigPojo getSalesScheduleForSalesScheduleId(int salesScheduleId) {
		log.debug("retrieve sales pack data for sales pack "
				+ salesScheduleId);
		if (salesScheduleIdsToSalesSchedules == null) {
			setStaticSalesScheduleIdsToSalesSchedules();
		}
		return salesScheduleIdsToSalesSchedules.get(salesScheduleId);
	}

	public List<SalesScheduleConfigPojo> getSalesSchedulesForSalesPackageId(int salesPackageId) {
		if(salesScheduleIdsToSalesSchedules == null) {
			setStaticSalesScheduleIdsToSalesSchedules();
		}
		List<SalesScheduleConfigPojo> returnList = new ArrayList<SalesScheduleConfigPojo>();
		for(Integer salesScheduleId : salesScheduleIdsToSalesSchedules.keySet()) {
			SalesScheduleConfigPojo ss = salesScheduleIdsToSalesSchedules.get(salesScheduleId);
			if(ss.getSalesPackageId() == salesPackageId) {
				returnList.add(ss);
			}
		}
		return returnList;
	}

	public Map<Integer, SalesScheduleConfigPojo> getActiveSalesPackagesIdsToSalesSchedule(Date now, TimeUtils timeUtils) {
		if (salesScheduleIdsToSalesSchedules == null) {
			setStaticSalesScheduleIdsToSalesSchedules();
		}
		Map<Integer, SalesScheduleConfigPojo> listOfSalesPackageIds = new HashMap<Integer, SalesScheduleConfigPojo>();
		for(Integer salesScheduleId : salesScheduleIdsToSalesSchedules.keySet()) {
			SalesScheduleConfigPojo ss = salesScheduleIdsToSalesSchedules.get(salesScheduleId);
			if(ss.getTimeStart() != null) {
				Date timeStart = new Date(ss.getTimeStart().getTime());
				Date timeEnd = new Date(ss.getTimeEnd().getTime());
				if(timeUtils.isFirstEarlierThanSecond(timeStart, now) &&
						timeUtils.isFirstEarlierThanSecond(now, timeEnd)) {
					listOfSalesPackageIds.put(ss.getSalesPackageId(), ss);
				}	
			}
		}
		return listOfSalesPackageIds;
	}

	private void setStaticSalesScheduleIdsToSalesSchedules() {
		log.debug("setting static map of salesScheduleIds to salesSchedules");
		salesScheduleIdsToSalesSchedules = new HashMap<Integer, SalesScheduleConfigPojo>();

		List<SalesScheduleConfigPojo> salesSchedules = sscDao.findAll(); 
		for(SalesScheduleConfigPojo sscp : salesSchedules) {
			salesScheduleIdsToSalesSchedules.put(sscp.getId(), sscp);
		}
	}

	public void reload() {
		setStaticSalesScheduleIdsToSalesSchedules();
	}


}
