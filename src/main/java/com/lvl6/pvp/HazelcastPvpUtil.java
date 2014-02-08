package com.lvl6.pvp;


import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.hazelcast.core.HazelcastInstance;
import com.hazelcast.core.IMap;
import com.hazelcast.query.EntryObject;
import com.hazelcast.query.Predicate;
import com.hazelcast.query.PredicateBuilder;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
public class HazelcastPvpUtil implements InitializingBean {

    private static final Logger log = LoggerFactory.getLogger(HazelcastPvpUtil.class);
    
    public static final String OFFLINE_PVP_USER_MAP = "offlinePvpUserMap";
    
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
    			.and(e.get(elo).lessThan(maxElo)).and(e.get(elo).greaterThan(minElo));
    	
    	Set<OfflinePvpUser> users = (Set<OfflinePvpUser>) userIdToOfflinePvpUser.values(predicate);
    	return users;
    }
    
    
    
    
    
    

    @Override
    public void afterPropertiesSet() throws Exception {
    	populateOfflinePvpUserMap();
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
    	sb.append(" =? AND");
    	sb.append(DBConstants.USER__SHIELD_END_TIME);
    	sb.append(" < ?;");
    	String query = sb.toString();
    	
    	List<Object> values = new ArrayList<Object>();
    	values.add(false);
    	values.add(now);
    	
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

    
    
    public class OfflinePvpUser {
    	
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