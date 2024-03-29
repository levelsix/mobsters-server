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

import com.lvl6.mobsters.db.jooq.generated.tables.daos.GiftForTangoUserDao;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.GiftForTangoUserPojo;
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

	//TODO: Consider deleting, rendered obsolete with GiftRewardConfigDao
	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;

	@Autowired
	protected GiftForTangoUserDao giftForTangoUserDao;

	//CONTROLLER LOGIC******************************************************************

	//RETRIEVE QUERIES*********************************************************************
	public Map<String, GiftForTangoUserPojo> getGftuForGfuIdsMap(Collection<String> gfuIds)
	{
		if (null == gfuIds || gfuIds.isEmpty()) {
			return new HashMap<String, GiftForTangoUserPojo>();
		}

		Map<String, GiftForTangoUserPojo> gfuIdToGftu = new HashMap<String, GiftForTangoUserPojo>();
		List<GiftForTangoUserPojo> gftus = getGftuForGfuIds(gfuIds);
		for (GiftForTangoUserPojo gftu : gftus) {
			String gfuId = gftu.getGiftForUserId();
			gfuIdToGftu.put(gfuId, gftu);
		}

		return gfuIdToGftu;
	}

	public List<GiftForTangoUserPojo> getGftuForGfuIds(Collection<String> gfuIds) {
		if (null == gfuIds || gfuIds.isEmpty()) {
			return new ArrayList<GiftForTangoUserPojo>();
		}

		int amt = gfuIds.size();
		List<String> questions = Collections.nCopies(amt, "?");
		String questionMarks = StringUtils.implode(questions, ",");

		String query = String.format("select * from %s where %s in (%s)",
				TABLE_NAME, DBConstants.GIFT_FOR_TANGO_USER__GIFT_FOR_USER_ID,
				questionMarks);

		List<GiftForTangoUserPojo> gftuList = null;
		log.info("getGftuForGfuIds() query={} values={}", query, gfuIds);
		try {
			gftuList = this.jdbcTemplate.query(
					query, gfuIds.toArray(), rowMapper);

		} catch (Exception e) {
			log.error(String.format(
					"could not retrieve GiftForTangoUser for gfuIds=%s",
					gfuIds), e);
			gftuList = new ArrayList<GiftForTangoUserPojo>();
		}

		return gftuList;
	}

	public GiftForTangoUserPojo getGftuForGfuId(String gfuId) {
		GiftForTangoUserPojo gftu = null;
		if (null == gfuId || gfuId.isEmpty()) {
			return gftu;
		}

		/*Object[] values = new Object[] { gfuId };
		String query = String.format("select * from %s where %s in (?)",
				TABLE_NAME, DBConstants.GIFT_FOR_TANGO_USER__GIFT_FOR_USER_ID );
				log.info("getGftuForGfuIds() query={} values={}", query, values);
		*/
		try {
			/*List<GiftForTangoUser> gftuList = this.jdbcTemplate.query(
					query, values, rowMapper);

			if (null != gftuList && !gftuList.isEmpty())
			{
				gftu = gftuList.get(0);
			}*/

			gftu = giftForTangoUserDao.fetchOneByGiftForUserId(gfuId);
		} catch (Exception e) {
			log.error(String.format(
					"could not retrieve GiftForTangoUser for gfuId=%s",
					gfuId), e);
		}
		return gftu;
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
			RowMapper<GiftForTangoUserPojo> {

//		private static List<String> columnsSelected;

		@Override
		public GiftForTangoUserPojo mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			GiftForTangoUserPojo gftu = new GiftForTangoUserPojo();
			gftu.setGiftForUserId(rs
					.getString(DBConstants.GIFT_FOR_TANGO_USER__GIFT_FOR_USER_ID));
			gftu.setGifterTangoName(rs
					.getString(DBConstants.GIFT_FOR_TANGO_USER__GIFTER_TANGO_NAME));

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
