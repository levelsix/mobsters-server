/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.BoosterPackPurchaseHistoryRecord;

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
public class BoosterPackPurchaseHistory extends TableImpl<BoosterPackPurchaseHistoryRecord> {

	private static final long serialVersionUID = -555029756;

	/**
	 * The reference instance of <code>mobsters.booster_pack_purchase_history</code>
	 */
	public static final BoosterPackPurchaseHistory BOOSTER_PACK_PURCHASE_HISTORY = new BoosterPackPurchaseHistory();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<BoosterPackPurchaseHistoryRecord> getRecordType() {
		return BoosterPackPurchaseHistoryRecord.class;
	}

	/**
	 * The column <code>mobsters.booster_pack_purchase_history.user_id</code>.
	 */
	public final TableField<BoosterPackPurchaseHistoryRecord, String> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.booster_pack_purchase_history.booster_pack_id</code>.
	 */
	public final TableField<BoosterPackPurchaseHistoryRecord, Integer> BOOSTER_PACK_ID = createField("booster_pack_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.booster_pack_purchase_history.time_of_purchase</code>.
	 */
	public final TableField<BoosterPackPurchaseHistoryRecord, Timestamp> TIME_OF_PURCHASE = createField("time_of_purchase", org.jooq.impl.SQLDataType.TIMESTAMP.nullable(false).defaulted(true), this, "");

	/**
	 * The column <code>mobsters.booster_pack_purchase_history.booster_item_id</code>.
	 */
	public final TableField<BoosterPackPurchaseHistoryRecord, Integer> BOOSTER_ITEM_ID = createField("booster_item_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.booster_pack_purchase_history.monster_id</code>.
	 */
	public final TableField<BoosterPackPurchaseHistoryRecord, Integer> MONSTER_ID = createField("monster_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.booster_pack_purchase_history.num_pieces</code>. How many pieces of the monster the user gets if a monster is rewarded.
	 */
	public final TableField<BoosterPackPurchaseHistoryRecord, Byte> NUM_PIECES = createField("num_pieces", org.jooq.impl.SQLDataType.TINYINT, this, "How many pieces of the monster the user gets if a monster is rewarded.");

	/**
	 * The column <code>mobsters.booster_pack_purchase_history.is_complete</code>. specifies if user can skip combine wait time and use monster after purchasing
	 */
	public final TableField<BoosterPackPurchaseHistoryRecord, Boolean> IS_COMPLETE = createField("is_complete", org.jooq.impl.SQLDataType.BIT.defaulted(true), this, "specifies if user can skip combine wait time and use monster after purchasing");

	/**
	 * The column <code>mobsters.booster_pack_purchase_history.is_special</code>. client uses this to determine if it is displayed
	 */
	public final TableField<BoosterPackPurchaseHistoryRecord, Boolean> IS_SPECIAL = createField("is_special", org.jooq.impl.SQLDataType.BIT.defaulted(true), this, "client uses this to determine if it is displayed");

	/**
	 * The column <code>mobsters.booster_pack_purchase_history.gem_reward</code>.
	 */
	public final TableField<BoosterPackPurchaseHistoryRecord, Integer> GEM_REWARD = createField("gem_reward", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.booster_pack_purchase_history.cash_reward</code>. At the moment, unused
	 */
	public final TableField<BoosterPackPurchaseHistoryRecord, Integer> CASH_REWARD = createField("cash_reward", org.jooq.impl.SQLDataType.INTEGER, this, "At the moment, unused");

	/**
	 * The column <code>mobsters.booster_pack_purchase_history.chance_to_appear</code>.
	 */
	public final TableField<BoosterPackPurchaseHistoryRecord, Double> CHANCE_TO_APPEAR = createField("chance_to_appear", org.jooq.impl.SQLDataType.FLOAT, this, "");

	/**
	 * The column <code>mobsters.booster_pack_purchase_history.changed_monster_for_user_ids</code>.
	 */
	public final TableField<BoosterPackPurchaseHistoryRecord, String> CHANGED_MONSTER_FOR_USER_IDS = createField("changed_monster_for_user_ids", org.jooq.impl.SQLDataType.VARCHAR.length(511), this, "");

	/**
	 * Create a <code>mobsters.booster_pack_purchase_history</code> table reference
	 */
	public BoosterPackPurchaseHistory() {
		this("booster_pack_purchase_history", null);
	}

	/**
	 * Create an aliased <code>mobsters.booster_pack_purchase_history</code> table reference
	 */
	public BoosterPackPurchaseHistory(String alias) {
		this(alias, BOOSTER_PACK_PURCHASE_HISTORY);
	}

	private BoosterPackPurchaseHistory(String alias, Table<BoosterPackPurchaseHistoryRecord> aliased) {
		this(alias, aliased, null);
	}

	private BoosterPackPurchaseHistory(String alias, Table<BoosterPackPurchaseHistoryRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<BoosterPackPurchaseHistoryRecord> getPrimaryKey() {
		return Keys.KEY_BOOSTER_PACK_PURCHASE_HISTORY_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<BoosterPackPurchaseHistoryRecord>> getKeys() {
		return Arrays.<UniqueKey<BoosterPackPurchaseHistoryRecord>>asList(Keys.KEY_BOOSTER_PACK_PURCHASE_HISTORY_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoosterPackPurchaseHistory as(String alias) {
		return new BoosterPackPurchaseHistory(alias, this);
	}

	/**
	 * Rename this table
	 */
	public BoosterPackPurchaseHistory rename(String name) {
		return new BoosterPackPurchaseHistory(name, null);
	}
}
