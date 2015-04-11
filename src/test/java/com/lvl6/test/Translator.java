//package com.lvl6.test;
//
//import com.lvl6.proto.ChatProto.TranslateLanguages;
//import com.memetix.mst.detect.Detect;
//import com.memetix.mst.language.Language;
//
////
////import com.lvl6.proto.ChatProto.TranslateLanguages;
////
//////package com.lvl6.test;
//////
//////import java.util.Map;
//////
//////import com.lvl6.misc.MiscMethods;
//////import com.memetix.mst.language.Language;
//////import com.memetix.mst.translate.Translate;
//////
//////public class Translator {
//////	
//////	private static String pClientId = "ToonSquad";
//////	private static String secretId = "bZ3WX/tZHV2KoljCFOwYOWRuR9WpSaa7O/L4oZuUhHo=";
//////
//////	public static void main(String[] args) throws Exception {
//////		long startTime = System.nanoTime();
//////		Translate.setClientId(pClientId);
//////		Translate.setClientSecret(secretId);
//////		
//////		
////////		String translatedText = Translate.execute("u guys suck", Language.CHINESE_TRADITIONAL);
////////		long endTime = System.nanoTime();
////////		long duration = (endTime - startTime);
////////		
////////		System.out.println(translatedText);
////////		System.out.println("time: " + (endTime-startTime)/1000000000 + " seconds");
//////		
////////		String text = "hello everyone";
////////		Map<Language, String> translatedMap = MiscMethods.translate(null, text);
////////		
////////		for(Language language : translatedMap.keySet()) {
////////			System.out.println(language.toString() + " converts to " + translatedMap.get(language));
////////		}
//////		
//////		
//////	}
//////}
////
//public class Translator {
//	
//	private static String pClientId = "ToonSquad";
//	private static String secretId = "bZ3WX/tZHV2KoljCFOwYOWRuR9WpSaa7O/L4oZuUhHo=";
//	
//	public static void main(String[] args) throws Exception {
//		
//		Detect.setClientId(pClientId);
//		Detect.setClientSecret(secretId);
//		
//		Language detelctedLanguage = Detect.execute("hi");
//		System.out.println("hi is" + detelctedLanguage.getName(Language.ENGLISH));
//		
//		
//		
//	}
//}