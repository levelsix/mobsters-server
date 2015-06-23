package com.lvl6.eventhandlers;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.hazelcast.core.ITopic;
import com.hazelcast.core.Message;
import com.hazelcast.core.MessageListener;
import com.lvl6.clansearch.ClanSearch;
import com.lvl6.misc.ReloadAllRareChangeStaticData;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ClanProto.UserClanStatus;
import com.lvl6.retrieveutils.ClanChatPostRetrieveUtils2;
import com.lvl6.retrieveutils.UserClanRetrieveUtils2;
import com.lvl6.server.ServerMessage;

public class ServerEventHandler implements MessageListener<ServerMessage>,
		InitializingBean {

	private static final Logger log = LoggerFactory
			.getLogger(ServerEventHandler.class);

	@Resource(name = "serverEvents")
	protected ITopic<ServerMessage> topic;

	@Resource(name = "staticDataReloadDone")
	protected ITopic<ServerMessage> staticDataReloadDone;
	
	@Autowired
	protected ReloadAllRareChangeStaticData reloadAllRareChangeStaticData;
	
	@Autowired
	protected ServerToggleRetrieveUtils toggle;

	public ITopic<ServerMessage> getStaticDataReloadDone() {
		return staticDataReloadDone;
	}

	public void setStaticDataReloadDone(
			ITopic<ServerMessage> staticDataReloadDone) {
		this.staticDataReloadDone = staticDataReloadDone;
	}

	public ITopic<ServerMessage> getTopic() {
		return topic;
	}

	public void setTopic(ITopic<ServerMessage> topic) {
		this.topic = topic;
	}

	@Autowired
	protected ClanChatPostRetrieveUtils2 clanChatPostRetrieveUtil;

	public ClanChatPostRetrieveUtils2 getClanChatPostRetrieveUtil() {
		return clanChatPostRetrieveUtil;
	}

	public void setClanChatPostRetrieveUtil(
			ClanChatPostRetrieveUtils2 clanChatPostRetrieveUtil) {
		this.clanChatPostRetrieveUtil = clanChatPostRetrieveUtil;
	}

	@Autowired
	protected UserClanRetrieveUtils2 userClanRetrieveUtil;

	public UserClanRetrieveUtils2 getUserClanRetrieveUtil() {
		return userClanRetrieveUtil;
	}

	public void setUserClanRetrieveUtil(
			UserClanRetrieveUtils2 userClanRetrieveUtil) {
		this.userClanRetrieveUtil = userClanRetrieveUtil;
	}

	@Autowired
	protected ClanSearch clanSearch;

	public ClanSearch getClanSearch() {
		return clanSearch;
	}

	public void setClanSearch(ClanSearch clanSearch) {
		this.clanSearch = clanSearch;
	}

	@Override
	public void onMessage(Message<ServerMessage> msg) {
		log.info("Handling serverEvent of type: " + msg.getMessageObject());
		if (msg.getMessageObject().equals(ServerMessage.RELOAD_STATIC_DATA)) {
			log.info("Reloading all static data");
			reloadAllRareChangeStaticData.reloadAllRareChangeStaticData();
			if(toggle.getToggleValueForName(
					ControllerConstants.SERVER_TOGGLE__OLD_CLAN_SEARCH) {
				reloadRecommendedClans();
			}
			getStaticDataReloadDone().publish(
					ServerMessage.DONE_RELOADING_STATIC_DATA);
		}
	}


	public void reloadRecommendedClans() {
		log.info("recalculating recommended clans");
		try {
			//find the last chat time for every clan
			Map<String, Date> clanIdsToLastChatTime = clanChatPostRetrieveUtil
					.getLastChatPostForAllClans();

			//log.info("clanLastChats: {}", clanIdsToLastChatTime);

			//find the amount of members for every clan
			List<String> statuses = new ArrayList<String>();
			statuses.add(UserClanStatus.LEADER.name());
			statuses.add(UserClanStatus.JUNIOR_LEADER.name());
			statuses.add(UserClanStatus.CAPTAIN.name());
			statuses.add(UserClanStatus.MEMBER.name());
			Map<String, Integer> clanIdsToClanSize = userClanRetrieveUtil
					.getClanSizeForStatuses(statuses);

			//log.info("clanSizes: {}", clanIdsToClanSize);

			Collection<String> clanIds = clanIdsToClanSize.keySet();

			for (String cId : clanIds) {
				int clanSize = clanIdsToClanSize.get(cId);

				//not all clans may have chatted
				Date lastChatTime = ControllerConstants.INCEPTION_DATE;
				if (clanIdsToLastChatTime.containsKey(cId)) {
					lastChatTime = clanIdsToLastChatTime.get(cId);
				}

				clanSearch.updateClanSearchRank(cId, clanSize, lastChatTime);
			}

			log.info("finished calculating recommended clans");
		} catch (Exception e) {
			log.error("failed to calculate recommended clans", e);
		}

	}

	@Override
	public void afterPropertiesSet() throws Exception {
		log.info("Adding serverEvent listener");
		topic.addMessageListener(this);
	}

}
