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

import com.lvl6.info.Structure;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class StructureRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static Map<Integer, Structure> structIdsToStructs;

	private static final String TABLE_NAME = DBConstants.TABLE_STRUCTURE_CONFIG;

	public static Map<Integer, Structure> getStructIdsToStructs() {
		log.debug("retrieving all structs data");
		if (structIdsToStructs == null) {
			setStaticStructIdsToStructs();
		}
		return structIdsToStructs;
	}

	public static Structure getStructForStructId(int structId) {
		log.debug("retrieve struct data for structId " + structId);
		if (structIdsToStructs == null) {
			setStaticStructIdsToStructs();
		}
		return structIdsToStructs.get(structId);
	}

	public static Structure getUpgradedStructForStructId(int structId) {
		log.debug("retrieve upgraded struct data for structId " + structId);
		if (structIdsToStructs == null) {
			setStaticStructIdsToStructs();
		}
		Structure curStruct = structIdsToStructs.get(structId);
		if (null != curStruct) {
			int successorStructId = curStruct.getSuccessorStructId();

			Structure upgradedStruct = structIdsToStructs
					.get(successorStructId);
			return upgradedStruct;
		}
		return null;
	}

	public static Structure getPredecessorStructForStructId(int structId) {
		log.debug("retrieve predecessor struct data for structId " + structId);
		if (null == structIdsToStructs) {
			setStaticStructIdsToStructs();
		}
		Structure curStruct = structIdsToStructs.get(structId);
		if (null != curStruct) {
			int predecessorStructId = curStruct.getPredecessorStructId();

			Structure predecessorStruct = structIdsToStructs
					.get(predecessorStructId);
			return predecessorStruct;
		}
		return null;
	}

	//RECURSION!!!
	public static Structure getPredecessorStructForStructIdAndLvl(int structId,
			int lvl) {
		log.debug("retrieve predecessor struct data for structId=" + structId
				+ " and lvl=" + lvl);
		if (null == structIdsToStructs) {
			setStaticStructIdsToStructs();
		}
		//base case, structure does not have a predecessor with specified level "lvl"
		if (!structIdsToStructs.containsKey(structId)) {
			return null;
		}

		Structure curStruct = structIdsToStructs.get(structId);
		//second base case, we found a structure with specified level "lvl"
		int curLvl = curStruct.getLevel();
		if (curLvl == lvl) {
			return curStruct;
		}

		//recursive case, move on to the predecessor and see if its level matches "lvl"
		int predecessorStructId = curStruct.getPredecessorStructId();
		return getPredecessorStructForStructIdAndLvl(predecessorStructId, lvl);

	}

	private static void setStaticStructIdsToStructs() {
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
						HashMap<Integer, Structure> structIdsToStructsTemp = new HashMap<Integer, Structure>();
						while (rs.next()) {
							Structure struct = convertRSRowToStruct(rs);
							if (struct != null)
								structIdsToStructsTemp.put(struct.getId(),
										struct);
						}
						structIdsToStructs = structIdsToStructsTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("structure retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public static void reload() {
		setStaticStructIdsToStructs();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private static Structure convertRSRowToStruct(ResultSet rs)
			throws SQLException {
		int id = rs.getInt(DBConstants.STRUCTURE__ID);
		String name = rs.getString(DBConstants.STRUCTURE__NAME);
		int level = rs.getInt(DBConstants.STRUCTURE__LEVEL);
		String structType = rs.getString(DBConstants.STRUCTURE__STRUCT_TYPE);
		String buildResourceType = rs
				.getString(DBConstants.STRUCTURE__BUILD_RESOURCE_TYPE);
		int buildCost = rs.getInt(DBConstants.STRUCTURE__BUILD_COST);
		int minutesToBuild = rs.getInt(DBConstants.STRUCTURE__MINUTES_TO_BUILD);
		int width = rs.getInt(DBConstants.STRUCTURE__WIDTH);
		int height = rs.getInt(DBConstants.STRUCTURE__HEIGHT);
		int predecessorStructId = rs
				.getInt(DBConstants.STRUCTURE__PREDECESSOR_STRUCT_ID);
		int successorStructId = rs
				.getInt(DBConstants.STRUCTURE__SUCCESSOR_STRUCT_ID);
		String imgName = rs.getString(DBConstants.STRUCTURE__IMG_NAME);
		float imgVerticalPixelOffset = rs
				.getFloat(DBConstants.STRUCTURE__IMG_VERTICAL_PIXEL_OFFSET);
		float imgHorizontalPixelOffset = rs
				.getFloat(DBConstants.STRUCTURE__IMG_HORIZONTAL_PIXEL_OFFSET);
		String description = rs.getString(DBConstants.STRUCTURE__DESCRIPTION);
		String shortDescription = rs
				.getString(DBConstants.STRUCTURE__SHORT_DESCRIPTION);
		String shadowImgName = rs
				.getString(DBConstants.STRUCTURE__SHADOW_IMG_NAME);
		float shadowVerticalOffset = rs
				.getFloat(DBConstants.STRUCTURE__SHADOW_VERTICAL_OFFSET);
		float shadowHorizontalOffset = rs
				.getFloat(DBConstants.STRUCTURE__SHADOW_HORIZONTAL_OFFSET);
		float shadowScale = rs.getFloat(DBConstants.STRUCTURE__SHADOW_SCALE);
		int expReward = rs.getInt(DBConstants.STRUCTURE__EXP_REWARD);

		if (null != structType) {
			String newStructType = structType.trim().toUpperCase();
			if (!structType.equals(newStructType)) {
				log.error(String.format("struct type incorrect: %s, id=%s",
						structType, id));
				structType = newStructType;
			}
		}

		if (null != buildResourceType) {
			String newBuildResourceType = buildResourceType.trim()
					.toUpperCase();
			if (!buildResourceType.equals(newBuildResourceType)) {
				log.error(String.format("incorrect ResourceType. %s, id=%s",
						buildResourceType, id));
				buildResourceType = newBuildResourceType;
			}
		}
		
		int strength = rs.getInt(DBConstants.STRUCTURE__STRENGTH);

		Structure s = new Structure(id, name, level, structType,
				buildResourceType, buildCost, minutesToBuild, width, height,
				predecessorStructId, successorStructId, imgName,
				imgVerticalPixelOffset, imgHorizontalPixelOffset, description,
				shortDescription, shadowImgName, shadowVerticalOffset,
				shadowHorizontalOffset, shadowScale, expReward, strength);

		return s;
	}
}
