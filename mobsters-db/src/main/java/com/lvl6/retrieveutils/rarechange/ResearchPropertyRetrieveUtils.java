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

import com.lvl6.info.ResearchProperty;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class ResearchPropertyRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static Map<Integer, Map<Integer, ResearchProperty>> researchIdsToIdsToResearchProperties;
	private static Map<Integer, ResearchProperty> researchPropertyIdsToResearchProperties;

	private static final String TABLE_NAME = DBConstants.TABLE_RESEARCH_PROPERTY_CONFIG;

	public Map<Integer, Map<Integer, ResearchProperty>> getResearchIdsToIdsToResearchProperties() {
		log.debug("retrieving all researchIds to ResearchProperty data map");
		if (null == researchIdsToIdsToResearchProperties) {
			setStaticResearchIdsToIdsToResearchProperties();
		}
		return researchIdsToIdsToResearchProperties;
	}

	public ResearchProperty getResearchPropertyForResearchPropertyId(
			int researchPropertyId) {
		if (null == researchPropertyIdsToResearchProperties) {
			setStaticResearchIdsToIdsToResearchProperties();
		}
		if (!researchPropertyIdsToResearchProperties
				.containsKey(researchPropertyId)) {
			log.warn(String.format(
					"no researchProperty for researchPropertyId=%s",
					researchPropertyId));
			return null;
		}
		return researchPropertyIdsToResearchProperties.get(researchPropertyId);
	}

	public Map<Integer, ResearchProperty> getResearchPropertiesForResearchId(
			int researchId) {
		log.debug(String.format("retrieve research data for research=%s",
				researchId));
		if (null == researchIdsToIdsToResearchProperties) {
			setStaticResearchIdsToIdsToResearchProperties();
		}
		return researchIdsToIdsToResearchProperties.get(researchId);
	}

	private void setStaticResearchIdsToIdsToResearchProperties() {
		log.debug("setting static map of researchIds to researchProperties");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						//<researchId, <researchPropertyId, researchProperty>>
						Map<Integer, Map<Integer, ResearchProperty>> researchIdsToIdsToResearchPropertiesTemp = new HashMap<Integer, Map<Integer, ResearchProperty>>();
						//<researchPropertyId, researchProperty>
						Map<Integer, ResearchProperty> researchPropertyIdsToResearchPropertiesTemp = new HashMap<Integer, ResearchProperty>();
						//loop through each row and convert it into a java object
						while (rs.next()) {
							ResearchProperty researchProperty = convertRSRowToResearchProperty(rs);
							if (researchProperty == null) {
								continue;
							}

							int researchId = researchProperty.getResearchId();
							//base case, no key with research id exists, so create map with
							//key: research id, to value: another map
							if (!researchIdsToIdsToResearchPropertiesTemp
									.containsKey(researchId)) {
								researchIdsToIdsToResearchPropertiesTemp
										.put(researchId,
												new HashMap<Integer, ResearchProperty>());
							}

							//get map of researchProperties related to current research id
							//stick researchProperty into the map of ResearchProperty ids to ResearchProperty objects
							Map<Integer, ResearchProperty> idsToResearchProperties = researchIdsToIdsToResearchPropertiesTemp
									.get(researchId);

							int researchPropertyId = researchProperty.getId();
							idsToResearchProperties.put(researchPropertyId,
									researchProperty);
							researchPropertyIdsToResearchPropertiesTemp.put(
									researchPropertyId, researchProperty);
						}
						researchIdsToIdsToResearchProperties = researchIdsToIdsToResearchPropertiesTemp;
						researchPropertyIdsToResearchProperties = researchPropertyIdsToResearchPropertiesTemp;

					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("ResearchProperty retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public void reload() {
		setStaticResearchIdsToIdsToResearchProperties();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private ResearchProperty convertRSRowToResearchProperty(ResultSet rs)
			throws SQLException {
		int id = rs.getInt(DBConstants.RESEARCH_PROPERTY__ID);
		String name = rs.getString(DBConstants.RESEARCH_PROPERTY__NAME);
		float value = rs.getFloat(DBConstants.RESEARCH_PROPERTY__VALUE);
		int researchId = rs.getInt(DBConstants.RESEARCH_PROPERTY__RESEARCH_ID);

		ResearchProperty researchProperty = new ResearchProperty(id, name,
				value, researchId);

		return researchProperty;
	}
}
