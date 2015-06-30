/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ILockBoxEventConfig;

import java.sql.Timestamp;

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
@Table(name = "lock_box_event_config", schema = "mobsters")
public class LockBoxEventConfigPojo implements ILockBoxEventConfig {

	private static final long serialVersionUID = 1128508867;

	private Integer   id;
	private Timestamp startTime;
	private Timestamp endTime;
	private String    lockBoxImageName;
	private String    eventName;
	private Integer   prizeEquipId;
	private String    descriptionString;
	private String    descriptionImageName;
	private String    tagImageName;

	public LockBoxEventConfigPojo() {}

	public LockBoxEventConfigPojo(LockBoxEventConfigPojo value) {
		this.id = value.id;
		this.startTime = value.startTime;
		this.endTime = value.endTime;
		this.lockBoxImageName = value.lockBoxImageName;
		this.eventName = value.eventName;
		this.prizeEquipId = value.prizeEquipId;
		this.descriptionString = value.descriptionString;
		this.descriptionImageName = value.descriptionImageName;
		this.tagImageName = value.tagImageName;
	}

	public LockBoxEventConfigPojo(
		Integer   id,
		Timestamp startTime,
		Timestamp endTime,
		String    lockBoxImageName,
		String    eventName,
		Integer   prizeEquipId,
		String    descriptionString,
		String    descriptionImageName,
		String    tagImageName
	) {
		this.id = id;
		this.startTime = startTime;
		this.endTime = endTime;
		this.lockBoxImageName = lockBoxImageName;
		this.eventName = eventName;
		this.prizeEquipId = prizeEquipId;
		this.descriptionString = descriptionString;
		this.descriptionImageName = descriptionImageName;
		this.tagImageName = tagImageName;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public LockBoxEventConfigPojo setId(Integer id) {
		this.id = id;
		return this;
	}

	@Column(name = "start_time")
	@Override
	public Timestamp getStartTime() {
		return this.startTime;
	}

	@Override
	public LockBoxEventConfigPojo setStartTime(Timestamp startTime) {
		this.startTime = startTime;
		return this;
	}

	@Column(name = "end_time")
	@Override
	public Timestamp getEndTime() {
		return this.endTime;
	}

	@Override
	public LockBoxEventConfigPojo setEndTime(Timestamp endTime) {
		this.endTime = endTime;
		return this;
	}

	@Column(name = "lock_box_image_name", length = 40)
	@Size(max = 40)
	@Override
	public String getLockBoxImageName() {
		return this.lockBoxImageName;
	}

	@Override
	public LockBoxEventConfigPojo setLockBoxImageName(String lockBoxImageName) {
		this.lockBoxImageName = lockBoxImageName;
		return this;
	}

	@Column(name = "event_name", length = 100)
	@Size(max = 100)
	@Override
	public String getEventName() {
		return this.eventName;
	}

	@Override
	public LockBoxEventConfigPojo setEventName(String eventName) {
		this.eventName = eventName;
		return this;
	}

	@Column(name = "prize_equip_id", precision = 10)
	@Override
	public Integer getPrizeEquipId() {
		return this.prizeEquipId;
	}

	@Override
	public LockBoxEventConfigPojo setPrizeEquipId(Integer prizeEquipId) {
		this.prizeEquipId = prizeEquipId;
		return this;
	}

	@Column(name = "description_string", length = 100)
	@Size(max = 100)
	@Override
	public String getDescriptionString() {
		return this.descriptionString;
	}

	@Override
	public LockBoxEventConfigPojo setDescriptionString(String descriptionString) {
		this.descriptionString = descriptionString;
		return this;
	}

	@Column(name = "description_image_name", length = 40)
	@Size(max = 40)
	@Override
	public String getDescriptionImageName() {
		return this.descriptionImageName;
	}

	@Override
	public LockBoxEventConfigPojo setDescriptionImageName(String descriptionImageName) {
		this.descriptionImageName = descriptionImageName;
		return this;
	}

	@Column(name = "tag_image_name", length = 40)
	@Size(max = 40)
	@Override
	public String getTagImageName() {
		return this.tagImageName;
	}

	@Override
	public LockBoxEventConfigPojo setTagImageName(String tagImageName) {
		this.tagImageName = tagImageName;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(ILockBoxEventConfig from) {
		setId(from.getId());
		setStartTime(from.getStartTime());
		setEndTime(from.getEndTime());
		setLockBoxImageName(from.getLockBoxImageName());
		setEventName(from.getEventName());
		setPrizeEquipId(from.getPrizeEquipId());
		setDescriptionString(from.getDescriptionString());
		setDescriptionImageName(from.getDescriptionImageName());
		setTagImageName(from.getTagImageName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends ILockBoxEventConfig> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.LockBoxEventConfigRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.LockBoxEventConfigRecord();
		poop.from(this);
		return "LockBoxEventConfigPojo[" + poop.valuesRow() + "]";
	}
}
