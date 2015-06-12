/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IUserBeforeTutorialCompletion;

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
@Table(name = "user_before_tutorial_completion", schema = "mobsters")
public class UserBeforeTutorialCompletionPojo implements IUserBeforeTutorialCompletion {

	private static final long serialVersionUID = 1915409714;

	private String    id;
	private String    openUdid;
	private String    udid;
	private String    mac;
	private String    advertiserId;
	private Timestamp createTime;

	public UserBeforeTutorialCompletionPojo() {}

	public UserBeforeTutorialCompletionPojo(UserBeforeTutorialCompletionPojo value) {
		this.id = value.id;
		this.openUdid = value.openUdid;
		this.udid = value.udid;
		this.mac = value.mac;
		this.advertiserId = value.advertiserId;
		this.createTime = value.createTime;
	}

	public UserBeforeTutorialCompletionPojo(
		String    id,
		String    openUdid,
		String    udid,
		String    mac,
		String    advertiserId,
		Timestamp createTime
	) {
		this.id = id;
		this.openUdid = openUdid;
		this.udid = udid;
		this.mac = mac;
		this.advertiserId = advertiserId;
		this.createTime = createTime;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public UserBeforeTutorialCompletionPojo setId(String id) {
		this.id = id;
		return this;
	}

	@Column(name = "open_udid", unique = true, length = 250)
	@Size(max = 250)
	@Override
	public String getOpenUdid() {
		return this.openUdid;
	}

	@Override
	public UserBeforeTutorialCompletionPojo setOpenUdid(String openUdid) {
		this.openUdid = openUdid;
		return this;
	}

	@Column(name = "udid", length = 250)
	@Size(max = 250)
	@Override
	public String getUdid() {
		return this.udid;
	}

	@Override
	public UserBeforeTutorialCompletionPojo setUdid(String udid) {
		this.udid = udid;
		return this;
	}

	@Column(name = "mac", length = 250)
	@Size(max = 250)
	@Override
	public String getMac() {
		return this.mac;
	}

	@Override
	public UserBeforeTutorialCompletionPojo setMac(String mac) {
		this.mac = mac;
		return this;
	}

	@Column(name = "advertiser_id", length = 250)
	@Size(max = 250)
	@Override
	public String getAdvertiserId() {
		return this.advertiserId;
	}

	@Override
	public UserBeforeTutorialCompletionPojo setAdvertiserId(String advertiserId) {
		this.advertiserId = advertiserId;
		return this;
	}

	@Column(name = "create_time", unique = true)
	@Override
	public Timestamp getCreateTime() {
		return this.createTime;
	}

	@Override
	public UserBeforeTutorialCompletionPojo setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IUserBeforeTutorialCompletion from) {
		setId(from.getId());
		setOpenUdid(from.getOpenUdid());
		setUdid(from.getUdid());
		setMac(from.getMac());
		setAdvertiserId(from.getAdvertiserId());
		setCreateTime(from.getCreateTime());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IUserBeforeTutorialCompletion> E into(E into) {
		into.from(this);
		return into;
	}
}