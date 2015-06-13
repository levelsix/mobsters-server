package com.lvl6.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lvl6.properties.ControllerConstants;
import com.lvl6.proto.ChatProto.TranslateLanguages;
import com.lvl6.retrieveutils.rarechange.ServerToggleRetrieveUtils;
import com.memetix.mst.detect.Detect;
import com.memetix.mst.language.Language;
import com.memetix.mst.translate.Translate;

@Component
public class TranslationUtils {

	private static String byronPClientId = "ToonSquad";
	private static String byronSecretId = "bZ3WX/tZHV2KoljCFOwYOWRuR9WpSaa7O/L4oZuUhHo=";

	private static String pClientId = "ToonSquadProd";
	private static String secretId = "KhWUZfHUsJ484zCVAOmWOdqzYhqFri0EzgutiLRdqJg=";

	
	private static final Logger log = LoggerFactory
			.getLogger(TranslationUtils.class);
	

	public String[] translateInBulk(String[] text, Language recipientLanguage,
			ServerToggleRetrieveUtils serverToggleRetrieveUtils) {
		if (serverToggleRetrieveUtils.getToggleValueForName(
				ControllerConstants.SERVER_TOGGLE__USE_BYRON_TRANSLATIONS))
		{
			log.error("byron translator on!");
			Translate.setClientId(byronPClientId);
			Translate.setClientSecret(byronSecretId);
		} else {
			Translate.setClientId(pClientId);
			Translate.setClientSecret(secretId);
		}
		String[] returnArray = null;

		try {
			returnArray = Translate.execute(text, recipientLanguage);
		} catch (Exception e) {
			List<String> listForLog = new ArrayList<String>();
			for(int i=0; i<text.length; i++) {
				listForLog.add(text[i]);
			}
			log.error("translateInBulk error, textarray: {}", listForLog, e);
		}
		return returnArray;
	}


//	public Map<TranslateLanguages, String> translate(Language sourceLanguage,
//			Language recipientLanguage, String text, ServerToggleRetrieveUtils 
//			serverToggleRetrieveUtils) {
//		if (serverToggleRetrieveUtils.getToggleValueForName(
//				ControllerConstants.SERVER_TOGGLE__USE_BYRON_TRANSLATIONS))
//		{
//			log.error("byron translator on!");
//			Translate.setClientId(byronPClientId);
//			Translate.setClientSecret(byronSecretId);
//		} else {
//			Translate.setClientId(pClientId);
//			Translate.setClientSecret(secretId);
//		}
//
//		String translatedText = "";
//		Map<TranslateLanguages, String> returnMap = new HashMap<TranslateLanguages, String>();
//
//		List<Language> listOfLanguages = new ArrayList<Language>();
//		listOfLanguages.add(Language.ARABIC);
//		listOfLanguages.add(Language.ENGLISH);
//		listOfLanguages.add(Language.FRENCH);
//		listOfLanguages.add(Language.GERMAN);
//		listOfLanguages.add(Language.SPANISH);
//		listOfLanguages.add(Language.RUSSIAN);
//
//		try {
//			if(recipientLanguage != null) {
//				if(sourceLanguage == null) {
//					translatedText = Translate.execute(text, recipientLanguage);
//				}
//				else {
//					translatedText = Translate.execute(text, sourceLanguage, recipientLanguage);
//				}
//				TranslateLanguages tl2 = convertFromLanguageToEnum(recipientLanguage, 
//						serverToggleRetrieveUtils);
//				returnMap.put(tl2, translatedText);
//			}
//			else {
//				for(Language language2 : listOfLanguages) {
//					if(sourceLanguage != null) {
//						if(sourceLanguage.toString().equalsIgnoreCase(language2.toString())) {
//							TranslateLanguages tl = convertFromLanguageToEnum(language2, 
//									serverToggleRetrieveUtils);
//							returnMap.put(tl, text);
//						}
//					}
//					else {
//						translatedText = Translate.execute(text, language2);
//
//					}
//				}
//			}
//		} catch (Exception e) {
//			log.error("translate", e);
//		}
//		return returnMap;
//	}


