/**
 * This class is generated by jOOQ
 */
package com.lvl6.mobsters.db.jooq.generated.tables.daos;


import com.lvl6.mobsters.db.jooq.generated.tables.MonsterBattleDialogueConfig;
import com.lvl6.mobsters.db.jooq.generated.tables.records.MonsterBattleDialogueConfigRecord;

import java.util.List;

import javax.annotation.Generated;

import org.jooq.Configuration;
import org.jooq.impl.DAOImpl;


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
public class MonsterBattleDialogueConfigDao extends DAOImpl<MonsterBattleDialogueConfigRecord, com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterBattleDialogueConfig, Integer> {

	/**
	 * Create a new MonsterBattleDialogueConfigDao without any configuration
	 */
	public MonsterBattleDialogueConfigDao() {
		super(MonsterBattleDialogueConfig.MONSTER_BATTLE_DIALOGUE_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterBattleDialogueConfig.class);
	}

	/**
	 * Create a new MonsterBattleDialogueConfigDao with an attached configuration
	 */
	public MonsterBattleDialogueConfigDao(Configuration configuration) {
		super(MonsterBattleDialogueConfig.MONSTER_BATTLE_DIALOGUE_CONFIG, com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterBattleDialogueConfig.class, configuration);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Integer getId(com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterBattleDialogueConfig object) {
		return object.getId();
	}

	/**
	 * Fetch records that have <code>id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterBattleDialogueConfig> fetchById(Integer... values) {
		return fetch(MonsterBattleDialogueConfig.MONSTER_BATTLE_DIALOGUE_CONFIG.ID, values);
	}

	/**
	 * Fetch a unique record that has <code>id = value</code>
	 */
	public com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterBattleDialogueConfig fetchOneById(Integer value) {
		return fetchOne(MonsterBattleDialogueConfig.MONSTER_BATTLE_DIALOGUE_CONFIG.ID, value);
	}

	/**
	 * Fetch records that have <code>monster_id IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterBattleDialogueConfig> fetchByMonsterId(Integer... values) {
		return fetch(MonsterBattleDialogueConfig.MONSTER_BATTLE_DIALOGUE_CONFIG.MONSTER_ID, values);
	}

	/**
	 * Fetch records that have <code>dialogue_type IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterBattleDialogueConfig> fetchByDialogueType(String... values) {
		return fetch(MonsterBattleDialogueConfig.MONSTER_BATTLE_DIALOGUE_CONFIG.DIALOGUE_TYPE, values);
	}

	/**
	 * Fetch records that have <code>dialogue IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterBattleDialogueConfig> fetchByDialogue(String... values) {
		return fetch(MonsterBattleDialogueConfig.MONSTER_BATTLE_DIALOGUE_CONFIG.DIALOGUE, values);
	}

	/**
	 * Fetch records that have <code>probability_uttered IN (values)</code>
	 */
	public List<com.lvl6.mobsters.db.jooq.generated.tables.pojos.MonsterBattleDialogueConfig> fetchByProbabilityUttered(Double... values) {
		return fetch(MonsterBattleDialogueConfig.MONSTER_BATTLE_DIALOGUE_CONFIG.PROBABILITY_UTTERED, values);
	}
}