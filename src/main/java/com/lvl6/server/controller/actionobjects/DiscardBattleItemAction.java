package com.lvl6.server.controller.actionobjects;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.BattleItemForUser;
import com.lvl6.info.User;
import com.lvl6.proto.EventBattleItemProto.DiscardBattleItemResponseProto.Builder;
import com.lvl6.proto.EventBattleItemProto.DiscardBattleItemResponseProto.DiscardBattleItemStatus;
import com.lvl6.utils.utilmethods.DeleteUtil;

public class DiscardBattleItemAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String userId;
	private User user;
	private List<BattleItemForUser> discardedBattleItemList;
	protected DeleteUtil deleteUtil;

	public DiscardBattleItemAction(String userId, User user,
			List<BattleItemForUser> discardedBattleItemList,
			DeleteUtil deleteUtil) {
		super();
		this.userId = userId;
		this.user = user;
		this.discardedBattleItemList = discardedBattleItemList;
		this.deleteUtil = deleteUtil;
	}

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(DiscardBattleItemStatus.FAIL_OTHER);

		boolean valid = false;
		valid = verifySemantics(resBuilder);

		if (!valid) {
			return;
		}

		boolean success = writeChangesToDB(resBuilder);
		if (!success) {
			return;
		}

		resBuilder.setStatus(DiscardBattleItemStatus.SUCCESS);
	}

	private boolean verifySemantics(Builder resBuilder) {
		if (null == user) {
			resBuilder.setStatus(DiscardBattleItemStatus.FAIL_OTHER);
			log.error("no user with id={}", userId);
			return false;
		}

		if (discardedBattleItemList == null
				|| discardedBattleItemList.isEmpty()) {
			resBuilder
					.setStatus(DiscardBattleItemStatus.FAIL_BATTLE_ITEMS_DONT_EXIST);
			log.error("no battle item list");
			return false;
		}

		return true;
	}

	private boolean writeChangesToDB(Builder resBuilder) {
		int numDeleted = deleteUtil.deleteUserBattleItems(userId,
				discardedBattleItemList);
		if (numDeleted != discardedBattleItemList.size()) {
			log.error("did not properly delete all user battle items");
			return false;
		}

		return true;
	}

}
