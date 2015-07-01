package com.lvl6.mobsters.db.jooq

import org.jooq.util.JavaGenerator
import org.jooq.tools.JooqLogger
import org.jooq.util.SchemaDefinition
import scala.collection.JavaConversions._
import org.jooq.util.TableDefinition
import java.nio.file.Paths
import java.nio.file.Files
import org.jooq.util.GeneratorStrategy.Mode
import java.beans.Introspector
import org.jooq.util.Database
import java.lang.RuntimeException
import org.jooq.util.JavaWriter

class MobstersJooqCodeGenerator extends JavaGenerator {
/*  private val log:JooqLogger = JooqLogger.getLogger(classOf[MobstersJooqCodeGenerator])
  val nl = System.lineSeparator()
  
  override def generateUserDefinedTypes(schema:SchemaDefinition)={
    log.info("Generating user defined types")
    generateSpringDao(schema)
  }
  
  protected def generateSpringDao(schema:SchemaDefinition)={
    log.info("Generating spring-daos.xml")
    val sb = new StringBuilder();
    addSpringDaosHeader(sb)
    schema.getTables.foreach{ table =>
      addTableDao(sb, table)  
    }
    addSpringDaosFooter(sb)
    val springDaoString = sb.toString()
    log.info(springDaoString)
    Files.write(Paths.get("src/main/resources/spring-daos.xml"), springDaoString.getBytes());
  }
  
  protected def addTableDao(sb:StringBuilder, table:TableDefinition)={
    val packageName = getStrategy.getJavaPackageName(table, Mode.DAO)
    val className = getStrategy().getJavaClassName(table, Mode.DAO)
    val classId = Introspector.decapitalize(className)
    sb.append(
s"""  
  <bean class="$packageName.$className" id="$classId">
    <constructor-arg index="0" ref="config" />
  </bean>
  """)
  }
  
  protected def addSpringDaosHeader(sb:StringBuilder) = {
  sb.append(
"""<?xml version="1.0" encoding="UTF-8"?>
<beans xmlns="http://www.springframework.org/schema/beans"
  xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" xmlns:aop="http://www.springframework.org/schema/aop"
  xmlns:hz="http://www.hazelcast.com/schema/spring"
  xmlns:tx="http://www.springframework.org/schema/tx"
  xsi:schemaLocation="http://www.springframework.org/schema/beans 
  http://www.springframework.org/schema/beans/spring-beans.xsd
  http://www.springframework.org/schema/aop 
  http://www.springframework.org/schema/aop/spring-aop.xsd
  http://www.hazelcast.com/schema/spring
      http://www.hazelcast.com/schema/spring/hazelcast-spring.xsd
      http://www.springframework.org/schema/tx 
    http://www.springframework.org/schema/tx/spring-tx.xsd
  ">""").append(nl)
  }
  
  protected def addSpringDaosFooter(sb:StringBuilder)={
    sb.append(nl).append("""</beans>""")
  }*/
  
  override def generatePojoClassFooter(table:TableDefinition, out:JavaWriter)={
      super.generatePojoClassFooter(table, out)
      
      val recordClassName = getStrategy().getFullJavaClassName(table, Mode.RECORD);
      val pojoClassName = getStrategy().getJavaClassName(table, Mode.POJO);
      
      val toStringHelperName = "poop"
      
      var sb = new StringBuilder()
      sb.append("return \"")
      sb.append(pojoClassName)
      sb.append("[\" + ")
      sb.append(toStringHelperName)
      sb.append(".valuesRow() + \"]\";")
      
      out.println();
      out.tab(1).println()
      out.tab(1).println("public String toString() {");
      out.tab(2).println(s"$recordClassName $toStringHelperName = new $recordClassName();");
      out.tab(2).println(s"$toStringHelperName.from(this);")
      out.tab(2).println(sb.toString());
      out.tab(1).println("}");
  }

}