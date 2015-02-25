package com.lvl6.retrieveutils.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.lvl6.utils.utilmethods.StringUtils;

@Component
public class QueryConstructionUtil {

	private static final Logger log = LoggerFactory.getLogger(QueryConstructionUtil.class);
	private final String COMMA = ",";
	private final String EQUALITY = "=";
	private final String GREATERTHAN = ">";
	private final String LESSTHAN = "<";
	private final String PERCENT = "%";
	
	private final String AND = " AND ";
	private final String FROM = "from";
	private final String NOTNULL = " NOT NULL ";
	private final String NULLSTR = " NULL ";
	private final String OR = " OR ";
	private final String NOTIN = "NOT IN";
	private final String IN = "IN"; 
	private final String IS = "IS"; 
	private final String LPAREN = "(";
	private final String RPAREN = ")";
	private final String LIKE = "LIKE";
	private final String LIMIT = "LIMIT";
	private final String QUESTION = "?";
	private final String SELECT = "select";
	private final String SELECTSTAR = "select *";
	private final String SPACE = " ";
	private final String WHERE = " where ";

	//at the moment, just EQUALITY conditions, GREATER THAN conditions,  "IN ()" and IS
	//conditions. the argument "values" is another return value. It will contain
	//the values to be set into the CqlPreparedStatement IN the proper order, but is not
	//used at the moment. NOT USED ATM.
	public String selectRowsQueryManyConditions(List<String> columnsToSelect,
			String tableName, Map<String, ?> equalityConditions, String equalityCondDelim,
			Map<String, ?> greaterThanConditions, String greaterThanCondDelim,
			Map<String, Collection<?>> inConditions, String inCondDelim,
			Map<String, ?> isConditions, String isCondDelim, String delimAcrossConditions,
			List<Object> values) {
		StringBuilder sb = createSelectColumnsFromTableString(columnsToSelect, tableName);

		boolean emptyEqConditions = (null == equalityConditions || equalityConditions.isEmpty()); 
		boolean emptyGtConditions = (null == greaterThanConditions || greaterThanConditions.isEmpty());
		boolean emptyInConditions = (null == inConditions || inConditions.isEmpty());
		boolean emptyIsConditions = (null == isConditions || isConditions.isEmpty());

		//(paranoia) if caller doesn't provide anything, select whole table
		if (emptyEqConditions && emptyGtConditions && emptyInConditions && emptyIsConditions) {
			//sb.append(";");
			String query = sb.toString();
			log.info("no args provided. query=" + query);
			return query;
		}
		sb.append(WHERE);

		//EQUALITY CONDITIONS
		String conjunction = "";
		if (!emptyEqConditions) {
			String eqConditionsStr = createComparisonConditionsString(equalityConditions,
					EQUALITY, equalityCondDelim);
			sb.append(eqConditionsStr);

			conjunction = delimAcrossConditions;
		}
		//GREATER THAN CONDITIONS
		if (!emptyGtConditions) {
			String gtConditionsStr = createComparisonConditionsString(greaterThanConditions,
					GREATERTHAN, greaterThanCondDelim);
			sb.append(conjunction);
			sb.append(gtConditionsStr);

			conjunction = delimAcrossConditions;
		} else {
			conjunction = "";
		}
		// IN (VALUES) CONDITIONS
		if (!emptyInConditions) {
			List<String> allInConditions = new ArrayList<String>(); 
			for (String column : inConditions.keySet()) {
				Collection<?> inValues = inConditions.get(column);
				String inConditionsStr = createColInValuesString(column, inValues);
				
				allInConditions.add(inConditionsStr);
			}
			sb.append(conjunction);
			sb.append(StringUtils.implode(allInConditions, inCondDelim));
			conjunction = delimAcrossConditions;
			
		} else {
			conjunction = "";
		}
		// IS CONDITIONS
		if (!emptyIsConditions) {
			String strConditionStr = createIsConditionString(isConditions, isCondDelim);
			sb.append(conjunction);
			sb.append(strConditionStr);
			
			conjunction = delimAcrossConditions;
		} else {
			conjunction = "";
		}

		//CLOSE THE QUERY
		//sb.append(";");
		log.info("conditional selectRowsQuery=" + sb.toString() + "\t values=" + values);
		return sb.toString();
	}

