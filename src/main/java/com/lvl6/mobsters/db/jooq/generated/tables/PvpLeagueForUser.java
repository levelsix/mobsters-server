/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.PvpLeagueForUserRecord;

import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.impl.TableImpl;
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
public class PvpLeagueForUser extends TableImpl<PvpLeagueForUserRecord> {

	private static final long serialVersionUID = 2116825226;

	/**
	 * The reference instance of <code>mobsters.pvp_league_for_user</code>
	 */
	public static final PvpLeagueForUser PVP_LEAGUE_FOR_USER = new PvpLeagueForUser();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<PvpLeagueForUserRecord> getRecordType() {
		return PvpLeagueForUserRecord.class;
	}

	/**
	 * The column <code>mobsters.pvp_league_for_user.user_id</code>.
	 */
	public final TableField<PvpLeagueForUserRecord, String> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.pvp_league_for_user.league_id</code>.
	 */
	public final TableField<PvpLeagueForUserRecord, UInteger> LEAGUE_ID = createField("league_id", org.jooq.impl.SQLDataType.INTEGERUNSIGNED, this, "");

	/**
	 * The column <code>mobsters.pvp_league_for_user.rank</code>.
	 */
	public final TableField<PvpLeagueForUserRecord, UInteger> RANK = createField("rank", org.jooq.impl.SQLDataType.INTEGERUNSIGNED, this, "");

	/**
	 * The column <code>mobsters.pvp_league_for_user.elo</code>. if lower than lowest league in pvp league table, then user is in lowest league. if higher than highest league, then user is in highest league
	 */
	public final TableField<PvpLeagueForUserRecord, Integer> ELO = createField("elo", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaulted(true), this, "if lower than lowest league in pvp league table, then user is in lowest league. if higher than highest league, then user is in highest league");

	/**
	 * The column <code>mobsters.pvp_league_for_user.shield_end_time</code>.
	 */
	public final TableField<PvpLeagueForUserRecord, Timestamp> SHIELD_END_TIME = createField("shield_end_time", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>mobsters.pvp_league_for_user.battle_end_time</code>. does not really matter if null
	 */
	public final TableField<PvpLeagueForUserRecord, Timestamp> BATTLE_END_TIME = createField("battle_end_time", org.jooq.impl.SQLDataType.TIMESTAMP, this, "does not really matter if null");

	/**
	 * The column <code>mobsters.pvp_league_for_user.attacks_won</code>.
	 */
	public final TableField<PvpLeagueForUserRecord, Integer> ATTACKS_WON = createField("attacks_won", org.jooq.impl.SQLDataType.INTEGER.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.pvp_league_for_user.defenses_won</code>.
	 */
	public final TableField<PvpLeagueForUserRecord, Integer> DEFENSES_WON = createField("defenses_won", org.jooq.impl.SQLDataType.INTEGER.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.pvp_league_for_user.attacks_lost</code>.
	 */
	public final TableField<PvpLeagueForUserRecord, Integer> ATTACKS_LOST = createField("attacks_lost", org.jooq.impl.SQLDataType.INTEGER.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.pvp_league_for_user.defenses_lost</code>.
	 */
	public final TableField<PvpLeagueForUserRecord, Integer> DEFENSES_LOST = createField("defenses_lost", org.jooq.impl.SQLDataType.INTEGER.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.pvp_league_for_user.monster_dmg_multiplier</code>.
	 */
	public final TableField<PvpLeagueForUserRecord, Double> MONSTER_DMG_MULTIPLIER = createField("monster_dmg_multiplier", org.jooq.impl.SQLDataType.FLOAT.defaulted(true), this, "");

	/**
	 * Create a <code>mobsters.pvp_league_for_user</code> table reference
	 */
	public PvpLeagueForUser() {
		this("pvp_league_for_user", null);
	}

	/**
	 * Create an aliased <code>mobsters.pvp_league_for_user</code> table reference
	 */
	public PvpLeagueForUser(String alias) {
		this(alias, PVP_LEAGUE_FOR_USER);
	}

	private PvpLeagueForUser(String alias, Table<PvpLeagueForUserRecord> aliased) {
		this(alias, aliased, null);
	}

	private PvpLeagueForUser(String alias, Table<PvpLeagueForUserRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<PvpLeagueForUserRecord> getPrimaryKey() {
		return Keys.KEY_PVP_LEAGUE_FOR_USER_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<PvpLeagueForUserRecord>> getKeys() {
		return Arrays.<UniqueKey<PvpLeagueForUserRecord>>asList(Keys.KEY_PVP_LEAGUE_FOR_USER_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public PvpLeagueForUser as(String alias) {
		return new PvpLeagueForUser(alias, this);
	}

	/**
	 * Rename this table
	 */
	public PvpLeagueForUser rename(String name) {
		return new PvpLeagueForUser(name, null);
	}
}
