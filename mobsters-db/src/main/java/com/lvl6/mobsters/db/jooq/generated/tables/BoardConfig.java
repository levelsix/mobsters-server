/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.BoardConfigRecord;

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
public class BoardConfig extends TableImpl<BoardConfigRecord> {

	private static final long serialVersionUID = 1822590430;

	/**
	 * The reference instance of <code>mobsters.board_config</code>
	 */
	public static final BoardConfig BOARD_CONFIG = new BoardConfig();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<BoardConfigRecord> getRecordType() {
		return BoardConfigRecord.class;
	}

	/**
	 * The column <code>mobsters.board_config.id</code>.
	 */
	public final TableField<BoardConfigRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

	/**
	 * The column <code>mobsters.board_config.width</code>.
	 */
	public final TableField<BoardConfigRecord, Byte> WIDTH = createField("width", org.jooq.impl.SQLDataType.TINYINT, this, "");

	/**
	 * The column <code>mobsters.board_config.height</code>.
	 */
	public final TableField<BoardConfigRecord, Byte> HEIGHT = createField("height", org.jooq.impl.SQLDataType.TINYINT, this, "");

	/**
	 * The column <code>mobsters.board_config.orb_elements</code>. bit representation of what elements are active. Example 100001, only two elements are active
	 */
	public final TableField<BoardConfigRecord, String> ORB_ELEMENTS = createField("orb_elements", org.jooq.impl.SQLDataType.CLOB, this, "bit representation of what elements are active. Example 100001, only two elements are active");

	/**
	 * Create a <code>mobsters.board_config</code> table reference
	 */
	public BoardConfig() {
		this("board_config", null);
	}

	/**
	 * Create an aliased <code>mobsters.board_config</code> table reference
	 */
	public BoardConfig(String alias) {
		this(alias, BOARD_CONFIG);
	}

	private BoardConfig(String alias, Table<BoardConfigRecord> aliased) {
		this(alias, aliased, null);
	}

	private BoardConfig(String alias, Table<BoardConfigRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<BoardConfigRecord> getPrimaryKey() {
		return Keys.KEY_BOARD_CONFIG_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<BoardConfigRecord>> getKeys() {
		return Arrays.<UniqueKey<BoardConfigRecord>>asList(Keys.KEY_BOARD_CONFIG_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public BoardConfig as(String alias) {
		return new BoardConfig(alias, this);
	}

	/**
	 * Rename this table
	 */
	public BoardConfig rename(String name) {
		return new BoardConfig(name, null);
	}
}
