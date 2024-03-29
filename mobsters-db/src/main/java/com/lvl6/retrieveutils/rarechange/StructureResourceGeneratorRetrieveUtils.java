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
import com.lvl6.info.StructureResourceGenerator;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class StructureResourceGeneratorRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(StructureResourceGeneratorRetrieveUtils.class);
	
	@Autowired
	protected StructureRetrieveUtils structureRetrieveUtils;

	private static Map<Integer, StructureResourceGenerator> structIdsToResourceGenerators;

	private static final String TABLE_NAME = DBConstants.TABLE_STRUCTURE_RESOURCE_GENERATOR_CONFIG;

	public Map<Integer, StructureResourceGenerator> getStructIdsToResourceGenerators() {
		log.debug("retrieving all structs data");
		if (structIdsToResourceGenerators == null) {
			setStaticStructIdsToResourceGenerators();
		}
		return structIdsToResourceGenerators;
	}

	public StructureResourceGenerator getResourceGeneratorForStructId(
			int structId) {
		log.debug("retrieve struct data for structId " + structId);
		if (structIdsToResourceGenerators == null) {
			setStaticStructIdsToResourceGenerators();
		}
		return structIdsToResourceGenerators.get(structId);
	}

	public StructureResourceGenerator getUpgradedResourceGeneratorForStructId(
			int structId) {
		log.debug("retrieve upgraded struct data for structId " + structId);
		if (structIdsToResourceGenerators == null) {
			setStaticStructIdsToResourceGenerators();
		}
		Structure curStruct = structureRetrieveUtils
				.getUpgradedStructForStructId(structId);
		if (null != curStruct) {
			int successorStructId = curStruct.getId();
			StructureResourceGenerator upgradedStruct = structIdsToResourceGenerators
					.get(successorStructId);
			return upgradedStruct;
		}
		return null;
	}

	public StructureResourceGenerator getPredecessorResourceGeneratorForStructId(
			int structId) {
		log.debug("retrieve predecessor struct data for structId " + structId);
		if (structIdsToResourceGenerators == null) {
			setStaticStructIdsToResourceGenerators();
		}
		Structure curStruct = structureRetrieveUtils
				.getUpgradedStructForStructId(structId);
		if (null != curStruct) {
			int predecessorStructId = curStruct.getId();
			StructureResourceGenerator predecessorStruct = structIdsToResourceGenerators
					.get(predecessorStructId);
			return predecessorStruct;
		}
		return null;
	}

	private void setStaticStructIdsToResourceGenerators() {
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
						HashMap<Integer, StructureResourceGenerator> structIdsToStructsTemp = new HashMap<Integer, StructureResourceGenerator>();
						while (rs.next()) {
							StructureResourceGenerator struct = convertRSRowToResourceGenerator(rs);
							if (struct != null)
								structIdsToStructsTemp.put(
										struct.getStructId(), struct);
						}
						structIdsToResourceGenerators = structIdsToStructsTemp;
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
		setStaticStructIdsToResourceGenerators();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private StructureResourceGenerator convertRSRowToResourceGenerator(
			ResultSet rs) throws SQLException {
		int structId = rs
				.getInt(DBConstants.STRUCTURE_RESOURCE_GENERATOR__STRUCT_ID);
		String resourceTypeGenerated = rs
				.getString(DBConstants.STRUCTURE_RESOURCE_GENERATOR__RESOURCE_TYPE_GENERATED);
		float productionRate = rs
				.getFloat(DBConstants.STRUCTURE_RESOURCE_GENERATOR__PRODUCTION_RATE);
		int capacity = rs
				.getInt(DBConstants.STRUCTURE_RESOURCE_GENERATOR__CAPACITY);

		if (null != resourceTypeGenerated) {
			String newResourceTypeGenerated = resourceTypeGenerated.trim()
					.toUpperCase();
			if (!resourceTypeGenerated.equals(newResourceTypeGenerated)) {
				log.error(String.format(
						"incorrect ResourceType. %s, structId=%s",
						resourceTypeGenerated, structId));
				resourceTypeGenerated = newResourceTypeGenerated;
			}
		}

		StructureResourceGenerator srg = new StructureResourceGenerator(
				structId, resourceTypeGenerated, productionRate, capacity);

		return srg;
	}
}
