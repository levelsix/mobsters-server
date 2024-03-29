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

import com.lvl6.info.Research;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class ResearchRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(ResearchRetrieveUtils.class);

	private static Map<Integer, Research> idsToResearch;

	private static final String TABLE_NAME = DBConstants.TABLE_RESEARCH_CONFIG;

	public Map<Integer, Research> getIdsToResearch() {
		log.debug("retrieving all Research data map");
		if (null == idsToResearch) {
			setStaticIdsToResearch();
		}
		return idsToResearch;
	}

	public Research getResearchForId(int researchId) {
		log.debug(String.format("retrieve research data for research=%s",
				researchId));
		if (null == idsToResearch) {
			setStaticIdsToResearch();
		}
		return idsToResearch.get(researchId);
	}

	private void setStaticIdsToResearch() {
		log.debug("setting static map of researchIds to researchs");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						Map<Integer, Research> idsToResearchTemp = new HashMap<Integer, Research>();
						//loop through each row and convert it into a java object
						while (rs.next()) {
							Research research = convertRSRowToResearch(rs);
							if (research == null) {
								continue;
							}

							int researchId = research.getId();
							idsToResearchTemp.put(researchId, research);
						}
						idsToResearch = idsToResearchTemp;

					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("research retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public void reload() {
		setStaticIdsToResearch();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private Research convertRSRowToResearch(ResultSet rs)
			throws SQLException {
		int id = rs.getInt(DBConstants.RESEARCH__ID);
		String type = rs.getString(DBConstants.RESEARCH__RESEARCH_TYPE);
		String domain = rs.getString(DBConstants.RESEARCH__RESEARCH_DOMAIN);
		String iconImgName = rs.getString(DBConstants.RESEARCH__ICON_IMG_NAME);
		String name = rs.getString(DBConstants.RESEARCH__NAME);
		String desc = rs.getString(DBConstants.RESEARCH__DESC);
		int durationMin = rs.getInt(DBConstants.RESEARCH__DURATION_MIN);
		int costAmt = rs.getInt(DBConstants.RESEARCH__COST_AMT);
		String costType = rs.getString(DBConstants.RESEARCH__COST_TYPE);
		int level = rs.getInt(DBConstants.RESEARCH__LEVEL);
		float priority = rs.getFloat(DBConstants.RESEARCH__PRIORITY);
		int tier = rs.getInt(DBConstants.RESEARCH__TIER);

		int predId = rs.getInt(DBConstants.RESEARCH__PRED_ID);
		if (rs.wasNull()) {
			predId = 0;
		}

		int succId = rs.getInt(DBConstants.RESEARCH__SUCC_ID);
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

		if (null != costType) {
			String newCostType = costType.trim().toUpperCase();
			if (!costType.equals(newCostType)) {
				log.error(String.format("CostType incorrect: %s, id=%s",
						costType, id));
				costType = newCostType;
			}
		}
		
		int strength = rs.getInt(DBConstants.RESEARCH__STRENGTH);

		Research research = new Research(id, type, domain, iconImgName, name,
				predId, succId, desc, durationMin, costAmt, costType, level, 
				priority, tier, strength);
		return research;
	}

}