	//generalized method to construct a query, the argument "values" IS another return
	//value. It will contain the values to be set into the CqlPreparedStatement IN the
	//proper order. NOT USED ATM.
	public String selectRowsQueryEqualityConditions(List<String> columnsToSelect,
			String tableName, Map<String, ?> equalityConditions, String condDelim,
			List<Object> values, boolean preparedStatement) {
		StringBuilder sb = createSelectColumnsFromTableString(columnsToSelect, tableName);

		if (null == equalityConditions || equalityConditions.isEmpty()) {
			//sb.append(";");
			log.info("selectRowsQuery=" + sb.toString());
			return sb.toString();
		}
		sb.append(WHERE);

		if (preparedStatement) {
			String preparedEqualityConditionsStr = createPreparedComparisonConditionsString(
					equalityConditions, values, EQUALITY, condDelim);
			sb.append(preparedEqualityConditionsStr);
		} else {
			String equalityConditionsStr = createComparisonConditionsString(
					equalityConditions, EQUALITY, condDelim);
			sb.append(equalityConditionsStr);
		}

		//close the query
		//sb.append(";");
		log.info("conditional selectRowsQuery=" + sb.toString() + "\t values=" + values);
		return sb.toString();
	}
	
	//generalized method to construct a query, the argument "values" IS another return
	//value. It will contain the values to be set into the CqlPreparedStatement IN the
	//proper order. NOT USED ATM.
	public String selectRowsQueryComparisonConditions(List<String> columnsToSelect,
			String tableName, Map<String, ?> equalityConditions, String equalityCondDelim,
			Map<String, ?> lessThanConditions, String lessThanCondDelim,
			Map<String,  ?> greaterThanConditions, String greaterThanCondDelim,
			String delimAcrossConditions, List<Object> values, boolean preparedStatement,
			int limit) {
		StringBuilder sb = createSelectColumnsFromTableString(columnsToSelect, tableName);

		boolean emptyEqConditions = (null == equalityConditions || equalityConditions.isEmpty()); 
		boolean emptyLtConditions = (null == lessThanConditions || lessThanConditions.isEmpty());
		boolean emptyGtConditions = (null == greaterThanConditions || greaterThanConditions.isEmpty());
		
		if (emptyEqConditions && emptyLtConditions && emptyGtConditions) {
			//sb.append(";");
			log.info("selectRowsQuery=" + sb.toString());
			return sb.toString();
		}
		sb.append(WHERE);

		//EQUALITY CONDITIONS
		String conjunction = "";
		if (!emptyEqConditions) {
			String eqConditionsStr = createComparisonConditionsString(equalityConditions,
					EQUALITY, equalityCondDelim);
			sb.append(eqConditionsStr);

			conjunction = delimAcrossConditions;
		}
		//GREATER THAN CONDITIONS
		if (!emptyGtConditions) {
			String gtConditionsStr = createComparisonConditionsString(greaterThanConditions,
					GREATERTHAN, greaterThanCondDelim);
			sb.append(conjunction);
			sb.append(gtConditionsStr);

			conjunction = delimAcrossConditions;
		} else {
			conjunction = "";
		}
		//PERCENT SIGN AT THE BEGINNING
		if (!emptyLtConditions) {
			String ltConditionsStr = createComparisonConditionsString(lessThanConditions,
					LESSTHAN, lessThanCondDelim);
			sb.append(conjunction);
			sb.append(ltConditionsStr);

			conjunction = delimAcrossConditions;
		} else {
			conjunction = "";
		}

		if (limit > 0) {
			sb.append(SPACE);
			sb.append(LIMIT);
			sb.append(SPACE);
			sb.append(limit);
		}
		
		//close the query
		//sb.append(";");
		log.info("conditional selectRowsQuery=" + sb.toString() + "\t values=" + values);
		return sb.toString();
	}

