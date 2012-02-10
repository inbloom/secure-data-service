package org.talend.designer.codegen.translators.databases.mysql;

import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;

public class TMysqlColumnListBeginJava
{
  protected static String nl;
  public static synchronized TMysqlColumnListBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TMysqlColumnListBeginJava result = new TMysqlColumnListBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "java.sql.Connection conn_";
  protected final String TEXT_3 = " = (java.sql.Connection)globalMap.get(\"";
  protected final String TEXT_4 = "\");" + NL + "" + NL + "String db_";
  protected final String TEXT_5 = " = (String)globalMap.get(\"";
  protected final String TEXT_6 = "\");" + NL + "" + NL + "String table_";
  protected final String TEXT_7 = " = ";
  protected final String TEXT_8 = ";" + NL + "" + NL + "String query_";
  protected final String TEXT_9 = " = \"SELECT column_name,data_type,column_default,is_nullable,column_key,character_maximum_length,numeric_precision,numeric_scale  FROM information_schema.columns WHERE table_schema='\" + db_";
  protected final String TEXT_10 = " + \"' AND table_name='\" + table_";
  protected final String TEXT_11 = " + \"' ORDER BY ordinal_position\";" + NL + "" + NL + "int nb_column_";
  protected final String TEXT_12 = " = 0;" + NL + "" + NL + "java.sql.Statement stmt2_";
  protected final String TEXT_13 = " = conn_";
  protected final String TEXT_14 = ".createStatement();" + NL + "" + NL + "java.sql.ResultSet rs_";
  protected final String TEXT_15 = " = stmt2_";
  protected final String TEXT_16 = ".executeQuery(query_";
  protected final String TEXT_17 = ");" + NL + "" + NL + "while (rs_";
  protected final String TEXT_18 = ".next()) {" + NL + "" + NL + "\tString currentColumnName_";
  protected final String TEXT_19 = " = rs_";
  protected final String TEXT_20 = ".getString(1);" + NL + "\t" + NL + "\tString dataType_";
  protected final String TEXT_21 = " = rs_";
  protected final String TEXT_22 = ".getString(2);" + NL + "\t" + NL + "\tString columnDefault_";
  protected final String TEXT_23 = " = rs_";
  protected final String TEXT_24 = ".getString(3);" + NL + "\t" + NL + "\tString isNullable_";
  protected final String TEXT_25 = " = rs_";
  protected final String TEXT_26 = ".getString(4);" + NL + "\t" + NL + "\tString columKey_";
  protected final String TEXT_27 = " = rs_";
  protected final String TEXT_28 = ".getString(5);" + NL + "\t" + NL + "\tString characterMaximumLength_";
  protected final String TEXT_29 = " = rs_";
  protected final String TEXT_30 = ".getString(6);" + NL + "\t" + NL + "\tString numericPrecision_";
  protected final String TEXT_31 = " = rs_";
  protected final String TEXT_32 = ".getString(7);" + NL + "\t" + NL + "\tString numericScale_";
  protected final String TEXT_33 = " = rs_";
  protected final String TEXT_34 = ".getString(8);" + NL + "" + NL + "\tnb_column_";
  protected final String TEXT_35 = "++;" + NL + "        " + NL + "\tglobalMap.put(\"";
  protected final String TEXT_36 = "_COLUMN_NAME\", currentColumnName_";
  protected final String TEXT_37 = ");" + NL + "\t" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_38 = "_DATA_TYPE\", dataType_";
  protected final String TEXT_39 = ");" + NL + "\t" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_40 = "_COLUMN_DEFAULT\", columnDefault_";
  protected final String TEXT_41 = ");" + NL + "\t" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_42 = "_IS_NULLABLE\", isNullable_";
  protected final String TEXT_43 = ");" + NL + "\t" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_44 = "_COLUMN_KEY\", columKey_";
  protected final String TEXT_45 = ");" + NL + "\t" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_46 = "_CHARACTER_MAXIMUM_LENGTH\", characterMaximumLength_";
  protected final String TEXT_47 = ");" + NL + "\t" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_48 = "_NUMERIC_PRECISION\", numericPrecision_";
  protected final String TEXT_49 = ");" + NL + "\t" + NL + "\tglobalMap.put(\"";
  protected final String TEXT_50 = "_NUMERIC_SCALE\", numericScale_";
  protected final String TEXT_51 = ");";
  protected final String TEXT_52 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();

    String cid = node.getUniqueName();
    
    String connection = ElementParameterParser.getValue(node,"__CONNECTION__");
    
    String conn = "conn_" + connection;
    
    String db = "db_" + connection;
    
    String table = ElementParameterParser.getValue(node,"__TABLE_NAME__");

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(conn);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(db );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(table );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_51);
    stringBuffer.append(TEXT_52);
    return stringBuffer.toString();
  }
}
