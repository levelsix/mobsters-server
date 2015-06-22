package com.lvl6.retrieveutils;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import com.lvl6.info.TranslationSettingsForUser;
import com.lvl6.properties.DBConstants;
import com.lvl6.proto.ChatProto.ChatScope;
import com.lvl6.utils.utilmethods.StringUtils;

@Component
@DependsOn("gameServer")
public class TranslationSettingsForUserRetrieveUtil {

	private static Logger log = LoggerFactory.getLogger(TranslationSettingsForUserRetrieveUtil.class);

	private final static String TABLE_NAME = DBConstants.TABLE_TRANSLATION_SETTINGS_FOR_USER;
	private static final UserTranslationSettingsForClientMapper rowMapper = new UserTranslationSettingsForClientMapper();
	private JdbcTemplate jdbcTemplate;

	@Resource
	public void setDataSource(DataSource dataSource) {
		log.info("Setting datasource and creating jdbcTemplate");
		this.jdbcTemplate = new JdbcTemplate(dataSource);
	}

	public List<TranslationSettingsForUser> getUserTranslationSettingsForUserGlobal(String userId) {
		log.debug(String.format("retrieving user translation settings for global userId %s",
				userId));

		Object[] values = { userId, ChatScope.GLOBAL.toString()};
		String query = String.format("select * from %s where %s=? and %s=?", TABLE_NAME,
				DBConstants.TRANSLATION_SETTINGS_FOR_USER__RECEIVER_USER_ID,
				DBConstants.TRANSLATION_SETTINGS_FOR_USER__CHAT_TYPE);

		List<TranslationSettingsForUser> userTranslationSettingss = null;
		try {
			userTranslationSettingss = this.jdbcTemplate.query(query, values, rowMapper);

		} catch (Exception e) {
			log.error("translation settings for user retrieve db error.", e);
			userTranslationSettingss = new ArrayList<TranslationSettingsForUser>();
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return userTranslationSettingss;
	}
	
	public Map<String, TranslationSettingsForUser> getUserTranslationSettingsMapForUsersGlobal(List<String> userIds) {
		StringBuilder querySb = new StringBuilder();
		querySb.append("SELECT * FROM ");
		querySb.append(TABLE_NAME);
		querySb.append(" WHERE ");
		querySb.append(DBConstants.TRANSLATION_SETTINGS_FOR_USER__CHAT_TYPE);
		querySb.append("=?");
		List<Object> values = new ArrayList<Object>();
		values.add(ChatScope.GLOBAL.toString());
		
		if (userIds != null && !userIds.isEmpty()) {
			log.debug("retrieving usertranslationsettings with userIds {}",
					userIds);
			querySb.append(" AND ");
			querySb.append(DBConstants.TRANSLATION_SETTINGS_FOR_USER__RECEIVER_USER_ID);
			querySb.append(" IN (");

			int amount = userIds.size();
			List<String> questions = Collections.nCopies(amount, "?");
			String questionMarkStr = StringUtils.csvList(questions);

			querySb.append(questionMarkStr);
			querySb.append(");");
			values.addAll(userIds);
		}
		
		String query = querySb.toString();
		log.info("query={}, values={}", query, values);

		List<TranslationSettingsForUser> tsfuList = null;
		Map<String, TranslationSettingsForUser> userIdsToTranslationSettings =
				new HashMap<String, TranslationSettingsForUser>();
		try {
			tsfuList = this.jdbcTemplate.query(query, values.toArray(),
					rowMapper);

		} catch (Exception e) {
			log.error("structure for user retrieve db error.", e);
			tsfuList = new ArrayList<TranslationSettingsForUser>();
		}
		
		for(TranslationSettingsForUser tsfu : tsfuList) {
			String userId = tsfu.getReceiverUserId();
			userIdsToTranslationSettings.put(userId, tsfu);
		}

		return userIdsToTranslationSettings;
	}
	

	public List<TranslationSettingsForUser> getUserTranslationSettingsForUser(String recipientUserId) {
		log.debug(String.format("retrieving user translation settings for userId %s",
				recipientUserId));

		Object[] values = { recipientUserId };
		String query = String.format("select * from %s where %s=?", TABLE_NAME,
				DBConstants.TRANSLATION_SETTINGS_FOR_USER__RECEIVER_USER_ID);

		List<TranslationSettingsForUser> userTranslationSettingss = null;
		try {
			userTranslationSettingss = this.jdbcTemplate.query(query, values, rowMapper);

		} catch (Exception e) {
			log.error("translation settings for user retrieve db error.", e);
			userTranslationSettingss = new ArrayList<TranslationSettingsForUser>();
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return userTranslationSettingss;
	}

	public List<TranslationSettingsForUser> getUserTranslationSettingsForUserForStartup(String userId) {
		log.debug(String.format("retrieving user translation settings for userId %s",
				userId));

		Object[] values = { userId, userId };
		String query = String.format("select * from %s where %s=? and %s=?", TABLE_NAME,
				DBConstants.TRANSLATION_SETTINGS_FOR_USER__SENDER_USER_ID, DBConstants.TRANSLATION_SETTINGS_FOR_USER__SENDER_USER_ID);

		List<TranslationSettingsForUser> userTranslationSettingss = null;
		try {
			userTranslationSettingss = this.jdbcTemplate.query(query, values, rowMapper);

		} catch (Exception e) {
			log.error("translation settings for user retrieve db error.", e);
			userTranslationSettingss = new ArrayList<TranslationSettingsForUser>();
			//		} finally {
			//			DBConnection.get().close(rs, null, conn);
		}
		return userTranslationSettingss;
	}


	////@Cacheable(value="structIdsToUserStructsForUser", key="#userId")
	public Map<String, TranslationSettingsForUser> getSenderIdToUserTranslationSettingsForUser(
			String userId) {
		log.debug("retrieving map of translation settings to id for userId "
				+ userId);

		Map<String, TranslationSettingsForUser> translationSettingsIdToTranslationSettingsForUser = new HashMap<String, TranslationSettingsForUser>();
		try {

			List<TranslationSettingsForUser> bifuList = getUserTranslationSettingsForUser(userId);

			for (TranslationSettingsForUser bifu : bifuList) {
				String senderUserId = bifu.getSenderUserId();
				translationSettingsIdToTranslationSettingsForUser.put(senderUserId, bifu);
			}

		} catch (Exception e) {
			log.error(
					String.format(
							"translation settings for user retrieve db error. userId=%s",
							userId), e);
		}

		return translationSettingsIdToTranslationSettingsForUser;
	}

	////@Cacheable(value="specificUserStruct", key="#userStructId")
	public TranslationSettingsForUser getSpecificUserTranslationSettings(String recipientUserId,
			String senderUserId) {
		log.debug(
				"retrieving translation settings with recipientUserId={}, senderUserId={}",
				recipientUserId, senderUserId);

		Object[] values = { recipientUserId, senderUserId };
		String query = String.format("select * from %s where %s=? and %s=?",
				TABLE_NAME, DBConstants.TRANSLATION_SETTINGS_FOR_USER__RECEIVER_USER_ID,
				DBConstants.TRANSLATION_SETTINGS_FOR_USER__SENDER_USER_ID);

		TranslationSettingsForUser userTranslationSettings = null;
		try {
			List<TranslationSettingsForUser> bifuList = this.jdbcTemplate.query(query,
					values, rowMapper);

			if (null != bifuList && !bifuList.isEmpty()) {
				userTranslationSettings = bifuList.get(0);
			}

		} catch (Exception e) {
			log.error(
					String.format(
							"translation settings for user retrieve db error. recipientUserId=%s, senderUserId=%s",
							recipientUserId, senderUserId), e);
		}

		return userTranslationSettings;
	}

	public TranslationSettingsForUser getSpecificUserGlobalTranslationSettings(String recipientUserId,
			ChatScope chatType) {
		log.debug(
				"retrieving translation settings with recipientUserId={}, chatType={}",
				recipientUserId, chatType);

		Object[] values = { recipientUserId, chatType.toString() };
		String query = String.format("select * from %s where %s=? and %s=?",
				TABLE_NAME, DBConstants.TRANSLATION_SETTINGS_FOR_USER__RECEIVER_USER_ID,
				DBConstants.TRANSLATION_SETTINGS_FOR_USER__CHAT_TYPE);

		TranslationSettingsForUser userTranslationSettings = null;
		try {
			List<TranslationSettingsForUser> bifuList = this.jdbcTemplate.query(query,
					values, rowMapper);

			if (null != bifuList && !bifuList.isEmpty()) {
				userTranslationSettings = bifuList.get(0);
			}
			else return null;

		} catch (Exception e) {
			log.error(
					String.format(
							"translation settings for user retrieve db error. recipientUserId=%s, chatType=%s",
							recipientUserId, chatType), e);
		}

		return userTranslationSettings;
	}

	//Equivalent to convertRS* in the *RetrieveUtils.java classes for nonstatic data
	//mimics PvpHistoryProto in Battle.proto (PvpBattleHistory.java)
	//made static final class because http://docs.spring.io/spring/docs/3.0.x/spring-framework-reference/html/jdbc.html
	//says so (search for "private static final")
	private static final class UserTranslationSettingsForClientMapper implements
			RowMapper<TranslationSettingsForUser> {

		private static List<String> columnsSelected;

		@Override
		public TranslationSettingsForUser mapRow(ResultSet rs, int rowNum)
				throws SQLException {
			TranslationSettingsForUser tsfu = new TranslationSettingsForUser();
			tsfu.setId(rs.getString(DBConstants.TRANSLATION_SETTINGS_FOR_USER__ID));
			tsfu.setReceiverUserId(rs
					.getString(DBConstants.TRANSLATION_SETTINGS_FOR_USER__RECEIVER_USER_ID));
			tsfu.setSenderUserId(rs
					.getString(DBConstants.TRANSLATION_SETTINGS_FOR_USER__SENDER_USER_ID));
			tsfu.setLanguage(rs
					.getString(DBConstants.TRANSLATION_SETTINGS_FOR_USER__LANGUAGE));
			tsfu.setChatType(rs.getString(DBConstants.TRANSLATION_SETTINGS_FOR_USER__CHAT_TYPE));
			tsfu.setTranslationsOn(rs.getBoolean(DBConstants.TRANSLATION_SETTINGS_FOR_USER__TRANSLATIONS_ON));

			return tsfu;
		}

		public static List<String> getColumnsSelected() {
			if (null == columnsSelected) {
				columnsSelected = new ArrayList<String>();
				columnsSelected.add(DBConstants.BATTLE_ITEM_FOR_USER__ID);
				columnsSelected.add(DBConstants.BATTLE_ITEM_FOR_USER__USER_ID);
				columnsSelected
						.add(DBConstants.BATTLE_ITEM_FOR_USER__BATTLE_ITEM_ID);
				columnsSelected.add(DBConstants.BATTLE_ITEM_FOR_USER__QUANTITY);
			}
			return columnsSelected;
		}
	}

}
