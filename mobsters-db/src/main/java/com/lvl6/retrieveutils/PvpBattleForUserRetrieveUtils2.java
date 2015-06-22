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

import com.lvl6.info.PvpBattleForUser;
import com.lvl6.properties.DBConstants;

@Component
@DependsOn("gameServer")
public class PvpBattleForUserRetrieveUtils2 {

	private static Logger log = LoggerFactory.getLogger(PvpBattleForUserRetrieveUtils2.class);

	private static final String TABLE_NAME = DBConstants.TABLE_PVP_BATTLE_FOR_USER;
	private static final UserPvpBattleForClientMapper rowMapper = new UserPvpBattleForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	//only used in script
	public PvpBattleForUser getPvpBattleForUserForAttacker(String userId) {
		log.debug("retrieving pvp battle for user");

		Object[] values = { userId };
		String query = String.format("select * from %s where %s=?", TABLE_NAME,
				DBConstants.PVP_BATTLE_FOR_USER__ATTACKER_ID);

		PvpBattleForUser pvpBattleForUser = null;
		try {
			List<PvpBattleForUser> pbfuList = this.jdbcTemplate.query(query,
					values, rowMapper);

			if (null != pbfuList && !pbfuList.isEmpty()) {
				pvpBattleForUser = pbfuList.get(0);
			}

		} catch (Exception e) {
			log.error("pvp battle for user retrieve db error.", e);
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return pvpBattleForUser;
	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserPvpBattleForClientMapper implements
			RowMapper<PvpBattleForUser> {

		private static List<String> columnsSelected;

		@Override
		public PvpBattleForUser mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			PvpBattleForUser pbfu = new PvpBattleForUser();
			pbfu.setAttackerId(rs
					.getString(DBConstants.PVP_BATTLE_FOR_USER__ATTACKER_ID));
			pbfu.setDefenderId(rs
					.getString(DBConstants.PVP_BATTLE_FOR_USER__DEFENDER_ID));
			pbfu.setAttackerWinEloChange(rs
					.getInt(DBConstants.PVP_BATTLE_FOR_USER__ATTACKER_WIN_ELO_CHANGE));
			pbfu.setDefenderLoseEloChange(rs
					.getInt(DBConstants.PVP_BATTLE_FOR_USER__DEFENDER_LOSE_ELO_CHANGE));
			pbfu.setAttackerLoseEloChange(rs
					.getInt(DBConstants.PVP_BATTLE_FOR_USER__ATTACKER_LOSE_ELO_CHANGE));
			pbfu.setDefenderWinEloChange(rs
					.getInt(DBConstants.PVP_BATTLE_FOR_USER__DEFENDER_WIN_ELO_CHANGE));

			try {
				Timestamp time = rs
						.getTimestamp(DBConstants.PVP_BATTLE_FOR_USER__BATTLE_START_TIME);
				if (null != time && !rs.wasNull()) {
					Date date = new Date(time.getTime());
					pbfu.setBattleStartTime(date);
				}
			} catch (Exception e) {
				log.error(String.format(
						"maybe battle start time is invalid, pbfu=%s", pbfu), e);
			}

			return pbfu;
		}

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected
						.add(DBConstants.PVP_BATTLE_FOR_USER__ATTACKER_ID);
				columnsSelected
						.add(DBConstants.PVP_BATTLE_FOR_USER__DEFENDER_ID);
				columnsSelected
						.add(DBConstants.PVP_BATTLE_FOR_USER__ATTACKER_WIN_ELO_CHANGE);
				columnsSelected
						.add(DBConstants.PVP_BATTLE_FOR_USER__DEFENDER_LOSE_ELO_CHANGE);
				columnsSelected
						.add(DBConstants.PVP_BATTLE_FOR_USER__ATTACKER_LOSE_ELO_CHANGE);
				columnsSelected
						.add(DBConstants.PVP_BATTLE_FOR_USER__DEFENDER_WIN_ELO_CHANGE);
				columnsSelected
						.add(DBConstants.PVP_BATTLE_FOR_USER__BATTLE_START_TIME);
			}
			return columnsSelected;
		}
	}
}
