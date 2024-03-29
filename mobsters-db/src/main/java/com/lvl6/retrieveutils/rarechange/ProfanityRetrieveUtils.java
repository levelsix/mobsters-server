package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashSet;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class ProfanityRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(ProfanityRetrieveUtils.class);

	private static Set<String> oneWordProfanity;

	private static final String TABLE_NAME = DBConstants.TABLE_PROFANITY_CONFIG;

	public Set<String> getAllProfanity() {
		log.debug("retrieving all profanity placed in a set");
		if (oneWordProfanity == null) {
			setStaticProfanity();
		}
		return oneWordProfanity;
	}

	private void setStaticProfanity() {
		log.debug("setting static Set of profanity");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						Set<String> oneWordProfanityTemp = new HashSet<String>();
						while (rs.next()) {
							String profanityTerm = convertRSRowToProfanity(rs);
							if (null != profanityTerm)
								oneWordProfanityTemp.add(profanityTerm);
						}
						oneWordProfanity = oneWordProfanityTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("profanity retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public void reload() {
		setStaticProfanity();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private String convertRSRowToProfanity(ResultSet rs)
			throws SQLException {
		String profanityTerm = rs.getString(DBConstants.PROFANITY__TERM);

		return profanityTerm;
	}
}
