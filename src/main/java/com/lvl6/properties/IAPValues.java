package com.lvl6.properties;

import java.util.Arrays;
import java.util.List;

import com.lvl6.proto.InAppPurchaseProto.InAppPurchasePackageProto.InAppPurchasePackageType;

public class IAPValues {

	/*APPLE'S VARIABLES*/
	public static final String RECEIPT_DATA = "receipt-data";
	public static final String STATUS = "status";
	public static final String RECEIPT = "receipt";
	public static final String TRANSACTION_ID = "transaction_id";
	public static final String PRODUCT_ID = "product_id";
	public static final String QUANTITY = "quantity";
	public static final String BID = "bid";
	public static final String BVRS = "bvrs";
	public static final String APP_ITEM_ID = "app_item_id";
	public static final String PURCHASE_DATE = "purchase_date";
	public static final String PURCHASE_DATE_MS = "purchase_date_ms";

	public static final String PACKAGE1 = Globals.APPLE_BUNDLE_ID() + ".gem1";
	public static final String PACKAGE2 = Globals.APPLE_BUNDLE_ID() + ".gem2";
	public static final String PACKAGE3 = Globals.APPLE_BUNDLE_ID() + ".gem3";
	public static final String PACKAGE4 = Globals.APPLE_BUNDLE_ID() + ".gem4";
	public static final String PACKAGE5 = Globals.APPLE_BUNDLE_ID() + ".gem5";
	public static final String STARTERPACK = Globals.APPLE_BUNDLE_ID()
			+ ".starterpack";
	public static final String BUILDERPACK = Globals.APPLE_BUNDLE_ID()
			+ ".builderpack";
	public static final String MONEYTREE = Globals.APPLE_BUNDLE_ID()
			+ ".moneytree1";
	public static final String MONEYTREENOSALE = Globals.APPLE_BUNDLE_ID()
			+ ".moneytreenosale";
	public static final String SALE5 = Globals.APPLE_BUNDLE_ID()
			+ ".sale5";
	public static final String SALE10 = Globals.APPLE_BUNDLE_ID()
			+ ".sale10";
	public static final String SALE20 = Globals.APPLE_BUNDLE_ID()
			+ ".sale20";
	public static final String SALE50 = Globals.APPLE_BUNDLE_ID()
			+ ".sale50";
	public static final String SALE100 = Globals.APPLE_BUNDLE_ID()
			+ ".sale100";

	public static final String PACKAGE1IMG = "pilegems.png";
	public static final String PACKAGE2IMG = "baggems.png";
	public static final String PACKAGE3IMG = "casegems.png";
	public static final String PACKAGE4IMG = "safegems.png";
	public static final String PACKAGE5IMG = "vaultgems.png";

	/*
	 * 1- $1 for 10 diamonds
	 * 2- $1 for 12 diamonds
	 * 3- $1 for 12.5 diamonds
	 * 4- $1 for 13 diamonds
	 * 5- $1 for 15 diamonds
	 */

	public static final int PACKAGE_1_DIAMONDS = 50;
	public static final int PACKAGE_2_DIAMONDS = 120;
	public static final int PACKAGE_3_DIAMONDS = 250;
	public static final int PACKAGE_4_DIAMONDS = 650;
	public static final int PACKAGE_5_DIAMONDS = 1500;

	public static final double PACKAGE_1_PRICE = 4.99;
	public static final double PACKAGE_2_PRICE = 9.99;
	public static final double PACKAGE_3_PRICE = 19.99;
	public static final double PACKAGE_4_PRICE = 49.99;
	public static final double PACKAGE_5_PRICE = 99.99;
	public static final double STARTER_PACK_PRICE = 4.99;
	public static final double BUILDER_PACK_PRICE = 4.99;
	public static final double MONEY_TREE_PRICE = 4.99;

	public static final List<String> iapPackageNames = Arrays.asList(PACKAGE1,
			PACKAGE2, PACKAGE3, PACKAGE4, PACKAGE5, STARTERPACK, BUILDERPACK,
			MONEYTREE, MONEYTREENOSALE, SALE5, SALE10, SALE20, SALE50, SALE100);

