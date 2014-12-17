package com.lvl6.server.controller.actionobjects;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.ClanAvenge;
import com.lvl6.proto.EventClanProto.BeginClanAvengingResponseProto.BeginClanAvengingStatus;
import com.lvl6.proto.EventClanProto.BeginClanAvengingResponseProto.Builder;
import com.lvl6.utils.utilmethods.InsertUtil;
import com.lvl6.utils.utilmethods.UpdateUtil;

public class BeginClanAvengingAction
{
	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private String defenderId;
	private String clanId;
	private Date clientTime;
	private List<ClanAvenge> caList;
	private InsertUtil insertUtil;
	private UpdateUtil updateUtil;
	
	public BeginClanAvengingAction(
		String defenderId,
		String clanId,
		Date clientTime,
		List<ClanAvenge> caList,
		UpdateUtil updateUtil,
		InsertUtil insertUtil )
	{
		super();
		this.defenderId = defenderId;
		this.clanId = clanId;
		this.clientTime = clientTime;
		this.caList = caList;
		this.updateUtil = updateUtil;
		this.insertUtil = insertUtil;
	}
	
//	//encapsulates the return value from this Action Object
//	static class BeginClanAvengingResource {
//		
//		
//		public BeginClanAvengingResource() {
//			
//		}
//	}
//
//	public BeginClanAvengingResource execute() {
//		
//	}
	
	//derived state
	List<ClanAvenge> retaliationRequestsWithIds;
	List<String> historyAttackerId;
	List<String> historyDefenderId;
	List<Timestamp> battleEndTime;

	public void execute(Builder resBuilder) {
		resBuilder.setStatus(BeginClanAvengingStatus.FAIL_OTHER);
		
		//check out inputs before db interaction
		boolean valid = verifySyntax(resBuilder);
		
		if (!valid) {
			return;
		}
		
		valid = verifySemantics(resBuilder);
		
		if (!valid) {
			return;
		}
		
		boolean success = writeChangesToDB(resBuilder);
		if (!success) {
			return;
		}
		
		resBuilder.setStatus(BeginClanAvengingStatus.SUCCESS);
	}
	
	private boolean verifySyntax(Builder resBuilder) {
		
		if (caList.isEmpty()) {
			log.error( "invalid request: no valid avenging" );
			return false;
		}
		
		return true;
	}
	
	private boolean verifySemantics(Builder resBuilder) {
		return true;
	}
	
	private boolean writeChangesToDB(Builder resBuilder) {
		
		//update the pvp_battle_history so that the row has avenged=true
		setUpdateArgs();
		
		int numUpdated = updateUtil.updatePvpBattleHistoryClanRetaliated(
			historyAttackerId, historyDefenderId, battleEndTime);
		log.info("numUpdated pvp_battle_history {}", numUpdated);
		List<String> ids = insertUtil
			.insertIntoClanAvengeGetId(caList, clanId);
		
		retaliationRequestsWithIds = new ArrayList<ClanAvenge>();
		for (int i = 0; i < ids.size(); i++)
		{
			String id = ids.get(i);
			ClanAvenge ca = caList.get(i);
			
			ClanAvenge nuCa = new ClanAvenge(ca);
			nuCa.setId(id);
			
			retaliationRequestsWithIds.add(nuCa);
		}
		
		return true;
	}
	
	private void setUpdateArgs()
	{
		historyAttackerId = new ArrayList<String>();
		historyDefenderId = new ArrayList<String>();
		battleEndTime = new ArrayList<Timestamp>();
		
		for (ClanAvenge ca : caList)
		{
			historyAttackerId.add(ca.getAttackerId());
			historyDefenderId.add(ca.getDefenderId());
			Date battleEndTimeDate = ca.getBattleEndTime();
			battleEndTime.add(
				new Timestamp(battleEndTimeDate.getTime()));
		}
	}
	
	public List<ClanAvenge> getRetaliationRequestsWithIds()
	{
		return retaliationRequestsWithIds;
	}
}
