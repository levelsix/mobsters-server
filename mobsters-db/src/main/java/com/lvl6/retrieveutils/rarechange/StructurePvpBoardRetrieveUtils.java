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
import com.lvl6.info.StructurePvpBoard;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class StructurePvpBoardRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(StructurePvpBoardRetrieveUtils.class);
	
	@Autowired
	protected StructureRetrieveUtils structureRetrieveUtils;

	private static Map<Integer, StructurePvpBoard> structIdsToPvpBoards;

	private static final String TABLE_NAME = DBConstants.TABLE_STRUCTURE_PVP_BOARD_CONFIG;

	public Map<Integer, StructurePvpBoard> getStructIdsToPvpBoards() {
		log.debug("retrieving all structs data");
		if (structIdsToPvpBoards == null) {
			setStaticStructIdsToPvpBoards();
		}
		return structIdsToPvpBoards;
	}

	public StructurePvpBoard getPvpBoardForStructId(int structId) {
		log.debug("retrieve struct data for structId {}", structId);
		if (structIdsToPvpBoards == null) {
			setStaticStructIdsToPvpBoards();
		}
		return structIdsToPvpBoards.get(structId);
	}

	public StructurePvpBoard getUpgradedPvpBoardForStructId(int structId) {
		log.debug("retrieve upgraded struct data for structId {}", structId);
		if (structIdsToPvpBoards == null) {
			setStaticStructIdsToPvpBoards();
		}
		Structure curStruct = structureRetrieveUtils
				.getUpgradedStructForStructId(structId);
		if (null != curStruct) {
			int successorStructId = curStruct.getId();
			StructurePvpBoard upgradedStruct = structIdsToPvpBoards
					.get(successorStructId);
			return upgradedStruct;
		}
		return null;
	}

	public StructurePvpBoard getPredecessorPvpBoardForStructId(
			int structId) {
		log.debug("retrieve predecessor struct data for structId {}", structId);
		if (structIdsToPvpBoards == null) {
			setStaticStructIdsToPvpBoards();
		}
		Structure curStruct = structureRetrieveUtils
				.getUpgradedStructForStructId(structId);
		if (null != curStruct) {
			int predecessorStructId = curStruct.getId();
			StructurePvpBoard predecessorStruct = structIdsToPvpBoards
					.get(predecessorStructId);
			return predecessorStruct;
		}
		return null;
	}

	private void setStaticStructIdsToPvpBoards() {
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
						HashMap<Integer, StructurePvpBoard> structIdsToStructsTemp = new HashMap<Integer, StructurePvpBoard>();
						while (rs.next()) {
							StructurePvpBoard struct = convertRSRowToPvpBoard(rs);
							if (struct != null)
								structIdsToStructsTemp.put(
										struct.getStructId(), struct);
						}
						structIdsToPvpBoards = structIdsToStructsTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("PvpBoard retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public void reload() {
		setStaticStructIdsToPvpBoards();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private StructurePvpBoard convertRSRowToPvpBoard(ResultSet rs)
			throws SQLException {
		int structId = rs.getInt(DBConstants.STRUCTURE_PVP_BOARD__STRUCT_ID);
		int powerLimit = rs
				.getInt(DBConstants.STRUCTURE_PVP_BOARD__POWER_LIMIT);

		return new StructurePvpBoard(structId, powerLimit);
	}
}
