/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.ClanChatPostsCountLastHour;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanChatPostsCountLastHour;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row;
import org.jooq.Row2;
import org.jooq.impl.TableRecordImpl;


/**
 * VIEW
 */
@Generated(
	value = {
		"http://www.jooq.org",
		"jOOQ version:3.6.1"
	},
	comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
@Entity
@Table(name = "clan_chat_posts_count_last_hour", schema = "mobsters")
public class ClanChatPostsCountLastHourRecord extends TableRecordImpl<ClanChatPostsCountLastHourRecord> implements Record2<String, Long>, IClanChatPostsCountLastHour {

	private static final long serialVersionUID = 2134372164;

	/**
	 * Setter for <code>mobsters.clan_chat_posts_count_last_hour.clan_id</code>.
	 */
	@Override
	public ClanChatPostsCountLastHourRecord setClanId(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_chat_posts_count_last_hour.clan_id</code>.
	 */
	@Column(name = "clan_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getClanId() {
		return (String) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.clan_chat_posts_count_last_hour.posts</code>.
	 */
	@Override
	public ClanChatPostsCountLastHourRecord setPosts(Long value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_chat_posts_count_last_hour.posts</code>.
	 */
	@Column(name = "posts", nullable = false, precision = 19)
	@NotNull
	@Override
	public Long getPosts() {
		return (Long) getValue(1);
	}

	// -------------------------------------------------------------------------
	// Record2 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row2<String, Long> fieldsRow() {
		return (Row2) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row2<String, Long> valuesRow() {
		return (Row2) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field1() {
		return ClanChatPostsCountLastHour.CLAN_CHAT_POSTS_COUNT_LAST_HOUR.CLAN_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Long> field2() {
		return ClanChatPostsCountLastHour.CLAN_CHAT_POSTS_COUNT_LAST_HOUR.POSTS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value1() {
		return getClanId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Long value2() {
		return getPosts();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanChatPostsCountLastHourRecord value1(String value) {
		setClanId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanChatPostsCountLastHourRecord value2(Long value) {
		setPosts(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanChatPostsCountLastHourRecord values(String value1, Long value2) {
		value1(value1);
		value2(value2);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IClanChatPostsCountLastHour from) {
		setClanId(from.getClanId());
		setPosts(from.getPosts());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IClanChatPostsCountLastHour> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached ClanChatPostsCountLastHourRecord
	 */
	public ClanChatPostsCountLastHourRecord() {
		super(ClanChatPostsCountLastHour.CLAN_CHAT_POSTS_COUNT_LAST_HOUR);
	}

	/**
	 * Create a detached, initialised ClanChatPostsCountLastHourRecord
	 */
	public ClanChatPostsCountLastHourRecord(String clanId, Long posts) {
		super(ClanChatPostsCountLastHour.CLAN_CHAT_POSTS_COUNT_LAST_HOUR);

		setValue(0, clanId);
		setValue(1, posts);
	}
}