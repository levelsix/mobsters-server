/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ObstacleForUserRecord;

import java.sql.Timestamp;
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
public class ObstacleForUser extends TableImpl<ObstacleForUserRecord> {

	private static final long serialVersionUID = -1257981241;

	/**
	 * The reference instance of <code>mobsters.obstacle_for_user</code>
	 */
	public static final ObstacleForUser OBSTACLE_FOR_USER = new ObstacleForUser();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<ObstacleForUserRecord> getRecordType() {
		return ObstacleForUserRecord.class;
	}

	/**
	 * The column <code>mobsters.obstacle_for_user.id</code>.
	 */
	public final TableField<ObstacleForUserRecord, String> ID = createField("id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.obstacle_for_user.user_id</code>.
	 */
	public final TableField<ObstacleForUserRecord, String> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.CHAR.length(36), this, "");

	/**
	 * The column <code>mobsters.obstacle_for_user.obstacle_id</code>.
	 */
	public final TableField<ObstacleForUserRecord, Integer> OBSTACLE_ID = createField("obstacle_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.obstacle_for_user.xcoord</code>.
	 */
	public final TableField<ObstacleForUserRecord, Integer> XCOORD = createField("xcoord", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.obstacle_for_user.ycoord</code>.
	 */
	public final TableField<ObstacleForUserRecord, Integer> YCOORD = createField("ycoord", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.obstacle_for_user.removal_time</code>.
	 */
	public final TableField<ObstacleForUserRecord, Timestamp> REMOVAL_TIME = createField("removal_time", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

	/**
	 * The column <code>mobsters.obstacle_for_user.orientation</code>.
	 */
	public final TableField<ObstacleForUserRecord, String> ORIENTATION = createField("orientation", org.jooq.impl.SQLDataType.VARCHAR.length(45), this, "");

	/**
	 * Create a <code>mobsters.obstacle_for_user</code> table reference
	 */
	public ObstacleForUser() {
		this("obstacle_for_user", null);
	}

	/**
	 * Create an aliased <code>mobsters.obstacle_for_user</code> table reference
	 */
	public ObstacleForUser(String alias) {
		this(alias, OBSTACLE_FOR_USER);
	}

	private ObstacleForUser(String alias, Table<ObstacleForUserRecord> aliased) {
		this(alias, aliased, null);
	}

	private ObstacleForUser(String alias, Table<ObstacleForUserRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<ObstacleForUserRecord> getPrimaryKey() {
		return Keys.KEY_OBSTACLE_FOR_USER_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<ObstacleForUserRecord>> getKeys() {
		return Arrays.<UniqueKey<ObstacleForUserRecord>>asList(Keys.KEY_OBSTACLE_FOR_USER_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ObstacleForUser as(String alias) {
		return new ObstacleForUser(alias, this);
	}

	/**
	 * Rename this table
	 */
	public ObstacleForUser rename(String name) {
		return new ObstacleForUser(name, null);
	}
}
