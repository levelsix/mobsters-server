/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.interfaces;


import java.io.Serializable;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;


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
@Table(name = "chat_translations", schema = "mobsters")
public interface IChatTranslations extends Serializable {

	/**
	 * Setter for <code>mobsters.chat_translations.id</code>.
	 */
	public IChatTranslations setId(String value);

	/**
	 * Getter for <code>mobsters.chat_translations.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	public String getId();

	/**
	 * Setter for <code>mobsters.chat_translations.chat_type</code>. for now it’s just private chat
	 */
	public IChatTranslations setChatType(String value);

	/**
	 * Getter for <code>mobsters.chat_translations.chat_type</code>. for now it’s just private chat
	 */
	@Column(name = "chat_type", length = 36)
	@Size(max = 36)
	public String getChatType();

	/**
	 * Setter for <code>mobsters.chat_translations.chat_id</code>.
	 */
	public IChatTranslations setChatId(String value);

	/**
	 * Getter for <code>mobsters.chat_translations.chat_id</code>.
	 */
	@Column(name = "chat_id", length = 36)
	@Size(max = 36)
	public String getChatId();

	/**
	 * Setter for <code>mobsters.chat_translations.language</code>.
	 */
	public IChatTranslations setLanguage(String value);

	/**
	 * Getter for <code>mobsters.chat_translations.language</code>.
	 */
	@Column(name = "language", length = 36)
	@Size(max = 36)
	public String getLanguage();

	/**
	 * Setter for <code>mobsters.chat_translations.text</code>.
	 */
	public IChatTranslations setText(String value);

	/**
	 * Getter for <code>mobsters.chat_translations.text</code>.
	 */
	@Column(name = "text", length = 65535)
	@Size(max = 65535)
	public String getText();

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * Load data from another generated Record/POJO implementing the common interface IChatTranslations
	 */
	public void from(com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IChatTranslations from);

	/**
	 * Copy data into another generated Record/POJO implementing the common interface IChatTranslations
	 */
	public <E extends com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IChatTranslations> E into(E into);
}
