/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.CustomTranslations;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ICustomTranslations;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row;
import org.jooq.Row3;
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
@Table(name = "custom_translations", schema = "mobsters")
public class CustomTranslationsRecord extends UpdatableRecordImpl<CustomTranslationsRecord> implements Record3<Integer, String, String>, ICustomTranslations {

	private static final long serialVersionUID = 10588905;

	/**
	 * Setter for <code>mobsters.custom_translations.id</code>.
	 */
	@Override
	public CustomTranslationsRecord setId(Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.custom_translations.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.custom_translations.phrase</code>. needs to be lower case
	 */
	@Override
	public CustomTranslationsRecord setPhrase(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.custom_translations.phrase</code>. needs to be lower case
	 */
	@Column(name = "phrase", length = 45)
	@Size(max = 45)
	@Override
	public String getPhrase() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.custom_translations.language</code>.
	 */
	@Override
	public CustomTranslationsRecord setLanguage(String value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.custom_translations.language</code>.
	 */
	@Column(name = "language", length = 45)
	@Size(max = 45)
	@Override
	public String getLanguage() {
		return (String) getValue(2);
	}

	// -------------------------------------------------------------------------
	// Primary key information
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Record1<Integer> key() {
		return (Record1) super.key();
	}

	// -------------------------------------------------------------------------
	// Record3 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row3<Integer, String, String> fieldsRow() {
		return (Row3) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row3<Integer, String, String> valuesRow() {
		return (Row3) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return CustomTranslations.CUSTOM_TRANSLATIONS.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return CustomTranslations.CUSTOM_TRANSLATIONS.PHRASE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field3() {
		return CustomTranslations.CUSTOM_TRANSLATIONS.LANGUAGE;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value1() {
		return getId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value2() {
		return getPhrase();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value3() {
		return getLanguage();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CustomTranslationsRecord value1(Integer value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CustomTranslationsRecord value2(String value) {
		setPhrase(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CustomTranslationsRecord value3(String value) {
		setLanguage(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public CustomTranslationsRecord values(Integer value1, String value2, String value3) {
		value1(value1);
		value2(value2);
		value3(value3);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(ICustomTranslations from) {
		setId(from.getId());
		setPhrase(from.getPhrase());
		setLanguage(from.getLanguage());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends ICustomTranslations> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached CustomTranslationsRecord
	 */
	public CustomTranslationsRecord() {
		super(CustomTranslations.CUSTOM_TRANSLATIONS);
	}

	/**
	 * Create a detached, initialised CustomTranslationsRecord
	 */
	public CustomTranslationsRecord(Integer id, String phrase, String language) {
		super(CustomTranslations.CUSTOM_TRANSLATIONS);

		setValue(0, id);
		setValue(1, phrase);
		setValue(2, language);
	}
}