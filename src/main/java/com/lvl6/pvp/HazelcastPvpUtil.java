package com.lvl6.pvp;


import java.io.Serializable;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import com.lvl6.properties.DBConstants;
import com.lvl6.scriptsjava.generatefakeusers.NameGenerator;
import com.lvl6.utils.DBConnection;

@Component
public class HazelcastPvpUtil implements InitializingBean, Serializable {
	
	

	private static final long serialVersionUID = 7033740347971426291L;

		private static final Logger log = LoggerFactory.getLogger(HazelcastPvpUtil.class);

		//properties for random names
	  private static int syllablesInName1 = 2;
	  private static int syllablesInName2 = 3;
	  private static int numRandomNames = 2000;
	  private static Random rand;
	  private List<String> randomNames;
	  private static final String FILE_OF_RANDOM_NAMES = "classpath:namerulesElven.txt";
    
	  public static final String OFFLINE_PVP_USER_MAP = "offlinePvpUserMap";
	  
	  
//	  @Autowired
//	  protected TextFileResourceLoaderAware textFileResourceLoaderAware;
    
    @Autowired
  	protected HazelcastInstance hazel;

    protected IMap<String, OfflinePvpUser> userIdToOfflinePvpUser;
    
    
    //METHOD TO ACTUALLY USE IMAP
    public Set<OfflinePvpUser> retrieveOfflinePvpUsers(int minElo, int maxElo, 
    		Date shieldEndTimeBeforeNow) {
    	log.info("querying for people to attack. shieldEndTime should be before now=" +
    		shieldEndTimeBeforeNow + "\t elo should be between minElo=" + minElo + ", maxElo=" + maxElo);
    	
    	String shieldEndTime = PvpConstants.OFFLINE_PVP_USER__SHIELD_END_TIME;
    	String elo = PvpConstants.OFFLINE_PVP_USER__ELO;
    	EntryObject e = new PredicateBuilder().getEntryObject();
    	Predicate predicate = e.get(shieldEndTime).lessThan(shieldEndTimeBeforeNow)
    			.and(e.get(elo).between(minElo, maxElo));
//    			.and(e.get(elo).lessThan(maxElo)).and(e.get(elo).greaterThan(minElo));
    	
    	Set<OfflinePvpUser> users = (Set<OfflinePvpUser>) userIdToOfflinePvpUser.values(predicate);
    	log.info("users:" + users);
    	
    	return users;
    }

    //REQUIRED INITIALIZING BEAN STUFF
		@Override
    public void afterPropertiesSet() throws Exception {
//    	createRandomNames();
    	populateOfflinePvpUserMap();
    }
    
    protected void createRandomNames() {
//    	rand = new Random();
//    	Resource nameFile = getTextFileResourceLoaderAware().getResource(FILE_OF_RANDOM_NAMES);
//    	
//    	try {
//    		//maybe don't need to close, deallocate nameFile?
//    		if (!nameFile.exists()) {
//    			log.error("file with random names does not exist. filePath=" + FILE_OF_RANDOM_NAMES);
//    			return;
//    		}
//    		
//    		NameGenerator nameGenerator = new NameGenerator(nameFile);
//    		if (null != nameGenerator) {
//    			log.info("creating random ELVEN NAMES");
//    			for (int i = 0; i < numRandomNames; i++) {
//    				createName(rand, nameGenerator);
//    			}
//    			
//    			log.info("num rand ELVEN NAMES created:" + randomNames.size());
//    		}
//    		
//    	} catch (Exception e) {
//    		log.error("could not create fake user name", e);
//    	}
    }
    
    protected void createName(Random rand, NameGenerator nameGenerator) {
    	int syllablesInName = (Math.random() < .5) ? syllablesInName1 : syllablesInName2;
    	String name = nameGenerator.compose(syllablesInName);
    	if (Math.random() < .5) {
    		name = name.toLowerCase();
    	}
      if (Math.random() < .3) {
      	name = name + (int)(Math.ceil(Math.random() * 98));
      }
      randomNames.add(name);
      
    }

    public String getRandomName() {
    	int len = randomNames.size();
    	int randInt = rand.nextInt(len);
    	
    	return randomNames.get(randInt);
    }
    

    protected void populateOfflinePvpUserMap() {
    	
    	
    	//this will create the map if it doesn't exist
    	userIdToOfflinePvpUser = hazel.getMap(OFFLINE_PVP_USER_MAP);
    	
    	addOfflinePvpUserIndexes();
    	
    	//now we have almost all offline users, put them into the userIdToOfflinePvpUser IMap
    	Collection<OfflinePvpUser> validUsers = getPvpValidUsers();
    	if (null != validUsers && !validUsers.isEmpty()) {
    		log.info("populating the IMap with users that can be attacked in pvp. numUsers="
    				+ validUsers.size());
    		populateOfflinePvpUserMap(validUsers);
    	} else {
    		log.error("no available users that can be attacked in pvp.");
    	}
    	
    }
    
