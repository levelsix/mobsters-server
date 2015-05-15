/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.PvpBoardObstacleForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IPvpBoardObstacleForUser;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record5;
import org.jooq.Row;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;


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
@Entity
@Table(name = "pvp_board_obstacle_for_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "id"})
})
public class PvpBoardObstacleForUserRecord extends UpdatableRecordImpl<PvpBoardObstacleForUserRecord> implements Record5<String, Integer, Integer, Integer, Integer>, IPvpBoardObstacleForUser {

	private static final long serialVersionUID = 1972897824;

	/**
	 * Setter for <code>mobsters.pvp_board_obstacle_for_user.user_id</code>.
	 */
	@Override
	public PvpBoardObstacleForUserRecord setUserId(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_board_obstacle_for_user.user_id</code>.
	 */
	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return (String) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.pvp_board_obstacle_for_user.id</code>.
	 */
	@Override
	public PvpBoardObstacleForUserRecord setId(Integer value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_board_obstacle_for_user.id</code>.
	 */
	@Column(name = "id", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return (Integer) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.pvp_board_obstacle_for_user.obstacle_id</code>.
	 */
	@Override
	public PvpBoardObstacleForUserRecord setObstacleId(Integer value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_board_obstacle_for_user.obstacle_id</code>.
	 */
	@Column(name = "obstacle_id", precision = 10)
	@Override
	public Integer getObstacleId() {
		return (Integer) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.pvp_board_obstacle_for_user.pos_x</code>.
	 */
	@Override
	public PvpBoardObstacleForUserRecord setPosX(Integer value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_board_obstacle_for_user.pos_x</code>.
	 */
	@Column(name = "pos_x", precision = 10)
	@Override
	public Integer getPosX() {
		return (Integer) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.pvp_board_obstacle_for_user.pos_y</code>.
	 */
	@Override
	public PvpBoardObstacleForUserRecord setPosY(Integer value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.pvp_board_obstacle_for_user.pos_y</code>.
	 */
	@Column(name = "pos_y", precision = 10)
	@Override
	public Integer getPosY() {
		return (Integer) getValue(4);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record2<String, Integer> key() {
		return (Record2) super.key();
	}

	// -------------------------------------------------------------------------
	// Record5 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row5<String, Integer, Integer, Integer, Integer> fieldsRow() {
		return (Row5) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row5<String, Integer, Integer, Integer, Integer> valuesRow() {
		return (Row5) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field1() {
		return PvpBoardObstacleForUser.PVP_BOARD_OBSTACLE_FOR_USER.USER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field2() {
		return PvpBoardObstacleForUser.PVP_BOARD_OBSTACLE_FOR_USER.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field3() {
		return PvpBoardObstacleForUser.PVP_BOARD_OBSTACLE_FOR_USER.OBSTACLE_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field4() {
		return PvpBoardObstacleForUser.PVP_BOARD_OBSTACLE_FOR_USER.POS_X;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field5() {
		return PvpBoardObstacleForUser.PVP_BOARD_OBSTACLE_FOR_USER.POS_Y;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value1() {
		return getUserId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value2() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value3() {
		return getObstacleId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value4() {
		return getPosX();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value5() {
		return getPosY();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PvpBoardObstacleForUserRecord value1(String value) {
		setUserId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PvpBoardObstacleForUserRecord value2(Integer value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PvpBoardObstacleForUserRecord value3(Integer value) {
		setObstacleId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PvpBoardObstacleForUserRecord value4(Integer value) {
		setPosX(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PvpBoardObstacleForUserRecord value5(Integer value) {
		setPosY(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PvpBoardObstacleForUserRecord values(String value1, Integer value2, Integer value3, Integer value4, Integer value5) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IPvpBoardObstacleForUser from) {
		setUserId(from.getUserId());
		setId(from.getId());
		setObstacleId(from.getObstacleId());
		setPosX(from.getPosX());
		setPosY(from.getPosY());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IPvpBoardObstacleForUser> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached PvpBoardObstacleForUserRecord
	 */
	public PvpBoardObstacleForUserRecord() {
		super(PvpBoardObstacleForUser.PVP_BOARD_OBSTACLE_FOR_USER);
	}

	/**
	 * Create a detached, initialised PvpBoardObstacleForUserRecord
	 */
	public PvpBoardObstacleForUserRecord(String userId, Integer id, Integer obstacleId, Integer posX, Integer posY) {
		super(PvpBoardObstacleForUser.PVP_BOARD_OBSTACLE_FOR_USER);

		setValue(0, userId);
		setValue(1, id);
		setValue(2, obstacleId);
		setValue(3, posX);
		setValue(4, posY);
	}
}
