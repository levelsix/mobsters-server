/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.BattleReplayForUserRecord;

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
public class BattleReplayForUser extends TableImpl<BattleReplayForUserRecord> {

	private static final long serialVersionUID = -2120673433;

	/**
	 * The reference instance of <code>mobsters.battle_replay_for_user</code>
	 */
	public static final BattleReplayForUser BATTLE_REPLAY_FOR_USER = new BattleReplayForUser();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<BattleReplayForUserRecord> getRecordType() {
		return BattleReplayForUserRecord.class;
	}

	/**
	 * The column <code>mobsters.battle_replay_for_user.id</code>.
	 */
	public final TableField<BattleReplayForUserRecord, String> ID = createField("id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.battle_replay_for_user.creator_id</code>.
	 */
	public final TableField<BattleReplayForUserRecord, String> CREATOR_ID = createField("creator_id", org.jooq.impl.SQLDataType.CHAR.length(36), this, "");

	/**
	 * The column <code>mobsters.battle_replay_for_user.replay</code>.
	 */
	public final TableField<BattleReplayForUserRecord, byte[]> REPLAY = createField("replay", org.jooq.impl.SQLDataType.BLOB, this, "");

	/**
	 * The column <code>mobsters.battle_replay_for_user.create_time</code>.
	 */
	public final TableField<BattleReplayForUserRecord, Timestamp> CREATE_TIME = createField("create_time", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

	/**
	 * Create a <code>mobsters.battle_replay_for_user</code> table reference
	 */
	public BattleReplayForUser() {
		this("battle_replay_for_user", null);
	}

	/**
	 * Create an aliased <code>mobsters.battle_replay_for_user</code> table reference
	 */
	public BattleReplayForUser(String alias) {
		this(alias, BATTLE_REPLAY_FOR_USER);
	}

	private BattleReplayForUser(String alias, Table<BattleReplayForUserRecord> aliased) {
		this(alias, aliased, null);
	}

	private BattleReplayForUser(String alias, Table<BattleReplayForUserRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<BattleReplayForUserRecord> getPrimaryKey() {
		return Keys.KEY_BATTLE_REPLAY_FOR_USER_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<BattleReplayForUserRecord>> getKeys() {
		return Arrays.<UniqueKey<BattleReplayForUserRecord>>asList(Keys.KEY_BATTLE_REPLAY_FOR_USER_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BattleReplayForUser as(String alias) {
		return new BattleReplayForUser(alias, this);
	}

	/**
	 * Rename this table
	 */
	public BattleReplayForUser rename(String name) {
		return new BattleReplayForUser(name, null);
	}
}
