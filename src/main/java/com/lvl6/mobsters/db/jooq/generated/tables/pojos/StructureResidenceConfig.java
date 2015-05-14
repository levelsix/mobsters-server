/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IStructureResidenceConfig;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.jooq.types.UInteger;


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
public class StructureResidenceConfig implements IStructureResidenceConfig {

	private static final long serialVersionUID = 1407884092;

	private Integer  structId;
	private UInteger numMonsterSlots;
	private UInteger numBonusMonsterSlots;
	private UInteger numGemsRequired;
	private UInteger numAccepetedFbInvites;
	private String   occupationName;
	private String   imgSuffix;

	public StructureResidenceConfig() {}

	public StructureResidenceConfig(StructureResidenceConfig value) {
		this.structId = value.structId;
		this.numMonsterSlots = value.numMonsterSlots;
		this.numBonusMonsterSlots = value.numBonusMonsterSlots;
		this.numGemsRequired = value.numGemsRequired;
		this.numAccepetedFbInvites = value.numAccepetedFbInvites;
		this.occupationName = value.occupationName;
		this.imgSuffix = value.imgSuffix;
	}

	public StructureResidenceConfig(
		Integer  structId,
		UInteger numMonsterSlots,
		UInteger numBonusMonsterSlots,
		UInteger numGemsRequired,
		UInteger numAccepetedFbInvites,
		String   occupationName,
		String   imgSuffix
	) {
		this.structId = structId;
		this.numMonsterSlots = numMonsterSlots;
		this.numBonusMonsterSlots = numBonusMonsterSlots;
		this.numGemsRequired = numGemsRequired;
		this.numAccepetedFbInvites = numAccepetedFbInvites;
		this.occupationName = occupationName;
		this.imgSuffix = imgSuffix;
	}

	@Id
	@Column(name = "struct_id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getStructId() {
		return this.structId;
	}

	@Override
	public StructureResidenceConfig setStructId(Integer structId) {
		this.structId = structId;
		return this;
	}

	@Column(name = "num_monster_slots", nullable = false, precision = 7)
	@NotNull
	@Override
	public UInteger getNumMonsterSlots() {
		return this.numMonsterSlots;
	}

	@Override
	public StructureResidenceConfig setNumMonsterSlots(UInteger numMonsterSlots) {
		this.numMonsterSlots = numMonsterSlots;
		return this;
	}

	@Column(name = "num_bonus_monster_slots", nullable = false, precision = 7)
	@NotNull
	@Override
	public UInteger getNumBonusMonsterSlots() {
		return this.numBonusMonsterSlots;
	}

	@Override
	public StructureResidenceConfig setNumBonusMonsterSlots(UInteger numBonusMonsterSlots) {
		this.numBonusMonsterSlots = numBonusMonsterSlots;
		return this;
	}

	@Column(name = "num_gems_required", nullable = false, precision = 7)
	@NotNull
	@Override
	public UInteger getNumGemsRequired() {
		return this.numGemsRequired;
	}

	@Override
	public StructureResidenceConfig setNumGemsRequired(UInteger numGemsRequired) {
		this.numGemsRequired = numGemsRequired;
		return this;
	}

	@Column(name = "num_accepeted_fb_invites", nullable = false, precision = 7)
	@NotNull
	@Override
	public UInteger getNumAccepetedFbInvites() {
		return this.numAccepetedFbInvites;
	}

	@Override
	public StructureResidenceConfig setNumAccepetedFbInvites(UInteger numAccepetedFbInvites) {
		this.numAccepetedFbInvites = numAccepetedFbInvites;
		return this;
	}

	@Column(name = "occupation_name", length = 45)
	@Size(max = 45)
	@Override
	public String getOccupationName() {
		return this.occupationName;
	}

	@Override
	public StructureResidenceConfig setOccupationName(String occupationName) {
		this.occupationName = occupationName;
		return this;
	}

	@Column(name = "img_suffix", length = 45)
	@Size(max = 45)
	@Override
	public String getImgSuffix() {
		return this.imgSuffix;
	}

	@Override
	public StructureResidenceConfig setImgSuffix(String imgSuffix) {
		this.imgSuffix = imgSuffix;
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
}
