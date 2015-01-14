package com.lvl6.utils.utilmethods;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class StringUtils {
  

	private static final Logger log = LoggerFactory.getLogger(StringUtils.class);

	public static String comma = ",";
  public static String getListInString(List<String> clauses, String delimiter) {
    String toreturn = "";
    if (clauses == null || clauses.size() < 1) {
      log.error("invalid parameters passed into StringUtils getListInString. clauses=" + clauses + ", delimiter=" + delimiter);
      return "";
    }
    for (String clause : clauses) {
      toreturn += clause + " " + delimiter + " ";
    }
    return toreturn.substring(0, toreturn.length() - delimiter.length() - 1);
  }
//  
//  public  static String csvIntList(List<Integer> clauses) {
//    String toreturn = "";
//    if (clauses == null || clauses.size() < 1) {
//      log.error("invalid parameters passed into StringUtils csvIntList. clauses=" + clauses);
//      return "";
//    }
//    
//    for (Object clause : clauses) {
//      toreturn += clause + ",";
//    }
//    return toreturn.substring(0, toreturn.length() - 1);
//  }
//
//	public static String csvLongList(List<Long> clauses) {
//		String toreturn = "";
//    if (clauses == null || clauses.size() < 1) {
//      log.error("invalid parameters passed into StringUtils csvIntList. clauses=" + clauses);
//      return "";
//    }
//    
//    for (Object clause : clauses) {
//      toreturn += clause + ",";
//    }
//    return toreturn.substring(0, toreturn.length() - 1);
//	}

  public  static String csvList(List<?> clauses) {
    if (clauses == null || clauses.size() < 1) {
      log.error("invalid parameters passed into StringUtils csvList. clauses=" + clauses);
      return "";
    }
    
    StringBuilder toreturn = new StringBuilder();
    for (Object clause : clauses) {
      toreturn.append(clause); 
      toreturn.append(comma);
    }
    return toreturn.substring(0, toreturn.length() - 1);
  }
  

	public static String implode(Collection<?> thingsToImplode, String delimiter) {
		if (null == thingsToImplode || thingsToImplode.isEmpty()) {
			log.error("invalid parameters passed into StringUtils getListInString. clauses=" +
					thingsToImplode + ", delimiter=" + delimiter);
			return "";
		}
		StringBuilder strBuilder = new StringBuilder();

		for (Object thing : thingsToImplode) {
			strBuilder.append(thing);
//			strBuilder.append(" ");
			strBuilder.append(delimiter);
//			strBuilder.append(" ");
		}

		int length = strBuilder.length();
		int delimLength = delimiter.length(); 
		return strBuilder.substring(0, length - delimLength);
	}

	public static List<Integer> explodeIntoInts(String stringToExplode, 
			String delimiter) {
		List<Integer> returnValue = new ArrayList<Integer>();
		
		if (null == stringToExplode) {
			return returnValue;
		}
		
		StringTokenizer st = new StringTokenizer(stringToExplode, delimiter);
		while (st.hasMoreTokens()) {
			returnValue.add(Integer.parseInt(st.nextToken()));
		}
		return returnValue;
	}

	public static List<Long> explodeIntoLongs(String stringToExplode, 
			String delimiter) {
		List<Long> returnValue = new ArrayList<Long>();
		
		if (null == stringToExplode) {
			return returnValue;
		}
		
		StringTokenizer st = new StringTokenizer(stringToExplode, delimiter);
		while (st.hasMoreTokens()) {
			String token = st.nextToken();
			returnValue.add(Long.parseLong(token.trim()));
		}
		return returnValue;
	}


	public static List<String> explodeIntoStrings(String stringToExplode, 
		String delimiter)
	{
		List<String> returnVal = new ArrayList<String>();
		StringTokenizer st = new StringTokenizer(stringToExplode, delimiter);
		while (st.hasMoreTokens()) {
			String tok = st.nextToken().trim();
			if (tok.isEmpty()) {
				continue;
			}
			returnVal.add(tok);
		}
		
		return returnVal;
	}
	
	public static Map<String, UUID> convertToUUID(Collection<String> strList) {
		Map<String, UUID> retMap = new HashMap<String, UUID>();
		
		if (null == strList) {
			return retMap;
		}
		for (String str : strList) {
			retMap.put(str, UUID.fromString(str));
		}
		return retMap;
	}
}