	public Map<TranslateLanguages, String> translate(Language sourceLanguage, Language recipientLanguage,
			String text, ServerToggleRetrieveUtils serverToggleRetrieveUtils) {
		if (serverToggleRetrieveUtils.getToggleValueForName(
				ControllerConstants.SERVER_TOGGLE__USE_BYRON_TRANSLATIONS))
		{
			log.error("byron translator on!");
			Translate.setClientId(byronPClientId);
			Translate.setClientSecret(byronSecretId);
		} else {
			Translate.setClientId(pClientId);
			Translate.setClientSecret(secretId);
		}

		String translatedText = "";
		Map<TranslateLanguages, String> returnMap = new HashMap<TranslateLanguages, String>();

		List<Language> listOfLanguages = new ArrayList<Language>();
		if(recipientLanguage == null) {
			listOfLanguages.add(Language.ARABIC);
			listOfLanguages.add(Language.ENGLISH);
			listOfLanguages.add(Language.FRENCH);
			listOfLanguages.add(Language.GERMAN);
			listOfLanguages.add(Language.SPANISH);
			listOfLanguages.add(Language.RUSSIAN);
		}
		else {
			listOfLanguages.add(recipientLanguage);
		}
		
		try {
			for(Language language2 : listOfLanguages) {
				//				if(sourceLanguage.toString().equalsIgnoreCase(language2.toString())) {
				//					TranslateLanguages tl = convertFromLanguageToEnum(language2);
				//					returnMap.put(tl, text);
				//				}
				//				else {
				if(sourceLanguage == null) {
					translatedText = Translate.execute(text, language2);
				}
				else {
					translatedText = Translate.execute(text, sourceLanguage, language2);
				}
				TranslateLanguages tl = convertFromLanguageToEnum(language2, 
						serverToggleRetrieveUtils);
				returnMap.put(tl, translatedText);
				log.info("Translating to {}: {}", language2, translatedText);
				//				}
			}
		} catch (Exception e) {
			log.error("translateForGlobal", e);
		}
		return returnMap;

	}

	public TranslateLanguages convertFromLanguageToEnum(Language language,
			ServerToggleRetrieveUtils serverToggleRetrieveUtils) {
		TranslateLanguages tl = TranslateLanguages.NO_TRANSLATION;
		try {
			if(language.getName(Language.ENGLISH).equalsIgnoreCase("ARABIC")) {
				tl = TranslateLanguages.ARABIC;
			}
			else if(language.getName(Language.ENGLISH).equalsIgnoreCase("ENGLISH")) {
				tl = TranslateLanguages.ENGLISH;
			}
			else if(language.getName(Language.ENGLISH).equalsIgnoreCase("FRENCH")) {
				tl = TranslateLanguages.FRENCH;
			}
			else if(language.getName(Language.ENGLISH).equalsIgnoreCase("GERMAN")) {
				tl = TranslateLanguages.GERMAN;
			}
			else if(language.getName(Language.ENGLISH).equalsIgnoreCase("RUSSIAN")) {
				tl = TranslateLanguages.RUSSIAN;
			}
			else if(language.getName(Language.ENGLISH).equalsIgnoreCase("SPANISH")){
				tl = TranslateLanguages.SPANISH;
			}
			else {
				tl = TranslateLanguages.NO_TRANSLATION;
			}
		} catch (Exception e) {
			log.error("error converting from language to enum, language " + language, e);
		}
		return tl;

	}

	public Language convertFromEnumToLanguage(TranslateLanguages tl) {
		if(tl.toString().equalsIgnoreCase("ARABIC")) {
			return Language.ARABIC;
		}
		else if(tl.toString().equalsIgnoreCase("ENGLISH")) {
			return Language.ENGLISH;
		}
		else if(tl.toString().equalsIgnoreCase("FRENCH")) {
			return Language.FRENCH;
		}
		else if(tl.toString().equalsIgnoreCase("GERMAN")) {
			return Language.GERMAN;
		}
		else if(tl.toString().equalsIgnoreCase("SPANISH")) {
			return Language.SPANISH;
		}
		else if(tl.toString().equalsIgnoreCase("RUSSIAN")) {
			return Language.RUSSIAN;
		}
		else return null;
	}

	public Language detectedLanguage(String text, ServerToggleRetrieveUtils 
			serverToggleRetrieveUtils) {
		if (serverToggleRetrieveUtils.getToggleValueForName(
				ControllerConstants.SERVER_TOGGLE__USE_BYRON_TRANSLATIONS))
		{
			log.error("byron translator on!");
			Translate.setClientId(byronPClientId);
			Translate.setClientSecret(byronSecretId);
		} else {
			Translate.setClientId(pClientId);
			Translate.setClientSecret(secretId);
		}
        Language detectedLanguage = null;

        try {
			detectedLanguage = Detect.execute(text);
		} catch (Exception e) {
			log.error("error detecting language of text={}", text, e);
		}
        return detectedLanguage;
	}
	
	
}
