/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.UserPrivateChatPost;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IUserPrivateChatPost;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row;
import org.jooq.Row6;
import org.jooq.impl.UpdatableRecordImpl;


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
@Entity
@Table(name = "user_private_chat_post", schema = "mobsters")
public class UserPrivateChatPostRecord extends UpdatableRecordImpl<UserPrivateChatPostRecord> implements Record6<String, String, String, Timestamp, String, String>, IUserPrivateChatPost {

	private static final long serialVersionUID = 445883208;

	/**
	 * Setter for <code>mobsters.user_private_chat_post.id</code>.
	 */
	@Override
	public UserPrivateChatPostRecord setId(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user_private_chat_post.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getId() {
		return (String) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.user_private_chat_post.poster_id</code>.
	 */
	@Override
	public UserPrivateChatPostRecord setPosterId(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user_private_chat_post.poster_id</code>.
	 */
	@Column(name = "poster_id", length = 36)
	@Size(max = 36)
	@Override
	public String getPosterId() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.user_private_chat_post.recipient_id</code>.
	 */
	@Override
	public UserPrivateChatPostRecord setRecipientId(String value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user_private_chat_post.recipient_id</code>.
	 */
	@Column(name = "recipient_id", length = 36)
	@Size(max = 36)
	@Override
	public String getRecipientId() {
		return (String) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.user_private_chat_post.time_of_post</code>.
	 */
	@Override
	public UserPrivateChatPostRecord setTimeOfPost(Timestamp value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user_private_chat_post.time_of_post</code>.
	 */
	@Column(name = "time_of_post", nullable = false)
	@NotNull
	@Override
	public Timestamp getTimeOfPost() {
		return (Timestamp) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.user_private_chat_post.content</code>.
	 */
	@Override
	public UserPrivateChatPostRecord setContent(String value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user_private_chat_post.content</code>.
	 */
	@Column(name = "content", length = 65535)
	@Size(max = 65535)
	@Override
	public String getContent() {
		return (String) getValue(4);
	}

	/**
	 * Setter for <code>mobsters.user_private_chat_post.content_language</code>.
	 */
	@Override
	public UserPrivateChatPostRecord setContentLanguage(String value) {
		setValue(5, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.user_private_chat_post.content_language</code>.
	 */
	@Column(name = "content_language", length = 45)
	@Size(max = 45)
	@Override
	public String getContentLanguage() {
		return (String) getValue(5);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record1<String> key() {
		return (Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record6 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row6<String, String, String, Timestamp, String, String> fieldsRow() {
		return (Row6) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row6<String, String, String, Timestamp, String, String> valuesRow() {
		return (Row6) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field1() {
		return UserPrivateChatPost.USER_PRIVATE_CHAT_POST.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return UserPrivateChatPost.USER_PRIVATE_CHAT_POST.POSTER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field3() {
		return UserPrivateChatPost.USER_PRIVATE_CHAT_POST.RECIPIENT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Timestamp> field4() {
		return UserPrivateChatPost.USER_PRIVATE_CHAT_POST.TIME_OF_POST;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field5() {
		return UserPrivateChatPost.USER_PRIVATE_CHAT_POST.CONTENT;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field6() {
		return UserPrivateChatPost.USER_PRIVATE_CHAT_POST.CONTENT_LANGUAGE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value1() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value2() {
		return getPosterId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value3() {
		return getRecipientId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Timestamp value4() {
		return getTimeOfPost();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value5() {
		return getContent();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value6() {
		return getContentLanguage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserPrivateChatPostRecord value1(String value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserPrivateChatPostRecord value2(String value) {
		setPosterId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserPrivateChatPostRecord value3(String value) {
		setRecipientId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserPrivateChatPostRecord value4(Timestamp value) {
		setTimeOfPost(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserPrivateChatPostRecord value5(String value) {
		setContent(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserPrivateChatPostRecord value6(String value) {
		setContentLanguage(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UserPrivateChatPostRecord values(String value1, String value2, String value3, Timestamp value4, String value5, String value6) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IUserPrivateChatPost from) {
		setId(from.getId());
		setPosterId(from.getPosterId());
		setRecipientId(from.getRecipientId());
		setTimeOfPost(from.getTimeOfPost());
		setContent(from.getContent());
		setContentLanguage(from.getContentLanguage());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IUserPrivateChatPost> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached UserPrivateChatPostRecord
	 */
	public UserPrivateChatPostRecord() {
		super(UserPrivateChatPost.USER_PRIVATE_CHAT_POST);
	}

	/**
	 * Create a detached, initialised UserPrivateChatPostRecord
	 */
	public UserPrivateChatPostRecord(String id, String posterId, String recipientId, Timestamp timeOfPost, String content, String contentLanguage) {
		super(UserPrivateChatPost.USER_PRIVATE_CHAT_POST);

		setValue(0, id);
		setValue(1, posterId);
		setValue(2, recipientId);
		setValue(3, timeOfPost);
		setValue(4, content);
		setValue(5, contentLanguage);
	}
}