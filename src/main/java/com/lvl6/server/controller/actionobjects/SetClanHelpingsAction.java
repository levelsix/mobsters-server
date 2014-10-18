package com.lvl6.server.controller.actionobjects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.Clan;
import com.lvl6.info.ClanHelp;
import com.lvl6.info.User;
import com.lvl6.proto.ClanProto.ClanHelpProto;
import com.lvl6.proto.EventStartupProto.StartupResponseProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanHelpRetrieveUtil;
import com.lvl6.utils.CreateInfoProtoUtils;

public class SetClanHelpingsAction implements StartUpAction
{
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private final StartupResponseProto.Builder resBuilder;
	private final User user;
	private final int userId;
	private final ClanHelpRetrieveUtil clanHelpRetrieveUtil;
	
	public SetClanHelpingsAction(
		StartupResponseProto.Builder resBuilder, User user,
		int userId, ClanHelpRetrieveUtil clanHelpRetrieveUtil)
	{
		this.resBuilder = resBuilder;
		this.user = user;
		this.userId = userId;
		this.clanHelpRetrieveUtil = clanHelpRetrieveUtil;
	}
	
	//derived state
	private Map<Integer, List<ClanHelp>> allSolicitations;
	
	//Extracted from Startup
	@Override
	public void setUp(StartUpResource fillMe)
	{
		allSolicitations = clanHelpRetrieveUtil
			.getUserIdToClanHelp(
				user.getClanId(),
				userId);
		log.info(String.format("allSolicitations=%s", allSolicitations));
		

		if (null == allSolicitations || allSolicitations.isEmpty()) {
			return;
		}
		fillMe.addUserId(allSolicitations.keySet());
		
	}

	@Override
	public void execute( StartUpResource useMe )
	{
		if (null == allSolicitations || allSolicitations.isEmpty()) {
			return;
		}
		Map<Integer, User> solicitors = useMe.getUserIdsToUsers(allSolicitations.keySet());
		
		if (solicitors.isEmpty()) {
			return;
		}
		
		Map<Integer, Clan> clanIdsToClans = useMe.getClanIdsToClans();
		
		//convert all solicitors into MinimumUserProtos
		Map<Integer, MinimumUserProto> mupSolicitors = new HashMap<Integer, MinimumUserProto>();
		for (Integer solicitorId : solicitors.keySet()) {
			User moocher = solicitors.get(solicitorId);
			int clanId = moocher.getClanId();
			Clan c = null;
			
			if (clanIdsToClans.containsKey(solicitorId)) {
				c = clanIdsToClans.get(clanId);
			}
			
			MinimumUserProto mup = CreateInfoProtoUtils.createMinimumUserProtoFromUserAndClan(moocher, c);
			mupSolicitors.put(solicitorId, mup);
		}

		for (Integer solicitorId : allSolicitations.keySet()) {
			List<ClanHelp> solicitations = allSolicitations.get(solicitorId);

			User solicitor = solicitors.get( solicitorId );
			MinimumUserProto mup = mupSolicitors.get( solicitorId );

			for (ClanHelp aid : solicitations) {
				
				//null for clan since mup exists and mup has clan in it
				ClanHelpProto chp = CreateInfoProtoUtils
					.createClanHelpProtoFromClanHelp(aid, solicitor, null, mup);

				resBuilder.addClanHelpings(chp);
			}
		}
	}
	
}
