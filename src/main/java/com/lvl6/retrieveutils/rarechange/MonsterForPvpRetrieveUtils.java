package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.lvl6.info.MonsterForPvp;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component /*@DependsOn("gameServer")*/
public class MonsterForPvpRetrieveUtils implements InitializingBean {

  private Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

  private Map<Integer, MonsterForPvp> idsToMonsterForPvps;

  private static final String TABLE_NAME = DBConstants.TABLE_MONSTER_FOR_PVP;
  
  public static final String MONSTER_FOR_PVP_MAP = "monsterForPvpMap";
  
  public static final String MONSTER_FOR_PVP__MIN_ELO = "minElo";
  public static final String MONSTER_FOR_PVP__MAX_ELO = "maxElo";
  
  
  
  @Autowired
	protected HazelcastInstance hazel;
	
  protected IMap<String, MonsterForPvp> idToMonsterForPvp;

  @Override
  public void afterPropertiesSet() throws Exception {
  	populateMonsterForPvpMap();
  }

  protected void populateMonsterForPvpMap() {
  	reload();
  }
  

  public void reload() {
    setStaticIdsToMonsterForPvps();
    
    //this will create the map if it doesn't exist
    idToMonsterForPvp = hazel.getMap(MONSTER_FOR_PVP_MAP);
    
    addMonsterForPvpIndexes();
    
    populateMonsterForPvp();
  }

  //this is to make queries faster
  protected void addMonsterForPvpIndexes() {
  	//the true is for indicating that there will be ranged queries on this property
  	idToMonsterForPvp.addIndex(MONSTER_FOR_PVP__MAX_ELO, true);
  	idToMonsterForPvp.addIndex(MONSTER_FOR_PVP__MIN_ELO, true);
  	
  }
  
  protected void populateMonsterForPvp() {
  	//go through all the monsters that will make up a fake user's team, and store them into
  	//the hazelcast distributed map
  	
  	log.info("populating IMap with fake user monsters. num fake monsters=" +
  			idsToMonsterForPvps.size());
  	
  	for (Integer id : idsToMonsterForPvps.keySet()) {
  		MonsterForPvp mfp = idsToMonsterForPvps.get(id);
  		
  		String idStr = id.toString();
  		idToMonsterForPvp.put(idStr, mfp);
  	}

  }
  
  

  public MonsterForPvp getMonsterForPvpForMonsterForPvpId(int id) {
	  if (idsToMonsterForPvps == null) {
		  setStaticIdsToMonsterForPvps();      
	  }
	  return idsToMonsterForPvps.get(id); 
  }



  private void setStaticIdsToMonsterForPvps() {
    log.debug("setting map of ids to monsters for pvp");

    Connection conn = DBConnection.get().getConnection();
    ResultSet rs = null;
    try {
			if (conn != null) {
			  rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

			  if (rs != null) {
			    try {
			      rs.last();
			      rs.beforeFirst();
			      Map<Integer, MonsterForPvp> idsToMonsterForPvpsTemp =
			    		  new HashMap<Integer, MonsterForPvp>();
			      //loop through each row and convert it into a java object
			      while(rs.next()) {  
			        MonsterForPvp mfp = convertRSRowToMonsterForPvp(rs);
			        if (mfp == null) {
			          continue;
			        }

			        int id = mfp.getId();
			        idsToMonsterForPvpsTemp.put(id, mfp);
			      }
			      idsToMonsterForPvps = idsToMonsterForPvpsTemp;
			    } catch (SQLException e) {
			      log.error("problem with database call.", e);
			      
			    }
			  }    
			}
		} catch (Exception e) {
    	log.error("task stage retrieve db error.", e);
    } finally {
    	DBConnection.get().close(rs, null, conn);
    }
  }
  

  /*
   * assumes the resultset is apprpriately set up. traverses the row it's on.
   */
  private MonsterForPvp convertRSRowToMonsterForPvp(ResultSet rs) throws SQLException {
    int i = 1;
    int id = rs.getInt(i++);
    int monsterId = rs.getInt(i++);
    int monsterLvl = rs.getInt(i++);
    int minElo = rs.getInt(i++);
    int maxElo = rs.getInt(i++);
    
    MonsterForPvp taskStage = new MonsterForPvp(id, monsterId, monsterLvl, minElo, maxElo);
        
    return taskStage;
  }
  
  
  
  
  

  public HazelcastInstance getHazel() {
  	return hazel;
  }
  
  public void setHazel(HazelcastInstance hazel) {
  	this.hazel = hazel;
  }
  
}