	//generalized method to construct a query, the argument "values" is another return
	//value. It will contain the values to be set into the CqlPreparedStatement in the
	//proper order. NOT USED ATM.
	public String selectRowsQueryLikeConditions(List<String> columnsToSelect,
			String tableName, Map<String, ?> beginsWith, String beginsWithCondDelim,
			Map<String, ?> beginsAndEndsWith, String beginsAndEndsWithCondDelim,
			Map<String, ?> endsWith, String endsWithCondDelim, String overallDelimiter,
			List<Object> values, boolean preparedStatement) {
		StringBuilder sb = createSelectColumnsFromTableString(columnsToSelect, tableName);
		
		boolean emptyBeginsWith = (null == beginsWith || beginsWith.isEmpty());
		boolean emptyBeginsAndEndsWith = (null == beginsAndEndsWith || beginsAndEndsWith.isEmpty());
		boolean emptyEndsWith = (null == endsWith || endsWith.isEmpty());
		
		if (emptyBeginsWith && emptyBeginsAndEndsWith && emptyEndsWith) {
			//sb.append(";");
			log.info("selectRowsQuery=" + sb.toString());
			return sb.toString();
		}
		sb.append(WHERE);
		
		String conjunction = "";
		//PERCENT SIGN AT THE END
		if (!emptyBeginsWith) {
			String beginsWithConditionsStr = createLikeConditionsString(
					beginsWith, beginsWithCondDelim, true, false);
			sb.append(beginsWithConditionsStr);
			
			conjunction = overallDelimiter;
		}
		//PERCENT SIGN AT THE BEGINNING AND END
		if (!emptyBeginsAndEndsWith) {
			String beginsAndEndsWithCondStr = createLikeConditionsString(beginsAndEndsWith,
					beginsAndEndsWithCondDelim, true, true);
			sb.append(conjunction);
			sb.append(beginsAndEndsWithCondStr);
			
			conjunction = overallDelimiter;
		} else {
			conjunction = "";
		}
		//PERCENT SIGN AT THE BEGINNING
		if (!emptyEndsWith) {
			String endsWithCondStr = createLikeConditionsString(beginsAndEndsWith,
					beginsAndEndsWithCondDelim, true, true);
			sb.append(conjunction);
			sb.append(endsWithCondStr);
			
			conjunction = overallDelimiter;
		} else {
			conjunction = "";
		}
		
		//close the query
		//sb.append(";");
		log.info("(LIKE) selectRowsQuery=" + sb.toString() + "\t values=" + values);
		return sb.toString();
	}
	
	
	//generalized method to construct a query, the argument "values" is another return
	//value. It will contain the values to be set into the CqlPreparedStatement in the
	//proper order. NOT USED ATM.
	public String selectRowsQueryInConditions(List<String> columnsToSelect, String tableName,
			Map<String, Collection<?>> inConditions, String conditionDelimiter,
			List<Object> values, boolean preparedStatement) {
		StringBuilder sb = createSelectColumnsFromTableString(columnsToSelect, tableName);

		if (null == inConditions || inConditions.isEmpty()) {
			//sb.append(";");
			log.info("selectRowsQuery=" + sb.toString());
			return sb.toString();
		}
		sb.append(WHERE);

		if (preparedStatement) {
			List<String> allInConditions = new ArrayList<String>(); 
			for (String column : inConditions.keySet()) {
				Collection<?> inValues = inConditions.get(column);
				String inConditionsStr = createPreparedColInValuesString(
						column, inValues, values);
				
				allInConditions.add(inConditionsStr);
			}
			sb.append(StringUtils.implode(allInConditions, conditionDelimiter));
			
		} else {
			List<String> allInConditions = new ArrayList<String>(); 
			for (String column : inConditions.keySet()) {
				Collection<?> inValues = inConditions.get(column);
				String inConditionsStr = createColInValuesString(column, inValues);
				
				allInConditions.add(inConditionsStr);
			}
			sb.append(StringUtils.implode(allInConditions, conditionDelimiter));
		}

		//close the query
		//sb.append(";");
		log.info("(IN) selectRowsQuery=" + sb.toString() + "\t values=" + values);
		return sb.toString();
	}

