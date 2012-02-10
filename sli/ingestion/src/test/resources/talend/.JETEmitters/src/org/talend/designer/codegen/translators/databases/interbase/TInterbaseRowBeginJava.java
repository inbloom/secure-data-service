package org.talend.designer.codegen.translators.databases.interbase;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TInterbaseRowBeginJava
{
  protected static String nl;
  public static synchronized TInterbaseRowBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TInterbaseRowBeginJava result = new TInterbaseRowBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "        java.sql.Connection conn_";
  protected final String TEXT_3 = " = (java.sql.Connection)globalMap.get(\"";
  protected final String TEXT_4 = "\");";
  protected final String TEXT_5 = NL + "        java.lang.Class.forName(\"";
  protected final String TEXT_6 = "\");        " + NL + "        String url_";
  protected final String TEXT_7 = " = \"jdbc:interbase://\" + ";
  protected final String TEXT_8 = " + \"/\" + ";
  protected final String TEXT_9 = ";        " + NL + "        String userName_";
  protected final String TEXT_10 = " = ";
  protected final String TEXT_11 = ";        " + NL + "        String password_";
  protected final String TEXT_12 = " = ";
  protected final String TEXT_13 = ";        " + NL + "        java.sql.Connection conn_";
  protected final String TEXT_14 = " = java.sql.DriverManager.getConnection(url_";
  protected final String TEXT_15 = ",userName_";
  protected final String TEXT_16 = ",password_";
  protected final String TEXT_17 = ");";
  protected final String TEXT_18 = NL;
  protected final String TEXT_19 = NL + "            conn_";
  protected final String TEXT_20 = ".setAutoCommit(false);            " + NL + "            int commitEvery_";
  protected final String TEXT_21 = " = ";
  protected final String TEXT_22 = ";            " + NL + "            int commitCounter_";
  protected final String TEXT_23 = " = 0;    ";
  protected final String TEXT_24 = NL;
  protected final String TEXT_25 = NL + "\tjava.sql.PreparedStatement pstmt_";
  protected final String TEXT_26 = " = conn_";
  protected final String TEXT_27 = ".prepareStatement(";
  protected final String TEXT_28 = ");\t";
  protected final String TEXT_29 = NL + "\tjava.sql.Statement stmt_";
  protected final String TEXT_30 = " = conn_";
  protected final String TEXT_31 = ".createStatement();";
  protected final String TEXT_32 = NL + "String query_";
  protected final String TEXT_33 = " = \"\";" + NL + "    boolean whetherReject_";
  protected final String TEXT_34 = " = false;   ";
  protected final String TEXT_35 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	
	INode node = (INode)codeGenArgument.getArgument();
	
	String cid = node.getUniqueName();
	
	String host =  ElementParameterParser.getValue(node, "__HOST__");
	
	String dbname= ElementParameterParser.getValue(node, "__DBNAME__");
	
	String username = ElementParameterParser.getValue(node, "__USER__");
	
	String password = ElementParameterParser.getValue(node, "__PASS__");
			
	
    String commitEvery = ElementParameterParser.getValue(node, "__COMMIT_EVERY__");
    String dbquery= ElementParameterParser.getValue(node, "__QUERY__");
	       dbquery = dbquery.replaceAll("\n"," ");
    	   dbquery = dbquery.replaceAll("\r"," ");
    boolean usePrepareStatement = "true".equals(ElementParameterParser.getValue(node,"__USE_PREPAREDSTATEMENT__"));
    String javaDbDriver   = "interbase.interclient.Driver";  
    
    String useExistingConn = ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__");
    
    if(("true").equals(useExistingConn)) {
    	String connection = ElementParameterParser.getValue(node,"__CONNECTION__");
        String conn = "conn_" + connection;
        
    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(conn);
    stringBuffer.append(TEXT_4);
    
    } else {
        
    stringBuffer.append(TEXT_5);
    stringBuffer.append(javaDbDriver );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_7);
    stringBuffer.append(host);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(username);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(password);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    
    }
    
    stringBuffer.append(TEXT_18);
    
    if(!("true").equals(useExistingConn)) {
        if(!("").equals(commitEvery) && !("0").equals(commitEvery)) {
            
    stringBuffer.append(TEXT_19);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(commitEvery);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    
        }
    }
    
    stringBuffer.append(TEXT_24);
    
	if (usePrepareStatement ) {

    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(dbquery);
    stringBuffer.append(TEXT_28);
    
	} else {

    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_31);
    
	}

    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(TEXT_35);
    return stringBuffer.toString();
  }
}
