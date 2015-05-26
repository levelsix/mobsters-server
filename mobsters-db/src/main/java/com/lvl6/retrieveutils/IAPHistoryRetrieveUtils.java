package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.lvl6.properties.DBConstants;

@Component
@DependsOn("gameServer")
public class IAPHistoryRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static final String TABLE_NAME = DBConstants.TABLE_IAP_HISTORY;
	private static final IapHistoryMapper iapHistoryMapper = new IapHistoryMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public boolean checkIfDuplicateTransaction(long transactionId) {
		log.debug("checking if transaction already exists for transaction Id"
				+ transactionId);

		Object[] values = { transactionId };
		String query = String.format("select %s from %s where %s=?",
				DBConstants.IAP_HISTORY__ID, TABLE_NAME,
				DBConstants.IAP_HISTORY__TRANSACTION_ID);

		boolean isDuplicateTransaction = true;
		try {
			List<String> idList = this.jdbcTemplate.queryForList(query, values,
					String.class);

			if (null == idList || idList.isEmpty()) {
				isDuplicateTransaction = false;
				log.info(String.format("not a duplicate transactionId: %s",
						transactionId));
			}

		} catch (Exception e) {
			log.error("iap history retrieve db error.", e);
			//    } finally {
			//    	DBConnection.get().close(rs, null, conn);
		}
		return isDuplicateTransaction;
	}

	public boolean checkIfUserHasPurchased(String userId) {
		log.debug("checking if player has purchased anything");

		String query = String.format("select count(*) from %s where %s=?",
				TABLE_NAME, DBConstants.IAP_HISTORY__USER_ID);

		Object[] values = new Object[] { userId };

		boolean hasBought = false;
		try {
			List<Integer> numBoughtContainer = this.jdbcTemplate.query(query,
					values, iapHistoryMapper);

			int numBought = 0;
			if (null != numBoughtContainer && !numBoughtContainer.isEmpty()) {
				numBought = numBoughtContainer.get(0);
			}
			if (numBought > 0) {
				hasBought = true;
			}
		} catch (Exception e) {
			log.error("iap history retrieve db error.", e);
			//    } finally {
			//    	DBConnection.get().close(rs, null, conn);
		}
		return hasBought;
	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class IapHistoryMapper implements RowMapper<Integer> {

		@Override
		public Integer mapRow(ResultSet rs, int rowNum) throws SQLException {
			return rs.getInt(1);
		}

	}

}
