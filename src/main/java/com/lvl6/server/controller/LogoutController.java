package com.lvl6.server.controller;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.stereotype.Component;

import com.lvl6.events.RequestEvent;
import com.lvl6.events.request.LogoutRequestEvent;
import com.lvl6.info.User;
import com.lvl6.proto.EventUserProto.LogoutRequestProto;
import com.lvl6.proto.ProtocolsProto.EventProtocolRequest;
import com.lvl6.proto.UserProto.MinimumUserProto;
import com.lvl6.pvp.HazelcastPvpUtil;
import com.lvl6.retrieveutils.PvpLeagueForUserRetrieveUtil2;
import com.lvl6.retrieveutils.UserRetrieveUtils2;
import com.lvl6.server.Locker;
import com.lvl6.server.eventsender.ToClientEvents;
import com.lvl6.utils.ConnectedPlayer;
import com.lvl6.utils.utilmethods.InsertUtils;

@Component
@DependsOn("gameServer")
public class LogoutController extends EventController {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	public LogoutController() {
		
	}

	@Resource(name = "playersByPlayerId")
	protected Map<Integer, ConnectedPlayer> playersByPlayerId;

	public Map<Integer, ConnectedPlayer> getPlayersByPlayerId() {
		return playersByPlayerId;
	}

	public void setPlayersByPlayerId(
			Map<Integer, ConnectedPlayer> playersByPlayerId) {
		this.playersByPlayerId = playersByPlayerId;
	}

	@Autowired
	protected HazelcastPvpUtil hazelcastPvpUtil;

	@Autowired
	protected Locker locker;

	@Autowired
	protected PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil;

	@Autowired
	protected UserRetrieveUtils2 userRetrieveUtils;

	@Override
	public RequestEvent createRequestEvent() {
		return new LogoutRequestEvent();
	}

	@Override
	public EventProtocolRequest getEventType() {
		return EventProtocolRequest.C_LOGOUT_EVENT;
	}

	@Override
	public void processRequestEvent(RequestEvent event, ToClientEvents responses)  {
		LogoutRequestProto reqProto = ((LogoutRequestEvent) event)
				.getLogoutRequestProto();

		MinimumUserProto sender = reqProto.getSender();
		String userId = sender.getUserUuid();
		Timestamp lastLogout = new Timestamp(new Date().getTime());

		if (userId != null) {

			UUID userUuid = null;
			boolean invalidUuids = true;
			try {
				userUuid = UUID.fromString(userId);

				invalidUuids = false;
			} catch (Exception e) {
				log.error(String.format("UUID error. incorrect userId=%s",
						userId), e);
				invalidUuids = true;
			}

			//UUID checks
			if (invalidUuids) {
				return;
			}

			getLocker().lockPlayer(userUuid, this.getClass().getSimpleName());
			try {
				User user = getUserRetrieveUtils().getUserById(userId);
				if (null != user) {
					//TODO: figure out if still deducting elo from user after logging out
					//FOR NOW DON'T DO THE FOLLOWING
					//if user has unfinished battle, reward defender and penalize attacker
					//					List<Integer> eloChangeList = new ArrayList<Integer>();
					//					pvpBattleStuff(user, userId, eloChangeList, lastLogout);
					//					
					//					int eloChange = 0;
					//					if (!eloChangeList.isEmpty()) {
					//						eloChange = eloChangeList.get(0);
					//					}
					if (!user.updateLastLogout(lastLogout)) {
						log.error("problem with updating user's last logout time for user "
								+ userId);
					}
					if (!InsertUtils.get()
							.insertLastLoginLastLogoutToUserSessions(
									user.getId(), null, lastLogout)) {
						log.error("problem with inserting last logout time for user "
								+ user + ", logout=" + lastLogout);
					}
					String udid = user.getUdid();
					boolean isLogin = false;
					boolean isNewUser = false;
					InsertUtils.get().insertIntoLoginHistory(udid, userId,
							lastLogout, isLogin, isNewUser);

					//put this user back into pool of people who can be attacked,
					//don't really need to, since will most likely still be there. eh might as well
					//			    int elo = user.getElo();
					//			    String userIdStr = String.valueOf(userId);
					//			    Date shieldEndTime = user.getShieldEndTime();
					//			    Date inBattleEndTime = user.getInBattleShieldEndTime();
					//			    PvpUser userOpu = new PvpUser(userIdStr, elo, shieldEndTime, inBattleEndTime);
					//			    getHazelcastPvpUtil().replacePvpUser(userOpu, userId);
					//			    
				}
				log.info("Player logged out: " + userId);
				playersByPlayerId.remove(userId);
			} catch (Exception e) {
				log.error("exception in updating user logout", e);
			} finally {
				getLocker().unlockPlayer(userUuid,
						this.getClass().getSimpleName());
			}
		} else {
			log.error("cannot update last logout because playerid of sender:"
					+ sender.getName() + " is <= 0, it's " + userId);
		}
		// TODO: clear cache
	}

