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

import com.lvl6.info.SkillSideEffect;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class SkillSideEffectRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static Map<Integer, SkillSideEffect> idsToSkillSideEffects;

	private static final String TABLE_NAME = DBConstants.TABLE_SKILL_SIDE_EFFECT_CONFIG;

	public Map<Integer, SkillSideEffect> getIdsToSkillSideEffects() {
		log.debug("retrieving all SkillSideEffects data map");
		if (null == idsToSkillSideEffects) {
			setStaticIdsToSkillSideEffects();
		}
		return idsToSkillSideEffects;
	}

	public SkillSideEffect getSkillSideEffectForId(int skillSideEffectId) {
		log.debug(String.format("retrieve skill data for skill=%s",
				skillSideEffectId));
		if (null == idsToSkillSideEffects) {
			setStaticIdsToSkillSideEffects();
		}
		return idsToSkillSideEffects.get(skillSideEffectId);
	}

	private void setStaticIdsToSkillSideEffects() {
		log.debug("setting static map of skillSideEffectIds to skillSideEffects");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		Map<Integer, SkillSideEffect> idsToSkillSideEffectsTemp = new HashMap<Integer, SkillSideEffect>();
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						//loop through each row and convert it into a java object
						while (rs.next()) {
							SkillSideEffect skillSideEffect = convertRSRowToSkillSideEffect(rs);
							if (skillSideEffect == null) {
								continue;
							}

							int skillSideEffectId = skillSideEffect.getId();
							idsToSkillSideEffectsTemp.put(skillSideEffectId,
									skillSideEffect);
						}

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
		idsToSkillSideEffects = idsToSkillSideEffectsTemp;
	}

	public void reload() {
		setStaticIdsToSkillSideEffects();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private SkillSideEffect convertRSRowToSkillSideEffect(ResultSet rs)
			throws SQLException {
		int id = rs.getInt(DBConstants.SKILL_SIDE_EFFECT__ID);
		String name = rs.getString(DBConstants.SKILL_SIDE_EFFECT__NAME);
		String desc = rs.getString(DBConstants.SKILL_SIDE_EFFECT__DESC);
		String type = rs.getString(DBConstants.SKILL_SIDE_EFFECT__TYPE);
		String traitType = rs
				.getString(DBConstants.SKILL_SIDE_EFFECT__TRAIT_TYPE);
		String imgName = rs.getString(DBConstants.SKILL_SIDE_EFFECT__IMG_NAME);
		int imgPixelOffsetX = rs
				.getInt(DBConstants.SKILL_SIDE_EFFECT__IMG_PIXEL_OFFSET_X);
		int imgPixelOffsetY = rs
				.getInt(DBConstants.SKILL_SIDE_EFFECT__IMG_PIXEL_OFFSET_Y);
		String iconImgName = rs
				.getString(DBConstants.SKILL_SIDE_EFFECT__ICON_IMG_NAME);
		String pfxName = rs.getString(DBConstants.SKILL_SIDE_EFFECT__PFX_NAME);
		String pfxColor = rs
				.getString(DBConstants.SKILL_SIDE_EFFECT__PFX_COLOR);
		String positionType = rs
				.getString(DBConstants.SKILL_SIDE_EFFECT__POSITION_TYPE);
		int pfxPixelOffsetX = rs
				.getInt(DBConstants.SKILL_SIDE_EFFECT__PFX_PIXEL_OFFSET_X);
		int pfxPixelOffsetY = rs
				.getInt(DBConstants.SKILL_SIDE_EFFECT__PFX_PIXEL_OFFSET_Y);
		String blendMode = rs
				.getString(DBConstants.SKILL_SIDE_EFFECT__BLEND_MODE);

		if (null != type) {
			String newType = type.trim().toUpperCase();
			if (!type.equals(newType)) {
				log.error(String.format("type incorrect: %s, id=%s", type, id));
				type = newType;
			}
		}

		if (null != traitType) {
			String newTraitType = traitType.trim().toUpperCase();
			if (!traitType.equals(newTraitType)) {
				log.error(String.format("traitType incorrect: %s, id=%s",
						traitType, id));
				traitType = newTraitType;
			}
		}

		SkillSideEffect skillSideEffect = new SkillSideEffect(id, name, desc,
				type, traitType, imgName, imgPixelOffsetX, imgPixelOffsetY,
				iconImgName, pfxName, pfxColor, positionType, pfxPixelOffsetX,
				pfxPixelOffsetY, blendMode);
		return skillSideEffect;
	}

}