	//generalized method to construct a query, the argument "values" is another return
	//value. It will contain the values to be set into the CqlPreparedStatement in the
	//proper order. NOT USED ATM (used in researchretrieve)
	public String selectRowsQueryEqualityAndInConditions(List<String> columnsToSelect,
			String tableName, Map<String, ?> equalityConditions, String eqDelim,
			Map<String, Collection<?>> inConditions, String inDelim,
			String overallDelim, List<Object> values, boolean preparedStatement) {
		StringBuilder sb = createSelectColumnsFromTableString(
				columnsToSelect, tableName);

		boolean emptyEqConditions = (null == equalityConditions || equalityConditions.isEmpty());
		boolean emptyInConditions = (null == inConditions || inConditions.isEmpty());
		
		sb.append(WHERE);

		String conjunction = "";
		if (preparedStatement) {
			if (!emptyEqConditions) {
				String preparedEqualityConditionsStr =
						createPreparedComparisonConditionsString(
								equalityConditions, values, EQUALITY, eqDelim);
				sb.append(preparedEqualityConditionsStr);
				
				//this is so if inConditions are set, the conjunction is 
				//used after setting these equality conditions
				conjunction = overallDelim;
			}
			if (!emptyInConditions) {
				List<String> allInConditions = new ArrayList<String>(); 
				for (String column : inConditions.keySet()) {
					Collection<?> inValues = inConditions.get(column);
					String inConditionsStr = createPreparedColInValuesString(
							column, inValues, values);
					
					allInConditions.add(inConditionsStr);
				}
				
				sb.append(conjunction); //in case equality conditions are set
				sb.append(StringUtils.implode(allInConditions, inDelim));
			}
		} else {
			if (!emptyEqConditions) {
				String equalityConditionsStr = createComparisonConditionsString(
						equalityConditions, EQUALITY, eqDelim);
				sb.append(equalityConditionsStr);
				
				//this is so if inConditions are set, the conjunction is 
				//used after setting these equality conditions
				conjunction = overallDelim;
			}
			if (!emptyInConditions) {
				List<String> allInConditions = new ArrayList<String>(); 
				for (String column : inConditions.keySet()) {
					Collection<?> inValues = inConditions.get(column);
					String inConditionsStr = createColInValuesString(column, inValues);
					
					allInConditions.add(inConditionsStr);
				}
				
				sb.append(conjunction); //in case equality conditions are set
				sb.append(StringUtils.implode(allInConditions, inDelim));
				
			}
		}
		
		//close the query
		//sb.append(";");
		log.info("(Equality and IN) selectRowsQuery=" + sb.toString() +
				"\t values=" + values);
		return sb.toString();
	}
	
	public StringBuilder createSelectColumnsFromTableString(
			List<String> columnsToSelect, String tableName) {
		StringBuilder sb = new StringBuilder();
		if (null == columnsToSelect || columnsToSelect.isEmpty()) { 
			sb.append(SELECTSTAR);
		} else {
			sb.append(SELECT);
			sb.append(SPACE);
			String columnsToSelectStr = StringUtils.implode(columnsToSelect, COMMA);
			sb.append(columnsToSelectStr);
		}
		sb.append(SPACE);
		sb.append(FROM);
		sb.append(SPACE);
		sb.append(tableName);
		return sb;
	}

