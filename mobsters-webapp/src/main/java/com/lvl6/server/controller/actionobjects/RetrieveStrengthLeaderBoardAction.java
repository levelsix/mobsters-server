package com.lvl6.server.controller.actionobjects;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.lvl6.info.StrengthLeaderBoard;
import com.lvl6.leaderboards.LeaderBoardImpl;
import com.lvl6.mobsters.jooq.daos.service.LeaderBoardService;
import com.lvl6.proto.EventLeaderBoardProto.RetrieveStrengthLeaderBoardResponseProto.Builder;
import com.lvl6.proto.LeaderBoardProto.StrengthLeaderBoardProto;
import com.lvl6.proto.SharedEnumConfigProto.ResponseStatus;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.utils.CreateInfoProtoUtils;

@Component@Scope("prototype")public class RetrieveStrengthLeaderBoardAction {
	private static Logger log = LoggerFactory.getLogger( RetrieveStrengthLeaderBoardAction.class);

	private String retrieverUserId;
	private int minRank;
	private int maxRank;
	private LeaderBoardService leaderBoard;
	@Autowired protected UserRetrieveUtils2 userRetrieveUtils; 
	@Autowired protected CreateInfoProtoUtils createInfoProtoUtils; 

	public RetrieveStrengthLeaderBoardAction( String retrieverUserId, 
			int minRank, int maxRank, LeaderBoardService leaderBoard,
			UserRetrieveUtils2 userRetrieveUtils,
			CreateInfoProtoUtils createInfoProtoUtils)
	{
		super();
		this.retrieverUserId = retrieverUserId;
		this.minRank = minRank;
		this.maxRank = maxRank;
		this.leaderBoard = leaderBoard;
		this.userRetrieveUtils = userRetrieveUtils;
		this.createInfoProtoUtils = createInfoProtoUtils;
	}

	//	//encapsulates the return value from this Action Object
	//	static class RetrieveStrengthLeaderBoardResource {
	//
	//
	//		public RetrieveStrengthLeaderBoardResource() {
	//
	//		}
	//	}
	//
	//	public RetrieveStrengthLeaderBoardResource execute() {
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
//		if (leaderBoard.getSize() == 0) {
//			resBuilder.setStatus(ResponseStatus.FAIL_NO_RESULTS);
//			log.info("!!!!!!!!!!!!!!!!LEADERBOARD IS EMPTY!!!!!!!!!!!!!!!!!!!!");
//			return false;
//		}
//		
//		if(leaderBoard.getSize() < minRank) {
//			resBuilder.setStatus(ResponseStatus.FAIL_NO_RESULTS);
//			log.info("the leaderboard ranks request fall outside the possible range");
//			return false;
//		}
		return true;
	}

	private boolean verifySemantics(Builder resBuilder) {
		return true;
	}
	
	public void retrieveInfo(Builder resBuilder) {
		int retrieverRank = leaderBoard.getUserRank(retrieverUserId);
		StrengthLeaderBoardProto.Builder b = StrengthLeaderBoardProto.newBuilder();
		b.setRank(retrieverRank);
		resBuilder.setSenderLeaderBoardInfo(b.build());
		
		resBuilder.addAllLeaderBoardInfo(leaderBoard.getStrengths(minRank, maxRank));
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
