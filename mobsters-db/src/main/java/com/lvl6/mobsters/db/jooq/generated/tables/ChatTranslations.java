/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables;


import com.lvl6.mobsters.db.jooq.generated.Keys;
import com.lvl6.mobsters.db.jooq.generated.Mobsters;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ChatTranslationsRecord;

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
public class ChatTranslations extends TableImpl<ChatTranslationsRecord> {

	private static final long serialVersionUID = -276034870;

	/**
	 * The reference instance of <code>mobsters.chat_translations</code>
	 */
	public static final ChatTranslations CHAT_TRANSLATIONS = new ChatTranslations();

	/**
	 * The class holding records for this type
	 */
	@Override
	public Class<ChatTranslationsRecord> getRecordType() {
		return ChatTranslationsRecord.class;
	}

	/**
	 * The column <code>mobsters.chat_translations.id</code>.
	 */
	public final TableField<ChatTranslationsRecord, String> ID = createField("id", org.jooq.impl.SQLDataType.CHAR.length(36).nullable(false), this, "");

	/**
	 * The column <code>mobsters.chat_translations.chat_type</code>. for now it’s just private chat
	 */
	public final TableField<ChatTranslationsRecord, String> CHAT_TYPE = createField("chat_type", org.jooq.impl.SQLDataType.CHAR.length(36), this, "for now it’s just private chat");

	/**
	 * The column <code>mobsters.chat_translations.chat_id</code>.
	 */
	public final TableField<ChatTranslationsRecord, String> CHAT_ID = createField("chat_id", org.jooq.impl.SQLDataType.CHAR.length(36), this, "");

	/**
	 * The column <code>mobsters.chat_translations.language</code>.
	 */
	public final TableField<ChatTranslationsRecord, String> LANGUAGE = createField("language", org.jooq.impl.SQLDataType.CHAR.length(36), this, "");

	/**
	 * The column <code>mobsters.chat_translations.text</code>.
	 */
	public final TableField<ChatTranslationsRecord, String> TEXT = createField("text", org.jooq.impl.SQLDataType.CLOB, this, "");

	/**
	 * Create a <code>mobsters.chat_translations</code> table reference
	 */
	public ChatTranslations() {
		this("chat_translations", null);
	}

	/**
	 * Create an aliased <code>mobsters.chat_translations</code> table reference
	 */
	public ChatTranslations(String alias) {
		this(alias, CHAT_TRANSLATIONS);
	}

	private ChatTranslations(String alias, Table<ChatTranslationsRecord> aliased) {
		this(alias, aliased, null);
	}

	private ChatTranslations(String alias, Table<ChatTranslationsRecord> aliased, Field<?>[] parameters) {
		super(alias, Mobsters.MOBSTERS, aliased, parameters, "");
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public UniqueKey<ChatTranslationsRecord> getPrimaryKey() {
		return Keys.KEY_CHAT_TRANSLATIONS_PRIMARY;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public List<UniqueKey<ChatTranslationsRecord>> getKeys() {
		return Arrays.<UniqueKey<ChatTranslationsRecord>>asList(Keys.KEY_CHAT_TRANSLATIONS_PRIMARY);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ChatTranslations as(String alias) {
		return new ChatTranslations(alias, this);
	}

	/**
	 * Rename this table
	 */
	public ChatTranslations rename(String name) {
		return new ChatTranslations(name, null);
	}
}