	//the argument "values" is another return value. It will contain the values
	//to be set into the CqlPreparedStatement IN the proper order.
	//look at createComparisonConditionsString() for details. This IS just a copy of it.
	public String createPreparedComparisonConditionsString(Map<String, ?> conditions,
			List<Object> values, String comparator, String conditionDelimiter) {
		List<Object> clauses = new ArrayList<Object>();

		for (String key : conditions.keySet()) {
			StringBuilder clauseSb = new StringBuilder();
			Object obj = conditions.get(key);

			clauseSb.append(key);
			clauseSb.append(comparator);
			clauseSb.append(QUESTION);

			values.add(obj);

			String clause = clauseSb.toString();
			clauses.add(clause);
		}
		String equalityConditionsStr = 
				StringUtils.implode(clauses, conditionDelimiter); 

		log.info("equalityConditionsStr=" + equalityConditionsStr +
				"\t values=" + values);
		return equalityConditionsStr;
	}

	public String createComparisonConditionsString(Map<String, ?> conditions,
			String comparator, String conditionDelimiter) {

		List<Object> clauses = new ArrayList<Object>();

		//stringify the EQUALITY conditions. 
		//e.g. Map(col1=>x, col2=>y,...,colN=>something) 
		//col1 => x, now becomes String(col1) + String(=) + String(x)
		for (String key : conditions.keySet()) {
			StringBuilder clauseSb = new StringBuilder();
			Object obj = conditions.get(key);

			clauseSb.append(key);
			clauseSb.append(comparator);
			clauseSb.append(obj);

			String clause = clauseSb.toString();
			clauses.add(clause);
		}

		//We made Map(col1=>x, col2=>y,...,colN=>something) into 
		//List(String(col1=x), String(col2=y),..., String(colN=Something))
		//implode (join together) the EQUALITY conditions with "AND"

		//e.g. List becomes String(String(col1=x) + AND +...+String(colN=something))
		String equalityConditionsStr = 
				StringUtils.implode(clauses, conditionDelimiter); 

		log.info("equalityConditionsStr=" + equalityConditionsStr);
		return equalityConditionsStr;
	}

	public String createPreparedColInValuesString(String column,
			Collection<?> inValues, List<Object> values) {
		StringBuilder sb = new StringBuilder();
		sb.append(column);
		sb.append(SPACE);
		sb.append(IN);
		sb.append(SPACE);
		sb.append(LPAREN);
		
		int size = inValues.size();
		List<String> questions = Collections.nCopies(size, QUESTION);
		String valuesStr = StringUtils.implode(questions, COMMA);
		sb.append(valuesStr);
		
		sb.append(RPAREN);
		values.addAll(inValues);
		
		String result = sb.toString();
		return result;
	}
	
	public String createColInValuesString(String column, Collection<?> inValues) {
		StringBuilder sb = new StringBuilder();
		sb.append(column);
		sb.append(SPACE);
		sb.append(IN);
		sb.append(SPACE);
		sb.append(LPAREN);

		String valuesStr = StringUtils.implode(inValues, COMMA);
		sb.append(valuesStr);

		sb.append(RPAREN);
		
		String result = sb.toString();
		return result;
	}
	
	public String createColNotInValuesString(String column, Collection<?> inValues) {
		StringBuilder sb = new StringBuilder();
		sb.append(column);
		sb.append(SPACE);
		sb.append(NOTIN);
		sb.append(SPACE);
		sb.append(LPAREN);

		String valuesStr = StringUtils.implode(inValues, COMMA);
		sb.append(valuesStr);

		sb.append(RPAREN);
		String result = sb.toString();
		return result;
	}
	
	public String createIsConditionString(Map<String, ?> isConditions,
			String conditionDelimiter) {
		List<Object> clauses = new ArrayList<Object>();

		for (String key : isConditions.keySet()) {
			StringBuilder clauseSb = new StringBuilder();
			Object obj = isConditions.get(key);

			clauseSb.append(key);
			clauseSb.append(SPACE);
			clauseSb.append(IS);
			clauseSb.append(SPACE);
			clauseSb.append(obj);

			String clause = clauseSb.toString();
			clauses.add(clause);
		}


		String isConditionsStr = StringUtils.implode(clauses, conditionDelimiter); 

		log.info("equalityConditionsStr=" + isConditionsStr);
		return isConditionsStr;
	}
	
