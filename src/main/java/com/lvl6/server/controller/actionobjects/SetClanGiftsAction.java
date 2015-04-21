package com.lvl6.server.controller.actionobjects;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.ClanGiftForUser;
import com.lvl6.info.User;
import com.lvl6.proto.ClanGiftsProto.UserClanGiftProto;
import com.lvl6.proto.EventStartupProto.StartupResponseProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanGiftForUserRetrieveUtils;
import com.lvl6.utils.CreateInfoProtoUtils;

public class SetClanGiftsAction implements StartUpAction {
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private final StartupResponseProto.Builder resBuilder;
	private final User user;
	private final String userId;
	private final ClanGiftForUserRetrieveUtils clanGiftForUserRetrieveUtils;
	private final CreateInfoProtoUtils createInfoProtoUtils;

	public SetClanGiftsAction(StartupResponseProto.Builder resBuilder, User user,
			String userId, ClanGiftForUserRetrieveUtils clanGiftForUserRetrieveUtils,
			CreateInfoProtoUtils createInfoProtoUtils) {
		super();
		this.resBuilder = resBuilder;
		this.user = user;
		this.userId = userId;
		this.clanGiftForUserRetrieveUtils = clanGiftForUserRetrieveUtils;
		this.createInfoProtoUtils = createInfoProtoUtils;
	}

	//derived state
	private List<ClanGiftForUser> allClanGifts;
	private List<String> userIds;

	//Extracted from Startup
	@Override
	public void setUp(StartUpResource fillMe) {
		allClanGifts = clanGiftForUserRetrieveUtils.getUserClanGiftsForUser(userId);
		log.info(String.format("allClanGifts=%s", allClanGifts));

		if (null == allClanGifts || allClanGifts.isEmpty()) {
			return;
		}
		
		userIds = new ArrayList<String>();
		for(ClanGiftForUser cgfu : allClanGifts) {
			userIds.add(cgfu.getGifterUserId());
		}
		
		fillMe.addUserId(userIds);

	}

	@Override
	public void execute(StartUpResource useMe) {
		if (null == allClanGifts || allClanGifts.isEmpty()) {
			return;
		}
		Map<String, User> gifters = useMe.getUserIdsToUsers(userIds);

		if (null == gifters || gifters.isEmpty()) {
			return;
		}

		//convert all solicitors into MinimumUserProtos
		Map<String, MinimumUserProto> mupGifters = new HashMap<String, MinimumUserProto>();
		for (String gifterId : gifters.keySet()) {
			User gifterUser = gifters.get(gifterId);
			
			MinimumUserProto mup = createInfoProtoUtils
					.createMinimumUserProtoFromUserAndClan(gifterUser, null);
			mupGifters.put(gifterId, mup);
		}

		for (ClanGiftForUser cgfu : allClanGifts) {
			//null for clan since mup exists and mup has clan in it
			MinimumUserProto mup = mupGifters.get(cgfu.getGifterUserId());
			UserClanGiftProto ucgp = createInfoProtoUtils
					.createUserClanGiftProto(cgfu, mup);

			resBuilder.addUserClanGifts(ucgp);

		}
	}

}
