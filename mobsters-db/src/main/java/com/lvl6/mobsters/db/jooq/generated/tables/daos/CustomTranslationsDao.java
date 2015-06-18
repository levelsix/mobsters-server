/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.CustomTranslations;
import com.lvl6.mobsters.db.jooq.generated.tables.pojos.CustomTranslationsPojo;
import com.lvl6.mobsters.db.jooq.generated.tables.records.CustomTranslationsRecord;

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
public class CustomTranslationsDao extends DAOImpl<CustomTranslationsRecord, CustomTranslationsPojo, Integer> {

	/**
	 * Create a new CustomTranslationsDao without any configuration
	 */
	public CustomTranslationsDao() {
		super(CustomTranslations.CUSTOM_TRANSLATIONS, CustomTranslationsPojo.class);
	}

	/**
	 * Create a new CustomTranslationsDao with an attached configuration
	 */
	public CustomTranslationsDao(Configuration configuration) {
		super(CustomTranslations.CUSTOM_TRANSLATIONS, CustomTranslationsPojo.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(CustomTranslationsPojo object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<CustomTranslationsPojo> fetchById(Integer... values) {
		return fetch(CustomTranslations.CUSTOM_TRANSLATIONS.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public CustomTranslationsPojo fetchOneById(Integer value) {
		return fetchOne(CustomTranslations.CUSTOM_TRANSLATIONS.ID, value);
	}

	/**
	 * Fetch records that have <code>phrase IN (values)</code>
	 */
	public List<CustomTranslationsPojo> fetchByPhrase(String... values) {
		return fetch(CustomTranslations.CUSTOM_TRANSLATIONS.PHRASE, values);
	}

	/**
	 * Fetch records that have <code>language IN (values)</code>
	 */
	public List<CustomTranslationsPojo> fetchByLanguage(String... values) {
		return fetch(CustomTranslations.CUSTOM_TRANSLATIONS.LANGUAGE, values);
	}
}
