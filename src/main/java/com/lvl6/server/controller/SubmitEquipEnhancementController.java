package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.SubmitEquipEnhancementRequestEvent;
import com.lvl6.events.response.SubmitEquipEnhancementResponseEvent;
import com.lvl6.events.response.UpdateClientUserResponseEvent;
import com.lvl6.info.Equipment;
import com.lvl6.info.User;
import com.lvl6.info.UserEquip;
import com.lvl6.misc.MiscMethods;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.EventProto.SubmitEquipEnhancementRequestProto;
import com.lvl6.proto.EventProto.SubmitEquipEnhancementResponseProto;
import com.lvl6.proto.EventProto.SubmitEquipEnhancementResponseProto.Builder;
import com.lvl6.proto.EventProto.SubmitEquipEnhancementResponseProto.EnhanceEquipStatus;
import com.lvl6.proto.InfoProto.FullUserEquipProto;
import com.lvl6.proto.InfoProto.MinimumUserProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.retrieveutils.rarechange.EquipmentRetrieveUtils;
import com.lvl6.utils.CreateInfoProtoUtils;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;
import com.lvl6.utils.utilmethods.UpdateUtils;

@Component @DependsOn("gameServer") public class SubmitEquipEnhancementController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());

	public SubmitEquipEnhancementController() {
		numAllocatedThreads = 3;
	}

	@Override
	public RequestEvent createRequestEvent() {
		return new SubmitEquipEnhancementRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_SUBMIT_EQUIP_ENHANCEMENT_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		SubmitEquipEnhancementRequestProto reqProto = ((SubmitEquipEnhancementRequestEvent)event)
				.getSubmitEquipEnhancementRequestProto();

		MinimumUserProto senderProto = reqProto.getSender();
		long enhancingUserEquipId = reqProto.getEnhancingUserEquipId(); 
		List<Long> feederUserEquipIds = reqProto.getFeederUserEquipIdsList();
		Timestamp clientTime = new Timestamp(reqProto.getClientTime());
		int userId = senderProto.getUserId();


		SubmitEquipEnhancementResponseProto.Builder resBuilder = SubmitEquipEnhancementResponseProto.newBuilder();
		resBuilder.setSender(senderProto);

		server.lockPlayer(senderProto.getUserId(), getClass().getSimpleName());

		try {
			//The main user equip to enhance.
			UserEquip enhancingUserEquip = RetrieveUtils.userEquipRetrieveUtils()
					.getSpecificUserEquip(enhancingUserEquipId);

			User user = RetrieveUtils.userRetrieveUtils().getUserById(senderProto.getUserId());
			int previousSilver = 0;

			//The user equips to be sacrified for enhancing.
			//this could be null, or does not contain null but could still be empty
			List<UserEquip> feederUserEquips = RetrieveUtils.userEquipRetrieveUtils()
					.getSpecificUserEquips(feederUserEquipIds);

			Map<Integer, Equipment> equipmentIdsToEquipment = EquipmentRetrieveUtils.getEquipmentIdsToEquipment();

			boolean legitEquip = checkEquip(resBuilder, user, userId, enhancingUserEquip, feederUserEquipIds, feederUserEquips, 
					equipmentIdsToEquipment, clientTime);

			boolean successful = false;
			List<Integer> enhancementInteger = new ArrayList<Integer>();
			List<Integer> enhancementFeederIds = new ArrayList<Integer>(); 
			Map<String, Integer> money = new HashMap<String, Integer>();

			int enhancementPercentageBeforeEnhancement = enhancingUserEquip.getEnhancementPercentage();
			int enhancementPercentageAfterEnhancement = MiscMethods.calculateEnhancementForEquip(enhancingUserEquip,
					feederUserEquips)+enhancementPercentageBeforeEnhancement;
			
			if (legitEquip) {
				previousSilver = user.getCoins() + user.getVaultBalance();
				successful = writeChangesToDB(resBuilder, user, enhancingUserEquip,
						feederUserEquipIds, feederUserEquips, money, enhancementPercentageAfterEnhancement);
			}
		
			int enhancementId = 0;
			if (successful) {
				enhancementId = enhancementInteger.get(0);
				FullUserEquipProto fuep = CreateInfoProtoUtils.createFullUserEquipProto(enhancingUserEquip.getId(),
						userId, enhancingUserEquip.getEquipId(), enhancingUserEquip.getLevel(), enhancementPercentageAfterEnhancement);
				resBuilder.setResultingEquip(fuep);
			}

			SubmitEquipEnhancementResponseEvent resEvent = new SubmitEquipEnhancementResponseEvent(senderProto.getUserId());
			resEvent.setTag(event.getTag());
			resEvent.setSubmitEquipEnhancementResponseProto(resBuilder.build());  
			server.writeEvent(resEvent);

			if (successful) {
				UpdateClientUserResponseEvent resEventUpdate = MiscMethods.createUpdateClientUserResponseEventAndUpdateLeaderboard(user);
				resEventUpdate.setTag(event.getTag());
				server.writeEvent(resEventUpdate);

				writeToUserCurrencyHistory(user, clientTime, money, previousSilver);
				writeIntoEquipEnhancementHistory(enhancingUserEquip, feederUserEquips, false, clientTime);		      
				writeIntoEquipEnhancementFeederHistory(enhancingUserEquip.getId(), feederUserEquips);
				MiscMethods.writeIntoDUEFE(enhancingUserEquip, feederUserEquips, enhancementId, enhancementFeederIds);
			}

		} catch (Exception e) {
			log.error("exception in EnhanceEquip processEvent", e);
		} finally {
			server.unlockPlayer(senderProto.getUserId(), getClass().getSimpleName());   
		}
	}

	//delete enhancing user equip; make entry in equip enhancement table
	//delete all the feeder user equips; make entries in equip enhancement feeders table
	//delete feederuserequip, write enhanceduserequip to the db
	private boolean writeChangesToDB(Builder resBuilder, User user, UserEquip mainUserEquip,
			List<Long> feederUserEquipIds, List<UserEquip> feederUserEquips, Map<String, Integer> money, int enhancementPercentageAfterEnhancement) {

		int silverChange = -1*costOfEnhancement(feederUserEquips);
		int goldChange = 0;

		if (!user.updateRelativeDiamondsCoinsExperienceNaive(goldChange, silverChange, 0)) {
			log.error("problem with updating user stats: diamondChange=" + goldChange
					+ ", coinChange=" + silverChange + ", user is " + user);
		} else {
			//everything went well
			if (0 != silverChange) {
				money.put(MiscMethods.silver, silverChange);
			}
		}
		
		//update the main user equip's enhancement
		if(!UpdateUtils.get().updateUserEquipEnhancementPercentage(mainUserEquip.getId(), enhancementPercentageAfterEnhancement)) {
			log.error("problem updating user equip's enhancement");
		}
		
		//unequip all the feeder user equips
		List<UserEquip> userEquips = new ArrayList<UserEquip>(feederUserEquips);
		for (UserEquip ue : userEquips) {
			if (!MiscMethods.unequipUserEquipIfEquipped(user, ue)) {
				resBuilder.setStatus(EnhanceEquipStatus.OTHER_FAIL);
				log.error("problem with unequipping user equip" + ue.getId());
				return false;
			}
		}

		//delete the feeder user equips
		List<Long> allUserEquipIds = new ArrayList<Long>(feederUserEquipIds);
		if(!DeleteUtils.get().deleteUserEquips(allUserEquipIds)) {
			resBuilder.setStatus(EnhanceEquipStatus.OTHER_FAIL);
			log.error("could not delete user equips with ids: "
					+ MiscMethods.shallowListToString(allUserEquipIds));
			return false;
		}
		return true;
	}

	//feederUserEquips could be null or empty
	private List<Equipment> getFeederEquips(List<UserEquip> feederUserEquips, Map<Integer, Equipment> equipmentIdsToEquipment) {
		List<Equipment> returnValue = new ArrayList<Equipment>();

		for(UserEquip feederUserEquip: feederUserEquips) {
			int equipId = feederUserEquip.getEquipId();
			Equipment e = equipmentIdsToEquipment.get(equipId);
			returnValue.add(e);
		}

		return returnValue;
	}

	private boolean checkEquip(Builder resBuilder, User user, int userId, UserEquip enhancingUserEquip, List<Long> feederUserEquipIds, 
			List<UserEquip> feederUserEquips, Map<Integer, Equipment> equipmentIdsToEquipment, Timestamp startTime) {
		//the case where client asked for a user equip and user equip is not there.
		if (!MiscMethods.checkClientTimeAroundApproximateNow(startTime)) {
			resBuilder.setStatus(EnhanceEquipStatus.CLIENT_TOO_APART_FROM_SERVER_TIME);
			log.error("client time too apart of server time. client time=" + startTime + ", servertime~="
					+ new Date());
			return false;
		}

		if (null == enhancingUserEquip || null == feederUserEquips ||
				feederUserEquipIds.size() != feederUserEquips.size()) {
			resBuilder.setStatus(EnhanceEquipStatus.MAIN_OR_FEEDER_OR_EQUIPS_NONEXISTENT);
			log.error("main/feeder equips non existent: enhancingUserEquip=" + enhancingUserEquip
					+ ", feederUserEquips=" + MiscMethods.shallowListToString(feederUserEquips) + ", requested feederUserEquipIds="
					+ MiscMethods.shallowListToString(feederUserEquipIds));
			return false;
		}
		//user equips exist, check if equips exist
		int equipId = enhancingUserEquip.getEquipId();
		Equipment anEquip = equipmentIdsToEquipment.get(equipId);
		List<Equipment> feederEquips = getFeederEquips(feederUserEquips, equipmentIdsToEquipment);
		if(null == anEquip || feederEquips.isEmpty() || feederEquips.size() != feederUserEquips.size()) {
			resBuilder.setStatus(EnhanceEquipStatus.MAIN_OR_FEEDER_OR_EQUIPS_NONEXISTENT);
			log.error("equip to enhance=" + anEquip + ", feederEquips=" + MiscMethods.shallowListToString(feederEquips)
					+ ", feederUserEquips=" + MiscMethods.shallowListToString(feederUserEquips));
			return false;
		}

		if (MiscMethods.isEquipAtMaxEnhancementLevel(enhancingUserEquip)) {
			resBuilder.setStatus(EnhanceEquipStatus.TRYING_TO_SURPASS_MAX_LEVEL);
			log.error("user is trying to enhance equip past the max level. user equip to enhance = " 
					+ enhancingUserEquip);
			return false;
		}

		if(user.getCoins() < costOfEnhancement(feederUserEquips)) {
			resBuilder.setStatus(EnhanceEquipStatus.NOT_ENOUGH_SILVER);
			log.error("not enough silver to enhance, user has" + user.getCoins() + "silver but requires" + costOfEnhancement(feederUserEquips));
			return false;
		}

		resBuilder.setStatus(EnhanceEquipStatus.SUCCESS);
		return true;
	}

	//based on total stats of feeder equips
	private int costOfEnhancement(List<UserEquip> feederUserEquips) {
		Iterator<UserEquip> enhancementIterator = feederUserEquips.iterator();
		int totalStats=0;
		//calculate total stats of feeder equips
		while(enhancementIterator.hasNext()) {
			UserEquip feederEquip = enhancementIterator.next();
			totalStats += MiscMethods.attackPowerForEquip(feederEquip.getEquipId(), feederEquip.getLevel(), feederEquip.getEnhancementPercentage()) + MiscMethods.defensePowerForEquip(feederEquip.getEquipId(), feederEquip.getLevel(), feederEquip.getEnhancementPercentage());
		}
		return (int)Math.ceil(totalStats * ControllerConstants.ENHANCEMENT__COST_CONSTANT);
	}

	public void writeToUserCurrencyHistory(User aUser, Timestamp date, Map<String, Integer> money,
			int previousSilver) {
		Map<String, Integer> previousGoldSilver = new HashMap<String, Integer>();
		Map<String, String> reasonsForChanges = new HashMap<String, String>();
		String reasonForChange = ControllerConstants.UCHRFC__ENHANCING;
		String silver = MiscMethods.silver;

		previousGoldSilver.put(silver, previousSilver);
		reasonsForChanges.put(silver, reasonForChange);

		MiscMethods.writeToUserCurrencyOneUserGoldAndOrSilver(aUser, date, money, previousGoldSilver, reasonsForChanges);
	}

	private void writeIntoEquipEnhancementHistory(UserEquip equipUnderEnhancement, List<UserEquip> feederUserEquips,
			boolean speedUp, Timestamp clientTime) {
		long equipEnhancementId = equipUnderEnhancement.getId();
		int userId = equipUnderEnhancement.getUserId();
		int equipId = equipUnderEnhancement.getEquipId();
		int equipLevel = equipUnderEnhancement.getLevel();
		int previousEnhancementPercentage = equipUnderEnhancement.getEnhancementPercentage();

		int currentEnhancementPercentage = MiscMethods.calculateEnhancementForEquip(equipUnderEnhancement,
				feederUserEquips)+previousEnhancementPercentage;

		Timestamp timeOfEnhancement = clientTime;


		int numInserted = InsertUtils.get().insertIntoEquipEnhancementHistory(equipEnhancementId,
				userId, equipId, equipLevel, currentEnhancementPercentage, previousEnhancementPercentage,
				timeOfEnhancement);

		if(1 != numInserted) {
			log.error("could not record equip enhancing collection. equipUnderEnhancement="
					+ equipUnderEnhancement + ", speedUp=" + speedUp + ", clientTime=" + clientTime);
		}
	}

	private void writeIntoEquipEnhancementFeederHistory(long l, 
			List<UserEquip> feeders) {
		int size = feeders.size();
		int numInserted = InsertUtils.get()
				.insertMultipleIntoEquipEnhancementFeedersHistory(l, feeders);
		if(size != numInserted) {
			log.error("numInserted into feeder history table: " + numInserted + ", should have been:" + size);
		}
	}

}