	/*
	private void pvpBattleStuff(User user, int userId, List<Integer> eloChange,
			Timestamp now) {
		PvpBattleForUser battle = PvpBattleForUserRetrieveUtils
			.getPvpBattleForUserForAttacker(userId);
		
		if (null == battle) {
			return;
		}

		PvpLeagueForUser plfu = getPvpLeagueForUserRetrieveUtil()
				.getUserPvpLeagueForId(userId);
		//capping max elo attacker loses
		int eloAttackerLoses = battle.getAttackerLoseEloChange();
		if (plfu.getElo() + eloAttackerLoses < 0) {
			eloAttackerLoses = -1 * plfu.getElo();
		}
		int defenderId = battle.getDefenderId();
		int eloDefenderWins = battle.getDefenderWinEloChange();
		
		//user has unfinished battle, reward defender and penalize attacker
	//nested try catch's in order to prevent exception bubbling up, all because of
	//some stinkin' elo XP
	try {
		//eloChange will be filled up if defender is real
		penalizeUserForLeavingGameWhileInPvp(userId, defenderId, eloAttackerLoses,
				eloDefenderWins, now, battle, eloChange);
	} catch (Exception e2) {
		log.error("could not successfully penalize, reward attacker, defender respectively." +
				" battle=" + battle, e2);
	}
	} */

	/*
	private void penalizeUserForLeavingGameWhileInPvp(int userId, int defenderId,
			int eloAttackerLoses, int eloDefenderWins, Timestamp now, PvpBattleForUser battle,
			List<Integer> eloChange) {
		//NOTE: this lock ordering might result in a temp deadlock
		//doesn't reeeally matter if can't penalize defender...
		User defender = null;
		PvpUser defenderOpu = null;
		

		//only lock real users
	//		if (0 != defenderId) {
	//			getLocker().lockPlayer(defenderId, this.getClass().getSimpleName());
	//		}
		try {
			if (0 != defenderId) {
				defender = RetrieveUtils.userRetrieveUtils().getUserById(defenderId);
				defenderOpu = getHazelcastPvpUtil().getPvpUser(defenderId);
			}
			
			//update attacker
			eloChange.add(eloAttackerLoses);
			
			//TODO: figure out if still doing any of this
			//update defender if real, might need to cap defenderElo, defender can now be
			//attacked
	//			if (null != defender) {
	//				defender.updateEloInBattleEndTime(eloDefenderWins, now);
	//				int defenderElo = defender.getElo();                    
	//				defenderOpu = new PvpUser();
	//				defenderOpu.setElo(defenderElo);                        
	//				Date nowDate = new Date(now.getTime());                 
	//				defenderOpu.setInBattleEndTime(nowDate);                
	//				getHazelcastPvpUtil().replacePvpUser(defenderOpu, defenderId);
	//			}
	//			if (null != defenderOpu) { //update if exists
	//				int defenderElo = defender.getElo();
	//				defenderOpu.setElo(defenderElo);
	//				Date nowDate = new Date(now.getTime());
	//				defenderOpu.setInBattleEndTime(nowDate);
	//				getHazelcastPvpUtil().updateOfflinePvpUser(defenderOpu);
	//			}
			
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
	}*/

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

	public UserRetrieveUtils2 getUserRetrieveUtils() {
		return userRetrieveUtils;
	}

	public void setUserRetrieveUtils(UserRetrieveUtils2 userRetrieveUtils) {
		this.userRetrieveUtils = userRetrieveUtils;
	}

	public PvpLeagueForUserRetrieveUtil2 getPvpLeagueForUserRetrieveUtil() {
		return pvpLeagueForUserRetrieveUtil;
	}

	public void setPvpLeagueForUserRetrieveUtil(
			PvpLeagueForUserRetrieveUtil2 pvpLeagueForUserRetrieveUtil) {
		this.pvpLeagueForUserRetrieveUtil = pvpLeagueForUserRetrieveUtil;
	}

}
