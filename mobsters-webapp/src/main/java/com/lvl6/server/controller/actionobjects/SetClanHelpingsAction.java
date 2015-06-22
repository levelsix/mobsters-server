package com.lvl6.server.controller.actionobjects;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.info.Clan;
import com.lvl6.info.ClanHelp;
import com.lvl6.info.User;
import com.lvl6.proto.ClanProto.ClanDataProto;
import com.lvl6.proto.ClanProto.ClanHelpProto;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.retrieveutils.ClanHelpRetrieveUtil;
import com.lvl6.utils.CreateInfoProtoUtils;

@Component@Scope("prototype")public class SetClanHelpingsAction implements StartUpAction {
	private static Logger log = LoggerFactory.getLogger( SetClanHelpingsAction.class);

	@Autowired protected ClanHelpRetrieveUtil clanHelpRetrieveUtil;
	@Autowired protected CreateInfoProtoUtils createInfoProtoUtils;

	
	protected ClanDataProto.Builder cdpBuilder;
	protected User user;
	protected String userId;

	public void wire(ClanDataProto.Builder cdpBuilder, User user, String userId) {
		this.cdpBuilder = cdpBuilder;
		this.user = user;
		this.userId = userId;
	}

	//derived state
	private Map<String, List<ClanHelp>> allSolicitations;

	//Extracted from Startup
	@Override
	public void setUp(StartUpResource fillMe) {
		allSolicitations = clanHelpRetrieveUtil.getUserIdToClanHelp(
				user.getClanId(), userId);
//		log.info("allSolicitations={}", allSolicitations);

		if (null == allSolicitations || allSolicitations.isEmpty()) {
			return;
		}
		fillMe.addUserId(allSolicitations.keySet());

	}

	@Override
	public void execute(StartUpResource useMe) {
		if (null == allSolicitations || allSolicitations.isEmpty()) {
			return;
		}
		Map<String, User> solicitors = useMe.getUserIdsToUsers(allSolicitations
				.keySet());

		if (null == solicitors || solicitors.isEmpty()) {
			return;
		}

		Map<String, Clan> clanIdsToClans = useMe.getClanIdsToClans();

		//convert all solicitors into MinimumUserProtos
		Map<String, MinimumUserProto> mupSolicitors = new HashMap<String, MinimumUserProto>();
		for (String solicitorId : solicitors.keySet()) {
			User moocher = solicitors.get(solicitorId);
			String clanId = moocher.getClanId();
			Clan c = null;

			if (clanIdsToClans.containsKey(solicitorId)) {
				c = clanIdsToClans.get(clanId);
			}

			MinimumUserProto mup = createInfoProtoUtils
					.createMinimumUserProtoFromUserAndClan(moocher, c);
			mupSolicitors.put(solicitorId, mup);
		}

		for (String solicitorId : allSolicitations.keySet()) {
			List<ClanHelp> solicitations = allSolicitations.get(solicitorId);

			User solicitor = solicitors.get(solicitorId);
			MinimumUserProto mup = mupSolicitors.get(solicitorId);

			for (ClanHelp aid : solicitations) {

				//null for clan since mup exists and mup has clan in it
				ClanHelpProto chp = createInfoProtoUtils
						.createClanHelpProtoFromClanHelp(aid, solicitor, null,
								mup);

				cdpBuilder.addClanHelpings(chp);
			}
		}
	}

}
