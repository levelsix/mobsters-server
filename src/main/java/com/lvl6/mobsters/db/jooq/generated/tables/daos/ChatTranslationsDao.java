/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.ChatTranslations;
import com.lvl6.mobsters.db.jooq.generated.tables.records.ChatTranslationsRecord;

import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


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
public class ChatTranslationsDao extends DAOImpl<ChatTranslationsRecord, com.lvl6.mobsters.db.jooq.generated.tables.pojos.ChatTranslations, String> {

	/**
	 * Create a new ChatTranslationsDao without any configuration
	 */
	public ChatTranslationsDao() {
		super(ChatTranslations.CHAT_TRANSLATIONS, com.lvl6.mobsters.db.jooq.generated.tables.pojos.ChatTranslations.class);
	}

	/**
	 * Create a new ChatTranslationsDao with an attached configuration
	 */
	public ChatTranslationsDao(Configuration configuration) {
		super(ChatTranslations.CHAT_TRANSLATIONS, com.lvl6.mobsters.db.jooq.generated.tables.pojos.ChatTranslations.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected String getId(com.lvl6.mobsters.db.jooq.generated.tables.pojos.ChatTranslations object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.ChatTranslations> fetchById(String... values) {
		return fetch(ChatTranslations.CHAT_TRANSLATIONS.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public com.lvl6.mobsters.db.jooq.generated.tables.pojos.ChatTranslations fetchOneById(String value) {
		return fetchOne(ChatTranslations.CHAT_TRANSLATIONS.ID, value);
	}

	/**
	 * Fetch records that have <code>chat_type IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.ChatTranslations> fetchByChatType(String... values) {
		return fetch(ChatTranslations.CHAT_TRANSLATIONS.CHAT_TYPE, values);
	}

	/**
	 * Fetch records that have <code>chat_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.ChatTranslations> fetchByChatId(String... values) {
		return fetch(ChatTranslations.CHAT_TRANSLATIONS.CHAT_ID, values);
	}

	/**
	 * Fetch records that have <code>language IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.ChatTranslations> fetchByLanguage(String... values) {
		return fetch(ChatTranslations.CHAT_TRANSLATIONS.LANGUAGE, values);
	}

	/**
	 * Fetch records that have <code>text IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.ChatTranslations> fetchByText(String... values) {
		return fetch(ChatTranslations.CHAT_TRANSLATIONS.TEXT, values);
	}
}
