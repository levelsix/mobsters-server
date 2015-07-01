package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.db.jooq.generated.tables.pojos.CustomMenuConfigPojo;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class CustomMenuRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static Map<Integer, List<CustomMenuConfigPojo>> idsToCustomMenus;

	private static final String TABLE_NAME = DBConstants.TABLE_CUSTOM_MENU_CONFIG;

	public Map<Integer, List<CustomMenuConfigPojo>> getIdsToCustomMenus() {
		log.debug("retrieving all filedownload map");
		if (null == idsToCustomMenus) {
			setStaticIdsToCustomMenus();
		}
		return idsToCustomMenus;
	}

	public List<CustomMenuConfigPojo> getCustomMenuForId(int customMenuId) {
		log.debug(String.format("retrieve skill data for skill=%s",
				customMenuId));
		if (null == idsToCustomMenus) {
			setStaticIdsToCustomMenus();
		}
		return idsToCustomMenus.get(customMenuId);
	}

	private void setStaticIdsToCustomMenus() {
		log.debug("setting static map of skillIds to skills");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						Map<Integer, List<CustomMenuConfigPojo>> idsToCustomMenuTemp = new HashMap<Integer, List<CustomMenuConfigPojo>>();
						//loop through each row and convert it into a java object
						while (rs.next()) {
							CustomMenuConfigPojo fd = convertRSRowToCustomMenu(rs);
							if (fd == null) {
								continue;
							}

							int customMenuId = fd.getCustomMenuId();
							
							List<CustomMenuConfigPojo> cms = idsToCustomMenuTemp.get(customMenuId);
							
							if (cms == null) {
							    cms = new ArrayList<CustomMenuConfigPojo>();
							    idsToCustomMenuTemp.put(customMenuId, cms);
							}
							
							cms.add(fd);
						}
						idsToCustomMenus = idsToCustomMenuTemp;

					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("skill retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public void reload() {
		setStaticIdsToCustomMenus();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private CustomMenuConfigPojo convertRSRowToCustomMenu(ResultSet rs)
			throws SQLException {
		int id = rs.getInt(DBConstants.CUSTOM_MENU__ID);
		int positionX = rs.getInt(DBConstants.CUSTOM_MENU__POSITION_X);
		int positionY = rs.getInt(DBConstants.CUSTOM_MENU__POSITION_Y);
		int positionZ = rs.getInt(DBConstants.CUSTOM_MENU__POSITION_Z);
        boolean isJiggle = rs.getBoolean(DBConstants.CUSTOM_MENU__IS_JIGGLE);
        String imgName = rs.getString(DBConstants.CUSTOM_MENU__IMAGE_NAME);
        int ipadPositionX = rs.getInt(DBConstants.CUSTOM_MENU__IPAD_POSITION_X);
        int ipadPositionY = rs.getInt(DBConstants.CUSTOM_MENU__IPAD_POSITION_Y);

        CustomMenuConfigPojo cmcp = new CustomMenuConfigPojo();
        cmcp.setCustomMenuId(id);
        cmcp.setPositionX(positionX);
        cmcp.setPositionY(positionY);
        cmcp.setPositionZ(positionZ);
        cmcp.setIsJiggle(isJiggle);
        cmcp.setImageName(imgName);
        cmcp.setIpadPositionX(ipadPositionX);
        cmcp.setIpadPositionY(ipadPositionY);
        
		return cmcp;
	}

}