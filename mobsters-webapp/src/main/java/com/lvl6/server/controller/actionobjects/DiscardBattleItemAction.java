package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.info.BattleItemForUser;
import com.lvl6.info.User;
import com.lvl6.proto.EventBattleItemProto.DiscardBattleItemResponseProto.Builder;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.retrieveutils.BattleItemForUserRetrieveUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

@Component@Scope("prototype")public class DiscardBattleItemAction {
	private static Logger log = LoggerFactory.getLogger( DiscardBattleItemAction.class);

	private String userId;
	private User user;
	private Map<Integer, Integer> deletedBattleItemIdsToQuantity;
	@Autowired protected BattleItemForUserRetrieveUtil battleItemForUserRetrieveUtil;
	@Autowired protected UpdateUtil updateUtil;

	public DiscardBattleItemAction(String userId, User user,
			Map<Integer, Integer> deletedBattleItemIdsToQuantity,
			BattleItemForUserRetrieveUtil battleItemForUserRetrieveUtil,
			UpdateUtil updateUtil) {
		super();
		this.userId = userId;
		this.user = user;
		this.deletedBattleItemIdsToQuantity = deletedBattleItemIdsToQuantity;
		this.battleItemForUserRetrieveUtil = battleItemForUserRetrieveUtil;
		this.updateUtil = updateUtil;
	}

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);

		boolean valid = false;
		valid = verifySemantics(resBuilder);

		if (!valid) {
			return;
		}

		boolean success = writeChangesToDB(resBuilder);
		if (!success) {
			return;
		}

		resBuilder.setStatus(ResponseStatus.SUCCESS);
	}

	private boolean verifySemantics(Builder resBuilder) {
		if (null == user) {
			resBuilder.setStatus(ResponseStatus.FAIL_OTHER);
			log.error("no user with id={}", userId);
			return false;
		}

		if (deletedBattleItemIdsToQuantity == null
				|| deletedBattleItemIdsToQuantity.isEmpty()) {
			resBuilder
					.setStatus(ResponseStatus.FAIL_INVALID_BATTLE_ITEMS);
			log.error("no battle item list");
			return false;
		}

		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		List<BattleItemForUser> updateList = createMapForUserBattleItems(deletedBattleItemIdsToQuantity);
		boolean success = updateUtil.updateUserBattleItems(userId, updateList);
		if(success) {
			return true;
		}
		else return false;
	}

	private List<BattleItemForUser> createMapForUserBattleItems(Map<Integer, Integer> deletedBattleItemIdsToQuantity) {
		//this is really a Set, since each BatleItemForUser is different than another.
		List<BattleItemForUser> userBattleItemsList = battleItemForUserRetrieveUtil.getUserBattleItemsForUser(userId);
		List<BattleItemForUser> updatedBattleItemsList = new ArrayList<BattleItemForUser>();

		for(BattleItemForUser bifu : userBattleItemsList) {
			if(deletedBattleItemIdsToQuantity.containsKey(bifu.getBattleItemId())) {
				int newQuantity = bifu.getQuantity() - deletedBattleItemIdsToQuantity.get(bifu.getBattleItemId());
				if(newQuantity < 0) {
					log.error("client deleted more items than the user has. has={}, deleted={}",
							userBattleItemsList, deletedBattleItemIdsToQuantity);
					bifu.setQuantity(0);
				}
				else bifu.setQuantity(newQuantity);

				updatedBattleItemsList.add(bifu);
			}
		}
		return updatedBattleItemsList;
	}

}
