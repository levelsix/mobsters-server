package com.lvl6.server.controller.actionobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.mobsters.db.jooq.generated.tables.pojos.MiniEventTimetableConfigPojo;
import com.lvl6.mobsters.jooq.daos.service.MiniEventLeaderBoardService;
import com.lvl6.proto.EventLeaderBoardProto.RetrieveMiniEventLeaderBoardResponseProto.Builder;
import com.lvl6.proto.LeaderBoardProto.MiniEventLeaderBoardProto;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.retrieveutils.rarechange.MiniEventTimetableRetrieveUtils;
import com.lvl6.utils.CreateInfoProtoUtils;

@Component
@Scope("prototype")
public class RetrieveMiniEventLeaderBoardAction {
	private static Logger log = LoggerFactory.getLogger( RetrieveMiniEventLeaderBoardAction.class);

	private String retrieverUserId;
	private int miniEventTimetableId;
	private int minRank;
	private int maxRank;
	private MiniEventLeaderBoardService leaderBoard;
	@Autowired protected UserRetrieveUtils2 userRetrieveUtils; 
	@Autowired protected CreateInfoProtoUtils createInfoProtoUtils; 
	@Autowired protected MiniEventTimetableRetrieveUtils miniEventTimetableRetrieveUtils;

	public RetrieveMiniEventLeaderBoardAction( String retrieverUserId, 
			int miniEventTimetableId, int minRank, int maxRank, 
			MiniEventLeaderBoardService leaderBoard, UserRetrieveUtils2 userRetrieveUtils,
			CreateInfoProtoUtils createInfoProtoUtils,
			MiniEventTimetableRetrieveUtils miniEventTimetableRetrieveUtils)
	{
		super();
		this.retrieverUserId = retrieverUserId;
		this.miniEventTimetableId = miniEventTimetableId;
		this.minRank = minRank;
		this.maxRank = maxRank;
		this.leaderBoard = leaderBoard;
		this.userRetrieveUtils = userRetrieveUtils;
		this.createInfoProtoUtils = createInfoProtoUtils;
		this.miniEventTimetableRetrieveUtils = miniEventTimetableRetrieveUtils;
	}

	//	//encapsulates the return value from this Action Object
	//	static class RetrieveMiniEventLeaderBoardResource {
	//
	//
	//		public RetrieveMiniEventLeaderBoardResource() {
	//
	//		}
	//	}
	//
	//	public RetrieveMiniEventLeaderBoardResource execute() {
	//
	//	}

	//derived state

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(ResponseStatus.FAIL_OTHER);

		//check out inputs before db interaction
		boolean valid = verifySyntax(resBuilder);

		if (!valid) {
			return;
		}

		valid = verifySemantics(resBuilder);

		if (!valid) {
			return;
		}

		retrieveInfo(resBuilder);
		resBuilder.setStatus(ResponseStatus.SUCCESS);
	}

	private boolean verifySyntax(Builder resBuilder) {		
		if(miniEventTimetableId <= 0) {
			log.error("invalid mini event timetable id = {}", miniEventTimetableId);
			return false;
		}
		
		if(minRank <= 0 || maxRank < minRank) {
			log.error("invalid min/max ranks sent by client, minRank = {}, maxRank = {}", 
					minRank, maxRank);
			return false;
		}
		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		MiniEventTimetableConfigPojo metcp = 
				miniEventTimetableRetrieveUtils.getTimetableForId(miniEventTimetableId);
		if(metcp == null) {
			log.error("mini event timetable is null, id={}", miniEventTimetableId);
			return false;
		}
		return true;
	}
	
	public void retrieveInfo(Builder resBuilder) {
		int retrieverRank = leaderBoard.getUserRank(retrieverUserId, miniEventTimetableId);
		MiniEventLeaderBoardProto.Builder b = MiniEventLeaderBoardProto.newBuilder();
		b.setRank(retrieverRank);
		resBuilder.setSenderLeaderBoardInfo(b.build());
		
		resBuilder.addAllLeaderBoardInfo(leaderBoard.getStrengths(miniEventTimetableId, minRank, maxRank));
	}


	public String getRetrieverUserId() {
		return retrieverUserId;
	}

	public void setRetrieverUserId(String retrieverUserId) {
		this.retrieverUserId = retrieverUserId;
	}

	public int getMinRank() {
		return minRank;
	}

	public void setMinRank(int minRank) {
		this.minRank = minRank;
	}

	public int getMaxRank() {
		return maxRank;
	}

	public void setMaxRank(int maxRank) {
		this.maxRank = maxRank;
	}

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}

	public CreateInfoProtoUtils getCreateInfoProtoUtils() {
		return createInfoProtoUtils;
	}

	public void setCreateInfoProtoUtils(CreateInfoProtoUtils createInfoProtoUtils) {
		this.createInfoProtoUtils = createInfoProtoUtils;
	}

	

}
