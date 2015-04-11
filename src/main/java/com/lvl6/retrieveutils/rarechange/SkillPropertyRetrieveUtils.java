package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.SkillProperty;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class SkillPropertyRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static Map<Integer, Map<Integer, SkillProperty>> skillIdsToIdsToSkillPropertys;
	private static Map<Integer, SkillProperty> skillPropertyIdsToSkillPropertys;

	private static final String TABLE_NAME = DBConstants.TABLE_SKILL_PROPERTY_CONFIG;

	public static Map<Integer, Map<Integer, SkillProperty>> getSkillIdsToIdsToSkillPropertys() {
		log.debug("retrieving all skillIds to SkillProperty data map");
		if (null == skillIdsToIdsToSkillPropertys) {
			setStaticSkillIdsToIdsToSkillPropertys();
		}
		return skillIdsToIdsToSkillPropertys;
	}

	public static SkillProperty getSkillPropertyForSkillPropertyId(
			int skillPropertyId) {
		if (null == skillPropertyIdsToSkillPropertys) {
			setStaticSkillIdsToIdsToSkillPropertys();
		}
		if (!skillPropertyIdsToSkillPropertys.containsKey(skillPropertyId)) {
			log.warn(String.format("no skillProperty for skillPropertyId=%s",
					skillPropertyId));
			return null;
		}
		return skillPropertyIdsToSkillPropertys.get(skillPropertyId);
	}

	public static Map<Integer, SkillProperty> getSkillPropertysForSkillId(
			int skillId) {
		log.debug(String.format("retrieve skill data for skill=%s", skillId));
		if (null == skillIdsToIdsToSkillPropertys) {
			setStaticSkillIdsToIdsToSkillPropertys();
		}
		return skillIdsToIdsToSkillPropertys.get(skillId);
	}

	private static void setStaticSkillIdsToIdsToSkillPropertys() {
		log.debug("setting static map of skillIds to skillProperties");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						Map<Integer, Map<Integer, SkillProperty>> skillIdsToIdsToSkillPropertysTemp = new HashMap<Integer, Map<Integer, SkillProperty>>();
						Map<Integer, SkillProperty> skillPropertyIdsToSkillPropertysTemp = new HashMap<Integer, SkillProperty>();
						//loop through each row and convert it into a java object
						while (rs.next()) {
							SkillProperty skillProperty = convertRSRowToSkillProperty(rs);
							if (skillProperty == null) {
								continue;
							}

							int skillId = skillProperty.getSkillId();
							//base case, no key with skill id exists, so create map with
							//key: skill id, to value: another map
							if (!skillIdsToIdsToSkillPropertysTemp
									.containsKey(skillId)) {
								skillIdsToIdsToSkillPropertysTemp.put(skillId,
										new HashMap<Integer, SkillProperty>());
							}

							//get map of skillProperties related to current skill id
							//stick skillProperty into the map of SkillProperty ids to SkillProperty objects
							Map<Integer, SkillProperty> idsToSkillPropertys = skillIdsToIdsToSkillPropertysTemp
									.get(skillId);

							int skillPropertyId = skillProperty.getId();
							idsToSkillPropertys.put(skillPropertyId,
									skillProperty);
							skillPropertyIdsToSkillPropertysTemp.put(
									skillPropertyId, skillProperty);
						}
						skillIdsToIdsToSkillPropertys = skillIdsToIdsToSkillPropertysTemp;
						skillPropertyIdsToSkillPropertys = skillPropertyIdsToSkillPropertysTemp;

					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("skill property retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public static void reload() {
		setStaticSkillIdsToIdsToSkillPropertys();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private static SkillProperty convertRSRowToSkillProperty(ResultSet rs)
			throws SQLException {
		int id = rs.getInt(DBConstants.SKILL_PROPERTY__ID);
		String name = rs.getString(DBConstants.SKILL_PROPERTY__NAME);
		float value = rs.getFloat(DBConstants.SKILL_PROPERTY__VALUE);
		int skillId = rs.getInt(DBConstants.SKILL_PROPERTY__SKILL_ID);

		SkillProperty skillProperty = new SkillProperty(id, name, value,
				skillId);

		return skillProperty;
	}
}
