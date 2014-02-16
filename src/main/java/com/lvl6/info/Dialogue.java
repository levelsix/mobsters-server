package com.lvl6.info;

import java.io.Serializable;
import java.util.List;

public class Dialogue implements Serializable {

	private static final long serialVersionUID = -8118069262824842364L;
  List<Boolean> isLeftSides;
	List<String> speakers;
	List<String> speakerTexts;

	public Dialogue(List<String> speakers, List<String> speakerTexts, List<Boolean> isLeftSides) {
		this.speakers = speakers;
		this.speakerTexts = speakerTexts;
		this.isLeftSides = isLeftSides;
	}

  public List<Boolean> getIsLeftSides() {
    return isLeftSides;
  }

	public List<String> getSpeakers() {
		return speakers;
	}

	public List<String> getSpeakerTexts() {
		return speakerTexts;
	}

	@Override
	public String toString() {
		return "Dialogue [speakers=" + speakers + ", speakerTexts="
				+ speakerTexts + "]";
	}

}
