/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.TranslationSettingsForUser;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ITranslationSettingsForUser;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
@Table(name = "translation_settings_for_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"receiver_user_id", "sender_user_id"})
})
public class TranslationSettingsForUserRecord extends UpdatableRecordImpl<TranslationSettingsForUserRecord> implements Record6<String, String, String, String, String, Boolean>, ITranslationSettingsForUser {

	private static final long serialVersionUID = -1386428870;

	/**
	 * Setter for <code>mobsters.translation_settings_for_user.id</code>.
	 */
	@Override
	public TranslationSettingsForUserRecord setId(String value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.translation_settings_for_user.id</code>.
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
	 * Setter for <code>mobsters.translation_settings_for_user.receiver_user_id</code>. this is the person who gets the private chats/global messages, his language setting is what’s used
	 */
	@Override
	public TranslationSettingsForUserRecord setReceiverUserId(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.translation_settings_for_user.receiver_user_id</code>. this is the person who gets the private chats/global messages, his language setting is what’s used
	 */
	@Column(name = "receiver_user_id", length = 45)
	@Size(max = 45)
	@Override
	public String getReceiverUserId() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.translation_settings_for_user.sender_user_id</code>. null if it’s global chat
	 */
	@Override
	public TranslationSettingsForUserRecord setSenderUserId(String value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.translation_settings_for_user.sender_user_id</code>. null if it’s global chat
	 */
	@Column(name = "sender_user_id", length = 45)
	@Size(max = 45)
	@Override
	public String getSenderUserId() {
		return (String) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.translation_settings_for_user.language</code>. default is the global language setting
	 */
	@Override
	public TranslationSettingsForUserRecord setLanguage(String value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.translation_settings_for_user.language</code>. default is the global language setting
	 */
	@Column(name = "language", length = 45)
	@Size(max = 45)
	@Override
	public String getLanguage() {
		return (String) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.translation_settings_for_user.chat_type</code>. currently can be global or private
	 */
	@Override
	public TranslationSettingsForUserRecord setChatType(String value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.translation_settings_for_user.chat_type</code>. currently can be global or private
	 */
	@Column(name = "chat_type", length = 45)
	@Size(max = 45)
	@Override
	public String getChatType() {
		return (String) getValue(4);
	}

	/**
	 * Setter for <code>mobsters.translation_settings_for_user.translations_on</code>.
	 */
	@Override
	public TranslationSettingsForUserRecord setTranslationsOn(Boolean value) {
		setValue(5, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.translation_settings_for_user.translations_on</code>.
	 */
	@Column(name = "translations_on", precision = 1)
	@Override
	public Boolean getTranslationsOn() {
		return (Boolean) getValue(5);
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
	public Row6<String, String, String, String, String, Boolean> fieldsRow() {
		return (Row6) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row6<String, String, String, String, String, Boolean> valuesRow() {
		return (Row6) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field1() {
		return TranslationSettingsForUser.TRANSLATION_SETTINGS_FOR_USER.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return TranslationSettingsForUser.TRANSLATION_SETTINGS_FOR_USER.RECEIVER_USER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field3() {
		return TranslationSettingsForUser.TRANSLATION_SETTINGS_FOR_USER.SENDER_USER_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field4() {
		return TranslationSettingsForUser.TRANSLATION_SETTINGS_FOR_USER.LANGUAGE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field5() {
		return TranslationSettingsForUser.TRANSLATION_SETTINGS_FOR_USER.CHAT_TYPE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> field6() {
		return TranslationSettingsForUser.TRANSLATION_SETTINGS_FOR_USER.TRANSLATIONS_ON;
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
		return getReceiverUserId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value3() {
		return getSenderUserId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value4() {
		return getLanguage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value5() {
		return getChatType();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean value6() {
		return getTranslationsOn();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TranslationSettingsForUserRecord value1(String value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TranslationSettingsForUserRecord value2(String value) {
		setReceiverUserId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TranslationSettingsForUserRecord value3(String value) {
		setSenderUserId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TranslationSettingsForUserRecord value4(String value) {
		setLanguage(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TranslationSettingsForUserRecord value5(String value) {
		setChatType(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TranslationSettingsForUserRecord value6(Boolean value) {
		setTranslationsOn(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TranslationSettingsForUserRecord values(String value1, String value2, String value3, String value4, String value5, Boolean value6) {
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
	public void from(ITranslationSettingsForUser from) {
		setId(from.getId());
		setReceiverUserId(from.getReceiverUserId());
		setSenderUserId(from.getSenderUserId());
		setLanguage(from.getLanguage());
		setChatType(from.getChatType());
		setTranslationsOn(from.getTranslationsOn());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends ITranslationSettingsForUser> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached TranslationSettingsForUserRecord
	 */
	public TranslationSettingsForUserRecord() {
		super(TranslationSettingsForUser.TRANSLATION_SETTINGS_FOR_USER);
	}

	/**
	 * Create a detached, initialised TranslationSettingsForUserRecord
	 */
	public TranslationSettingsForUserRecord(String id, String receiverUserId, String senderUserId, String language, String chatType, Boolean translationsOn) {
		super(TranslationSettingsForUser.TRANSLATION_SETTINGS_FOR_USER);

		setValue(0, id);
		setValue(1, receiverUserId);
		setValue(2, senderUserId);
		setValue(3, language);
		setValue(4, chatType);
		setValue(5, translationsOn);
	}
}