	public String createPreparedLikeConditionsString(Map<String, ?> likeCondition,
			String likeCondDelim, boolean beginsWith, boolean endsWith,
			List<Object> values) {
		List<Object> clauses = new ArrayList<Object>();

		for (String key : likeCondition.keySet()) {
			StringBuilder clauseSb = new StringBuilder();
			Object obj = likeCondition.get(key);

			clauseSb.append(key);
			clauseSb.append(SPACE);
			clauseSb.append(LIKE);
			clauseSb.append(SPACE);
			
			if (endsWith) {
				clauseSb.append(PERCENT);
			}
			clauseSb.append(QUESTION);
			if (beginsWith) {
				clauseSb.append(PERCENT);
			}
			values.add(obj);

			String clause = clauseSb.toString();
			clauses.add(clause);
		}
		String likeConditionsStr = StringUtils.implode(clauses, likeCondDelim); 

		log.info("equalityConditionsStr=" + likeConditionsStr + "\t values=" + values);
		return likeConditionsStr;
	}
	
	public String createLikeConditionsString(Map<String, ?> likeCondition,
			String likeCondDelim, boolean beginsWith, boolean endsWith) {
		List<Object> clauses = new ArrayList<Object>();

		for (String key : likeCondition.keySet()) {
			StringBuilder clauseSb = new StringBuilder();
			Object obj = likeCondition.get(key);

			clauseSb.append(key);
			clauseSb.append(SPACE);
			clauseSb.append(LIKE);
			clauseSb.append(SPACE);

			if (endsWith) {
				clauseSb.append(PERCENT);
			}
			clauseSb.append(obj);
			if (beginsWith) {
				clauseSb.append(PERCENT);
			}

			String clause = clauseSb.toString();
			clauses.add(clause);
		}
		String likeConditionsStr = StringUtils.implode(clauses, likeCondDelim); 

		log.info("equalityConditionsStr=" + likeConditionsStr);
		return likeConditionsStr;
	}

	//atm only used by HazelcastPvpUtil
	public String createWhereConditionString(Map<String, ?> lessThanConditions,
			Map<String, ?> greaterThanConditions, boolean isNotIn,
			Map<String, Collection<?>> inConditions) {
		
		StringBuilder whereConditionsSb = new StringBuilder();
		
		boolean emptyLtConditions = (null == lessThanConditions ||
				lessThanConditions.isEmpty()); 
		boolean emptyGtConditions = (null == greaterThanConditions ||
				greaterThanConditions.isEmpty());
		boolean emptyInConditions = (null == inConditions ||
				inConditions.isEmpty());
		
		String conjunction = "";
		if (!emptyLtConditions) {
			String eqConditionsStr = createComparisonConditionsString(
					lessThanConditions, LESSTHAN, AND);
			whereConditionsSb.append(eqConditionsStr);

			conjunction = AND;
		}
		//GREATER THAN CONDITIONS
		if (!emptyGtConditions) {
			String gtConditionsStr = createComparisonConditionsString(
					greaterThanConditions, GREATERTHAN, AND);
			whereConditionsSb.append(conjunction);
			whereConditionsSb.append(gtConditionsStr);

			conjunction = AND;
		} else {
			conjunction = "";
		}
		// IN (SOME VALUES) CONDITIONS
		if (!emptyInConditions) {
			List<String> allInConditions = new ArrayList<String>(); 
			for (String column : inConditions.keySet()) {
				Collection<?> inValues = inConditions.get(column);
				String inConditionsStr = createColNotInValuesString(column, inValues);
				
				allInConditions.add(inConditionsStr);
			}
			whereConditionsSb.append(conjunction);
			whereConditionsSb.append(StringUtils.implode(allInConditions, AND));
		}
		
		return whereConditionsSb.toString();
	}
	
	
	
	public String getAnd() {
		return AND;
	}

	public String getNotNull() {
		return NOTNULL;
	}

	public String getNullStr() {
		return NULLSTR;
	}

	public String getOr() {
		return OR;
	}
	
}
