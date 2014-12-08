package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
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
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.lvl6.info.Clan;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.utilmethods.StringUtils;

@Component @DependsOn("gameServer") public class ClanRetrieveUtils2 {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_CLANS;
	private static final ClanForClientMapper rowMapper = new ClanForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	//CONTROLLER LOGIC******************************************************************
	public List<String> getClanIdsFromClans(Collection<Clan> clanList) {
		List<String> clanIdList = new ArrayList<String>();

		for (Clan clan : clanList) {
			String clanId = clan.getId();
			clanIdList.add(clanId);
		}
		return clanIdList;
	}

	//RETRIEVE QUERIES*********************************************************************
	@Cacheable(value="clanWithId", key="#clanId")
	public Clan getClanWithId(String clanId) {
		Clan clan = null;
		try {
			Object[] values = { clanId };
			String query = String.format(
				"select * from %s where %s=?",
				TABLE_NAME, DBConstants.CLANS__ID);

			clan = this.jdbcTemplate.queryForObject(query, values, rowMapper);
		} catch (Exception e) {
			log.error(String.format("could not retrieve clan for id=%s", clanId), e);
		}
		
		return clan;
	}



	public Map<String, Clan> getClansByIds(Collection<String> clanIds) {
		log.debug("retrieving clans with ids " + clanIds);

		if (clanIds == null || clanIds.size() <= 0 ) {
			return new HashMap<String, Clan>();
		}
		
		List<Object> values = new ArrayList<Object>();
		values.addAll(clanIds);

		List<String> questions = Collections.nCopies(clanIds.size(), "?");
		String csQuestions = StringUtils.getListInString(questions, ",");
		String query = String.format(
			"SELECT * FROM %s WHERE %s IN (%s)",
			TABLE_NAME, DBConstants.CLANS__ID, csQuestions);

		Map<String, Clan> clanIdToClanMap = new HashMap<String, Clan>();
		try {
			List<Clan> clans = this.jdbcTemplate.query(query, values.toArray(), rowMapper);
			
			for (Clan c : clans) {
				clanIdToClanMap.put(c.getId(), c);
			}
		} catch (Exception e) {
			log.error("clan retrieve db error. clanIds="+clanIds, e);
		}
		return clanIdToClanMap;
	}

	public List<Clan> getClansWithSimilarNameOrTag(String name, String tag) {
		log.debug(String.format(
			"retrieving clan with name=%s, tag=%s",
			name, tag));
		
		String query = String.format(
		           "SELECT * FROM %s WHERE %s LIKE ? OR %s LIKE ?",
		           TABLE_NAME, DBConstants.CLANS__NAME, DBConstants.CLANS__TAG);

		List<Object> values = new ArrayList<Object>();
		values.add("%"+name+"%");
		values.add("%"+tag+"%");

		List<Clan> clans = null;
		try {
			clans = this.jdbcTemplate.query(query, values.toArray(), rowMapper);
		} catch (Exception e) {
			log.error("clan retrieve db error.", e);
		}
		return clans;
	}

	public Clan getClanWithNameOrTag(String name, String tag) {
		log.debug(String.format(
			"retrieving clan with name=%s, tag=%s",
			name, tag));
		
		String query = String.format(
		           "SELECT * FROM %s WHERE %s=? OR %s=?",
		           TABLE_NAME, DBConstants.CLANS__NAME, DBConstants.CLANS__TAG);

		List<Object> values = new ArrayList<Object>();
		values.add(name);
		values.add(tag);

		Clan clan = null;
		try {
			List<Clan> clans = this.jdbcTemplate.query(query, values.toArray(), rowMapper);

			if (null != clans && !clans.isEmpty()) {
				clan = clans.get(0);
			}
		} catch (Exception e) {
			log.error("clan retrieve db error.", e);
		}
		return clan;
	}

	public List<Clan> getMostRecentClans(int limit) {
		Object[] values = { limit };
		String query = String.format(
		           "SELECT * FROM %s ORDER BY %s LIMIT ?",
		           TABLE_NAME, DBConstants.CLANS__CREATE_TIME);

		List<Clan> clans = null;
		try {
			clans = this.jdbcTemplate.query(query, values, rowMapper);
		} catch (Exception e) {
			log.error("clan retrieve db error.", e);
		}
		return clans;
	}

    public List<Clan> getRandomClans(int limit) {
        Object[] values = { limit };
        String query = String.format(
                   "SELECT * FROM %s ORDER BY rand() LIMIT ?",
                   TABLE_NAME);

        List<Clan> clans = null;
        try {
            clans = this.jdbcTemplate.query(query, values, rowMapper);
        } catch (Exception e) {
            log.error("clan retrieve db error.", e);
        }
        return clans;
    }

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class ClanForClientMapper implements RowMapper<Clan> {

		private static List<String> columnsSelected;

		public Clan mapRow(ResultSet rs, int rowNum) throws SQLException {
			Clan clan = new Clan();
			clan.setId(rs.getString(DBConstants.CLANS__ID));
			clan.setName(rs.getString(DBConstants.CLANS__NAME));
			try {
				Timestamp time = rs.getTimestamp(DBConstants.CLANS__CREATE_TIME);
				if (null != time && !rs.wasNull()) {
					Date date = new Date(time.getTime());
					clan.setCreateTime(date);
				}
			} catch (Exception e) {
				log.error(String.format(
					"maybe ocreateTime is invalid, clan=%s", clan), e);
			}
			
			clan.setDescription(rs.getString(DBConstants.CLANS__DESCRIPTION));
			clan.setTag(rs.getString(DBConstants.CLANS__TAG));
			clan.setRequestToJoinRequired(rs.getBoolean(DBConstants.CLANS__REQUEST_TO_JOIN_REQUIRED));
			clan.setClanIconId(rs.getInt(DBConstants.CLANS__CLAN_ICON_ID));
			
			return clan;
		}        

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.CLANS__ID);
				columnsSelected.add(DBConstants.CLANS__NAME);
				columnsSelected.add(DBConstants.CLANS__CREATE_TIME);
				columnsSelected.add(DBConstants.CLANS__DESCRIPTION);
				columnsSelected.add(DBConstants.CLANS__TAG);
				columnsSelected.add(DBConstants.CLANS__REQUEST_TO_JOIN_REQUIRED);
				columnsSelected.add(DBConstants.CLANS__CLAN_ICON_ID);
			}
			return columnsSelected;
		}
	} 	
}
