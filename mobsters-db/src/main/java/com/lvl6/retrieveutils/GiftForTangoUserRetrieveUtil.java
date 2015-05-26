package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.lvl6.info.GiftForTangoUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.retrieveutils.util.QueryConstructionUtil;
import com.lvl6.utils.utilmethods.StringUtils;

@Component
public class GiftForTangoUserRetrieveUtil {
	private static Logger log = LoggerFactory
			.getLogger(GiftForTangoUserRetrieveUtil.class);

	private static final String TABLE_NAME = DBConstants.TABLE_GIFT_FOR_TANGO_USER;
	private static final UserTaskClientStateForClientMapper rowMapper = new UserTaskClientStateForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;

	//CONTROLLER LOGIC******************************************************************

	//RETRIEVE QUERIES*********************************************************************
	public Map<String, GiftForTangoUser> getGftuForGfuIdsMap(Collection<String> gfuIds)
	{
		if (null == gfuIds || gfuIds.isEmpty()) {
			return new HashMap<String, GiftForTangoUser>();
		}

		Map<String, GiftForTangoUser> gfuIdToGftu = new HashMap<String, GiftForTangoUser>();
		List<GiftForTangoUser> gftus = getGftuForGfuIds(gfuIds);
		for (GiftForTangoUser gftu : gftus) {
			String gfuId = gftu.getGiftForUserId();
			gfuIdToGftu.put(gfuId, gftu);
		}

		return gfuIdToGftu;
	}

	public List<GiftForTangoUser> getGftuForGfuIds(Collection<String> gfuIds) {
		if (null == gfuIds || gfuIds.isEmpty()) {
			return new ArrayList<GiftForTangoUser>();
		}

		int amt = gfuIds.size();
		List<String> questions = Collections.nCopies(amt, "?");
		String questionMarks = StringUtils.implode(questions, ",");

		String query = String.format("select * from %s where %s in (%s)",
				TABLE_NAME, DBConstants.GIFT_FOR_TANGO_USER__GIFT_FOR_USER_ID,
				questionMarks);

		List<GiftForTangoUser> gftuList = null;
		log.info("getGftuForGfuIds() query={} values={}", query, gfuIds);
		try {
			gftuList = this.jdbcTemplate.query(
					query, gfuIds.toArray(), rowMapper);

		} catch (Exception e) {
			log.error(String.format(
					"could not retrieve GiftForTangoUser for gfuIds=%s",
					gfuIds), e);
			gftuList = new ArrayList<GiftForTangoUser>();
		}

		return gftuList;
	}

	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}

	public void setQueryConstructionUtil(
			QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}

	//Date twenty4ago = new DateTime().minusDays(1).toDate();
	protected String formatDateToString(Date date) {
		SimpleDateFormat format = new SimpleDateFormat("yyyy.MM.dd HH:mm:ss");
		String formatted = format.format(date);
		return formatted;
	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserTaskClientStateForClientMapper implements
			RowMapper<GiftForTangoUser> {

//		private static List<String> columnsSelected;

		@Override
		public GiftForTangoUser mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			GiftForTangoUser gftu = new GiftForTangoUser();
			gftu.setGiftForUserId(rs
					.getString(DBConstants.GIFT_FOR_TANGO_USER__GIFT_FOR_USER_ID));
			gftu.setGifterUserId(rs
					.getString(DBConstants.GIFT_FOR_TANGO_USER__GIFTER_USER_ID));
			gftu.setGifterTangoUserId(rs
					.getString(DBConstants.GIFT_FOR_TANGO_USER__GIFTER_TANGO_USER_ID));

			return gftu;
		}

//		public static List<String> getColumnsSelected() {
//			if (null == columnsSelected) {
//				columnsSelected = new ArrayList<String>();
//				columnsSelected
//						.add(DBConstants.GIFT_FOR_TANGO_USER__USER_ID);
//				columnsSelected
//						.add(DBConstants.GIFT_FOR_TANGO_USER__CLIENT_STATE);
//			}
//			return columnsSelected;
//		}
	}
}
