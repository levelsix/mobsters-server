/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.MonsterHealingForUserRecord;

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
public class MonsterHealingForUser extends TableImpl<MonsterHealingForUserRecord> {

	private static final long serialVersionUID = -770272560;

	/**
	 * The reference instance of <code>mobsters.monster_healing_for_user</code>
	 */
	public static final MonsterHealingForUser MONSTER_HEALING_FOR_USER = new MonsterHealingForUser();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<MonsterHealingForUserRecord> getRecordType() {
		return MonsterHealingForUserRecord.class;
	}

	/**
	 * The column <code>mobsters.monster_healing_for_user.user_id</code>.
	 */
	public final TableField<MonsterHealingForUserRecord, String> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.monster_healing_for_user.monster_for_user_id</code>.
	 */
	public final TableField<MonsterHealingForUserRecord, String> MONSTER_FOR_USER_ID = createField("monster_for_user_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.monster_healing_for_user.queued_time</code>.
	 */
	public final TableField<MonsterHealingForUserRecord, Timestamp> QUEUED_TIME = createField("queued_time", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

	/**
	 * The column <code>mobsters.monster_healing_for_user.health_progress</code>. How much health this monster has recouped.
	 */
	public final TableField<MonsterHealingForUserRecord, Double> HEALTH_PROGRESS = createField("health_progress", org.jooq.impl.SQLDataType.FLOAT, this, "How much health this monster has recouped.");

	/**
	 * The column <code>mobsters.monster_healing_for_user.priority</code>.
	 */
	public final TableField<MonsterHealingForUserRecord, Integer> PRIORITY = createField("priority", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.monster_healing_for_user.elapsed_seconds</code>.
	 */
	public final TableField<MonsterHealingForUserRecord, Double> ELAPSED_SECONDS = createField("elapsed_seconds", org.jooq.impl.SQLDataType.FLOAT, this, "");

	/**
	 * The column <code>mobsters.monster_healing_for_user.user_struct_hospital_id</code>.
	 */
	public final TableField<MonsterHealingForUserRecord, String> USER_STRUCT_HOSPITAL_ID = createField("user_struct_hospital_id", org.jooq.impl.SQLDataType.CHAR.length(36), this, "");

	/**
	 * Create a <code>mobsters.monster_healing_for_user</code> table reference
	 */
	public MonsterHealingForUser() {
		this("monster_healing_for_user", null);
	}

	/**
	 * Create an aliased <code>mobsters.monster_healing_for_user</code> table reference
	 */
	public MonsterHealingForUser(String alias) {
		this(alias, MONSTER_HEALING_FOR_USER);
	}

	private MonsterHealingForUser(String alias, Table<MonsterHealingForUserRecord> aliased) {
		this(alias, aliased, null);
	}

	private MonsterHealingForUser(String alias, Table<MonsterHealingForUserRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<MonsterHealingForUserRecord> getPrimaryKey() {
		return Keys.KEY_MONSTER_HEALING_FOR_USER_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<MonsterHealingForUserRecord>> getKeys() {
		return Arrays.<UniqueKey<MonsterHealingForUserRecord>>asList(Keys.KEY_MONSTER_HEALING_FOR_USER_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonsterHealingForUser as(String alias) {
		return new MonsterHealingForUser(alias, this);
	}

	/**
	 * Rename this table
	 */
	public MonsterHealingForUser rename(String name) {
		return new MonsterHealingForUser(name, null);
	}
}