package com.lvl6.info;

import java.io.Serializable;

//multiple clan raids can be available at the same time
public class ClanRaid implements Serializable {

	private static final long serialVersionUID = 3611542670148402997L;
	private int id;
	private String clanRaidName;
	private String activeTitleImgName;
	private String activeBackgroundImgName;
	private String activeDescription;
	private String inactiveMonsterImgName;
	private String inactiveDescription;
	private String dialogueText;
	private String spotlightMonsterImgName;

	public ClanRaid(int id, String clanRaidName, String activeTitleImgName,
			String activeBackgroundImgName, String activeDescription,
			String inactiveMonsterImgName, String inactiveDescription,
			String dialogueText, String spotlightMonsterImgName) {
		super();
		this.id = id;
		this.clanRaidName = clanRaidName;
		this.activeTitleImgName = activeTitleImgName;
		this.activeBackgroundImgName = activeBackgroundImgName;
		this.activeDescription = activeDescription;
		this.inactiveMonsterImgName = inactiveMonsterImgName;
		this.inactiveDescription = inactiveDescription;
		this.dialogueText = dialogueText;
		this.spotlightMonsterImgName = spotlightMonsterImgName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getClanRaidName() {
		return clanRaidName;
	}

	public void setClanRaidName(String clanRaidName) {
		this.clanRaidName = clanRaidName;
	}

	public String getActiveTitleImgName() {
		return activeTitleImgName;
	}

	public void setActiveTitleImgName(String activeTitleImgName) {
		this.activeTitleImgName = activeTitleImgName;
	}

	public String getActiveBackgroundImgName() {
		return activeBackgroundImgName;
	}

	public void setActiveBackgroundImgName(String activeBackgroundImgName) {
		this.activeBackgroundImgName = activeBackgroundImgName;
	}

	public String getActiveDescription() {
		return activeDescription;
	}

	public void setActiveDescription(String activeDescription) {
		this.activeDescription = activeDescription;
	}

	public String getInactiveMonsterImgName() {
		return inactiveMonsterImgName;
	}

	public void setInactiveMonsterImgName(String inactiveMonsterImgName) {
		this.inactiveMonsterImgName = inactiveMonsterImgName;
	}

	public String getInactiveDescription() {
		return inactiveDescription;
	}

	public void setInactiveDescription(String inactiveDescription) {
		this.inactiveDescription = inactiveDescription;
	}

	public String getDialogueText() {
		return dialogueText;
	}

	public void setDialogueText(String dialogueText) {
		this.dialogueText = dialogueText;
	}

	public String getSpotlightMonsterImgName() {
		return spotlightMonsterImgName;
	}

	public void setSpotlightMonsterImgName(String spotlightMonsterImgName) {
		this.spotlightMonsterImgName = spotlightMonsterImgName;
	}

	@Override
	public String toString() {
		return "ClanRaid [id=" + id + ", clanRaidName=" + clanRaidName
				+ ", activeTitleImgName=" + activeTitleImgName
				+ ", activeBackgroundImgName=" + activeBackgroundImgName
				+ ", activeDescription=" + activeDescription
				+ ", inactiveMonsterImgName=" + inactiveMonsterImgName
				+ ", inactiveDescription=" + inactiveDescription
				+ ", dialogueText=" + dialogueText
				+ ", spotlightMonsterImgName=" + spotlightMonsterImgName + "]";
	}

}
