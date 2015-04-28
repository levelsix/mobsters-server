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
import com.lvl6.info.StructureBattleItemFactory;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class StructureBattleItemFactoryRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static Map<Integer, StructureBattleItemFactory> structIdsToBattleItemFactorys;

	private static final String TABLE_NAME = DBConstants.TABLE_STRUCTURE_BATTLE_ITEM_FACTORY_CONFIG;

	public static Map<Integer, StructureBattleItemFactory> getStructIdsToBattleItemFactorys() {
		log.debug("retrieving all structs data");
		if (structIdsToBattleItemFactorys == null) {
			setStaticStructIdsToBattleItemFactorys();
		}
		return structIdsToBattleItemFactorys;
	}

	public static StructureBattleItemFactory getBattleItemFactoryForStructId(
			int structId) {
		log.debug("retrieve struct data for structId " + structId);
		if (structIdsToBattleItemFactorys == null) {
			setStaticStructIdsToBattleItemFactorys();
		}
		return structIdsToBattleItemFactorys.get(structId);
	}

	public static StructureBattleItemFactory getUpgradedBattleItemFactoryForStructId(
			int structId) {
		log.debug("retrieve upgraded struct data for structId " + structId);
		if (structIdsToBattleItemFactorys == null) {
			setStaticStructIdsToBattleItemFactorys();
		}
		Structure curStruct = StructureRetrieveUtils
				.getUpgradedStructForStructId(structId);
		if (null != curStruct) {
			int successorStructId = curStruct.getId();
			StructureBattleItemFactory upgradedStruct = structIdsToBattleItemFactorys
					.get(successorStructId);
			return upgradedStruct;
		}
		return null;
	}

	public static StructureBattleItemFactory getPredecessorBattleItemFactoryForStructId(
			int structId) {
		log.debug("retrieve predecessor struct data for structId " + structId);
		if (structIdsToBattleItemFactorys == null) {
			setStaticStructIdsToBattleItemFactorys();
		}
		Structure curStruct = StructureRetrieveUtils
				.getUpgradedStructForStructId(structId);
		if (null != curStruct) {
			int predecessorStructId = curStruct.getId();
			StructureBattleItemFactory predecessorStruct = structIdsToBattleItemFactorys
					.get(predecessorStructId);
			return predecessorStruct;
		}
		return null;
	}

	private static void setStaticStructIdsToBattleItemFactorys() {
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
						HashMap<Integer, StructureBattleItemFactory> structIdsToStructsTemp = new HashMap<Integer, StructureBattleItemFactory>();
						while (rs.next()) {
							StructureBattleItemFactory struct = convertRSRowToBattleItemFactory(rs);
							if (struct != null)
								structIdsToStructsTemp.put(
										struct.getStructId(), struct);
						}
						structIdsToBattleItemFactorys = structIdsToStructsTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("BattleItemFactory retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public static void reload() {
		setStaticStructIdsToBattleItemFactorys();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private static StructureBattleItemFactory convertRSRowToBattleItemFactory(
			ResultSet rs) throws SQLException {
		int structId = rs
				.getInt(DBConstants.STRUCTURE_BATTLE_ITEM_FACTORY__STRUCT_ID);
		int powerLimit = rs
				.getInt(DBConstants.STRUCTURE_BATTLE_ITEM_FACTORY__POWER_LIMIT);

		return new StructureBattleItemFactory(structId, powerLimit);
	}
}
