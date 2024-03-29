/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ResearchForUserRecord;

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
public class ResearchForUser extends TableImpl<ResearchForUserRecord> {

	private static final long serialVersionUID = -1247670990;

	/**
	 * The reference instance of <code>mobsters.research_for_user</code>
	 */
	public static final ResearchForUser RESEARCH_FOR_USER = new ResearchForUser();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<ResearchForUserRecord> getRecordType() {
		return ResearchForUserRecord.class;
	}

	/**
	 * The column <code>mobsters.research_for_user.id</code>.
	 */
	public final TableField<ResearchForUserRecord, String> ID = createField("id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.research_for_user.user_id</code>.
	 */
	public final TableField<ResearchForUserRecord, String> USER_ID = createField("user_id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.research_for_user.research_id</code>.
	 */
	public final TableField<ResearchForUserRecord, Integer> RESEARCH_ID = createField("research_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

	/**
	 * The column <code>mobsters.research_for_user.time_purchased</code>.
	 */
	public final TableField<ResearchForUserRecord, Timestamp> TIME_PURCHASED = createField("time_purchased", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

	/**
	 * The column <code>mobsters.research_for_user.is_complete</code>.
	 */
	public final TableField<ResearchForUserRecord, Boolean> IS_COMPLETE = createField("is_complete", org.jooq.impl.SQLDataType.BIT.defaulted(true), this, "");

	/**
	 * Create a <code>mobsters.research_for_user</code> table reference
	 */
	public ResearchForUser() {
		this("research_for_user", null);
	}

	/**
	 * Create an aliased <code>mobsters.research_for_user</code> table reference
	 */
	public ResearchForUser(String alias) {
		this(alias, RESEARCH_FOR_USER);
	}

	private ResearchForUser(String alias, Table<ResearchForUserRecord> aliased) {
		this(alias, aliased, null);
	}

	private ResearchForUser(String alias, Table<ResearchForUserRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<ResearchForUserRecord> getPrimaryKey() {
		return Keys.KEY_RESEARCH_FOR_USER_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<ResearchForUserRecord>> getKeys() {
		return Arrays.<UniqueKey<ResearchForUserRecord>>asList(Keys.KEY_RESEARCH_FOR_USER_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ResearchForUser as(String alias) {
		return new ResearchForUser(alias, this);
	}

	/**
	 * Rename this table
	 */
	public ResearchForUser rename(String name) {
		return new ResearchForUser(name, null);
	}
}
