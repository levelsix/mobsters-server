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
import com.lvl6.info.StructureHospital;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class StructureHospitalRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());
	
	@Autowired
	protected StructureRetrieveUtils structureRetrieveUtils;

	private static Map<Integer, StructureHospital> structIdsToHospitals;

	private static final String TABLE_NAME = DBConstants.TABLE_STRUCTURE_HOSPITAL_CONFIG;

	public Map<Integer, StructureHospital> getStructIdsToHospitals() {
		log.debug("retrieving all structs data");
		if (structIdsToHospitals == null) {
			setStaticStructIdsToHospitals();
		}
		return structIdsToHospitals;
	}

	public StructureHospital getHospitalForStructId(int structId) {
		log.debug("retrieve struct data for structId " + structId);
		if (structIdsToHospitals == null) {
			setStaticStructIdsToHospitals();
		}
		return structIdsToHospitals.get(structId);
	}

	public StructureHospital getUpgradedHospitalForStructId(int structId) {
		log.debug("retrieve upgraded struct data for structId " + structId);
		if (structIdsToHospitals == null) {
			setStaticStructIdsToHospitals();
		}
		Structure curStruct = structureRetrieveUtils
				.getUpgradedStructForStructId(structId);
		if (null != curStruct) {
			int successorStructId = curStruct.getId();
			StructureHospital upgradedStruct = structIdsToHospitals
					.get(successorStructId);
			return upgradedStruct;
		}
		return null;
	}

	public StructureHospital getPredecessorHospitalForStructId(
			int structId) {
		log.debug("retrieve predecessor struct data for structId " + structId);
		if (structIdsToHospitals == null) {
			setStaticStructIdsToHospitals();
		}
		Structure curStruct = structureRetrieveUtils
				.getUpgradedStructForStructId(structId);
		if (null != curStruct) {
			int predecessorStructId = curStruct.getId();
			StructureHospital predecessorStruct = structIdsToHospitals
					.get(predecessorStructId);
			return predecessorStruct;
		}
		return null;
	}

	private void setStaticStructIdsToHospitals() {
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
						HashMap<Integer, StructureHospital> structIdsToStructsTemp = new HashMap<Integer, StructureHospital>();
						while (rs.next()) {
							StructureHospital struct = convertRSRowToHospital(rs);
							if (struct != null)
								structIdsToStructsTemp.put(
										struct.getStructId(), struct);
						}
						structIdsToHospitals = structIdsToStructsTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("Hospital retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public void reload() {
		setStaticStructIdsToHospitals();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private StructureHospital convertRSRowToHospital(ResultSet rs)
			throws SQLException {
		int structId = rs.getInt(DBConstants.STRUCTURE_HOSPITAL__STRUCT_ID);
		int queueSize = rs.getInt(DBConstants.STRUCTURE_HOSPITAL__QUEUE_SIZE);
		float healthPerSecond = rs
				.getFloat(DBConstants.STRUCTURE_HOSPITAL__HEALTH_PER_SECOND);
		float secsToFullyHealMultiplier = rs
				.getFloat(DBConstants.STRUCTURE_HOSPITAL__SECS_TO_FULLY_HEAL_MULTIPLIER);

		return new StructureHospital(structId, queueSize, healthPerSecond,
				secsToFullyHealMultiplier);
	}
}
