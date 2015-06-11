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
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.db.jooq.generated.tables.pojos.SalesSchedule;
import com.lvl6.properties.DBConstants;
import com.lvl6.server.controller.utils.TimeUtils;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class SalesScheduleRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static Map<Integer, SalesSchedule> salesScheduleIdsToSalesSchedules;

	private static final String TABLE_NAME = DBConstants.TABLE_SALES_SCHEDULE_CONFIG;

	public Map<Integer, SalesSchedule> getSalesScheduleIdsToSalesSchedules() {
		log.debug("retrieving all sales packs data map");
		if (salesScheduleIdsToSalesSchedules == null) {
			setStaticSalesScheduleIdsToSalesSchedules();
		}
		return salesScheduleIdsToSalesSchedules;
	}

	public Map<Integer, SalesSchedule> getSalesSchedulesForSalesScheduleIds(
			List<Integer> salesScheduleIds) {
		if (salesScheduleIdsToSalesSchedules == null) {
			setStaticSalesScheduleIdsToSalesSchedules();
		}
		Map<Integer, SalesSchedule> returnValue = new HashMap<Integer, SalesSchedule>();
		for (int id : salesScheduleIds) {
			SalesSchedule aPack = salesScheduleIdsToSalesSchedules.get(id);
			returnValue.put(id, aPack);
		}

		return returnValue;
	}


	public SalesSchedule getSalesScheduleForSalesScheduleId(int salesScheduleId) {
		log.debug("retrieve sales pack data for sales pack "
				+ salesScheduleId);
		if (salesScheduleIdsToSalesSchedules == null) {
			setStaticSalesScheduleIdsToSalesSchedules();
		}
		return salesScheduleIdsToSalesSchedules.get(salesScheduleId);
	}

	public List<SalesSchedule> getSalesSchedulesForSalesPackageId(int salesPackageId) {
		if(salesScheduleIdsToSalesSchedules == null) {
			setStaticSalesScheduleIdsToSalesSchedules();
		}
		List<SalesSchedule> returnList = new ArrayList<SalesSchedule>();
		for(Integer salesScheduleId : salesScheduleIdsToSalesSchedules.keySet()) {
			SalesSchedule ss = salesScheduleIdsToSalesSchedules.get(salesScheduleId);
			if(ss.getSalesPackageId() == salesPackageId) {
				returnList.add(ss);
			}
		}
		return returnList;
	}

	public Map<Integer, SalesSchedule> getActiveSalesPackagesIdsToSalesSchedule(Date now, TimeUtils timeUtils) {
		if (salesScheduleIdsToSalesSchedules == null) {
			setStaticSalesScheduleIdsToSalesSchedules();
		}
		Map<Integer, SalesSchedule> listOfSalesPackageIds = new HashMap<Integer, SalesSchedule>();
		for(Integer salesScheduleId : salesScheduleIdsToSalesSchedules.keySet()) {
			SalesSchedule ss = salesScheduleIdsToSalesSchedules.get(salesScheduleId);
			Date timeStart = new Date(ss.getTimeStart().getTime());
			Date timeEnd = new Date(ss.getTimeEnd().getTime());
			if(timeUtils.isFirstEarlierThanSecond(timeStart, now) &&
                        timeUtils.isFirstEarlierThanSecond(now, timeEnd)) {
				listOfSalesPackageIds.put(ss.getSalesPackageId(), ss);
			}
		}
		return listOfSalesPackageIds;
	}

	private void setStaticSalesScheduleIdsToSalesSchedules() {
		log.debug("setting static map of salesScheduleIds to salesSchedules");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						HashMap<Integer, SalesSchedule> salesScheduleIdsToSalesSchedulesTemp = new HashMap<Integer, SalesSchedule>();
						while (rs.next()) {  //should only be one
							SalesSchedule salesSchedule = convertRSRowToSalesSchedule(rs);
							if (salesSchedule != null)
								salesScheduleIdsToSalesSchedulesTemp.put(
										salesSchedule.getId(), salesSchedule);
						}
						salesScheduleIdsToSalesSchedules = salesScheduleIdsToSalesSchedulesTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("sales pack retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public void reload() {
		setStaticSalesScheduleIdsToSalesSchedules();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private SalesSchedule convertRSRowToSalesSchedule(ResultSet rs)
			throws SQLException {
		int id = rs.getInt(DBConstants.SALES_SCHEDULE__ID);
		int salesPackageId = rs.getInt(DBConstants.SALES_SCHEDULE__SALES_PACKAGE_ID);
		Timestamp timeStart = null;
		Timestamp timeEnd = null;
		Timestamp ts = null;

		try {
			ts = rs.getTimestamp(DBConstants.SALES_SCHEDULE__TIME_START);
			if (!rs.wasNull()) {
				timeStart = ts;
			}
		} catch (Exception e) {
			log.error("last_purchase_time null...?", e);
		}

		try {
			ts = rs.getTimestamp(DBConstants.SALES_SCHEDULE__TIME_END);
			if (!rs.wasNull()) {
				timeEnd = ts;
			}
		} catch (Exception e) {
			log.error("last_purchase_time null...?", e);
		}

		SalesSchedule salesSchedule = new SalesSchedule(id, salesPackageId, timeStart, timeEnd);
		return salesSchedule;
	}
}
