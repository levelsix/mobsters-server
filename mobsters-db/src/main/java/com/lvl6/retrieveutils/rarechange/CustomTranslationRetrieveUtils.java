package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.db.jooq.generated.tables.pojos.CustomTranslationsPojo;
import com.lvl6.properties.DBConstants;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class CustomTranslationRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(CustomTranslationRetrieveUtils.class);

	private static Map<Integer, CustomTranslationsPojo> idsToCustomTranslationss;

	private static final String TABLE_NAME = DBConstants.TABLE_CUSTOM_TRANSLATION;

	public Map<Integer, CustomTranslationsPojo> getIdsToCustomTranslationss() {
		log.debug("retrieving all custom translations map");
		if (null == idsToCustomTranslationss) {
			setStaticIdsToCustomTranslationss();
		}
		return idsToCustomTranslationss;
	}

	public CustomTranslationsPojo getCustomTranslationsForId(int customTranslationId) {
		log.debug(String.format("retrieve custom translation for id {}",
				customTranslationId));
		if (null == idsToCustomTranslationss) {
			setStaticIdsToCustomTranslationss();
		}
		return idsToCustomTranslationss.get(customTranslationId);
	}

	private void setStaticIdsToCustomTranslationss() {
		log.debug("setting static map of custom translation ids to "
				+ "custom translations");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						Map<Integer, CustomTranslationsPojo> idsToCustomTranslationsTemp = new HashMap<Integer, CustomTranslationsPojo>();
						//loop through each row and convert it into a java object
						while (rs.next()) {
							CustomTranslationsPojo ct = convertRSRowToCustomTranslations(rs);
							if (ct == null) {
								continue;
							}

							int customTranslationId = ct.getId();
							idsToCustomTranslationsTemp.put(customTranslationId, ct);
						}
						idsToCustomTranslationss = idsToCustomTranslationsTemp;

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
		setStaticIdsToCustomTranslationss();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private CustomTranslationsPojo convertRSRowToCustomTranslations(ResultSet rs)
			throws SQLException {
		int id = rs.getInt(DBConstants.CUSTOM_TRANSLATION__ID);
		String phrase = rs.getString(DBConstants.CUSTOM_TRANSLATION__PHRASE);
		String language = rs.getString(DBConstants.CUSTOM_TRANSLATION__LANGUAGE);
		
		CustomTranslationsPojo ct = new CustomTranslationsPojo(id, phrase, language);
		return ct;
	}

}
