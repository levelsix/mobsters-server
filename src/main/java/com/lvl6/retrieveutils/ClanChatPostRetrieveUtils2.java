package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.lvl6.info.ClanChatPost;
import com.lvl6.properties.DBConstants;

@Component @DependsOn("gameServer") public class ClanChatPostRetrieveUtils2 {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_CLAN_CHAT_POST;
	private static final ClanChatPostForClientMapper rowMapper = new ClanChatPostForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	public List<ClanChatPost> getMostRecentClanChatPostsForClan(int limit, String clanId) {
		log.debug("retrieving " + limit + " clan wall posts for clan " + clanId);

		Object[] values = { clanId };
		String query = String.format(
			"select * from %s where %s=? order by %s desc limit %s",
			TABLE_NAME, DBConstants.CLAN_CHAT_POST__CLAN_ID,
			DBConstants.CLAN_CHAT_POST__TIME_OF_POST, limit);

		List<ClanChatPost> clanChatPosts = null;
		try {
			clanChatPosts = this.jdbcTemplate
				.query(query, values, rowMapper);
		} catch (Exception e) {
			log.error("ClanChatPost retrieve db error.", e);
			clanChatPosts = new ArrayList<ClanChatPost>();
//		} finally {
//			DBConnection.get().close(rs, null, conn);
		}
		return clanChatPosts;
	}

	public Date getLastChatPost(String clanId)
	{
		Object[] values = { clanId };
		String query = String.format(
			"select max(%s) from %s where %s=?;",
			DBConstants.CLAN_CHAT_POST__TIME_OF_POST,
			TABLE_NAME,
			DBConstants.CLAN_CHAT_POST__CLAN_ID);
		
		Date lastChatPost = null;
	    try {
	    	List<Date> dateList = this.jdbcTemplate
	    		.queryForList(query, values, Date.class);

	    	if (null == dateList || dateList.isEmpty()) {
	    		lastChatPost = dateList.get(0);
	    	}

	    } catch (Exception e) {
	    	log.error("ClanChatPost retrieve db error.", e);
	    	//    } finally {
	    	//    	DBConnection.get().close(rs, null, conn);
	    }
	    return lastChatPost;
	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class ClanChatPostForClientMapper implements RowMapper<ClanChatPost> {

		private static List<String> columnsSelected;

		public ClanChatPost mapRow(ResultSet rs, int rowNum) throws SQLException {
			ClanChatPost ccp = new ClanChatPost();
			ccp.setId(rs.getString(DBConstants.CLAN_CHAT_POST__ID));
			ccp.setPosterId(rs.getString(DBConstants.CLAN_CHAT_POST__POSTER_ID));
			ccp.setClanId(rs.getString(DBConstants.CLAN_CHAT_POST__CLAN_ID));
			
			Timestamp time = rs.getTimestamp(DBConstants.CLAN_CHAT_POST__TIME_OF_POST);
			if (null != time) {
				ccp.setTimeOfPost(new Date(time.getTime()));
			}
			ccp.setContent(rs.getString(DBConstants.CLAN_CHAT_POST__CONTENT));
			return ccp;
		}        

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.CLAN_CHAT_POST__ID);
				columnsSelected.add(DBConstants.CLAN_CHAT_POST__POSTER_ID);
				columnsSelected.add(DBConstants.CLAN_CHAT_POST__CLAN_ID);
				columnsSelected.add(DBConstants.CLAN_CHAT_POST__TIME_OF_POST);
				columnsSelected.add(DBConstants.CLAN_CHAT_POST__CONTENT);
			}
			return columnsSelected;
		}
	} 	
}
