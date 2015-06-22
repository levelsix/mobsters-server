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
import com.lvl6.info.StructureTownHall;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class StructureTownHallRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(StructureTownHallRetrieveUtils.class);
	
	@Autowired
	StructureRetrieveUtils structureRetrieveUtils;

	private static Map<Integer, StructureTownHall> structIdsToTownHalls;

	private static final String TABLE_NAME = DBConstants.TABLE_STRUCTURE_TOWN_HALL_CONFIG;

	public Map<Integer, StructureTownHall> getStructIdsToTownHalls() {
		log.debug("retrieving all structs data");
		if (structIdsToTownHalls == null) {
			setStaticStructIdsToTownHalls();
		}
		return structIdsToTownHalls;
	}

	//  public static StructureTownHall getTownHallRequiredForStructId(int structId) {
	//    log.debug("retrieve struct data for structId " + structId);
	//    if (structIdsToTownHalls == null) {
	//      setStaticStructIdsToTownHalls();      
	//    }
	//    return structIdsToTownHalls.get(structId);
	//  }

	public StructureTownHall getUpgradedTownHallForStructId(int structId) {
		log.debug("retrieve upgraded struct data for structId " + structId);
		if (structIdsToTownHalls == null) {
			setStaticStructIdsToTownHalls();
		}
		Structure curStruct = structureRetrieveUtils
				.getUpgradedStructForStructId(structId);
		if (null != curStruct) {
			int successorStructId = curStruct.getId();
			StructureTownHall upgradedStruct = structIdsToTownHalls
					.get(successorStructId);
			return upgradedStruct;
		}
		return null;
	}

	public StructureTownHall getPredecessorTownHallForStructId(
			int structId) {
		log.debug("retrieve predecessor struct data for structId " + structId);
		if (structIdsToTownHalls == null) {
			setStaticStructIdsToTownHalls();
		}
		Structure curStruct = structureRetrieveUtils
				.getUpgradedStructForStructId(structId);
		if (null != curStruct) {
			int predecessorStructId = curStruct.getId();
			StructureTownHall predecessorStruct = structIdsToTownHalls
					.get(predecessorStructId);
			return predecessorStruct;
		}
		return null;
	}

	private void setStaticStructIdsToTownHalls() {
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
						HashMap<Integer, StructureTownHall> structIdsToStructsTemp = new HashMap<Integer, StructureTownHall>();
						while (rs.next()) {
							StructureTownHall struct = convertRSRowToTownHall(rs);
							if (struct != null)
								structIdsToStructsTemp.put(
										struct.getStructId(), struct);
						}
						structIdsToTownHalls = structIdsToStructsTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("TownHall retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public void reload() {
		setStaticStructIdsToTownHalls();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private StructureTownHall convertRSRowToTownHall(ResultSet rs)
			throws SQLException {
		int structId = rs.getInt(DBConstants.STRUCTURE_TOWN_HALL__STRUCT_ID);
		int numResourceOneGenerators = rs
				.getInt(DBConstants.STRUCTURE_TOWN_HALL__NUM_RESOURCE_ONE_GENERATORS);
		int numResourceOneStorages = rs
				.getInt(DBConstants.STRUCTURE_TOWN_HALL__NUM_RESOURCE_ONE_STORAGES);
		int numResourceTwoGenerators = rs
				.getInt(DBConstants.STRUCTURE_TOWN_HALL__NUM_RESOURCE_TWO_GENERATORS);
		int numResourceTwoStorages = rs
				.getInt(DBConstants.STRUCTURE_TOWN_HALL__NUM_RESOURCE_TWO_STORAGES);
		int numHospitals = rs
				.getInt(DBConstants.STRUCTURE_TOWN_HALL__NUM_HOSPITALS);
		int numResidences = rs
				.getInt(DBConstants.STRUCTURE_TOWN_HALL__NUM_RESIDENCES);
		int numMonsterSlots = rs
				.getInt(DBConstants.STRUCTURE_TOWN_HALL__NUM_MONSTER_SLOTS);
		int numLabs = rs.getInt(DBConstants.STRUCTURE_TOWN_HALL__NUM_LABS);
		int pvpQueueCashCost = rs
				.getInt(DBConstants.STRUCTURE_TOWN_HALL__PVP_QUEUE_CASH_COST);
		int resourceCapacity = rs
				.getInt(DBConstants.STRUCTURE_TOWN_HALL__RESOURCE_CAPACITY);
		int numEvoChambers = rs
				.getInt(DBConstants.STRUCTURE_TOWN_HALL__NUM_EVO_CHAMBERS);

		return new StructureTownHall(structId, numResourceOneGenerators,
				numResourceOneStorages, numResourceTwoGenerators,
				numResourceTwoStorages, numHospitals, numResidences,
				numMonsterSlots, numLabs, pvpQueueCashCost, resourceCapacity,
				numEvoChambers);
	}
}
