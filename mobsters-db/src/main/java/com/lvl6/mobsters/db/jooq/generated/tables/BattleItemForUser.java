/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.BattleItemForUserRecord;

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
public class BattleItemForUser extends TableImpl<BattleItemForUserRecord> {

	private static final long serialVersionUID = 2034206212;

	/**
	 * The reference instance of <code>mobsters.battle_item_for_user</code>
	 */
	public static final BattleItemForUser BATTLE_ITEM_FOR_USER = new BattleItemForUser();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<BattleItemForUserRecord> getRecordType() {
		return BattleItemForUserRecord.class;
	}

	/**
	 * The column <code>mobsters.battle_item_for_user.id</code>.
	 */
	public final TableField<BattleItemForUserRecord, String> ID = createField("id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.battle_item_for_user.user_id</code>.
	 */
	public final TableField<BattleItemForUserRecord, String> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.battle_item_for_user.battle_item_id</code>.
	 */
	public final TableField<BattleItemForUserRecord, Integer> BATTLE_ITEM_ID = createField("battle_item_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.battle_item_for_user.quantity</code>.
	 */
	public final TableField<BattleItemForUserRecord, Integer> QUANTITY = createField("quantity", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * Create a <code>mobsters.battle_item_for_user</code> table reference
	 */
	public BattleItemForUser() {
		this("battle_item_for_user", null);
	}

	/**
	 * Create an aliased <code>mobsters.battle_item_for_user</code> table reference
	 */
	public BattleItemForUser(String alias) {
		this(alias, BATTLE_ITEM_FOR_USER);
	}

	private BattleItemForUser(String alias, Table<BattleItemForUserRecord> aliased) {
		this(alias, aliased, null);
	}

	private BattleItemForUser(String alias, Table<BattleItemForUserRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<BattleItemForUserRecord> getPrimaryKey() {
		return Keys.KEY_BATTLE_ITEM_FOR_USER_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<BattleItemForUserRecord>> getKeys() {
		return Arrays.<UniqueKey<BattleItemForUserRecord>>asList(Keys.KEY_BATTLE_ITEM_FOR_USER_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BattleItemForUser as(String alias) {
		return new BattleItemForUser(alias, this);
	}

	/**
	 * Rename this table
	 */
	public BattleItemForUser rename(String name) {
		return new BattleItemForUser(name, null);
	}
}
