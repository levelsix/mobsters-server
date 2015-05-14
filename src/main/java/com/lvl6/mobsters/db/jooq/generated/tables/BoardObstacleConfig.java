/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.BoardObstacleConfigRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;


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
public class BoardObstacleConfig extends TableImpl<BoardObstacleConfigRecord> {

	private static final long serialVersionUID = -1399971597;

	/**
	 * The reference instance of <code>mobsters.board_obstacle_config</code>
	 */
	public static final BoardObstacleConfig BOARD_OBSTACLE_CONFIG = new BoardObstacleConfig();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<BoardObstacleConfigRecord> getRecordType() {
		return BoardObstacleConfigRecord.class;
	}

	/**
	 * The column <code>mobsters.board_obstacle_config.id</code>.
	 */
	public final TableField<BoardObstacleConfigRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.board_obstacle_config.name</code>.
	 */
	public final TableField<BoardObstacleConfigRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR.length(45).nullable(false), this, "");

	/**
	 * The column <code>mobsters.board_obstacle_config.type</code>.
	 */
	public final TableField<BoardObstacleConfigRecord, String> TYPE = createField("type", org.jooq.impl.SQLDataType.VARCHAR.length(45), this, "");

	/**
	 * The column <code>mobsters.board_obstacle_config.power_amt</code>.
	 */
	public final TableField<BoardObstacleConfigRecord, Integer> POWER_AMT = createField("power_amt", org.jooq.impl.SQLDataType.INTEGER.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.board_obstacle_config.init_available</code>.
	 */
	public final TableField<BoardObstacleConfigRecord, Boolean> INIT_AVAILABLE = createField("init_available", org.jooq.impl.SQLDataType.BIT.defaulted(true), this, "");

	/**
	 * Create a <code>mobsters.board_obstacle_config</code> table reference
	 */
	public BoardObstacleConfig() {
		this("board_obstacle_config", null);
	}

	/**
	 * Create an aliased <code>mobsters.board_obstacle_config</code> table reference
	 */
	public BoardObstacleConfig(String alias) {
		this(alias, BOARD_OBSTACLE_CONFIG);
	}

	private BoardObstacleConfig(String alias, Table<BoardObstacleConfigRecord> aliased) {
		this(alias, aliased, null);
	}

	private BoardObstacleConfig(String alias, Table<BoardObstacleConfigRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<BoardObstacleConfigRecord> getPrimaryKey() {
		return Keys.KEY_BOARD_OBSTACLE_CONFIG_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<BoardObstacleConfigRecord>> getKeys() {
		return Arrays.<UniqueKey<BoardObstacleConfigRecord>>asList(Keys.KEY_BOARD_OBSTACLE_CONFIG_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoardObstacleConfig as(String alias) {
		return new BoardObstacleConfig(alias, this);
	}

	/**
	 * Rename this table
	 */
	public BoardObstacleConfig rename(String name) {
		return new BoardObstacleConfig(name, null);
	}
}
