//package com.lvl6.retrieveutils.rarechange;
//
//import java.sql.Connection;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.context.annotation.DependsOn;
//import org.springframework.stereotype.Component;
//
//import com.lvl6.info.CityElement;
//import com.lvl6.info.CoordinatePair;
//import com.lvl6.properties.ControllerConstants;
//import com.lvl6.properties.DBConstants;
//import com.lvl6.utils.DBConnection;
//
//@Component @DependsOn("gameServer") public class CityElementsRetrieveUtils {
//
//  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
//
//  private static Map<Integer, List<CityElement>> cityIdToCityElements;
//
//  private static final String TABLE_NAME = DBConstants.TABLE_CITY_ELEMENT_CONFIG;
//
//  public static Map<Integer, List<CityElement>> getCityIdToCityElements() {
//    log.debug("retrieving all city id to neutral city elements data");
//    if (cityIdToCityElements == null) {
//      setStaticCityIdToCityElements();
//    }
//    return cityIdToCityElements;
//  }
//
//  public static CityElement getCityElement(int cityId, int assetId) {
//    log.debug("retrieving neutral city element with assetId " + assetId + " in cityId " + cityId);
//    if (cityIdToCityElements == null) {
//      setStaticCityIdToCityElements();
//    }
//    List<CityElement> ncesForCity = cityIdToCityElements.get(cityId);
//    if (ncesForCity != null) {
//      for (CityElement nce : ncesForCity) {
//        if (nce.getAssetId() == assetId) {
//          return nce;
//        }
//      }
//    }
//    return null;
//  }
//
//  public static List<CityElement> getCityElementsForCity(int cityId) {
//    log.debug("retrieving neutral city elements for city " + cityId);
//    if (cityIdToCityElements == null) {
//      setStaticCityIdToCityElements();
//    }
//    return cityIdToCityElements.get(cityId);
//  }
//
//  public static void reload() {
//    setStaticCityIdToCityElements();
//  }
//
//  private static void setStaticCityIdToCityElements() {
//    log.debug("setting static map of city id to neutral city elements for city");
//
//    Connection conn = DBConnection.get().getConnection();
//    ResultSet rs = null;
//    try {
//    	if (conn != null) {
//    		rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);
//    		if (rs != null) {
//    			try {
//    				rs.last();
//    				rs.beforeFirst();
//    				Map <Integer, List<CityElement>> cityIdToCityElementsTemp = new HashMap<Integer, List<CityElement>>();
//    				while(rs.next()) {
//    					CityElement nce = convertRSRowToCityElement(rs);
//    					if (nce != null) {
//    						if (cityIdToCityElementsTemp.get(nce.getCityId()) == null) {
//    							cityIdToCityElementsTemp.put(nce.getCityId(), new ArrayList<CityElement>());
//    						}
//    						cityIdToCityElementsTemp.get(nce.getCityId()).add(nce);
//    					}
//    				}
//    				cityIdToCityElements = cityIdToCityElementsTemp;
//    			} catch (SQLException e) {
//    				log.error("problem with database call.", e);
//
//    			}
//    		}   
//    	}
//    } catch (Exception e) {
//    	log.error("city elements retrieve db error.", e);
//    } finally {
//    	DBConnection.get().close(rs, null, conn);
//    }
//  }
//
//  private static CityElement convertRSRowToCityElement(ResultSet rs) throws SQLException {
//    int i = 1;
//    int cityId = rs.getInt(i++);
//    int assetId = rs.getInt(i++);
////    String goodName = rs.getString(i++);
//    String type = rs.getString(i++);
//    CoordinatePair coords = new CoordinatePair(rs.getFloat(i++), rs.getFloat(i++));
//
//    float xLength = rs.getFloat(i++);
//    if (rs.wasNull()) xLength = ControllerConstants.NOT_SET;
//
//    float yLength = rs.getFloat(i++);
//    if (rs.wasNull()) yLength = ControllerConstants.NOT_SET;
//
//    String imgGood = rs.getString(i++);
//
//    String orientation = rs.getString(i++);
//    CoordinatePair spriteCoords = new CoordinatePair(rs.getFloat(i++), rs.getFloat(i++));
//    
//    return new CityElement(cityId, assetId, type, coords,
//    		xLength, yLength, imgGood, orientation, spriteCoords);
//  }
//
//
//}