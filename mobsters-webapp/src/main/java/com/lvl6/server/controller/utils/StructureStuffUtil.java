package com.lvl6.server.controller.utils;

import java.util.ArrayList;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lvl6.info.ObstacleForUser;
import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.StructureProto.CoordinateProto;
import com.lvl6.proto.StructureProto.MinimumObstacleProto;
import com.lvl6.proto.StructureProto.StructOrientation;

@Component
public class StructureStuffUtil {

	private static Logger log = LoggerFactory.getLogger(StructureStuffUtil.class);

	public List<ObstacleForUser> createObstacleForUserFromUserObstacleProtos(
			String userId, List<MinimumObstacleProto> mopList) {
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

			ObstacleForUser ofu = new ObstacleForUser(null, userId, obstacleId,
					xcoord, ycoord, null, orientation);

			ofuList.add(ofu);
		}
		return ofuList;
	}

	public void setObstacleForUserIds(List<String> ids,
			List<ObstacleForUser> ofuList) {
		for (int i = 0; i < ids.size(); i++) {
			String id = ids.get(i);
			ObstacleForUser ofu = ofuList.get(i);

			ofu.setId(id);
		}

	}

	public List<ObstacleForUser> createTutorialObstacleForUser(String userId) {
		List<ObstacleForUser> ofuList = new ArrayList<ObstacleForUser>();
		String orientation = StructOrientation.POSITION_1.name();
		for (int i = 0; i < ControllerConstants.TUTORIAL__INIT_OBSTACLE_ID.length; i++) {
			int obstacleId = ControllerConstants.TUTORIAL__INIT_OBSTACLE_ID[i];
			int posX = ControllerConstants.TUTORIAL__INIT_OBSTACLE_X[i];
			int posY = ControllerConstants.TUTORIAL__INIT_OBSTACLE_Y[i];

			ObstacleForUser ofu = new ObstacleForUser(null, userId, obstacleId,
					posX, posY, null, orientation);
			ofuList.add(ofu);
		}

		return ofuList;
	}
}
