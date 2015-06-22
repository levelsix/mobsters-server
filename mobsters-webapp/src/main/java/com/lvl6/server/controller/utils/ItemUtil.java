package com.lvl6.server.controller.utils;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lvl6.info.ItemForUser;
import com.lvl6.info.ItemForUserUsage;
import com.lvl6.proto.ItemsProto.UserItemProto;
import com.lvl6.proto.ItemsProto.UserItemUsageProto;

@Component
public class ItemUtil {

	private static Logger log = LoggerFactory.getLogger(ItemUtil.class);

	public static List<ItemForUserUsage> javafyUserItemUsageProto(
			List<UserItemUsageProto> itemsUsedProtos) {

		List<ItemForUserUsage> itemsUsed = new ArrayList<ItemForUserUsage>();

		for (UserItemUsageProto uiup : itemsUsedProtos) {
			ItemForUserUsage ifuu = new ItemForUserUsage();

			ifuu.setUserId(uiup.getUserUuid());
			ifuu.setItemId(uiup.getItemId());

			Date timeOfEntry = new Date(uiup.getTimeOfEntry());
			ifuu.setTimeOfEntry(timeOfEntry);

			ifuu.setUserDataId(uiup.getUserDataUuid());

			String gameActionType = uiup.getActionType().name();
			ifuu.setActionType(gameActionType);

			itemsUsed.add(ifuu);
		}

		return itemsUsed;
	}

	public static List<ItemForUser> javafyUserItemProto(
			List<UserItemProto> userItemsProtos) {
		List<ItemForUser> userItems = new ArrayList<ItemForUser>();

		for (UserItemProto uip : userItemsProtos) {
			ItemForUser ifu = new ItemForUser();

			ifu.setUserId(uip.getUserUuid());
			ifu.setItemId(uip.getItemId());
			ifu.setQuantity(uip.getQuantity());

			userItems.add(ifu);
		}

		return userItems;
	}
}
