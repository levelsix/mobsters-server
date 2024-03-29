package com.lvl6.properties;

import com.lvl6.spring.AppContext;

public class Globals {

	protected String appleBundleId;
	protected String appStoreUrl;
	protected String reviewPageUrl;
	protected boolean kabamEnabled = true;
	protected boolean offerChartEnabled = false;

	protected boolean addAllFbFriends = false;
	protected boolean sandbox = true;
	protected boolean allowCheats = false;
	protected boolean iddictionOn = true;

	protected int healthCheckTimeoutSeconds = 6;
	protected int initialDiamonds = 20;

	/** deprecated **************************/
	protected float versionNumber = 1.0f;
	/************************************/
	//	protected String appVersion = "0.1.0";

	protected int versionSuperNumber = 0;
	protected int versionMajorNumber = 1;
	protected int versionMinorNumber = 0;

	public boolean isOfferChartEnabled() {
		return offerChartEnabled;
	}

	public void setOfferChartEnabled(boolean offerChartEnabled) {
		this.offerChartEnabled = offerChartEnabled;
	}

	public int getInitialDiamonds() {
		return initialDiamonds;
	}

	public void setInitialDiamonds(int initialDiamonds) {
		this.initialDiamonds = initialDiamonds;
	}

	public boolean isKabamEnabled() {
		return kabamEnabled;
	}

	public void setKabamEnabled(boolean kabamEnabled) {
		this.kabamEnabled = kabamEnabled;
	}

	public String getAppleBundleId() {
		return appleBundleId;
	}

	public void setAppleBundleId(String appleBundleId) {
		this.appleBundleId = appleBundleId;
	}

	public String getAppStoreUrl() {
		return appStoreUrl;
	}

	public void setAppStoreUrl(String appStoreUrl) {
		this.appStoreUrl = appStoreUrl;
	}

	public String getReviewPageUrl() {
		return reviewPageUrl;
	}

	public void setReviewPageUrl(String reviewPageUrl) {
		this.reviewPageUrl = reviewPageUrl;
	}

	public int getHealthCheckTimeoutSeconds() {
		return healthCheckTimeoutSeconds;
	}

	public void setHealthCheckTimeoutSeconds(int healthCheckTimeoutSeconds) {
		this.healthCheckTimeoutSeconds = healthCheckTimeoutSeconds;
	}

	public float getVersionNumber() {
		return versionNumber;
	}

	public void setVersionNumber(float versionNumber) {
		this.versionNumber = versionNumber;
	}

	public boolean isIddictionOn() {
		return iddictionOn;
	}

	public void setIddictionOn(boolean iddictionOn) {
		this.iddictionOn = iddictionOn;
	}

	public boolean getSandbox() {
		return sandbox;
	}

	public void setSandbox(boolean isSandbox) {
		this.sandbox = isSandbox;
	}

	public boolean isAllowCheats() {
		return allowCheats;
	}

	public void setAllowCheats(boolean allowCheats) {
		this.allowCheats = allowCheats;
	}

	public boolean isAddAllFbFriends() {
		return addAllFbFriends;
	}

	public void setAddAllFbFriends(boolean addAllFbFriends) {
		this.addAllFbFriends = addAllFbFriends;
	}

	public int getVersionSuperNumber() {
		return versionSuperNumber;
	}

	public void setVersionSuperNumber(int versionSuperNumber) {
		this.versionSuperNumber = versionSuperNumber;
	}

	public int getVersionMajorNumber() {
		return versionMajorNumber;
	}

	public void setVersionMajorNumber(int versionMajorNumber) {
		this.versionMajorNumber = versionMajorNumber;
	}

	public int getVersionMinorNumber() {
		return versionMinorNumber;
	}

	public void setVersionMinorNumber(int versionMinorNumber) {
		this.versionMinorNumber = versionMinorNumber;
	}

	/** size of ByteBuffer for reading/writing from channels */

	public static final int NET_BUFFER_SIZE = 16384 * 64;

	/** maximum event size in bytes */
	public static final int MAX_EVENT_SIZE = 16384 * 64;

	/** interval to sleep between attempts to write to a channel. */
	public static final long CHANNEL_WRITE_SLEEP = 10L;

	/** number of worker threads for EventWriter */
	public static final int EVENT_WRITER_WORKERS = 5;

	/** number of worker threads for APNSWriter */
	public static final int APNS_WRITER_WORKERS = 5;

	/** default number of workers for GameControllers */
	public static final int DEFAULT_CONTROLLER_WORKERS = 2;

	public static final boolean IS_SANDBOX() {
		return AppContext.get().getBean(Globals.class)
				.getSandbox();
	};

	public static final boolean ALLOW_CHEATS() {
		return AppContext.get().getBean(Globals.class)
				.isAllowCheats();
	};

	public static final boolean KABAM_ENABLED() {
		return AppContext.get().getBean(Globals.class)
				.isKabamEnabled();
	};

	public static final boolean OFFERCHART_ENABLED() {
		return AppContext.get().getBean(Globals.class)
				.isOfferChartEnabled();
	};

	public static final int NUM_MINUTES_DIFFERENCE_LEEWAY_FOR_CLIENT_TIME = 10;

	public static final String APP_STORE_URL() {
		return AppContext.get().getBean(Globals.class)
				.getAppStoreUrl();
	};//"itms-apps://itunes.com/apps/ageofchaos";

	public static final String REVIEW_PAGE_URL() {
		return AppContext.get().getBean(Globals.class)
				.getReviewPageUrl();
	};//= "itms-apps://itunes.apple.com/WebObjects/MZStore.woa/wa/viewContentsUserReviews?id=548520325&pageNumber=0&sortOrdering=1&type=Purple+Software";

	public static final String REVIEW_PAGE_CONFIRMATION_MESSAGE = "Awesome! Rate us 5 Stars in the App Store to keep the updates coming!";

	//public static final Level LOG_LEVEL = Level.INFO;

	public static final int NUM_SECONDS_FOR_CONTROLLER_PROCESS_EVENT_LONGTIME_LOG_WARNING = 1;

	public static final String APPLE_BUNDLE_ID() {
		return AppContext.get().getBean(Globals.class)
				.getAppleBundleId();
	};

	public static final float VERSION_NUMBER() {
		return AppContext.get().getBean(Globals.class)
				.getVersionNumber();
	};

	public static int HEALTH_CHECK_TIMEOUT() {
		return AppContext.get().getBean(Globals.class)
				.getHealthCheckTimeoutSeconds();
	};

	public static final Integer INITIAL_DIAMONDS() {
		return AppContext.get().getBean(Globals.class)
				.getInitialDiamonds();
	};

	public static final Integer VERSION_SUPER_NUMBER() {
		return AppContext.get().getBean(Globals.class)
				.getVersionSuperNumber();
	};

	public static final Integer VERSION_MAJOR_NUMBER() {
		return AppContext.get().getBean(Globals.class)
				.getVersionMajorNumber();
	};

	public static final Integer VERSION_MINOR_NUMBER() {
		return AppContext.get().getBean(Globals.class)
				.getVersionMinorNumber();
	};

	public static final boolean IDDICTION_ON() {
		return AppContext.get().getBean(Globals.class)
				.isIddictionOn();
	};;

}