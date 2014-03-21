package com.lvl6.server.controller.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.ObstacleForUser;
import com.lvl6.proto.StructureProto.CoordinateProto;
import com.lvl6.proto.StructureProto.MinimumObstacleProto;


public class StructureStuffUtil {
	
	private static Logger log = LoggerFactory.getLogger(new Object() { }.getClass().getEnclosingClass());
	
	public List<ObstacleForUser> createObstacleForUserFromUserObstacleProtos(int userId,
			List<MinimumObstacleProto> mopList) {
		if (null == mopList || mopList.isEmpty()) {
			log.warn("no MinimumObstacleProto in list to convert to ObstacleForUser");
		}
		
		List<ObstacleForUser> ofuList = new ArrayList<ObstacleForUser>();
		
		for (MinimumObstacleProto mop : mopList) {
			
			int obstacleId = mop.getObstacleId();
			CoordinateProto cProto = mop.getCoordinate();
			int xcoord = (int) cProto.getX();
			int ycoord = (int) cProto.getY();
			
			String orientation = mop.getOrientation().name();
			
			ObstacleForUser ofu = new ObstacleForUser(0, userId, obstacleId, xcoord, ycoord,
					null, orientation);
			
			ofuList.add(ofu);
		}
		return ofuList;
	}
	
	public void setObstacleForUserIds(List<Integer> ids, List<ObstacleForUser> ofuList) {
		for (int i = 0; i < ids.size(); i++) {
			Integer id = ids.get(i);
			ObstacleForUser ofu = ofuList.get(i);
			
			ofu.setId(id);
		}
		
	}
}
