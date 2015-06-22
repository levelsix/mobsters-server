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

import com.lvl6.info.Skill;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class SkillRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(SkillRetrieveUtils.class);

	private static Map<Integer, Skill> idsToSkills;

	private static final String TABLE_NAME = DBConstants.TABLE_SKILL_CONFIG;

	public Map<Integer, Skill> getIdsToSkills() {
		log.debug("retrieving all Skills data map");
		if (null == idsToSkills) {
			setStaticIdsToSkills();
		}
		return idsToSkills;
	}

	public Skill getSkillForId(int skillId) {
		log.debug(String.format("retrieve skill data for skill=%s", skillId));
		if (null == idsToSkills) {
			setStaticIdsToSkills();
		}
		return idsToSkills.get(skillId);
	}

	private void setStaticIdsToSkills() {
		log.debug("setting static map of skillIds to skills");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						Map<Integer, Skill> idsToSkillsTemp = new HashMap<Integer, Skill>();
						//loop through each row and convert it into a java object
						while (rs.next()) {
							Skill skill = convertRSRowToSkill(rs);
							if (skill == null) {
								continue;
							}

							int skillId = skill.getId();
							idsToSkillsTemp.put(skillId, skill);
						}
						idsToSkills = idsToSkillsTemp;

					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("skill retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public void reload() {
		setStaticIdsToSkills();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private Skill convertRSRowToSkill(ResultSet rs) throws SQLException {
		int id = rs.getInt(DBConstants.SKILL__ID);
		String name = rs.getString(DBConstants.SKILL__NAME);
		int orbCost = rs.getInt(DBConstants.SKILL__ORB_COST);
		String type = rs.getString(DBConstants.SKILL__TYPE);
		String activationType = rs
				.getString(DBConstants.SKILL__ACTIVATION_TYPE);
		String defDesc = rs.getString(DBConstants.SKILL__DEFENSIVE_DESC);
		String offDesc = rs.getString(DBConstants.SKILL__OFFENSIVE_DESC);
		String imgNamePrefix = rs.getString(DBConstants.SKILL__IMG_NAME_PREFIX);
		//		String iconImgName = rs.getString(DBConstants.SKILL__ICON_IMG_NAME);
		//		String logoImgName = rs.getString(DBConstants.SKILL__LOGO_IMG_NAME);
		int skillEffectDuration = rs
				.getInt(DBConstants.SKILL__SKILL_EFFECT_DURATION);
		String shortDefDesc = rs.getString(DBConstants.SKILL__SHORT_DEF_DESC);
		String shortOffDesc = rs.getString(DBConstants.SKILL__SHORT_OFF_DESC);

		int predecId = rs.getInt(DBConstants.SKILL__PREDEC_ID);
		if (rs.wasNull()) {
			predecId = 0;
		}

		int succId = rs.getInt(DBConstants.SKILL__SUCC_ID);
		if (rs.wasNull()) {
			succId = 0;
		}

		if (null != type) {
			String newType = type.trim().toUpperCase();
			if (!type.equals(newType)) {
				log.error(String.format("type incorrect: %s, id=%s", type, id));
				type = newType;
			}
		}

		if (null != activationType) {
			String newActivationType = activationType.trim().toUpperCase();
			if (!activationType.equals(newActivationType)) {
				log.error(String.format("activationType incorrect: %s, id=%s",
						activationType, id));
				activationType = newActivationType;
			}
		}

		Skill skill = new Skill(id, name, orbCost, type, activationType,
				predecId, succId, defDesc, offDesc, imgNamePrefix,
				skillEffectDuration, shortDefDesc, shortOffDesc);//, logoImgName);
		return skill;
	}

}
