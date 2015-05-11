//package com.lvl6.leaderboards;
//
//import java.sql.Connection;
//import java.sql.PreparedStatement;
//import java.sql.ResultSet;
//import java.sql.SQLException;
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.annotation.Resource;
//import javax.sql.DataSource;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.jdbc.core.PreparedStatementCreator;
//import org.springframework.jdbc.core.RowCallbackHandler;
//import org.springframework.stereotype.Component;
//
//import com.hazelcast.core.HazelcastInstance;
//import com.lvl6.datastructures.DistributedZSet;
//import com.lvl6.datastructures.DistributedZSetHazelcast;
//import com.lvl6.datastructures.ZSetMember;
//import com.lvl6.info.StrengthLeaderBoard;
//import com.lvl6.properties.DBConstants;
//
//@Component
//public class LeaderBoardImpl {
//	
//	private static Logger log = LoggerFactory.getLogger(new Object() {
//	}.getClass().getEnclosingClass());
//	
//	protected JdbcTemplate jdbc; 
//
//	@Resource
//	protected DataSource dataSource;
//
//	public DataSource getDataSource() {
//		return dataSource;
//	}
//
//	@Resource
//	public void setDataSource(DataSource dataSource) {
//		this.dataSource = dataSource;
//		jdbc = new JdbcTemplate(dataSource);
//	}
//	
//	protected DistributedZSet strLeaderboard;
//	
//	@Autowired
//	protected HazelcastInstance hazelcastInstance;
//	public HazelcastInstance getHazelcastInstance() {
//		return hazelcastInstance;
//	}
//	
//	@Autowired
//	public void setHazelcastInstance(HazelcastInstance hazelcastInstance) {
//		this.hazelcastInstance = hazelcastInstance;
//		strLeaderboard = new DistributedZSetHazelcast("strength leaderboard", 
//				hazelcastInstance);
//	}
//	
//	
//	public void addToLeaderboard(String userId, long strength) {
//		strLeaderboard.add(userId, strength);
//		log.info("added to leaderboard, userId = {}, strength = {}", userId, strength);
//	}
//	
//	public List<StrengthLeaderBoard> getStrengths(int minRank, int maxRank) {
//		List<StrengthLeaderBoard> returnList = new ArrayList<StrengthLeaderBoard>();
//		for(ZSetMember m : strLeaderboard.range(minRank, maxRank)) {
//			int rank = m.getRank();
//			String userId = m.getKey();
//			long strength = m.getScore();
//			StrengthLeaderBoard slb = new StrengthLeaderBoard(rank, userId, strength);
//			returnList.add(slb);
//		}
//		return returnList;
//	}
//	
//	public List<StrengthLeaderBoard> getTopNStrengths(int num) {
//		List<StrengthLeaderBoard> returnList = new ArrayList<StrengthLeaderBoard>();
//		for(ZSetMember m : strLeaderboard.range(0, num)) {
//			int rank = m.getRank();
//			String userId = m.getKey();
//			long strength = m.getScore();
//			StrengthLeaderBoard slb = new StrengthLeaderBoard(rank, userId, strength);
//			returnList.add(slb);
//		}
//		return returnList;
//	}
//	
//	public int getUserRank(String userId) {
//		return strLeaderboard.get(userId).getRank();
//	}
//	
//	public void reload() {
//		jdbc.query(new StreamingStatementCreator("SELECT id, total_strength FROM user"),
//				new RowCallbackHandler() {
//			public void processRow(ResultSet resultSet) throws SQLException {
//			while (resultSet.next()) {
//				String userId = resultSet.getString(DBConstants.USER__ID);
//				long strength = resultSet.getLong(DBConstants.USER__TOTAL_STRENGTH);
//				addToLeaderboard(userId, strength);
//			}
//			}
//			});
//	}
//
//	private class StreamingStatementCreator implements PreparedStatementCreator {
//	    private final String sql;
//
//	    public StreamingStatementCreator(String sql) {
//	        this.sql = sql;
//	    }
//
//	    @Override
//	    public PreparedStatement createPreparedStatement(Connection connection)
//	    		throws SQLException {
//	        final PreparedStatement statement = connection.prepareStatement(sql, 
//	        		ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
//	        //this is the amount you tell the mysql driver how much to fetch at a time
//	        statement.setFetchSize(100);  
//	        return statement;
//	    }
//	}
//}