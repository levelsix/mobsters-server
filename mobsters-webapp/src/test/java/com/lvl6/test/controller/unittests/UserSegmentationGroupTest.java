package com.lvl6.test.controller.unittests;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.lvl6.info.SalesPackage;
import com.lvl6.server.controller.actionobjects.UserSegmentationGroupAction;

public class UserSegmentationGroupTest {

	private static Logger log = LoggerFactory.getLogger(UserSegmentationGroupTest.class);

	@Test
	public void testReturnAppropriateObjectGroup() {

		Object[] objArray = new Object[5];
		Float[] floatArray = new Float[5];

		SalesPackage sp = new SalesPackage();
		sp.setId(1);
		objArray[0] = sp;
		floatArray[0] = (float)0.1;

		SalesPackage sp2 = new SalesPackage();
		sp2.setId(2);
		objArray[1] = sp2;
		floatArray[1] = (float)0.1;

		SalesPackage sp3 = new SalesPackage();
		sp3.setId(3);
		objArray[2] = sp3;
		floatArray[2] = (float)0.2;

		SalesPackage sp4 = new SalesPackage();
		sp4.setId(4);
		objArray[3] = sp4;
		floatArray[3] = (float)0.2;

		SalesPackage sp5 = new SalesPackage();
		sp5.setId(5);
		objArray[4] = sp5;
		floatArray[4] = (float)0.4;

		//TODO: add a mockeduser with segmentation group
//		UserSegmentationGroupAction usga = new UserSegmentationGroupAction(objArray, floatArray, "baaaf", ); //baaf seg group value = 95
//		assertEquals(usga.returnAppropriateObjectGroup(), sp5);
//		assertFalse(usga.returnAppropriateObjectGroup().equals(sp4));
//
//		usga.setUserId("1abcd"); //81
//		assertTrue(usga.returnAppropriateObjectGroup().equals(sp5));
//
//		usga.setUserId("11234"); //60
//		assertTrue(usga.returnAppropriateObjectGroup().equals(sp5));
//
//		usga.setUserId("11232"); //58
//		assertTrue(usga.returnAppropriateObjectGroup().equals(sp4));
//
//		usga.setUserId("11009"); //5
//		assertTrue(usga.returnAppropriateObjectGroup().equals(sp));
//
//	}
//
//	@Test
//	public void test() {
//			Object[] objArray = new Object[2];
//			objArray[0] = "COOPER";
//			objArray[1] = "ALEX";
//
//			Float[] floatArray = new Float[2];
//			floatArray[0] = (float)0.9;
//			floatArray[1] = (float)0.1;
//
//			UserSegmentationGroupAction usga = new UserSegmentationGroupAction(objArray, floatArray, "baaaf");
//
//			assertTrue(usga.returnAppropriateObjectGroup().equals("ALEX"));
//
//		}


	}

}
