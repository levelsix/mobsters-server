/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.CityBoss;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.CityBossPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.CityBossRecord;

import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.1"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class CityBossDao extends DAOImpl<CityBossRecord, CityBossPojo, Integer> {

	/**
	 * Create a new CityBossDao without any configuration
	 */
	public CityBossDao() {
		super(CityBoss.CITY_BOSS, CityBossPojo.class);
	}

	/**
	 * Create a new CityBossDao with an attached configuration
	 */
	public CityBossDao(Configuration configuration) {
		super(CityBoss.CITY_BOSS, CityBossPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(CityBossPojo object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<CityBossPojo> fetchById(Integer... values) {
		return fetch(CityBoss.CITY_BOSS.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public CityBossPojo fetchOneById(Integer value) {
		return fetchOne(CityBoss.CITY_BOSS.ID, value);
	}

	/**
	 * Fetch records that have <code>city_id IN (values)</code>
	 */
	public List<CityBossPojo> fetchByCityId(Integer... values) {
		return fetch(CityBoss.CITY_BOSS.CITY_ID, values);
	}

	/**
	 * Fetch records that have <code>monster_id IN (values)</code>
	 */
	public List<CityBossPojo> fetchByMonsterId(Integer... values) {
		return fetch(CityBoss.CITY_BOSS.MONSTER_ID, values);
	}

	/**
	 * Fetch records that have <code>exp_reward IN (values)</code>
	 */
	public List<CityBossPojo> fetchByExpReward(Integer... values) {
		return fetch(CityBoss.CITY_BOSS.EXP_REWARD, values);
	}

	/**
	 * Fetch records that have <code>min_cash_drop IN (values)</code>
	 */
	public List<CityBossPojo> fetchByMinCashDrop(Integer... values) {
		return fetch(CityBoss.CITY_BOSS.MIN_CASH_DROP, values);
	}

	/**
	 * Fetch records that have <code>max_cash_drop IN (values)</code>
	 */
	public List<CityBossPojo> fetchByMaxCashDrop(Integer... values) {
		return fetch(CityBoss.CITY_BOSS.MAX_CASH_DROP, values);
	}

	/**
	 * Fetch records that have <code>puzzle_piece_drop_rate IN (values)</code>
	 */
	public List<CityBossPojo> fetchByPuzzlePieceDropRate(Double... values) {
		return fetch(CityBoss.CITY_BOSS.PUZZLE_PIECE_DROP_RATE, values);
	}

	/**
	 * Fetch records that have <code>level IN (values)</code>
	 */
	public List<CityBossPojo> fetchByLevel(Byte... values) {
		return fetch(CityBoss.CITY_BOSS.LEVEL, values);
	}
}