/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.ClanIconConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IClanIconConfig;

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
@Table(name = "clan_icon_config", schema = "mobsters")
public class ClanIconConfigRecord extends UpdatableRecordImpl<ClanIconConfigRecord> implements Record3<Integer, String, Boolean>, IClanIconConfig {

	private static final long serialVersionUID = -691370573;

	/**
	 * Setter for <code>mobsters.clan_icon_config.id</code>.
	 */
	@Override
	public ClanIconConfigRecord setId(Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_icon_config.id</code>.
	 */
	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.clan_icon_config.img_name</code>.
	 */
	@Override
	public ClanIconConfigRecord setImgName(String value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_icon_config.img_name</code>.
	 */
	@Column(name = "img_name", length = 45)
	@Size(max = 45)
	@Override
	public String getImgName() {
		return (String) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.clan_icon_config.is_available</code>.
	 */
	@Override
	public ClanIconConfigRecord setIsAvailable(Boolean value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.clan_icon_config.is_available</code>.
	 */
	@Column(name = "is_available", precision = 1)
	@Override
	public Boolean getIsAvailable() {
		return (Boolean) getValue(2);
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
	public Row3<Integer, String, Boolean> fieldsRow() {
		return (Row3) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row3<Integer, String, Boolean> valuesRow() {
		return (Row3) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return ClanIconConfig.CLAN_ICON_CONFIG.ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field2() {
		return ClanIconConfig.CLAN_ICON_CONFIG.IMG_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Boolean> field3() {
		return ClanIconConfig.CLAN_ICON_CONFIG.IS_AVAILABLE;
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
		return getImgName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Boolean value3() {
		return getIsAvailable();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanIconConfigRecord value1(Integer value) {
		setId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanIconConfigRecord value2(String value) {
		setImgName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanIconConfigRecord value3(Boolean value) {
		setIsAvailable(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ClanIconConfigRecord values(Integer value1, String value2, Boolean value3) {
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
	public void from(IClanIconConfig from) {
		setId(from.getId());
		setImgName(from.getImgName());
		setIsAvailable(from.getIsAvailable());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IClanIconConfig> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached ClanIconConfigRecord
	 */
	public ClanIconConfigRecord() {
		super(ClanIconConfig.CLAN_ICON_CONFIG);
	}

	/**
	 * Create a detached, initialised ClanIconConfigRecord
	 */
	public ClanIconConfigRecord(Integer id, String imgName, Boolean isAvailable) {
		super(ClanIconConfig.CLAN_ICON_CONFIG);

		setValue(0, id);
		setValue(1, imgName);
		setValue(2, isAvailable);
	}
}