	//    public static final List<String> packageNames =
	//            Arrays.asList(PACKAGE1, PACKAGE2, PACKAGE3, PACKAGE4, PACKAGE5);

	//    public static final List<Integer> packageGivenDiamonds =
	//            Arrays.asList(PACKAGE_1_DIAMONDS, PACKAGE_2_DIAMONDS, PACKAGE_3_DIAMONDS,
	//                    PACKAGE_4_DIAMONDS, PACKAGE_5_DIAMONDS);

	public static int getDiamondsForPackageName(String packageName) {
		if (packageName.equals(PACKAGE1)) {
			return PACKAGE_1_DIAMONDS;
		}
		if (packageName.equals(PACKAGE2)) {
			return PACKAGE_2_DIAMONDS;
		}
		if (packageName.equals(PACKAGE3)) {
			return PACKAGE_3_DIAMONDS;
		}
		if (packageName.equals(PACKAGE4)) {
			return PACKAGE_4_DIAMONDS;
		}
		if (packageName.equals(PACKAGE5)) {
			return PACKAGE_5_DIAMONDS;
		}
		return 0;
	}

	public static String getImageNameForPackageName(String packageName) {
		if (packageName.equals(PACKAGE1)) {
			return PACKAGE1IMG;
		}
		if (packageName.equals(PACKAGE2)) {
			return PACKAGE2IMG;
		}
		if (packageName.equals(PACKAGE3)) {
			return PACKAGE3IMG;
		}
		if (packageName.equals(PACKAGE4)) {
			return PACKAGE4IMG;
		}
		if (packageName.equals(PACKAGE5)) {
			return PACKAGE5IMG;
		}
		return null;
	}

	public static double getCashSpentForPackageName(String packageName) {
		if (packageName.equals(PACKAGE1)) {
			return PACKAGE_1_PRICE;
		}
		if (packageName.equals(PACKAGE2)) {
			return PACKAGE_2_PRICE;
		}
		if (packageName.equals(PACKAGE3)) {
			return PACKAGE_3_PRICE;
		}
		if (packageName.equals(PACKAGE4)) {
			return PACKAGE_4_PRICE;
		}
		if (packageName.equals(PACKAGE5)) {
			return PACKAGE_5_PRICE;
		}
		if (packageName.equals(STARTERPACK)) {
			return STARTER_PACK_PRICE;
		}
		if(packageName.equals(BUILDERPACK)) {
			return BUILDER_PACK_PRICE;
		}
		if (packageName.equals(MONEYTREE)) {
			return MONEY_TREE_PRICE;
		}
		return 0;
	}

	public static boolean packageIsStarterPack(String packageName) {
		if (packageName.equals(STARTERPACK)) {
			return true;
		}
		return false;
	}

	public static boolean packageIsBuilderPack(String packageName) {
		if(packageName.equals(BUILDERPACK)) {
			return true;
		}
		return false;
	}

	public static boolean packageIsMoneyTree(String packageName) {
		if (packageName.equals(MONEYTREE)) {
			return true;
		}
		return false;
	}

	public static InAppPurchasePackageType getPackageType(String packageName) {
		if (packageName.equals(PACKAGE1) ||
		    packageName.equals(PACKAGE2) ||
		    packageName.equals(PACKAGE3) ||
		    packageName.equals(PACKAGE4) ||
		    packageName.equals(PACKAGE5)) {

			return InAppPurchasePackageType.GEMS;
		}
        if (packageName.equals(SALE5) ||
                packageName.equals(SALE10) ||
                packageName.equals(SALE20) ||
                packageName.equals(SALE50) ||
                packageName.equals(SALE100)) {

                return InAppPurchasePackageType.SALE;
            }
		if (packageName.equals(STARTERPACK)) {
			return InAppPurchasePackageType.STARTER_PACK;
		}
		if(packageName.equals(BUILDERPACK)) {
			return InAppPurchasePackageType.BUILDER_PACK;
		}

		return InAppPurchasePackageType.NO_IN_APP_PURCHASE_PACKAGE_TYPE;
	}
}
