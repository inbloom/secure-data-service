package org.talend.designer.codegen.translators.orchestration;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TWaitForSqlDataBeginJava
{
  protected static String nl;
  public static synchronized TWaitForSqlDataBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TWaitForSqlDataBeginJava result = new TWaitForSqlDataBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL;
  protected final String TEXT_3 = NL + "    String sqlStr_";
  protected final String TEXT_4 = " = ";
  protected final String TEXT_5 = ";";
  protected final String TEXT_6 = NL + "        String sqlStr_";
  protected final String TEXT_7 = " = \"";
  protected final String TEXT_8 = "\" + ";
  protected final String TEXT_9 = " + \" \" + ";
  protected final String TEXT_10 = ";";
  protected final String TEXT_11 = NL + "        String sqlStr_";
  protected final String TEXT_12 = " = \"";
  protected final String TEXT_13 = "\" + ";
  protected final String TEXT_14 = ";";
  protected final String TEXT_15 = NL + NL + "java.sql.Connection connection_";
  protected final String TEXT_16 = " = (java.sql.Connection)globalMap.get(\"";
  protected final String TEXT_17 = "\");" + NL + "java.sql.Statement statement_";
  protected final String TEXT_18 = " = connection_";
  protected final String TEXT_19 = ".createStatement();" + NL + "int count_";
  protected final String TEXT_20 = " = 0;" + NL + "int rowCount_";
  protected final String TEXT_21 = "=0;" + NL + "while (true) {";
  protected final String TEXT_22 = NL + "        if(";
  protected final String TEXT_23 = " == count_";
  protected final String TEXT_24 = ") {" + NL + "        \tbreak;" + NL + "        }";
  protected final String TEXT_25 = NL + "    statement_";
  protected final String TEXT_26 = ".execute(sqlStr_";
  protected final String TEXT_27 = ");" + NL + "    java.sql.ResultSet set_";
  protected final String TEXT_28 = " = statement_";
  protected final String TEXT_29 = ".getResultSet();" + NL + "    set_";
  protected final String TEXT_30 = ".next();" + NL + "    rowCount_";
  protected final String TEXT_31 = " = set_";
  protected final String TEXT_32 = ".getInt(1);" + NL + "    connection_";
  protected final String TEXT_33 = ".commit();" + NL + "    globalMap.put(\"";
  protected final String TEXT_34 = "_ROW_COUNT\", rowCount_";
  protected final String TEXT_35 = ");" + NL + "    " + NL + "    count_";
  protected final String TEXT_36 = "++;" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_37 = "_CURRENT_ITERATION\", count_";
  protected final String TEXT_38 = ");" + NL + "                " + NL + "    if (!(rowCount_";
  protected final String TEXT_39 = ")) {" + NL + "    \tThread.currentThread().sleep(";
  protected final String TEXT_40 = "*1000);" + NL + "        continue;" + NL + "    }";
  protected final String TEXT_41 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
String cid = node.getUniqueName();
String table = ElementParameterParser.getValue(node,"__TABLE__");
String connection = ElementParameterParser.getValue(node,"__CONNECTION__");
String max = ElementParameterParser.getValue(node,"__MAX_ITERATIONS__");
String operator = ElementParameterParser.getValue(node,"__OPERATOR__");
String value = ElementParameterParser.getValue(node,"__VALUE__");
boolean isEnableWhereClause = ("true").equals(ElementParameterParser.getValue(node,"__ENABLE_WHERE_CLAUSE__"));
boolean isEnableSQLStmt = ("true").equals(ElementParameterParser.getValue(node,"__ENABLE_SQL_STMT__"));
String conn = "conn_" + connection ;
String query = "SELECT COUNT(*) FROM ";

    stringBuffer.append(TEXT_2);
    
if(isEnableSQLStmt) {
    
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(ElementParameterParser.getValue(node,"__SQL_STMT__"));
    stringBuffer.append(TEXT_5);
    
} else {
    if(isEnableWhereClause) {
        
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(query);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(table);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(ElementParameterParser.getValue(node,"__WHERE_CLAUSE__"));
    stringBuffer.append(TEXT_10);
    
    } else {
        
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(query);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(table);
    stringBuffer.append(TEXT_14);
    
    }
}

    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(conn);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    
    if(!("").equals(max)) {
        
    stringBuffer.append(TEXT_22);
    stringBuffer.append(max);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    
    }
    
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(operator);
    stringBuffer.append(value);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(ElementParameterParser.getValue(node, "__WAIT__"));
    stringBuffer.append(TEXT_40);
    stringBuffer.append(TEXT_41);
    return stringBuffer.toString();
  }
}
