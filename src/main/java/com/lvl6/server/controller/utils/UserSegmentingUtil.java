package com.lvl6.server.controller.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lvl6.info.User;

@Component
public class UserSegmentingUtil {

	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());



	public int convertUserIdIntoInt(String userId) {
		String lastFourString = userId.substring(userId.length()-4);
		int num = Integer.parseInt(lastFourString, 16);

		return num%100 + 1;
	}

	public boolean serviceCombinedStarterAndBuilderPack(User user) {
		if(user.getSegmentationGroup() < 51)
			return true;
		else return false;
	}



















}
