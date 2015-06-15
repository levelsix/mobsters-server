/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.pojos;


import com.lvl6.mobsters.db.jooq.generated.tables.interfaces.ITaskConfig;

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
@Table(name = "task_config", schema = "mobsters")
public class TaskConfig implements ITaskConfig {

	private static final long serialVersionUID = -2066193492;

	private Integer id;
	private String  goodName;
	private String  description;
	private Integer cityId;
	private Integer assetNumWithinCity;
	private Integer prerequisiteTaskId;
	private Integer prerequisiteQuestId;
	private Integer boardWidth;
	private Integer boardHeight;
	private String  groundImgPrefix;
	private String  initDefeatedDialogue;
	private Integer expReward;
	private Integer boardId;

	public TaskConfig() {}

	public TaskConfig(TaskConfig value) {
		this.id = value.id;
		this.goodName = value.goodName;
		this.description = value.description;
		this.cityId = value.cityId;
		this.assetNumWithinCity = value.assetNumWithinCity;
		this.prerequisiteTaskId = value.prerequisiteTaskId;
		this.prerequisiteQuestId = value.prerequisiteQuestId;
		this.boardWidth = value.boardWidth;
		this.boardHeight = value.boardHeight;
		this.groundImgPrefix = value.groundImgPrefix;
		this.initDefeatedDialogue = value.initDefeatedDialogue;
		this.expReward = value.expReward;
		this.boardId = value.boardId;
	}

	public TaskConfig(
		Integer id,
		String  goodName,
		String  description,
		Integer cityId,
		Integer assetNumWithinCity,
		Integer prerequisiteTaskId,
		Integer prerequisiteQuestId,
		Integer boardWidth,
		Integer boardHeight,
		String  groundImgPrefix,
		String  initDefeatedDialogue,
		Integer expReward,
		Integer boardId
	) {
		this.id = id;
		this.goodName = goodName;
		this.description = description;
		this.cityId = cityId;
		this.assetNumWithinCity = assetNumWithinCity;
		this.prerequisiteTaskId = prerequisiteTaskId;
		this.prerequisiteQuestId = prerequisiteQuestId;
		this.boardWidth = boardWidth;
		this.boardHeight = boardHeight;
		this.groundImgPrefix = groundImgPrefix;
		this.initDefeatedDialogue = initDefeatedDialogue;
		this.expReward = expReward;
		this.boardId = boardId;
	}

	@Id
	@Column(name = "id", unique = true, nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getId() {
		return this.id;
	}

	@Override
	public TaskConfig setId(Integer id) {
		this.id = id;
		return this;
	}

	@Column(name = "good_name", nullable = false, length = 65535)
	@NotNull
	@Size(max = 65535)
	@Override
	public String getGoodName() {
		return this.goodName;
	}

	@Override
	public TaskConfig setGoodName(String goodName) {
		this.goodName = goodName;
		return this;
	}

	@Column(name = "description", length = 65535)
	@Size(max = 65535)
	@Override
	public String getDescription() {
		return this.description;
	}

	@Override
	public TaskConfig setDescription(String description) {
		this.description = description;
		return this;
	}

	@Column(name = "city_id", nullable = false, precision = 10)
	@NotNull
	@Override
	public Integer getCityId() {
		return this.cityId;
	}

	@Override
	public TaskConfig setCityId(Integer cityId) {
		this.cityId = cityId;
		return this;
	}

	@Column(name = "asset_num_within_city", precision = 10)
	@Override
	public Integer getAssetNumWithinCity() {
		return this.assetNumWithinCity;
	}

	@Override
	public TaskConfig setAssetNumWithinCity(Integer assetNumWithinCity) {
		this.assetNumWithinCity = assetNumWithinCity;
		return this;
	}

	@Column(name = "prerequisite_task_id", precision = 10)
	@Override
	public Integer getPrerequisiteTaskId() {
		return this.prerequisiteTaskId;
	}

	@Override
	public TaskConfig setPrerequisiteTaskId(Integer prerequisiteTaskId) {
		this.prerequisiteTaskId = prerequisiteTaskId;
		return this;
	}

	@Column(name = "prerequisite_quest_id", precision = 10)
	@Override
	public Integer getPrerequisiteQuestId() {
		return this.prerequisiteQuestId;
	}

	@Override
	public TaskConfig setPrerequisiteQuestId(Integer prerequisiteQuestId) {
		this.prerequisiteQuestId = prerequisiteQuestId;
		return this;
	}

	@Column(name = "board_width", precision = 10)
	@Override
	public Integer getBoardWidth() {
		return this.boardWidth;
	}

	@Override
	public TaskConfig setBoardWidth(Integer boardWidth) {
		this.boardWidth = boardWidth;
		return this;
	}

	@Column(name = "board_height", precision = 10)
	@Override
	public Integer getBoardHeight() {
		return this.boardHeight;
	}

	@Override
	public TaskConfig setBoardHeight(Integer boardHeight) {
		this.boardHeight = boardHeight;
		return this;
	}

	@Column(name = "ground_img_prefix", length = 70)
	@Size(max = 70)
	@Override
	public String getGroundImgPrefix() {
		return this.groundImgPrefix;
	}

	@Override
	public TaskConfig setGroundImgPrefix(String groundImgPrefix) {
		this.groundImgPrefix = groundImgPrefix;
		return this;
	}

	@Column(name = "init_defeated_dialogue", length = 65535)
	@Size(max = 65535)
	@Override
	public String getInitDefeatedDialogue() {
		return this.initDefeatedDialogue;
	}

	@Override
	public TaskConfig setInitDefeatedDialogue(String initDefeatedDialogue) {
		this.initDefeatedDialogue = initDefeatedDialogue;
		return this;
	}

	@Column(name = "exp_reward", precision = 10)
	@Override
	public Integer getExpReward() {
		return this.expReward;
	}

	@Override
	public TaskConfig setExpReward(Integer expReward) {
		this.expReward = expReward;
		return this;
	}

	@Column(name = "board_id", precision = 10)
	@Override
	public Integer getBoardId() {
		return this.boardId;
	}

	@Override
	public TaskConfig setBoardId(Integer boardId) {
		this.boardId = boardId;
		return this;
	}

	// -------------------------------------------------------------------------
	// FROM and INTO
	// -------------------------------------------------------------------------

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void from(ITaskConfig from) {
		setId(from.getId());
		setGoodName(from.getGoodName());
		setDescription(from.getDescription());
		setCityId(from.getCityId());
		setAssetNumWithinCity(from.getAssetNumWithinCity());
		setPrerequisiteTaskId(from.getPrerequisiteTaskId());
		setPrerequisiteQuestId(from.getPrerequisiteQuestId());
		setBoardWidth(from.getBoardWidth());
		setBoardHeight(from.getBoardHeight());
		setGroundImgPrefix(from.getGroundImgPrefix());
		setInitDefeatedDialogue(from.getInitDefeatedDialogue());
		setExpReward(from.getExpReward());
		setBoardId(from.getBoardId());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public <E extends ITaskConfig> E into(E into) {
		into.from(this);
		return into;
	}
}