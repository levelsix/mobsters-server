/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.IQuestJobForUser;

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
@Table(name = "quest_job_for_user", schema = "mobsters", uniqueConstraints = {
	@UniqueConstraint(columnNames = {"user_id", "quest_id", "quest_job_id"})
})
public class QuestJobForUserPojo implements IQuestJobForUser {

	private static final long serialVersionUID = 923076444;

	private String  userId;
	private Integer questId;
	private Integer questJobId;
	private Boolean isComplete;
	private Integer progress;

	public QuestJobForUserPojo() {}

	public QuestJobForUserPojo(QuestJobForUserPojo value) {
		this.userId = value.userId;
		this.questId = value.questId;
		this.questJobId = value.questJobId;
		this.isComplete = value.isComplete;
		this.progress = value.progress;
	}

	public QuestJobForUserPojo(
		String  userId,
		Integer questId,
		Integer questJobId,
		Boolean isComplete,
		Integer progress
	) {
		this.userId = userId;
		this.questId = questId;
		this.questJobId = questJobId;
		this.isComplete = isComplete;
		this.progress = progress;
	}

	@Column(name = "user_id", nullable = false, length = 36)
	@NotNull
	@Size(max = 36)
	@Override
	public String getUserId() {
		return this.userId;
	}

	@Override
	public QuestJobForUserPojo setUserId(String userId) {
		this.userId = userId;
		return this;
	}

	@Column(name = "quest_id", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getQuestId() {
		return this.questId;
	}

	@Override
	public QuestJobForUserPojo setQuestId(Integer questId) {
		this.questId = questId;
		return this;
	}

	@Column(name = "quest_job_id", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getQuestJobId() {
		return this.questJobId;
	}

	@Override
	public QuestJobForUserPojo setQuestJobId(Integer questJobId) {
		this.questJobId = questJobId;
		return this;
	}

	@Column(name = "is_complete", precision = 1)
	@Override
	public Boolean getIsComplete() {
		return this.isComplete;
	}

	@Override
	public QuestJobForUserPojo setIsComplete(Boolean isComplete) {
		this.isComplete = isComplete;
		return this;
	}

	@Column(name = "progress", precision = 7)
	@Override
	public Integer getProgress() {
		return this.progress;
	}

	@Override
	public QuestJobForUserPojo setProgress(Integer progress) {
		this.progress = progress;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(IQuestJobForUser from) {
		setUserId(from.getUserId());
		setQuestId(from.getQuestId());
		setQuestJobId(from.getQuestJobId());
		setIsComplete(from.getIsComplete());
		setProgress(from.getProgress());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends IQuestJobForUser> E into(E into) {
		into.from(this);
		return into;
	}


	public String toString() {
		com.lvl6.mobsters.db.jooq.generated.tables.records.QuestJobForUserRecord poop = new com.lvl6.mobsters.db.jooq.generated.tables.records.QuestJobForUserRecord();
		poop.from(this);
		return "QuestJobForUserPojo[" + poop.valuesRow() + "]";
	}
}
