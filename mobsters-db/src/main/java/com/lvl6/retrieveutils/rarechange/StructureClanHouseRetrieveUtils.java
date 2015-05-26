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
import com.lvl6.info.StructureClanHouse;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class StructureClanHouseRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());
	
	@Autowired
	protected StructureRetrieveUtils structureRetrieveUtils;

	private static Map<Integer, StructureClanHouse> structIdsToClanHouses;

	private static final String TABLE_NAME = DBConstants.TABLE_STRUCTURE_CLAN_HOUSE_CONFIG;

	public Map<Integer, StructureClanHouse> getStructIdsToClanHouses() {
		log.debug("retrieving all structs data");
		if (structIdsToClanHouses == null) {
			setStaticStructIdsToClanHouses();
		}
		return structIdsToClanHouses;
	}

	public StructureClanHouse getClanHouseForStructId(int structId) {
		log.debug("retrieve struct data for structId " + structId);
		if (structIdsToClanHouses == null) {
			setStaticStructIdsToClanHouses();
		}
		return structIdsToClanHouses.get(structId);
	}

	public StructureClanHouse getUpgradedClanHouseForStructId(
			int structId) {
		log.debug("retrieve upgraded struct data for structId " + structId);
		if (structIdsToClanHouses == null) {
			setStaticStructIdsToClanHouses();
		}
		Structure curStruct = structureRetrieveUtils
				.getUpgradedStructForStructId(structId);
		if (null != curStruct) {
			int successorStructId = curStruct.getId();
			StructureClanHouse upgradedStruct = structIdsToClanHouses
					.get(successorStructId);
			return upgradedStruct;
		}
		return null;
	}

	public StructureClanHouse getPredecessorClanHouseForStructId(
			int structId) {
		log.debug("retrieve predecessor struct data for structId " + structId);
		if (structIdsToClanHouses == null) {
			setStaticStructIdsToClanHouses();
		}
		Structure curStruct = structureRetrieveUtils
				.getUpgradedStructForStructId(structId);
		if (null != curStruct) {
			int predecessorStructId = curStruct.getId();
			StructureClanHouse predecessorStruct = structIdsToClanHouses
					.get(predecessorStructId);
			return predecessorStruct;
		}
		return null;
	}

	private void setStaticStructIdsToClanHouses() {
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
						HashMap<Integer, StructureClanHouse> structIdsToStructsTemp = new HashMap<Integer, StructureClanHouse>();
						while (rs.next()) {
							StructureClanHouse struct = convertRSRowToClanHouse(rs);
							if (struct != null)
								structIdsToStructsTemp.put(
										struct.getStructId(), struct);
						}
						structIdsToClanHouses = structIdsToStructsTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("ClanHouse retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public void reload() {
		setStaticStructIdsToClanHouses();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private StructureClanHouse convertRSRowToClanHouse(ResultSet rs)
			throws SQLException {
		int structId = rs.getInt(DBConstants.STRUCTURE_CLAN_HOUSE__STRUCT_ID);
		int maxHelpersPerSolicitation = rs
				.getInt(DBConstants.STRUCTURE_CLAN_HOUSE__MAX_HELPERS_PER_SOLICITATION);
		int teamDonationPowerLimit = rs
				.getInt(DBConstants.STRUCTURE_CLAN_HOUSE__TEAM_DONATION_POWER_LIMIT);

		return new StructureClanHouse(structId, maxHelpersPerSolicitation,
				teamDonationPowerLimit);
	}
}
