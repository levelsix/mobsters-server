package com.lvl6.retrieveutils.rarechange;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.lvl6.info.ClanIcon;
import com.lvl6.properties.DBConstants;
import com.lvl6.retrieveutils.util.QueryConstructionUtil;

@Component
public class ClanIconRetrieveUtils {

  private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}
	
	@Autowired
	protected QueryConstructionUtil queryConstructionUtil;
	public QueryConstructionUtil getQueryConstructionUtil() {
		return queryConstructionUtil;
	}
	public void setQueryConstructionUtil(QueryConstructionUtil queryConstructionUtil) {
		this.queryConstructionUtil = queryConstructionUtil;
	}
	
//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class ClanIconMapper implements RowMapper<ClanIcon> {

		public ClanIcon mapRow(ResultSet rs, int rowNum) throws SQLException {
			ClanIcon ci = new ClanIcon();
			int i = 1;
			ci.setId(rs.getInt(i++));
			ci.setImgName(rs.getString(i++));
			ci.setAvailable(rs.getBoolean(i++));
			
			return ci;
		}        
	} 
	
  private static Map<Integer, ClanIcon> clanIconIdsToClanIcons;
  private static final String TABLE_NAME = DBConstants.TABLE_ITEM;
  

	//CONTROLLER LOGIC******************************************************************
	
	//RETRIEVE QUERIES*********************************************************************
  public static Map<Integer, ClanIcon> getAllIcons() {
  	//TODO: rethink how to set all the clan icons
  	if (null == clanIconIdsToClanIcons) {
  		(new ClanIconRetrieveUtils()).reload();
  	}
  	
  	return clanIconIdsToClanIcons;
  }
  
  public Map<Integer, ClanIcon> getClanIconIdsToClanIcons() {
  	if (null == clanIconIdsToClanIcons) {
  		setStaticClanIconIdsToClanIcons();
  	}
  	return clanIconIdsToClanIcons;
  }
  
  public Map<Integer, ClanIcon> getClanIconsForIds(Collection<Integer> ids) {
  	if (null == clanIconIdsToClanIcons) {
  		setStaticClanIconIdsToClanIcons();
  	}
  	Map<Integer, ClanIcon> returnMap = new HashMap<Integer, ClanIcon>();
  	
  	for (int id : ids) {
  		ClanIcon tsm = clanIconIdsToClanIcons.get(id);
  		returnMap.put(id, tsm);
  	}
  	return returnMap;
  }

  public void setStaticClanIconIdsToClanIcons() {
    log.debug("setting static map of clanIcon ids to clanIcons");
    try {
    	Map<String, Object> equalityConditions = null;
    	String conditionDelimiter = getQueryConstructionUtil().getAnd();
    	
    	//query db, "values" is not used 
			//(its purpose is to hold the values that were supposed to be put
			// into a prepared statement)
			List<Object> values = null;
			boolean preparedStatement = false;

			String query = getQueryConstructionUtil().selectRowsQueryEqualityConditions(
					TABLE_NAME, equalityConditions, conditionDelimiter, values,
					preparedStatement);

			log.info("clan icon query=" + query);
			
    	List<ClanIcon> clanIconList = this.jdbcTemplate.query(query, new ClanIconMapper());
    	
    	for (ClanIcon ci : clanIconList) {
    		int id = ci.getId();
    		clanIconIdsToClanIcons.put(id, ci);
    	}
    	
    } catch (Exception e) {
    	log.error("problem retrieving clan icon from db", e);
    }
  }
  
  //TODO: FIGURE OUT BETTER WAY TO RELOAD DATA
  public static void staticReload() {
  	(new ClanIconRetrieveUtils()).reload();
  }

  public void reload() {
    setStaticClanIconIdsToClanIcons();
  }

}
