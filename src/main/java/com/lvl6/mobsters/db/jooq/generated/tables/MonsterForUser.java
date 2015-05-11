/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.MonsterForUserRecord;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;
import org.jooq.types.UByte;
import org.jooq.types.UInteger;


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
public class MonsterForUser extends TableImpl<MonsterForUserRecord> {

	private static final long serialVersionUID = -302873013;

	/**
	 * The reference instance of <code>mobsters.monster_for_user</code>
	 */
	public static final MonsterForUser MONSTER_FOR_USER = new MonsterForUser();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<MonsterForUserRecord> getRecordType() {
		return MonsterForUserRecord.class;
	}

	/**
	 * The column <code>mobsters.monster_for_user.id</code>.
	 */
	public final TableField<MonsterForUserRecord, String> ID = createField("id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.monster_for_user.user_id</code>.
	 */
	public final TableField<MonsterForUserRecord, String> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.CHAR.length(36), this, "");

	/**
	 * The column <code>mobsters.monster_for_user.monster_id</code>.
	 */
	public final TableField<MonsterForUserRecord, UInteger> MONSTER_ID = createField("monster_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED, this, "");

	/**
	 * The column <code>mobsters.monster_for_user.current_experience</code>.
	 */
	public final TableField<MonsterForUserRecord, UInteger> CURRENT_EXPERIENCE = createField("current_experience", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.monster_for_user.current_level</code>.
	 */
	public final TableField<MonsterForUserRecord, UByte> CURRENT_LEVEL = createField("current_level", org.jooq.impl.SQLDataType.TINYINTUNSIGNED.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.monster_for_user.current_health</code>.
	 */
	public final TableField<MonsterForUserRecord, Integer> CURRENT_HEALTH = createField("current_health", org.jooq.impl.SQLDataType.INTEGER.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.monster_for_user.num_pieces</code>.
	 */
	public final TableField<MonsterForUserRecord, UByte> NUM_PIECES = createField("num_pieces", org.jooq.impl.SQLDataType.TINYINTUNSIGNED.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.monster_for_user.is_complete</code>.
	 */
	public final TableField<MonsterForUserRecord, Boolean> IS_COMPLETE = createField("is_complete", org.jooq.impl.SQLDataType.BIT.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.monster_for_user.combine_start_time</code>.
	 */
	public final TableField<MonsterForUserRecord, Timestamp> COMBINE_START_TIME = createField("combine_start_time", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

	/**
	 * The column <code>mobsters.monster_for_user.team_slot_num</code>.
	 */
	public final TableField<MonsterForUserRecord, UByte> TEAM_SLOT_NUM = createField("team_slot_num", org.jooq.impl.SQLDataType.TINYINTUNSIGNED.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.monster_for_user.source_of_pieces</code>.
	 */
	public final TableField<MonsterForUserRecord, String> SOURCE_OF_PIECES = createField("source_of_pieces", org.jooq.impl.SQLDataType.CLOB, this, "");

	/**
	 * The column <code>mobsters.monster_for_user.has_all_pieces</code>.
	 */
	public final TableField<MonsterForUserRecord, Boolean> HAS_ALL_PIECES = createField("has_all_pieces", org.jooq.impl.SQLDataType.BIT.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.monster_for_user.restricted</code>.
	 */
	public final TableField<MonsterForUserRecord, Boolean> RESTRICTED = createField("restricted", org.jooq.impl.SQLDataType.BIT.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.monster_for_user.offensive_skill_id</code>.
	 */
	public final TableField<MonsterForUserRecord, Integer> OFFENSIVE_SKILL_ID = createField("offensive_skill_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.monster_for_user.defensive_skill_id</code>.
	 */
	public final TableField<MonsterForUserRecord, Integer> DEFENSIVE_SKILL_ID = createField("defensive_skill_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * Create a <code>mobsters.monster_for_user</code> table reference
	 */
	public MonsterForUser() {
		this("monster_for_user", null);
	}

	/**
	 * Create an aliased <code>mobsters.monster_for_user</code> table reference
	 */
	public MonsterForUser(String alias) {
		this(alias, MONSTER_FOR_USER);
	}

	private MonsterForUser(String alias, Table<MonsterForUserRecord> aliased) {
		this(alias, aliased, null);
	}

	private MonsterForUser(String alias, Table<MonsterForUserRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<MonsterForUserRecord> getPrimaryKey() {
		return Keys.KEY_MONSTER_FOR_USER_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<MonsterForUserRecord>> getKeys() {
		return Arrays.<UniqueKey<MonsterForUserRecord>>asList(Keys.KEY_MONSTER_FOR_USER_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MonsterForUser as(String alias) {
		return new MonsterForUser(alias, this);
	}

	/**
	 * Rename this table
	 */
	public MonsterForUser rename(String name) {
		return new MonsterForUser(name, null);
	}
}