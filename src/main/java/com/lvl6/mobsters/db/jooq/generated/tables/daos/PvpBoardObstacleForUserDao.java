/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.PvpBoardObstacleForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.records.PvpBoardObstacleForUserRecord;

import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.Record2;
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
public class PvpBoardObstacleForUserDao extends DAOImpl<PvpBoardObstacleForUserRecord, com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBoardObstacleForUser, Record2<String, Integer>> {

	/**
	 * Create a new PvpBoardObstacleForUserDao without any configuration
	 */
	public PvpBoardObstacleForUserDao() {
		super(PvpBoardObstacleForUser.PVP_BOARD_OBSTACLE_FOR_USER, com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBoardObstacleForUser.class);
	}

	/**
	 * Create a new PvpBoardObstacleForUserDao with an attached configuration
	 */
	public PvpBoardObstacleForUserDao(Configuration configuration) {
		super(PvpBoardObstacleForUser.PVP_BOARD_OBSTACLE_FOR_USER, com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBoardObstacleForUser.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Record2<String, Integer> getId(com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBoardObstacleForUser object) {
		return compositeKeyRecord(object.getUserId(), object.getId());
	}

	/**
	 * Fetch records that have <code>user_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBoardObstacleForUser> fetchByUserId(String... values) {
		return fetch(PvpBoardObstacleForUser.PVP_BOARD_OBSTACLE_FOR_USER.USER_ID, values);
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBoardObstacleForUser> fetchById(Integer... values) {
		return fetch(PvpBoardObstacleForUser.PVP_BOARD_OBSTACLE_FOR_USER.ID, values);
	}

	/**
	 * Fetch records that have <code>obstacle_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBoardObstacleForUser> fetchByObstacleId(Integer... values) {
		return fetch(PvpBoardObstacleForUser.PVP_BOARD_OBSTACLE_FOR_USER.OBSTACLE_ID, values);
	}

	/**
	 * Fetch records that have <code>pos_x IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBoardObstacleForUser> fetchByPosX(Integer... values) {
		return fetch(PvpBoardObstacleForUser.PVP_BOARD_OBSTACLE_FOR_USER.POS_X, values);
	}

	/**
	 * Fetch records that have <code>pos_y IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.PvpBoardObstacleForUser> fetchByPosY(Integer... values) {
		return fetch(PvpBoardObstacleForUser.PVP_BOARD_OBSTACLE_FOR_USER.POS_Y, values);
	}
}
