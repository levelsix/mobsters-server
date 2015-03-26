package com.lvl6.retrieveutils.rarechange;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.info.ChatTranslations;
import com.lvl6.properties.DBConstants;
import com.lvl6.proto.ChatProto.ChatType;
import com.lvl6.proto.ChatProto.TranslateLanguages;
import com.lvl6.utils.DBConnection;

@Component
@DependsOn("gameServer")
public class ChatTranslationsRetrieveUtils {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private static Map<String, ChatTranslations> chatTranslationsIdsToChatTranslationss;
	
	private static Map<String, List<ChatTranslations>> chatIdsToChatTranslations;
	//key:booster pack id --> value:(key: booster item id --> value: booster item)

	private static final String TABLE_NAME = DBConstants.TABLE_CHAT_TRANSLATIONS;

	public static Map<String, ChatTranslations> getChatTranslationsIdsToChatTranslationss() {
		log.debug("retrieving all ChatTranslationss data map");
		if (chatTranslationsIdsToChatTranslationss == null) {
			setStaticChatTranslationsIdsToChatTranslationss();
		}
		return chatTranslationsIdsToChatTranslationss;
	}

	public static ChatTranslations getChatTranslationsForChatTranslationsId(String chatTranslationsId) {
		log.debug("retrieve chatTranslations data for chatTranslations " + chatTranslationsId);
		if (chatTranslationsIdsToChatTranslationss == null) {
			setStaticChatTranslationsIdsToChatTranslationss();
		}
		return chatTranslationsIdsToChatTranslationss.get(chatTranslationsId);
	}
	
	public static Map<String, List<ChatTranslations>> getChatTranslationsForSpecificChatIds(List<String> chatIds) {
		log.debug("retrieve chatTranslations data for chat Ids " + chatIds);
		if (chatIdsToChatTranslations == null) {
			setStaticChatTranslationsIdsToChatTranslationss();
		}
		Map<String, List<ChatTranslations>> chatTranslationsMap = new HashMap<String, List<ChatTranslations>>();
		
		for(String chatId : chatIds) {
			chatTranslationsMap.put(chatId, chatIdsToChatTranslations.get(chatId));
		}
		return chatTranslationsMap;
	}

	private static void setStaticChatTranslationsIdsToChatTranslationss() {
		log.debug("setting static map of chatTranslationsIds to chatTranslationss");

		Connection conn = DBConnection.get().getConnection();
		ResultSet rs = null;
		try {
			if (conn != null) {
				rs = DBConnection.get().selectWholeTable(conn, TABLE_NAME);

				if (rs != null) {
					try {
						rs.last();
						rs.beforeFirst();
						HashMap<String, ChatTranslations> chatTranslationsIdsToChatTranslationssTemp = new HashMap<String, ChatTranslations>();
						HashMap<String, List<ChatTranslations>> chatIdsToChatTranslationssTemp = new HashMap<String, List<ChatTranslations>>();
						while (rs.next()) {  //should only be one
							ChatTranslations chatTranslations = convertRSRowToChatTranslations(rs);
							if (chatTranslations != null) {
								String chatId = chatTranslations.getChatId();
								if(chatIdsToChatTranslationssTemp.containsKey(chatId)) {
									chatIdsToChatTranslationssTemp.get(chatId).add(chatTranslations);
								}
								else {
									List<ChatTranslations> list = new ArrayList<ChatTranslations>();
									list.add(chatTranslations);
									chatIdsToChatTranslationssTemp.put(chatId, list);
								}
								chatTranslationsIdsToChatTranslationssTemp.put(
										chatTranslations.getId(), chatTranslations);
							}
						}
						chatTranslationsIdsToChatTranslationss = chatTranslationsIdsToChatTranslationssTemp;
						chatIdsToChatTranslations = chatIdsToChatTranslationssTemp;
					} catch (SQLException e) {
						log.error("problem with database call.", e);

					}
				}
			}
		} catch (Exception e) {
			log.error("booster item retrieve db error.", e);
		} finally {
			DBConnection.get().close(rs, null, conn);
		}
	}

	public static void reload() {
		setStaticChatTranslationsIdsToChatTranslationss();
	}

	/*
	 * assumes the resultset is apprpriately set up. traverses the row it's on.
	 */
	private static ChatTranslations convertRSRowToChatTranslations(ResultSet rs)
			throws SQLException {
		String id = rs.getString(DBConstants.CHAT_TRANSLATIONS__ID);
		String chatTypeString = rs.getString(DBConstants.CHAT_TRANSLATIONS__CHAT_TYPE);
		ChatType ct = ChatType.valueOf(chatTypeString);
		
		String chatId = rs.getString(DBConstants.CHAT_TRANSLATIONS__CHAT_ID);

		String language = rs.getString(DBConstants.CHAT_TRANSLATIONS__LANGUAGE);
		TranslateLanguages tl = TranslateLanguages.valueOf(language);
		
		String text = rs.getString(DBConstants.CHAT_TRANSLATIONS__TEXT);

		ChatTranslations chatTranslations = new ChatTranslations(id, ct, chatId, tl, text);
		return chatTranslations;
	}
}
