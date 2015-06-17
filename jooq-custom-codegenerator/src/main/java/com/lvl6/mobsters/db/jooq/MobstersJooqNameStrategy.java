package com.lvl6.mobsters.db.jooq;

import org.jooq.tools.StringUtils;
import org.jooq.util.DefaultGeneratorStrategy;
import org.jooq.util.Definition;
import org.jooq.util.TableDefinition;

public class MobstersJooqNameStrategy extends DefaultGeneratorStrategy {
    
//    @Override
//    public String getJavaMemberName(Definition definition, Mode mode) {
//        return getJavaClassName0LC(definition, mode);
//    }
//    
//    private String getJavaClassName0LC(Definition definition, Mode mode) {
//        String result = getJavaClassName0(definition, mode);
//        return result.substring(0, 1).toLowerCase() + result.substring(1);
//    }
//    
//    private String getJavaClassName0(Definition definition, Mode mode) {
//        StringBuilder result = new StringBuilder();
//
//        result.append(StringUtils.toCamelCase(definition.getOutputName()));
//
//        if (mode == Mode.RECORD) {
//            result.append("Record");
//        }
//        else if (mode == Mode.DAO) {
//            result.append("Dao");
//        }
//        else if (mode == Mode.INTERFACE) {
//            result.insert(0, "I");
//        }
//        else if (mode == Mode.POJO) {
//        	result.append("Pojo");
//        }
//
//        return result.toString();
//    }
	
    @Override
    public String getJavaClassName(final Definition definition, final Mode mode) {
        if(mode == Mode.POJO) {
        	return super.getJavaClassName(definition, mode).concat("Pojo");
        }
        else return super.getJavaClassName(definition, mode);
    }
    
    
}
