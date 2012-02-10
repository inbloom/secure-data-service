package org.talend.designer.codegen.translators.databases.teradata;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TTeradataRowBeginJava
{
  protected static String nl;
  public static synchronized TTeradataRowBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TTeradataRowBeginJava result = new TTeradataRowBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "\t\tjava.sql.Connection conn_";
  protected final String TEXT_3 = " = (java.sql.Connection)globalMap.get(\"";
  protected final String TEXT_4 = "\");" + NL + "\t\t";
  protected final String TEXT_5 = NL + "\t    java.lang.Class.forName(\"com.teradata.jdbc.TeraDriver\");" + NL + "\t    String dbProperties_";
  protected final String TEXT_6 = " = ";
  protected final String TEXT_7 = ";" + NL + "\t\tString url_";
  protected final String TEXT_8 = " = null;" + NL + "\t\tif(dbProperties_";
  protected final String TEXT_9 = " == null || dbProperties_";
  protected final String TEXT_10 = ".trim().length() == 0) {" + NL + "\t\t\turl_";
  protected final String TEXT_11 = " = \"jdbc:teradata://\" + ";
  protected final String TEXT_12 = ";     " + NL + "\t\t} else {" + NL + "\t\t    url_";
  protected final String TEXT_13 = " = \"jdbc:teradata://\" + ";
  protected final String TEXT_14 = " + \"/\" + ";
  protected final String TEXT_15 = ";" + NL + "\t\t}       " + NL + "\t    String dbUser_";
  protected final String TEXT_16 = " = ";
  protected final String TEXT_17 = ";" + NL + "\t    String dbPwd_";
  protected final String TEXT_18 = " = ";
  protected final String TEXT_19 = ";" + NL + "\t    java.sql.Connection conn_";
  protected final String TEXT_20 = " = java.sql.DriverManager.getConnection(url_";
  protected final String TEXT_21 = ",dbUser_";
  protected final String TEXT_22 = ",dbPwd_";
  protected final String TEXT_23 = ");" + NL + "\t    ";
  protected final String TEXT_24 = NL;
  protected final String TEXT_25 = NL + "        conn_";
  protected final String TEXT_26 = ".setAutoCommit(false);        " + NL + "        int commitEvery_";
  protected final String TEXT_27 = " = ";
  protected final String TEXT_28 = ";    " + NL + "        int commitCounter_";
  protected final String TEXT_29 = " = 0;";
  protected final String TEXT_30 = NL + "\tjava.sql.PreparedStatement pstmt_";
  protected final String TEXT_31 = " = conn_";
  protected final String TEXT_32 = ".prepareStatement(";
  protected final String TEXT_33 = ");\t";
  protected final String TEXT_34 = NL + "\tjava.sql.Statement stmt_";
  protected final String TEXT_35 = " = conn_";
  protected final String TEXT_36 = ".createStatement();";
  protected final String TEXT_37 = NL + "String query_";
  protected final String TEXT_38 = " = \"\";" + NL + "boolean whetherReject_";
  protected final String TEXT_39 = " = false;";
  protected final String TEXT_40 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();
	
	String dbServer = ElementParameterParser.getValue(node, "__SERVER__");
	String dbhost = ElementParameterParser.getValue(node, "__HOST__");
	String dbname= ElementParameterParser.getValue(node, "__DBNAME__");
	String dbuser= ElementParameterParser.getValue(node, "__USER__");
	String dbpwd= ElementParameterParser.getValue(node, "__PASS__");
    String commitEvery = ElementParameterParser.getValue(node, "__COMMIT_EVERY__");
    String dbquery= ElementParameterParser.getValue(node, "__QUERY__");
	       dbquery = dbquery.replaceAll("\n"," ");
    	   dbquery = dbquery.replaceAll("\r"," ");
    	   
    String dbproperties = ElementParameterParser.getValue(node, "__PROPERTIES__");
    
	boolean usePrepareStatement = "true".equals(ElementParameterParser.getValue(node,"__USE_PREPAREDSTATEMENT__"));
    boolean useExistingConn = ("true").equals(ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__"));
    if(useExistingConn) {
		String connection = ElementParameterParser.getValue(node,"__CONNECTION__");
		String conn = "conn_" + connection;
		
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(conn );
    stringBuffer.append(TEXT_4);
    
	} else {
	    
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(dbproperties);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(dbhost);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(dbhost);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(dbproperties);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_16);
    stringBuffer.append(dbuser);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_18);
    stringBuffer.append(dbpwd);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    
	}

    stringBuffer.append(TEXT_24);
    
if(!useExistingConn) {
    if(!("").equals(commitEvery) && !("0").equals(commitEvery)) {
        
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(commitEvery);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    
    }
}

    
	if (usePrepareStatement ) {

    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(dbquery);
    stringBuffer.append(TEXT_33);
    
	} else {

    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_36);
    
	}

    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(TEXT_40);
    return stringBuffer.toString();
  }
}
