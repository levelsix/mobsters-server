/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IMiniEventConfig;

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
@Table(name = "mini_event_config", schema = "mobsters")
public class MiniEventConfig implements IMiniEventConfig {

	private static final long serialVersionUID = -647288007;

	private Integer   id;
	private Timestamp startTime;
	private Timestamp endTime;
	private String    name;
	private String    description;
	private String    img;
	private String    icon;

	public MiniEventConfig() {}

	public MiniEventConfig(MiniEventConfig value) {
		this.id = value.id;
		this.startTime = value.startTime;
		this.endTime = value.endTime;
		this.name = value.name;
		this.description = value.description;
		this.img = value.img;
		this.icon = value.icon;
	}

	public MiniEventConfig(
		Integer   id,
		Timestamp startTime,
		Timestamp endTime,
		String    name,
		String    description,
		String    img,
		String    icon
	) {
		this.id = id;
		this.startTime = startTime;
		this.endTime = endTime;
		this.name = name;
		this.description = description;
		this.img = img;
		this.icon = icon;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public MiniEventConfig setId(Integer id) {
		this.id = id;
		return this;
	}

	@Column(name = "start_time")
	@Override
	public Timestamp getStartTime() {
		return this.startTime;
	}

	@Override
	public MiniEventConfig setStartTime(Timestamp startTime) {
		this.startTime = startTime;
		return this;
	}

	@Column(name = "end_time")
	@Override
	public Timestamp getEndTime() {
		return this.endTime;
	}

	@Override
	public MiniEventConfig setEndTime(Timestamp endTime) {
		this.endTime = endTime;
		return this;
	}

	@Column(name = "name", length = 75)
	@Size(max = 75)
	@Override
	public String getName() {
		return this.name;
	}

	@Override
	public MiniEventConfig setName(String name) {
		this.name = name;
		return this;
	}

	@Column(name = "description", length = 200)
	@Size(max = 200)
	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public MiniEventConfig setDescription(String description) {
		this.description = description;
		return this;
	}

	@Column(name = "img", length = 75)
	@Size(max = 75)
	@Override
	public String getImg() {
		return this.img;
	}

	@Override
	public MiniEventConfig setImg(String img) {
		this.img = img;
		return this;
	}

	@Column(name = "icon", length = 75)
	@Size(max = 75)
	@Override
	public String getIcon() {
		return this.icon;
	}

	@Override
	public MiniEventConfig setIcon(String icon) {
		this.icon = icon;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IMiniEventConfig from) {
		setId(from.getId());
		setStartTime(from.getStartTime());
		setEndTime(from.getEndTime());
		setName(from.getName());
		setDescription(from.getDescription());
		setImg(from.getImg());
		setIcon(from.getIcon());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IMiniEventConfig> E into(E into) {
		into.from(this);
		return into;
	}
}
