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

import com.lvl6.info.StructureResearchHouse;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class StructureResearchHouseRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static Map<Integer, StructureResearchHouse> structIdsToResearchHouses;

	private static final String TABLE_NAME = DBConstants.TABLE_STRUCTURE_RESEARCH_HOUSE_CONFIG;

	public Map<Integer, StructureResearchHouse> getStructIdsToResearchHouses() {
		log.debug("retrieving all structs data");
		if (structIdsToResearchHouses == null) {
			setStaticStructIdsToResearchHouses();
		}
		return structIdsToResearchHouses;
	}

	public StructureResearchHouse getResearchHouseForStructId(
			int structId) {
		log.debug("retrieve struct data for structId " + structId);
		if (structIdsToResearchHouses == null) {
			setStaticStructIdsToResearchHouses();
		}
		return structIdsToResearchHouses.get(structId);
	}

	private void setStaticStructIdsToResearchHouses() {
		log.debug("setting static map of structIds to structs");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						HashMap<Integer, StructureResearchHouse> structIdsToStructsTemp = 
								new HashMap<Integer, StructureResearchHouse>();
						while (rs.next()) {
							StructureResearchHouse struct = convertRSRowToResearchHouse(rs);
							if (struct != null)
								structIdsToStructsTemp.put(
										struct.getStructId(), struct);
						}
						structIdsToResearchHouses = structIdsToStructsTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("resourceGenerator retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public void reload() {
		setStaticStructIdsToResearchHouses();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private StructureResearchHouse convertRSRowToResearchHouse(
			ResultSet rs) throws SQLException {
		int structId = rs
				.getInt(DBConstants.STRUCTURE_RESEARCH_HOUSE__STRUCT_ID);
		float researchSpeedMultiplier = rs.getFloat(DBConstants.
				STRUCTURE_RESEARCH_HOUSE__RESEARCH_SPEED_MULTIPLIER);
		StructureResearchHouse srh = new StructureResearchHouse(structId, researchSpeedMultiplier);
		return srh;
	}
}
