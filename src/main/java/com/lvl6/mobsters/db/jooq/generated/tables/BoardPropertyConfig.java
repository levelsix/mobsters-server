/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.BoardPropertyConfigRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Identity;
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
public class BoardPropertyConfig extends TableImpl<BoardPropertyConfigRecord> {

	private static final long serialVersionUID = -753610608;

	/**
	 * The reference instance of <code>mobsters.board_property_config</code>
	 */
	public static final BoardPropertyConfig BOARD_PROPERTY_CONFIG = new BoardPropertyConfig();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<BoardPropertyConfigRecord> getRecordType() {
		return BoardPropertyConfigRecord.class;
	}

	/**
	 * The column <code>mobsters.board_property_config.id</code>.
	 */
	public final TableField<BoardPropertyConfigRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.board_property_config.board_id</code>.
	 */
	public final TableField<BoardPropertyConfigRecord, Integer> BOARD_ID = createField("board_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.board_property_config.name</code>.
	 */
	public final TableField<BoardPropertyConfigRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.VARCHAR.length(45), this, "");

	/**
	 * The column <code>mobsters.board_property_config.pos_x</code>.
	 */
	public final TableField<BoardPropertyConfigRecord, Byte> POS_X = createField("pos_x", org.jooq.impl.SQLDataType.TINYINT, this, "");

	/**
	 * The column <code>mobsters.board_property_config.pos_y</code>.
	 */
	public final TableField<BoardPropertyConfigRecord, Byte> POS_Y = createField("pos_y", org.jooq.impl.SQLDataType.TINYINT, this, "");

	/**
	 * The column <code>mobsters.board_property_config.element</code>.
	 */
	public final TableField<BoardPropertyConfigRecord, String> ELEMENT = createField("element", org.jooq.impl.SQLDataType.VARCHAR.length(45), this, "");

	/**
	 * The column <code>mobsters.board_property_config.value</code>.
	 */
	public final TableField<BoardPropertyConfigRecord, Integer> VALUE = createField("value", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.board_property_config.quantity</code>. Mostly used for jelly, as in break jelly twice kind of thing.
	 */
	public final TableField<BoardPropertyConfigRecord, Integer> QUANTITY = createField("quantity", org.jooq.impl.SQLDataType.INTEGER, this, "Mostly used for jelly, as in break jelly twice kind of thing.");

	/**
	 * Create a <code>mobsters.board_property_config</code> table reference
	 */
	public BoardPropertyConfig() {
		this("board_property_config", null);
	}

	/**
	 * Create an aliased <code>mobsters.board_property_config</code> table reference
	 */
	public BoardPropertyConfig(String alias) {
		this(alias, BOARD_PROPERTY_CONFIG);
	}

	private BoardPropertyConfig(String alias, Table<BoardPropertyConfigRecord> aliased) {
		this(alias, aliased, null);
	}

	private BoardPropertyConfig(String alias, Table<BoardPropertyConfigRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Identity<BoardPropertyConfigRecord, Integer> getIdentity() {
		return Keys.IDENTITY_BOARD_PROPERTY_CONFIG;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<BoardPropertyConfigRecord> getPrimaryKey() {
		return Keys.KEY_BOARD_PROPERTY_CONFIG_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<BoardPropertyConfigRecord>> getKeys() {
		return Arrays.<UniqueKey<BoardPropertyConfigRecord>>asList(Keys.KEY_BOARD_PROPERTY_CONFIG_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoardPropertyConfig as(String alias) {
		return new BoardPropertyConfig(alias, this);
	}

	/**
	 * Rename this table
	 */
	public BoardPropertyConfig rename(String name) {
		return new BoardPropertyConfig(name, null);
	}
}
