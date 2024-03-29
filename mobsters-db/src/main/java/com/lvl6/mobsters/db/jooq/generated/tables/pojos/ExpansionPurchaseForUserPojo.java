/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IExpansionPurchaseForUser;

import java.sql.Timestamp;

import javax.annotation.Generated;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
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
@Table(name = "expansion_purchase_for_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "x_position", "y_position"})
})
public class ExpansionPurchaseForUserPojo implements IExpansionPurchaseForUser {

	private static final long serialVersionUID = -1764211571;

	private String    userId;
	private Integer   xPosition;
	private Integer   yPosition;
	private Byte      isExpanding;
	private Timestamp expandStartTime;

	public ExpansionPurchaseForUserPojo() {}

	public ExpansionPurchaseForUserPojo(ExpansionPurchaseForUserPojo value) {
		this.userId = value.userId;
		this.xPosition = value.xPosition;
		this.yPosition = value.yPosition;
		this.isExpanding = value.isExpanding;
		this.expandStartTime = value.expandStartTime;
	}

	public ExpansionPurchaseForUserPojo(
		String    userId,
		Integer   xPosition,
		Integer   yPosition,
		Byte      isExpanding,
		Timestamp expandStartTime
	) {
		this.userId = userId;
		this.xPosition = xPosition;
		this.yPosition = yPosition;
		this.isExpanding = isExpanding;
		this.expandStartTime = expandStartTime;
	}

	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public ExpansionPurchaseForUserPojo setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	@Column(name = "x_position", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getXPosition() {
		return this.xPosition;
	}

	@Override
	public ExpansionPurchaseForUserPojo setXPosition(Integer xPosition) {
		this.xPosition = xPosition;
		return this;
	}

	@Column(name = "y_position", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getYPosition() {
		return this.yPosition;
	}

	@Override
	public ExpansionPurchaseForUserPojo setYPosition(Integer yPosition) {
		this.yPosition = yPosition;
		return this;
	}

	@Column(name = "is_expanding", precision = 3)
	@Override
	public Byte getIsExpanding() {
		return this.isExpanding;
	}

	@Override
	public ExpansionPurchaseForUserPojo setIsExpanding(Byte isExpanding) {
		this.isExpanding = isExpanding;
		return this;
	}

	@Column(name = "expand_start_time")
	@Override
	public Timestamp getExpandStartTime() {
		return this.expandStartTime;
	}

	@Override
	public ExpansionPurchaseForUserPojo setExpandStartTime(Timestamp expandStartTime) {
		this.expandStartTime = expandStartTime;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IExpansionPurchaseForUser from) {
		setUserId(from.getUserId());
		setXPosition(from.getXPosition());
		setYPosition(from.getYPosition());
		setIsExpanding(from.getIsExpanding());
		setExpandStartTime(from.getExpandStartTime());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IExpansionPurchaseForUser> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.ExpansionPurchaseForUserRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.ExpansionPurchaseForUserRecord();
		poop.from(this);
		return "ExpansionPurchaseForUserPojo[" + poop.valuesRow() + "]";
	}
}
