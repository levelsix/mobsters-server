package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.LogoutRequestEvent;
import com.lvl6.info.PvpBattleForUser;
import com.lvl6.info.User;
import com.lvl6.proto.EventUserProto.LogoutRequestProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.pvp.OfflinePvpUser;
import com.lvl6.retrieveutils.PvpBattleForUserRetrieveUtils;
import com.lvl6.server.Locker;
import com.lvl6.utils.ConnectedPlayer;
import com.lvl6.utils.RetrieveUtils;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component
@DependsOn("gameServer")
public class LogoutController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	public LogoutController() {
		numAllocatedThreads = 4;
	}

	@Resource(name = "playersByPlayerId")
	protected Map<Integer, ConnectedPlayer> playersByPlayerId;

	public Map<Integer, ConnectedPlayer> getPlayersByPlayerId() {
		return playersByPlayerId;
	}

	public void setPlayersByPlayerId(Map<Integer, ConnectedPlayer> playersByPlayerId) {
		this.playersByPlayerId = playersByPlayerId;
	}

	@Autowired
  protected HazelcastPvpUtil hazelcastPvpUtil;
  
	@Autowired
	protected Locker locker;
	
	@Override
	public RequestEvent createRequestEvent() {
		return new LogoutRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_LOGOUT_EVENT;
	}

	@Override
	protected void processRequestEvent(RequestEvent event) throws Exception {
		LogoutRequestProto reqProto = ((LogoutRequestEvent) event)
				.getLogoutRequestProto();

		MinimumUserProto sender = reqProto.getSender();
		int userId = sender.getUserId();
		Timestamp lastLogout = new Timestamp(new Date().getTime());

		if (userId > 0) {
			getLocker().lockPlayer(userId, this.getClass().getSimpleName());
			try {
				User user = RetrieveUtils.userRetrieveUtils().getUserById(userId);
				if (null != user) {
					//if user has unfinished battle, reward defender and penalize attacker
					List<Integer> eloChangeList = new ArrayList<Integer>();
					pvpBattleStuff(user, userId, eloChangeList, lastLogout);
					
					int eloChange = 0;
					if (!eloChangeList.isEmpty()) {
						eloChange = eloChangeList.get(0);
					}
					if (!user.updateLastLogoutElo(lastLogout, eloChange)) {
						log.error("problem with updating user's last logout time for user "	+ userId);
					}
			    if (!InsertUtils.get().insertLastLoginLastLogoutToUserSessions(user.getId(), null, lastLogout)) {
			      log.error("problem with inserting last logout time for user " + user + ", logout=" + lastLogout);
			    }
			    String udid = user.getUdid();
			    boolean isLogin = false;
			    boolean isNewUser = false;
			    InsertUtils.get().insertIntoLoginHistory(udid, userId, lastLogout,
			        isLogin, isNewUser);
			    
			    
			    //put this user back into pool of people who can be attacked
			    int elo = user.getElo();
			    String userIdStr = String.valueOf(userId);
			    Date shieldEndTime = user.getShieldEndTime();
			    Date inBattleEndTime = user.getInBattleShieldEndTime();
			    OfflinePvpUser userOpu = new OfflinePvpUser(userIdStr, elo, shieldEndTime, inBattleEndTime);
			    getHazelcastPvpUtil().addOfflinePvpUser(userOpu);
			    
				}
				log.info("Player logged out: "+userId);
				playersByPlayerId.remove(userId);
			} catch (Exception e) {
				log.error("exception in updating user logout", e);
			} finally {
				getLocker().unlockPlayer(userId, this.getClass().getSimpleName());
			}
		} else {
			log.error("cannot update last logout because playerid of sender:"+sender.getName()+" is <= 0, it's "	+ userId);
		}
		// TODO: clear cache
	}
	
	private void pvpBattleStuff(User user, int userId, List<Integer> eloChange,
			Timestamp now) {
		PvpBattleForUser battle = PvpBattleForUserRetrieveUtils
  			.getPvpBattleForUserForAttacker(userId);
		
		if (null == battle) {
			return;
		}

		//capping max elo attacker loses
		int eloAttackerLoses = battle.getAttackerLoseEloChange();
		if (user.getElo() + eloAttackerLoses < 0) {
			eloAttackerLoses = -1 * user.getElo();
		}
		int defenderId = battle.getDefenderId();
		int eloDefenderWins = battle.getDefenderWinEloChange();
		
		User defender = null;
		OfflinePvpUser defenderOpu = null;
		
		//user has unfinished battle, reward defender and penalize attacker
  	//nested try catch's in order to prevent exception bubbling up, all because of
  	//some stinkin' elo XP
  	try {
  		//NOTE: this lock ordering might result in a temp deadlock
  		//doesn't reeeally matter if can't penalize defender...
  		
  		//only lock real users
  		if (0 != defenderId) {
  			getLocker().lockPlayer(defenderId, this.getClass().getSimpleName());
  		}
  		try {
  			if (0 != defenderId) {
  				defender = RetrieveUtils.userRetrieveUtils().getUserById(defenderId);
  				defenderOpu = getHazelcastPvpUtil().getOfflinePvpUser(defenderId);
  			}
  			
  			//update attacker
  			eloChange.add(eloAttackerLoses);
  			
  			//update defender if real, might need to cap defenderElo, defender can now be
  			//attacked
  			if (null != defender) {
  				defender.updateEloInBattleEndTime(eloDefenderWins, now);
  			}
  			if (null != defenderOpu) { //update if exists
  				int defenderElo = defender.getElo();
  				defenderOpu.setElo(defenderElo);
  				Date nowDate = new Date(now.getTime());
  				defenderOpu.setInBattleEndTime(nowDate);
  				getHazelcastPvpUtil().updateOfflinePvpUser(defenderOpu);
  			}
  			
  			//delete that this battle occurred
  			DeleteUtils.get().deletePvpBattleForUser(userId);
  			log.info("successfully penalized, rewarded attacker, defender respectively. battle= " +
  					battle);
  			
  		} catch (Exception e){
  			log.error("tried to penalize, reward attacker, defender respectively. battle=" +
  					battle, e);
  		} finally {
  			if (0 != defenderId) {
  				getLocker().unlockPlayer(defenderId, this.getClass().getSimpleName());
  			}
  		}
  	} catch (Exception e2) {
  		log.error("could not successfully penalize, reward attacker, defender respectively." +
  				" battle=" + battle, e2);
  	}
		
		
	}

	public HazelcastPvpUtil getHazelcastPvpUtil() {
		return hazelcastPvpUtil;
	}

	public void setHazelcastPvpUtil(HazelcastPvpUtil hazelcastPvpUtil) {
		this.hazelcastPvpUtil = hazelcastPvpUtil;
	}

	public Locker getLocker() {
		return locker;
	}

	public void setLocker(Locker locker) {
		this.locker = locker;
	}
	
}
