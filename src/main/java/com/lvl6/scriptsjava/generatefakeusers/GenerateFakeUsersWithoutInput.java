package com.lvl6.scriptsjava.generatefakeusers;

import java.io.IOException;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.core.io.Resource;

import com.lvl6.properties.ControllerConstants;
import com.lvl6.retrieveutils.AvailableReferralCodeRetrieveUtils;
import com.lvl6.spring.AppContext;
import com.lvl6.utils.DBConnection;
import com.lvl6.utils.utilmethods.DeleteUtils;
import com.lvl6.utils.utilmethods.InsertUtil;

public class GenerateFakeUsersWithoutInput {

	private static Resource nameFile;
	//  private static String nameRulesFile = "src/main/java/com/lvl6/scriptsjava/generatefakeusers/namerulesElven.txt";
	private static int numEnemiesToCreatePerLevel = 300;
	private static int minLevel = 51;
	private static int maxLevel = 70;

	private static int syllablesInName1 = 2;
	private static int syllablesInName2 = 3;

	public static void main(String[] args) {
		ApplicationContext context = new FileSystemXmlApplicationContext(
				"target/utopia-server-1.0-SNAPSHOT/WEB-INF/spring-application-context.xml");
		NameGenerator nameGenerator = null;
		Random random = new Random();
		nameFile = context.getResource("classpath:namerulesElven.txt");
		try {
			nameGenerator = new NameGenerator(nameFile);
		} catch (IOException e) {
			e.printStackTrace();
		}
		if (nameGenerator != null) {
			System.out.println("beginning!");
			DBConnection.get().init();
			for (int i = minLevel; i <= maxLevel; i++) {
				for (int j = 0; j < numEnemiesToCreatePerLevel; j++) {
					createUser(random, nameGenerator, i);
				}
			}
			System.out.println("successfully created users!");
		}
	}

	//DeviceTokens are null
	//udid = referral code repeated twice
	private static void createUser(Random random, NameGenerator nameGenerator,
			int level) {

		int syllablesInName = (Math.random() < .5) ? syllablesInName1
				: syllablesInName2;
		String name = nameGenerator.compose(syllablesInName);
		if (Math.random() < .5)
			name = name.toLowerCase();
		if (Math.random() < .3)
			name = name + (int) (Math.ceil(Math.random() * 98));

		String newReferCode = AvailableReferralCodeRetrieveUtils
				.getAvailableReferralCode();
		if (newReferCode != null && newReferCode.length() > 0) {
			while (!DeleteUtils.get().deleteAvailableReferralCode(newReferCode)) {
				newReferCode = AvailableReferralCodeRetrieveUtils
						.getAvailableReferralCode();
			}
		} else {
			//TODO: generate more codes?
		}

		InsertUtil insertUtils = (InsertUtil) AppContext
				.getApplicationContext().getBean("insertUtils");
		String facebookId = null;
		int avatarMonsterId = ControllerConstants.TUTORIAL__STARTING_MONSTER_ID;
		if (insertUtils.insertUser(name, null, level, 0, 0, 0, 0, true, null,
				new Timestamp((new Date()).getTime()), facebookId,
				avatarMonsterId, "foobar@bohica.com", "All my facebook data!", 0) != null) {
			System.out.println("error in creating user");
		}

		System.out.println("created " + name);
	}

}
