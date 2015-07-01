/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IStructureLabConfig;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;


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
@Table(name = "structure_lab_config", schema = "mobsters")
public class StructureLabConfigPojo implements IStructureLabConfig {

	private static final long serialVersionUID = -473285540;

	private Integer structId;
	private Byte    queueSize;
	private Double  pointsMultiplier;

	public StructureLabConfigPojo() {}

	public StructureLabConfigPojo(StructureLabConfigPojo value) {
		this.structId = value.structId;
		this.queueSize = value.queueSize;
		this.pointsMultiplier = value.pointsMultiplier;
	}

	public StructureLabConfigPojo(
		Integer structId,
		Byte    queueSize,
		Double  pointsMultiplier
	) {
		this.structId = structId;
		this.queueSize = queueSize;
		this.pointsMultiplier = pointsMultiplier;
	}

	@Id
	@Column(name = "struct_id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getStructId() {
		return this.structId;
	}

	@Override
	public StructureLabConfigPojo setStructId(Integer structId) {
		this.structId = structId;
		return this;
	}

	@Column(name = "queue_size", nullable = false, precision = 3)
	@NotNull
	@Override
	public Byte getQueueSize() {
		return this.queueSize;
	}

	@Override
	public StructureLabConfigPojo setQueueSize(Byte queueSize) {
		this.queueSize = queueSize;
		return this;
	}

	@Column(name = "points_multiplier", precision = 12)
	@Override
	public Double getPointsMultiplier() {
		return this.pointsMultiplier;
	}

	@Override
	public StructureLabConfigPojo setPointsMultiplier(Double pointsMultiplier) {
		this.pointsMultiplier = pointsMultiplier;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IStructureLabConfig from) {
		setStructId(from.getStructId());
		setQueueSize(from.getQueueSize());
		setPointsMultiplier(from.getPointsMultiplier());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IStructureLabConfig> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.StructureLabConfigRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.StructureLabConfigRecord();
		poop.from(this);
		return "StructureLabConfigPojo[" + poop.valuesRow() + "]";
	}
}
