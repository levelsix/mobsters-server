package com.lvl6.info;

public class TranslatedText {

	private String language;
	private String text;
	public TranslatedText() {
		super();
		// TODO Auto-generated constructor stub
	}
	public TranslatedText(String language, String text) {
		super();
		this.language = language;
		this.text = text;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	@Override
	public String toString() {
		return "TranslatedText [language=" + language + ", text=" + text + "]";
	}
	
	
}
