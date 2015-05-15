/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ItemForUserRecord;

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
public class ItemForUser extends TableImpl<ItemForUserRecord> {

	private static final long serialVersionUID = -1359145960;

	/**
	 * The reference instance of <code>mobsters.item_for_user</code>
	 */
	public static final ItemForUser ITEM_FOR_USER = new ItemForUser();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<ItemForUserRecord> getRecordType() {
		return ItemForUserRecord.class;
	}

	/**
	 * The column <code>mobsters.item_for_user.user_id</code>.
	 */
	public final TableField<ItemForUserRecord, String> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.item_for_user.item_id</code>.
	 */
	public final TableField<ItemForUserRecord, Integer> ITEM_ID = createField("item_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.item_for_user.quantity</code>.
	 */
	public final TableField<ItemForUserRecord, UInteger> QUANTITY = createField("quantity", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.defaulted(true), this, "");

	/**
	 * Create a <code>mobsters.item_for_user</code> table reference
	 */
	public ItemForUser() {
		this("item_for_user", null);
	}

	/**
	 * Create an aliased <code>mobsters.item_for_user</code> table reference
	 */
	public ItemForUser(String alias) {
		this(alias, ITEM_FOR_USER);
	}

	private ItemForUser(String alias, Table<ItemForUserRecord> aliased) {
		this(alias, aliased, null);
	}

	private ItemForUser(String alias, Table<ItemForUserRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<ItemForUserRecord> getPrimaryKey() {
		return Keys.KEY_ITEM_FOR_USER_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<ItemForUserRecord>> getKeys() {
		return Arrays.<UniqueKey<ItemForUserRecord>>asList(Keys.KEY_ITEM_FOR_USER_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemForUser as(String alias) {
		return new ItemForUser(alias, this);
	}

	/**
	 * Rename this table
	 */
	public ItemForUser rename(String name) {
		return new ItemForUser(name, null);
	}
}
