package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import com.lvl6.info.MonsterForPvp;
import com.lvl6.properties.DBConstants;
import com.lvl6.pvp.PvpConstants;
import com.lvl6.utils.DBConnection;

@Component
public class MonsterForPvpRetrieveUtils implements InitializingBean {

	private Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private Map<Integer, MonsterForPvp> idsToMonsterForPvps;

	private static final String TABLE_NAME = DBConstants.TABLE_MONSTER_FOR_PVP_CONFIG;

	public static final String MONSTER_FOR_PVP_MAP = "monsterForPvpMap";

	public static final String MONSTER_FOR_PVP__ELO = "elo";

	@Autowired
	protected HazelcastInstance hazel;

	//made this an IMap so I can query for monsters
	protected IMap<String, MonsterForPvp> idToMonsterForPvp;

	//METHOD TO ACTUALLY USE IMAP
	public Set<MonsterForPvp> retrievePvpMonsters(int minElo, int maxElo) {
		log.info("querying for people to attack. elo should be between minElo="
				+ minElo + ", maxElo=" + maxElo);

		String elo = PvpConstants.MONSTER_FOR_PVP__ELO;
		EntryObject e = new PredicateBuilder().getEntryObject();
		Predicate predicate = e.get(elo).between(minElo, maxElo);

		Set<MonsterForPvp> monsters = (Set<MonsterForPvp>) idToMonsterForPvp
				.values(predicate);
		//log.info("users:" + monsters);

		return monsters;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		populateMonsterForPvpMap();
	}

	protected void populateMonsterForPvpMap() {
		reload();
	}

	//  //hacky way of reloading
	//  public static void staticReload() {
	//  	(new MonsterForPvpRetrieveUtils()).reload();
	//  }

	public void reload() {

		log.info("reloading MonsterForPvp data!!!!!!!");
		setStaticIdsToMonsterForPvps();

		//this will create the map if it doesn't exist
		idToMonsterForPvp = hazel.getMap(MONSTER_FOR_PVP_MAP);

		//in mvn clean test, error was
		//Index can only be added before adding entries! Add indexes first and only once then put entries.
		//so this might be the issue
		if (null != idToMonsterForPvp && idToMonsterForPvp.isEmpty()) {
			log.info("adding indexes to MonsterForPvpRetrieveUtils.idToMonsterForPvp IMap");
			addMonsterForPvpIndexes();
		}

		populateMonsterForPvp();
	}

	//this is to make queries faster
	protected void addMonsterForPvpIndexes() {
		//the true is for indicating that there will be ranged queries on this property
		idToMonsterForPvp.addIndex(MONSTER_FOR_PVP__ELO, true);

	}

	protected void populateMonsterForPvp() {
		//go through all the monsters that will make up a fake user's team, and store them into
		//the hazelcast distributed map

		log.info("populating IMap with fake user monsters. num fake monsters="
				+ idsToMonsterForPvps.size());

		for (Integer id : idsToMonsterForPvps.keySet()) {
			MonsterForPvp mfp = idsToMonsterForPvps.get(id);

			String idStr = id.toString();
			idToMonsterForPvp.put(idStr, mfp);
		}

	}

	public MonsterForPvp getMonsterForPvpForMonsterForPvpId(int id) {
		if (idsToMonsterForPvps == null) {
			setStaticIdsToMonsterForPvps();
		}
		return idsToMonsterForPvps.get(id);
	}

	private void setStaticIdsToMonsterForPvps() {
		log.debug("setting map of ids to monsters for pvp");
		Random rand = new Random();

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						Map<Integer, MonsterForPvp> idsToMonsterForPvpsTemp = new HashMap<Integer, MonsterForPvp>();
						//loop through each row and convert it into a java object
						while (rs.next()) {
							MonsterForPvp mfp = convertRSRowToMonsterForPvp(rs);
							if (mfp == null) {
								continue;
							}
							mfp.setRand(rand);

							int id = mfp.getId();
							idsToMonsterForPvpsTemp.put(id, mfp);
						}
						idsToMonsterForPvps = idsToMonsterForPvpsTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("monster for pvp retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private MonsterForPvp convertRSRowToMonsterForPvp(ResultSet rs)
			throws SQLException {
		int id = rs.getInt(DBConstants.MONSTER_FOR_PVP__ID);
		int monsterId = rs.getInt(DBConstants.MONSTER_FOR_PVP__MONSTER_ID);
		int monsterLvl = rs.getInt(DBConstants.MONSTER_FOR_PVP__MONSTER_LVL);
		int elo = rs.getInt(DBConstants.MONSTER_FOR_PVP__ELO);
		int minCashReward = rs
				.getInt(DBConstants.MONSTER_FOR_PVP__MIN_CASH_REWARD);
		int maxCashReward = rs
				.getInt(DBConstants.MONSTER_FOR_PVP__MAX_CASH_REWARD);
		int minOilReward = rs
				.getInt(DBConstants.MONSTER_FOR_PVP__MIN_OIL_REWARD);
		int maxOilReward = rs
				.getInt(DBConstants.MONSTER_FOR_PVP__MAX_OIL_REWARD);

		MonsterForPvp mfp = new MonsterForPvp(id, monsterId, monsterLvl, elo,
				minCashReward, maxCashReward, minOilReward, maxOilReward);

		return mfp;
	}

	public HazelcastInstance getHazel() {
		return hazel;
	}

	public void setHazel(HazelcastInstance hazel) {
		this.hazel = hazel;
	}

}
