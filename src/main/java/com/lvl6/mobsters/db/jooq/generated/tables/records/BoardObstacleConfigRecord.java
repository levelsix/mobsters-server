/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.BoardObstacleConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IBoardObstacleConfig;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
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
@Table(name = "board_obstacle_config", schema = "mobsters")
public class BoardObstacleConfigRecord extends UpdatableRecordImpl<BoardObstacleConfigRecord> implements Record5<Integer, String, String, Integer, Boolean>, IBoardObstacleConfig {

	private static final long serialVersionUID = 1354825780;

	/**
	 * Setter for <code>mobsters.board_obstacle_config.id</code>.
	 */
	@Override
	public BoardObstacleConfigRecord setId(Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.board_obstacle_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.board_obstacle_config.name</code>.
	 */
	@Override
	public BoardObstacleConfigRecord setName(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.board_obstacle_config.name</code>.
	 */
	@Column(name = "name", nullable = false, length = 45)
	@NotNull
	@Size(max = 45)
	@Override
	public String getName() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.board_obstacle_config.type</code>.
	 */
	@Override
	public BoardObstacleConfigRecord setType(String value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.board_obstacle_config.type</code>.
	 */
	@Column(name = "type", length = 45)
	@Size(max = 45)
	@Override
	public String getType() {
		return (String) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.board_obstacle_config.power_amt</code>.
	 */
	@Override
	public BoardObstacleConfigRecord setPowerAmt(Integer value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.board_obstacle_config.power_amt</code>.
	 */
	@Column(name = "power_amt", precision = 10)
	@Override
	public Integer getPowerAmt() {
		return (Integer) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.board_obstacle_config.init_available</code>.
	 */
	@Override
	public BoardObstacleConfigRecord setInitAvailable(Boolean value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.board_obstacle_config.init_available</code>.
	 */
	@Column(name = "init_available", precision = 1)
	@Override
	public Boolean getInitAvailable() {
		return (Boolean) getValue(4);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record1<Integer> key() {
		return (Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record5 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row5<Integer, String, String, Integer, Boolean> fieldsRow() {
		return (Row5) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row5<Integer, String, String, Integer, Boolean> valuesRow() {
		return (Row5) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return BoardObstacleConfig.BOARD_OBSTACLE_CONFIG.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return BoardObstacleConfig.BOARD_OBSTACLE_CONFIG.NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field3() {
		return BoardObstacleConfig.BOARD_OBSTACLE_CONFIG.TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field4() {
		return BoardObstacleConfig.BOARD_OBSTACLE_CONFIG.POWER_AMT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> field5() {
		return BoardObstacleConfig.BOARD_OBSTACLE_CONFIG.INIT_AVAILABLE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value1() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value2() {
		return getName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value3() {
		return getType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value4() {
		return getPowerAmt();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean value5() {
		return getInitAvailable();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoardObstacleConfigRecord value1(Integer value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoardObstacleConfigRecord value2(String value) {
		setName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoardObstacleConfigRecord value3(String value) {
		setType(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoardObstacleConfigRecord value4(Integer value) {
		setPowerAmt(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoardObstacleConfigRecord value5(Boolean value) {
		setInitAvailable(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoardObstacleConfigRecord values(Integer value1, String value2, String value3, Integer value4, Boolean value5) {
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
	public void from(IBoardObstacleConfig from) {
		setId(from.getId());
		setName(from.getName());
		setType(from.getType());
		setPowerAmt(from.getPowerAmt());
		setInitAvailable(from.getInitAvailable());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IBoardObstacleConfig> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached BoardObstacleConfigRecord
	 */
	public BoardObstacleConfigRecord() {
		super(BoardObstacleConfig.BOARD_OBSTACLE_CONFIG);
	}

	/**
	 * Create a detached, initialised BoardObstacleConfigRecord
	 */
	public BoardObstacleConfigRecord(Integer id, String name, String type, Integer powerAmt, Boolean initAvailable) {
		super(BoardObstacleConfig.BOARD_OBSTACLE_CONFIG);

		setValue(0, id);
		setValue(1, name);
		setValue(2, type);
		setValue(3, powerAmt);
		setValue(4, initAvailable);
	}
}
