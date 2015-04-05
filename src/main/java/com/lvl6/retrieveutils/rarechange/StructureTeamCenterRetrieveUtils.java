package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.Structure;
import com.lvl6.info.StructureTeamCenter;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class StructureTeamCenterRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());
	
	@Autowired
	protected StructureRetrieveUtils structureRetrieveUtils;

	private static Map<Integer, StructureTeamCenter> structIdsToTeamCenters;

	private static final String TABLE_NAME = DBConstants.TABLE_STRUCTURE_TEAM_CENTER_CONFIG;

	public Map<Integer, StructureTeamCenter> getStructIdsToTeamCenters() {
		log.debug("retrieving all structs data");
		if (structIdsToTeamCenters == null) {
			setStaticStructIdsToTeamCenters();
		}
		return structIdsToTeamCenters;
	}

	public StructureTeamCenter getTeamCenterForStructId(int structId) {
		log.debug("retrieve struct data for structId " + structId);
		if (structIdsToTeamCenters == null) {
			setStaticStructIdsToTeamCenters();
		}
		return structIdsToTeamCenters.get(structId);
	}

	public StructureTeamCenter getUpgradedTeamCenterForStructId(
			int structId) {
		log.debug("retrieve upgraded struct data for structId " + structId);
		if (structIdsToTeamCenters == null) {
			setStaticStructIdsToTeamCenters();
		}
		Structure curStruct = structureRetrieveUtils
				.getUpgradedStructForStructId(structId);
		if (null != curStruct) {
			int successorStructId = curStruct.getId();
			StructureTeamCenter upgradedStruct = structIdsToTeamCenters
					.get(successorStructId);
			return upgradedStruct;
		}
		return null;
	}

	public StructureTeamCenter getPredecessorTeamCenterForStructId(
			int structId) {
		log.debug("retrieve predecessor struct data for structId " + structId);
		if (structIdsToTeamCenters == null) {
			setStaticStructIdsToTeamCenters();
		}
		Structure curStruct = structureRetrieveUtils
				.getUpgradedStructForStructId(structId);
		if (null != curStruct) {
			int predecessorStructId = curStruct.getId();
			StructureTeamCenter predecessorStruct = structIdsToTeamCenters
					.get(predecessorStructId);
			return predecessorStruct;
		}
		return null;
	}

	private void setStaticStructIdsToTeamCenters() {
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
						HashMap<Integer, StructureTeamCenter> structIdsToStructsTemp = new HashMap<Integer, StructureTeamCenter>();
						while (rs.next()) {
							StructureTeamCenter struct = convertRSRowToTeamCenter(rs);
							if (struct != null)
								structIdsToStructsTemp.put(
										struct.getStructId(), struct);
						}
						structIdsToTeamCenters = structIdsToStructsTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("TeamCenter retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public void reload() {
		setStaticStructIdsToTeamCenters();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private StructureTeamCenter convertRSRowToTeamCenter(ResultSet rs)
			throws SQLException {
		int structId = rs.getInt(DBConstants.STRUCTURE_TEAM_CENTER__STRUCT_ID);
		int teamCostLimit = rs
				.getInt(DBConstants.STRUCTURE_TEAM_CENTER__TEAM_COST_LIMIT);

		return new StructureTeamCenter(structId, teamCostLimit);
	}
}
