/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IUserBattleItemHistory;

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
@Table(name = "user_battle_item_history", schema = "mobsters")
public class UserBattleItemHistoryPojo implements IUserBattleItemHistory {

	private static final long serialVersionUID = 1731285803;

	private String    id;
	private String    userId;
	private Integer   battleItemId;
	private Timestamp date;
	private Byte      gainedBattleItem;
	private Byte      usedInBattle;
	private String    reasonForChange;
	private String    details;

	public UserBattleItemHistoryPojo() {}

	public UserBattleItemHistoryPojo(UserBattleItemHistoryPojo value) {
		this.id = value.id;
		this.userId = value.userId;
		this.battleItemId = value.battleItemId;
		this.date = value.date;
		this.gainedBattleItem = value.gainedBattleItem;
		this.usedInBattle = value.usedInBattle;
		this.reasonForChange = value.reasonForChange;
		this.details = value.details;
	}

	public UserBattleItemHistoryPojo(
		String    id,
		String    userId,
		Integer   battleItemId,
		Timestamp date,
		Byte      gainedBattleItem,
		Byte      usedInBattle,
		String    reasonForChange,
		String    details
	) {
		this.id = id;
		this.userId = userId;
		this.battleItemId = battleItemId;
		this.date = date;
		this.gainedBattleItem = gainedBattleItem;
		this.usedInBattle = usedInBattle;
		this.reasonForChange = reasonForChange;
		this.details = details;
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
	public UserBattleItemHistoryPojo setId(String id) {
		this.id = id;
		return this;
	}

	@Column(name = "user_id", length = 45)
	@Size(max = 45)
	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public UserBattleItemHistoryPojo setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	@Column(name = "battle_item_id", precision = 10)
	@Override
	public Integer getBattleItemId() {
		return this.battleItemId;
	}

	@Override
	public UserBattleItemHistoryPojo setBattleItemId(Integer battleItemId) {
		this.battleItemId = battleItemId;
		return this;
	}

	@Column(name = "date")
	@Override
	public Timestamp getDate() {
		return this.date;
	}

	@Override
	public UserBattleItemHistoryPojo setDate(Timestamp date) {
		this.date = date;
		return this;
	}

	@Column(name = "gained_battle_item", precision = 3)
	@Override
	public Byte getGainedBattleItem() {
		return this.gainedBattleItem;
	}

	@Override
	public UserBattleItemHistoryPojo setGainedBattleItem(Byte gainedBattleItem) {
		this.gainedBattleItem = gainedBattleItem;
		return this;
	}

	@Column(name = "used_in_battle", precision = 3)
	@Override
	public Byte getUsedInBattle() {
		return this.usedInBattle;
	}

	@Override
	public UserBattleItemHistoryPojo setUsedInBattle(Byte usedInBattle) {
		this.usedInBattle = usedInBattle;
		return this;
	}

	@Column(name = "reason_for_change", length = 45)
	@Size(max = 45)
	@Override
	public String getReasonForChange() {
		return this.reasonForChange;
	}

	@Override
	public UserBattleItemHistoryPojo setReasonForChange(String reasonForChange) {
		this.reasonForChange = reasonForChange;
		return this;
	}

	@Column(name = "details", length = 45)
	@Size(max = 45)
	@Override
	public String getDetails() {
		return this.details;
	}

	@Override
	public UserBattleItemHistoryPojo setDetails(String details) {
		this.details = details;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IUserBattleItemHistory from) {
		setId(from.getId());
		setUserId(from.getUserId());
		setBattleItemId(from.getBattleItemId());
		setDate(from.getDate());
		setGainedBattleItem(from.getGainedBattleItem());
		setUsedInBattle(from.getUsedInBattle());
		setReasonForChange(from.getReasonForChange());
		setDetails(from.getDetails());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IUserBattleItemHistory> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.UserBattleItemHistoryRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.UserBattleItemHistoryRecord();
		poop.from(this);
		return "UserBattleItemHistoryPojo[" + poop.valuesRow() + "]";
	}
}