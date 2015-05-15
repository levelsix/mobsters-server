/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ItemSecretGiftForUserRecord;

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
public class ItemSecretGiftForUser extends TableImpl<ItemSecretGiftForUserRecord> {

	private static final long serialVersionUID = 1389041511;

	/**
	 * The reference instance of <code>mobsters.item_secret_gift_for_user</code>
	 */
	public static final ItemSecretGiftForUser ITEM_SECRET_GIFT_FOR_USER = new ItemSecretGiftForUser();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<ItemSecretGiftForUserRecord> getRecordType() {
		return ItemSecretGiftForUserRecord.class;
	}

	/**
	 * The column <code>mobsters.item_secret_gift_for_user.id</code>.
	 */
	public final TableField<ItemSecretGiftForUserRecord, String> ID = createField("id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.item_secret_gift_for_user.user_id</code>.
	 */
	public final TableField<ItemSecretGiftForUserRecord, String> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.item_secret_gift_for_user.item_id</code>.
	 */
	public final TableField<ItemSecretGiftForUserRecord, String> ITEM_ID = createField("item_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.item_secret_gift_for_user.secs_until_collection</code>.
	 */
	public final TableField<ItemSecretGiftForUserRecord, Integer> SECS_UNTIL_COLLECTION = createField("secs_until_collection", org.jooq.impl.SQLDataType.INTEGER.defaulted(true), this, "");

	/**
	 * The column <code>mobsters.item_secret_gift_for_user.create_time</code>.
	 */
	public final TableField<ItemSecretGiftForUserRecord, Timestamp> CREATE_TIME = createField("create_time", org.jooq.impl.SQLDataType.TIMESTAMP.defaulted(true), this, "");

	/**
	 * Create a <code>mobsters.item_secret_gift_for_user</code> table reference
	 */
	public ItemSecretGiftForUser() {
		this("item_secret_gift_for_user", null);
	}

	/**
	 * Create an aliased <code>mobsters.item_secret_gift_for_user</code> table reference
	 */
	public ItemSecretGiftForUser(String alias) {
		this(alias, ITEM_SECRET_GIFT_FOR_USER);
	}

	private ItemSecretGiftForUser(String alias, Table<ItemSecretGiftForUserRecord> aliased) {
		this(alias, aliased, null);
	}

	private ItemSecretGiftForUser(String alias, Table<ItemSecretGiftForUserRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<ItemSecretGiftForUserRecord> getPrimaryKey() {
		return Keys.KEY_ITEM_SECRET_GIFT_FOR_USER_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<ItemSecretGiftForUserRecord>> getKeys() {
		return Arrays.<UniqueKey<ItemSecretGiftForUserRecord>>asList(Keys.KEY_ITEM_SECRET_GIFT_FOR_USER_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ItemSecretGiftForUser as(String alias) {
		return new ItemSecretGiftForUser(alias, this);
	}

	/**
	 * Rename this table
	 */
	public ItemSecretGiftForUser rename(String name) {
		return new ItemSecretGiftForUser(name, null);
	}
}