    //search entire user table for users who do not have active shields,
    //but only select certain columns
    protected Collection<OfflinePvpUser> getPvpValidUsers() {
    	Timestamp now = new Timestamp((new Date()).getTime());
    	
    	StringBuffer sb = new StringBuffer();
    	sb.append("SELECT ");
    	sb.append(DBConstants.USER__ID);
    	sb.append(", ");
    	sb.append(DBConstants.USER__ELO);
    	sb.append(", ");
    	sb.append(DBConstants.USER__SHIELD_END_TIME);
    	sb.append(" FROM ");
    	sb.append(DBConstants.TABLE_USER);
    	sb.append(" WHERE ");
    	sb.append(DBConstants.USER__HAS_ACTIVE_SHIELD);
    	sb.append(" =? AND ");
    	sb.append(DBConstants.USER__SHIELD_END_TIME);
    	sb.append(" < ?;");
    	String query = sb.toString();
    	
    	List<Object> values = new ArrayList<Object>();
    	values.add(false);
    	values.add(now);
    	
    	log.info("query=" + query);
    	log.info("values=" + values);
    	
    	Connection conn = null;
    	ResultSet rs = null;
    	List<OfflinePvpUser> validUsers = new ArrayList<OfflinePvpUser>();
    	try {
    		conn = DBConnection.get().getConnection();
    		rs = DBConnection.get().selectDirectQueryNaive(conn, query, values);
    		validUsers = convertRSToOfflinePvpUser(rs);
    	} catch (Exception e) {
    		log.error("user retrieve db error, in order to populate PvpUsers map.", e);
    	} finally {
    		DBConnection.get().close(rs, null, conn);
    	}
    	return validUsers;
    }
    
    protected List<OfflinePvpUser> convertRSToOfflinePvpUser(ResultSet rs) {
    	if (null != rs) {
    		try {
    			rs.last();
    			rs.beforeFirst();
    			List<OfflinePvpUser> users = new ArrayList<OfflinePvpUser>();
    			while (rs.next()) {
    				users.add(convertRSRowToOfflinePvpUser(rs));
    			}
    			return users;
    		} catch (SQLException e) {
    			log.error("problem with database call, in order to populate PvpUsers map.", e);
    		}
    	}
    	return null;
    }

    protected OfflinePvpUser convertRSRowToOfflinePvpUser(ResultSet rs) throws SQLException {
    	int i = 1;

    	String userId = Integer.toString((rs.getInt(i++)));
    	int elo = rs.getInt(i++);

    	Date shieldEndTime = null;
    	try {
    		Timestamp ts = rs.getTimestamp(i++);
    		if (!rs.wasNull()) {
    			shieldEndTime = new Date(ts.getTime());
    		}
    	} catch (Exception e) {
    		log.error("db error: shield_end_time not set. user_id=" + userId);
    	}

    	return new OfflinePvpUser(userId, elo, shieldEndTime);
    }

    protected void addOfflinePvpUserIndexes() {
    	//when specifying what property the index will be on, look at PvpConstants for the
    	//property name
    	
    	//the true is for indicating that there will be ranged queries on this property
    	userIdToOfflinePvpUser.addIndex(PvpConstants.OFFLINE_PVP_USER__ELO, true);
    	userIdToOfflinePvpUser.addIndex(PvpConstants.OFFLINE_PVP_USER__SHIELD_END_TIME, true);
    }
    
    protected void populateOfflinePvpUserMap(Collection<OfflinePvpUser> validUsers) {
    	//go through all the valid users that can be attacked, and store them into
    	//the hazelcast distributed map
    	for (OfflinePvpUser user : validUsers) {
    		String userId = user.getUserId();
    		
    		userIdToOfflinePvpUser.put(userId, user);
    	}
    }
    
    
    
    
    public HazelcastInstance getHazel() {
    	return hazel;
    }
    
    public void setHazel(HazelcastInstance hazel) {
    	this.hazel = hazel;
    }

//    public TextFileResourceLoaderAware getTextFileResourceLoaderAware() {
//			return textFileResourceLoaderAware;
//		}
//
//		public void setTextFileResourceLoaderAware(
//				TextFileResourceLoaderAware textFileResourceLoaderAware) {
//			this.textFileResourceLoaderAware = textFileResourceLoaderAware;
//		}



		public class OfflinePvpUser implements Serializable {
    	
			private static final long serialVersionUID = 3459023237592127885L;
			private String userId;
    	private int elo;
    	private Date shieldEndTime;
    	
			public OfflinePvpUser(String userId, int elo, Date shieldEndTime) {
				super();
				this.userId = userId;
				this.elo = elo;
				this.shieldEndTime = shieldEndTime;
			}

			public String getUserId() {
				return userId;
			}

			public void setUserId(String userId) {
				this.userId = userId;
			}

			public int getElo() {
				return elo;
			}

			public void setElo(int elo) {
				this.elo = elo;
			}

			public Date getShieldEndTime() {
				return shieldEndTime;
			}

			public void setShieldEndTime(Date shieldEndTime) {
				this.shieldEndTime = shieldEndTime;
			}

			@Override
			public String toString() {
				return "OfflinePvpUser [userId=" + userId + ", elo=" + elo
						+ ", shieldEndTime=" + shieldEndTime + "]";
			}
			
    }
    

}