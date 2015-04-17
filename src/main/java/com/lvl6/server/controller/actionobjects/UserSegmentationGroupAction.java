package com.lvl6.server.controller.actionobjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserSegmentationGroupAction {


	private static Logger log = LoggerFactory.getLogger(new Object() {
	}.getClass().getEnclosingClass());

	private Object[] arrayOfObjects;
	private Float[] arrayOfFloats;
	private String userId;

	public UserSegmentationGroupAction(Object[] arrayOfObjects, Float[] arrayOfFloats,
			String userId) {
		super();
		this.arrayOfObjects = arrayOfObjects;
		this.arrayOfFloats = arrayOfFloats;
		this.userId = userId;
	}



	public UserSegmentationGroupAction(String userId) {
		super();
		this.userId = userId;
	}



	private int segmentationGroup;
	private int[] arrayOfInts;

	public void convertUserIdIntoInt() {
		String lastFourString = userId.substring(userId.length()-4);
		int num = Integer.parseInt(lastFourString, 16);
		segmentationGroup = num%100 + 1;
	}


	public Object returnAppropriateObjectGroup() {
		convertUserIdIntoInt();

		float total = 0;

		for(int i=0;i<arrayOfFloats.length;i++) {
			total += arrayOfFloats[i];
		}

		int intTotal = 0;
		arrayOfInts = new int[arrayOfFloats.length];
		for(int j=0;j<arrayOfFloats.length;j++) {
			intTotal += (int)(arrayOfFloats[j]/total * 100);
			arrayOfInts[j] = intTotal;
			log.info("arrayofints at index : " + j + "is " + arrayOfInts[j]);
		}

		log.info("segmentation num : " + segmentationGroup);
		for(int k=0; k<arrayOfInts.length;k++) {
			if(segmentationGroup < arrayOfInts[k]) {
				return arrayOfObjects[k];
			}
		}
		log.error("didn't return anything when determing user segmentation");
		return null;
	}



	public static Logger getLog() {
		return log;
	}


	public static void setLog(Logger log) {
		UserSegmentationGroupAction.log = log;
	}


	public Object[] getArrayOfObjects() {
		return arrayOfObjects;
	}


	public void setArrayOfObjects(Object[] arrayOfObjects) {
		this.arrayOfObjects = arrayOfObjects;
	}


	public Float[] getArrayOfFloats() {
		return arrayOfFloats;
	}


	public void setArrayOfFloats(Float[] arrayOfFloats) {
		this.arrayOfFloats = arrayOfFloats;
	}


	public int[] getArrayOfInts() {
		return arrayOfInts;
	}


	public void setArrayOfInts(int[] arrayOfInts) {
		this.arrayOfInts = arrayOfInts;
	}


	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public int getSegmentationGroup() {
		return segmentationGroup;
	}

	public void setSegmentationGroup(int segmentationGroup) {
		this.segmentationGroup = segmentationGroup;
	}







}