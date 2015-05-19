/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.records;


import com.lvl6.mobsters.db.jooq.generated.tables.StructureResidenceConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IStructureResidenceConfig;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record7;
import org.jooq.Row;
import org.jooq.Row7;
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
@Table(name = "structure_residence_config", schema = "mobsters")
public class StructureResidenceConfigRecord extends UpdatableRecordImpl<StructureResidenceConfigRecord> implements Record7<Integer, Integer, Integer, Integer, Integer, String, String>, IStructureResidenceConfig {

	private static final long serialVersionUID = -984350005;

	/**
	 * Setter for <code>mobsters.structure_residence_config.struct_id</code>.
	 */
	@Override
	public StructureResidenceConfigRecord setStructId(Integer value) {
		setValue(0, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_residence_config.struct_id</code>.
	 */
	@Id
	@Column(name = "struct_id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getStructId() {
		return (Integer) getValue(0);
	}

	/**
	 * Setter for <code>mobsters.structure_residence_config.num_monster_slots</code>.
	 */
	@Override
	public StructureResidenceConfigRecord setNumMonsterSlots(Integer value) {
		setValue(1, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_residence_config.num_monster_slots</code>.
	 */
	@Column(name = "num_monster_slots", nullable = false, precision = 7)
	@NotNull
	@Override
	public Integer getNumMonsterSlots() {
		return (Integer) getValue(1);
	}

	/**
	 * Setter for <code>mobsters.structure_residence_config.num_bonus_monster_slots</code>.
	 */
	@Override
	public StructureResidenceConfigRecord setNumBonusMonsterSlots(Integer value) {
		setValue(2, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_residence_config.num_bonus_monster_slots</code>.
	 */
	@Column(name = "num_bonus_monster_slots", nullable = false, precision = 7)
	@NotNull
	@Override
	public Integer getNumBonusMonsterSlots() {
		return (Integer) getValue(2);
	}

	/**
	 * Setter for <code>mobsters.structure_residence_config.num_gems_required</code>.
	 */
	@Override
	public StructureResidenceConfigRecord setNumGemsRequired(Integer value) {
		setValue(3, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_residence_config.num_gems_required</code>.
	 */
	@Column(name = "num_gems_required", nullable = false, precision = 7)
	@NotNull
	@Override
	public Integer getNumGemsRequired() {
		return (Integer) getValue(3);
	}

	/**
	 * Setter for <code>mobsters.structure_residence_config.num_accepeted_fb_invites</code>.
	 */
	@Override
	public StructureResidenceConfigRecord setNumAccepetedFbInvites(Integer value) {
		setValue(4, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_residence_config.num_accepeted_fb_invites</code>.
	 */
	@Column(name = "num_accepeted_fb_invites", nullable = false, precision = 7)
	@NotNull
	@Override
	public Integer getNumAccepetedFbInvites() {
		return (Integer) getValue(4);
	}

	/**
	 * Setter for <code>mobsters.structure_residence_config.occupation_name</code>. This is flavor text, for which position the user invites his friends to do, in the journey for more slots.
	 */
	@Override
	public StructureResidenceConfigRecord setOccupationName(String value) {
		setValue(5, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_residence_config.occupation_name</code>. This is flavor text, for which position the user invites his friends to do, in the journey for more slots.
	 */
	@Column(name = "occupation_name", length = 45)
	@Size(max = 45)
	@Override
	public String getOccupationName() {
		return (String) getValue(5);
	}

	/**
	 * Setter for <code>mobsters.structure_residence_config.img_suffix</code>.
	 */
	@Override
	public StructureResidenceConfigRecord setImgSuffix(String value) {
		setValue(6, value);
		return this;
	}

	/**
	 * Getter for <code>mobsters.structure_residence_config.img_suffix</code>.
	 */
	@Column(name = "img_suffix", length = 45)
	@Size(max = 45)
	@Override
	public String getImgSuffix() {
		return (String) getValue(6);
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
	// Record7 type implementation
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row7<Integer, Integer, Integer, Integer, Integer, String, String> fieldsRow() {
		return (Row7) super.fieldsRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Row7<Integer, Integer, Integer, Integer, Integer, String, String> valuesRow() {
		return (Row7) super.valuesRow();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field1() {
		return StructureResidenceConfig.STRUCTURE_RESIDENCE_CONFIG.STRUCT_ID;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field2() {
		return StructureResidenceConfig.STRUCTURE_RESIDENCE_CONFIG.NUM_MONSTER_SLOTS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field3() {
		return StructureResidenceConfig.STRUCTURE_RESIDENCE_CONFIG.NUM_BONUS_MONSTER_SLOTS;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field4() {
		return StructureResidenceConfig.STRUCTURE_RESIDENCE_CONFIG.NUM_GEMS_REQUIRED;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<Integer> field5() {
		return StructureResidenceConfig.STRUCTURE_RESIDENCE_CONFIG.NUM_ACCEPETED_FB_INVITES;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field6() {
		return StructureResidenceConfig.STRUCTURE_RESIDENCE_CONFIG.OCCUPATION_NAME;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Field<String> field7() {
		return StructureResidenceConfig.STRUCTURE_RESIDENCE_CONFIG.IMG_SUFFIX;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value1() {
		return getStructId();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value2() {
		return getNumMonsterSlots();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value3() {
		return getNumBonusMonsterSlots();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value4() {
		return getNumGemsRequired();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Integer value5() {
		return getNumAccepetedFbInvites();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value6() {
		return getOccupationName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String value7() {
		return getImgSuffix();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureResidenceConfigRecord value1(Integer value) {
		setStructId(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureResidenceConfigRecord value2(Integer value) {
		setNumMonsterSlots(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureResidenceConfigRecord value3(Integer value) {
		setNumBonusMonsterSlots(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureResidenceConfigRecord value4(Integer value) {
		setNumGemsRequired(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureResidenceConfigRecord value5(Integer value) {
		setNumAccepetedFbInvites(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureResidenceConfigRecord value6(String value) {
		setOccupationName(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureResidenceConfigRecord value7(String value) {
		setImgSuffix(value);
		return this;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public StructureResidenceConfigRecord values(Integer value1, Integer value2, Integer value3, Integer value4, Integer value5, String value6, String value7) {
		value1(value1);
		value2(value2);
		value3(value3);
		value4(value4);
		value5(value5);
		value6(value6);
		value7(value7);
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IStructureResidenceConfig from) {
		setStructId(from.getStructId());
		setNumMonsterSlots(from.getNumMonsterSlots());
		setNumBonusMonsterSlots(from.getNumBonusMonsterSlots());
		setNumGemsRequired(from.getNumGemsRequired());
		setNumAccepetedFbInvites(from.getNumAccepetedFbInvites());
		setOccupationName(from.getOccupationName());
		setImgSuffix(from.getImgSuffix());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IStructureResidenceConfig> E into(E into) {
		into.from(this);
		return into;
	}

	// -------------------------------------------------------------------------
	// Constructors
	// -------------------------------------------------------------------------

	/**
	 * Create a detached StructureResidenceConfigRecord
	 */
	public StructureResidenceConfigRecord() {
		super(StructureResidenceConfig.STRUCTURE_RESIDENCE_CONFIG);
	}

	/**
	 * Create a detached, initialised StructureResidenceConfigRecord
	 */
	public StructureResidenceConfigRecord(Integer structId, Integer numMonsterSlots, Integer numBonusMonsterSlots, Integer numGemsRequired, Integer numAccepetedFbInvites, String occupationName, String imgSuffix) {
		super(StructureResidenceConfig.STRUCTURE_RESIDENCE_CONFIG);

		setValue(0, structId);
		setValue(1, numMonsterSlots);
		setValue(2, numBonusMonsterSlots);
		setValue(3, numGemsRequired);
		setValue(4, numAccepetedFbInvites);
		setValue(5, occupationName);
		setValue(6, imgSuffix);
	}
}
