package com.lvl6.info;

import java.io.Serializable;

public class MonsterBattleDialogue implements Serializable {

	private static final long serialVersionUID = -5528835530547990738L;
	private int id;
	private int monsterId;
	private String dialogueType;
	private String dialogue;
	private float probabilityUttered;
	
	public MonsterBattleDialogue(int id, int monsterId, String dialogueType,
			String dialogue, float probabilityUttered) {
		super();
		this.id = id;
		this.monsterId = monsterId;
		this.dialogueType = dialogueType;
		this.dialogue = dialogue;
		this.probabilityUttered = probabilityUttered;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public int getMonsterId() {
		return monsterId;
	}
	public void setMonsterId(int monsterId) {
		this.monsterId = monsterId;
	}
	public String getDialogueType() {
		return dialogueType;
	}
	public void setDialogueType(String dialogueType) {
		this.dialogueType = dialogueType;
	}
	public String getDialogue() {
		return dialogue;
	}
	public void setDialogue(String dialogue) {
		this.dialogue = dialogue;
	}
	public float getProbabilityUttered() {
		return probabilityUttered;
	}
	public void setProbabilityUttered(float probabilityUttered) {
		this.probabilityUttered = probabilityUttered;
	}

	@Override
	public String toString() {
		return "MonsterBattleDialogue [id=" + id + ", monsterId=" + monsterId
				+ ", dialogueType=" + dialogueType + ", dialogue=" + dialogue
				+ ", probabilityUttered=" + probabilityUttered + "]";
	}
	
}
