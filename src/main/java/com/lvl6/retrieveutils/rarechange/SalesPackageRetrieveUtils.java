package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.SalesPackage;
import com.lvl6.properties.DBConstants;
import com.lvl6.properties.Globals;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class SalesPackageRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static Map<Integer, SalesPackage> salesPackageIdsToSalesPackages;

	private static final String TABLE_NAME = DBConstants.TABLE_SALES_PACKAGE_CONFIG;

	public Map<Integer, SalesPackage> getSalesPackageIdsToSalesPackages() {
		log.debug("retrieving all sales packs data map");
		if (salesPackageIdsToSalesPackages == null) {
			setStaticSalesPackageIdsToSalesPackages();
		}
		return salesPackageIdsToSalesPackages;
	}

	//  public static Map<Integer, SalesPackage> getSalesPackagesForSalesPackageIds(
	//      List<Integer> salesPackageIds) {
	//    if (salesPackageIdsToSalesPackages == null) {
	//      setStaticSalesPackageIdsToSalesPackages();
	//    }
	//    Map<Integer, SalesPackage> returnValue = new HashMap<Integer, SalesPackage>();
	//    for (int id : salesPackageIds) {
	//      SalesPackage aPack = salesPackageIdsToSalesPackages.get(id);
	//      returnValue.put(id, aPack);
	//    }
	//
	//    return returnValue;
	//  }

	public Map<String, List<SalesPackage>> getSalesPackageProductIdToSalesPackages() {
		log.debug("retrieving all sales packs data map");
		if (salesPackageIdsToSalesPackages == null) {
			setStaticSalesPackageIdsToSalesPackages();
		}

		Map<String, List<SalesPackage>> returnMap = new HashMap<String, List<SalesPackage>>();
		for(Integer i : salesPackageIdsToSalesPackages.keySet()) {
			SalesPackage sp = salesPackageIdsToSalesPackages.get(i);

			List<SalesPackage> list = returnMap.get(sp.getProductId());

			if (list == null) {
			    list = new ArrayList<SalesPackage>();
			    returnMap.put(sp.getProductId(), list);
			}
			list.add(sp);
		}
		return returnMap;
	}

	public SalesPackage getSalesPackageForSalesPackageId(int salesPackageId) {
		log.debug("retrieve sales pack data for sales pack "
				+ salesPackageId);
		if (salesPackageIdsToSalesPackages == null) {
			setStaticSalesPackageIdsToSalesPackages();
		}
		return salesPackageIdsToSalesPackages.get(salesPackageId);
	}

	private void setStaticSalesPackageIdsToSalesPackages() {
		log.debug("setting static map of salesPackageIds to salesPackages");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						HashMap<Integer, SalesPackage> salesPackageIdsToSalesPackagesTemp = new HashMap<Integer, SalesPackage>();
						while (rs.next()) {  //should only be one
							SalesPackage salesPackage = convertRSRowToSalesPackage(rs);
							if (salesPackage != null)
								salesPackageIdsToSalesPackagesTemp.put(
										salesPackage.getId(), salesPackage);
						}
						salesPackageIdsToSalesPackages = salesPackageIdsToSalesPackagesTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("sales pack retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public void reload() {
		setStaticSalesPackageIdsToSalesPackages();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private SalesPackage convertRSRowToSalesPackage(ResultSet rs)
			throws SQLException {
		int id = rs.getInt(DBConstants.SALES_PACKAGE__ID);
		String productId = rs.getString(DBConstants.SALES_PACKAGE__PRODUCT_ID);
		int price = rs.getInt(DBConstants.SALES_PACKAGE__PRICE);
		String uuid = rs.getString(DBConstants.SALES_PACKAGE__UUID);
		Date timeStart = null;
		Date timeEnd = null;
		Timestamp ts = null;

		try {
			ts = rs.getTimestamp(DBConstants.SALES_PACKAGE__START_TIME);
			if (!rs.wasNull()) {
				timeStart = new Date(ts.getTime());
			}
		} catch (Exception e) {
			log.error("last_purchase_time null...?", e);
		}

		try {
			ts = rs.getTimestamp(DBConstants.SALES_PACKAGE__END_TIME);
			if (!rs.wasNull()) {
				timeEnd = new Date(ts.getTime());
			}
		} catch (Exception e) {
			log.error("last_purchase_time null...?", e);
		}

		int succId = rs.getInt(DBConstants.SALES_PACKAGE__SUCC_ID);
		int customMenuId = rs.getInt(DBConstants.SALES_PACKAGE__CUSTOM_MENU_ID);
		int priority = rs.getInt(DBConstants.SALES_PACKAGE__PRIORITY);
		
        productId =  Globals.APPLE_BUNDLE_ID() + "."
                + productId;

        String str;
        String name = "";
        String animatingIcon = "";
        String slamIcon = "";
        String titleColor = "";

        str = rs.getString(DBConstants.SALES_PACKAGE__NAME);
        if(!rs.wasNull()) {
        	name = str;
        }

        str = rs.getString(DBConstants.SALES_PACKAGE__ANIMATING_ICON);
        if(!rs.wasNull()) {
        	animatingIcon = str;
        }

        str = rs.getString(DBConstants.SALES_PACKAGE__SLAM_ICON);
        if(!rs.wasNull()) {
            slamIcon = str;
        }

        str = rs.getString(DBConstants.SALES_PACKAGE__TITLE_COLOR);
        if(!rs.wasNull()) {
            titleColor = str;
        }

		SalesPackage salesPackage = new SalesPackage(id, productId, name, price, uuid, 
				timeStart, timeEnd, succId, priority, customMenuId, animatingIcon, 
				slamIcon, titleColor);
		return salesPackage;
	}
}
