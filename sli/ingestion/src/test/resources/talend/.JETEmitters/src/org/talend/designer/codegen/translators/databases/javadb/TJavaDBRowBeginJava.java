package org.talend.designer.codegen.translators.databases.javadb;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TJavaDBRowBeginJava
{
  protected static String nl;
  public static synchronized TJavaDBRowBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TJavaDBRowBeginJava result = new TJavaDBRowBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "String jdbcDriver_";
  protected final String TEXT_3 = " = null;" + NL + "String url_";
  protected final String TEXT_4 = " = null;";
  protected final String TEXT_5 = NL + "\tjdbcDriver_";
  protected final String TEXT_6 = " = \"org.apache.derby.jdbc.EmbeddedDriver\";" + NL + "\turl_";
  protected final String TEXT_7 = " = \"jdbc:derby:\" + ";
  protected final String TEXT_8 = ";" + NL + "\t//set the root path of the database" + NL + "\tSystem.setProperty(\"derby.system.home\",";
  protected final String TEXT_9 = ");" + NL + "\t";
  protected final String TEXT_10 = NL + "\t\tjdbcDriver_";
  protected final String TEXT_11 = " = \"com.ibm.db2.jcc.DB2Driver\";" + NL + "\t\turl_";
  protected final String TEXT_12 = " = \"jdbc:derby:net://\" + ";
  protected final String TEXT_13 = " + \":\" + ";
  protected final String TEXT_14 = " + \"/\" + ";
  protected final String TEXT_15 = ";" + NL + "\t\t";
  protected final String TEXT_16 = NL + "\t\tjdbcDriver_";
  protected final String TEXT_17 = " = \"org.apache.derby.jdbc.ClientDriver\";" + NL + "\t\turl_";
  protected final String TEXT_18 = " = \"jdbc:derby://\" + ";
  protected final String TEXT_19 = " + \":\" + ";
  protected final String TEXT_20 = " + \"/\" + ";
  protected final String TEXT_21 = ";" + NL + "\t\t";
  protected final String TEXT_22 = NL + "\t";
  protected final String TEXT_23 = NL + "    \torg.apache.derby.drda.NetworkServerControl serverControl_";
  protected final String TEXT_24 = ";" + NL + "    \tserverControl_";
  protected final String TEXT_25 = " = new org.apache.derby.drda.NetworkServerControl(java.net.InetAddress.getByName(";
  protected final String TEXT_26 = "),Integer.parseInt(";
  protected final String TEXT_27 = "));" + NL + "    \t//start server" + NL + "    \tserverControl_";
  protected final String TEXT_28 = ".start(new java.io.PrintWriter(System.out,true));" + NL + "    \tboolean isServerUp_";
  protected final String TEXT_29 = " = false;" + NL + "    \tint timeOut_";
  protected final String TEXT_30 = " = 5;" + NL + "    \twhile(!isServerUp_";
  protected final String TEXT_31 = " && timeOut_";
  protected final String TEXT_32 = " > 0)" + NL + "    \t{" + NL + "    \t\ttry" + NL + "    \t\t{" + NL + "    \t\t\ttimeOut_";
  protected final String TEXT_33 = "--;" + NL + "    \t\t\t/*" + NL + "    \t\t\t* testing for connection to see if the network server is up and running." + NL + "    \t\t\t* if server is not ready yet, this method will throw an exception.\t\t" + NL + "    \t\t\t*/" + NL + "    \t\t\tserverControl_";
  protected final String TEXT_34 = ".ping();" + NL + "    \t\t\tisServerUp_";
  protected final String TEXT_35 = " = true;" + NL + "    \t\t}" + NL + "    \t\tcatch(Exception e) {" + NL + "    \t\t\t//Unable to obtain a connection to network server, trying again after 3000 ms." + NL + "    \t\t\tThread.currentThread().sleep(3000);" + NL + "    \t\t}    \t\t\t\t" + NL + "    \t}" + NL + "    \tif(!isServerUp_";
  protected final String TEXT_36 = ")" + NL + "    \t{" + NL + "    \t\t/*" + NL + "    \t\t * can not obtain a connection to network server." + NL + "    \t\t */ \t " + NL + "    \t\tSystem.exit(1);\t" + NL + "    \t}" + NL + "    \t//derby network server started." + NL + "\t";
  protected final String TEXT_37 = NL + "Class.forName(jdbcDriver_";
  protected final String TEXT_38 = ").newInstance();" + NL + "" + NL + "java.util.Properties properties_";
  protected final String TEXT_39 = " = new java.util.Properties();" + NL + "" + NL + "properties_";
  protected final String TEXT_40 = ".put(\"user\",";
  protected final String TEXT_41 = ");" + NL + "" + NL + "properties_";
  protected final String TEXT_42 = ".put(\"password\",";
  protected final String TEXT_43 = ");" + NL + "" + NL + "java.sql.Connection conn_";
  protected final String TEXT_44 = " = java.sql.DriverManager.getConnection(url_";
  protected final String TEXT_45 = ",properties_";
  protected final String TEXT_46 = ");" + NL + "" + NL + "int nb_line_";
  protected final String TEXT_47 = " = 0;" + NL + "" + NL + "boolean whetherReject_";
  protected final String TEXT_48 = " = false;" + NL;
  protected final String TEXT_49 = NL + NL + "int commitEvery_";
  protected final String TEXT_50 = " = ";
  protected final String TEXT_51 = ";" + NL + "" + NL + "int commitCounter_";
  protected final String TEXT_52 = " = 0;" + NL + "" + NL + "conn_";
  protected final String TEXT_53 = ".setAutoCommit(false);";
  protected final String TEXT_54 = NL;
  protected final String TEXT_55 = NL + "\tjava.sql.PreparedStatement pstmt_";
  protected final String TEXT_56 = " = conn_";
  protected final String TEXT_57 = ".prepareStatement(";
  protected final String TEXT_58 = ");\t";
  protected final String TEXT_59 = NL + "\tjava.sql.Statement stmt_";
  protected final String TEXT_60 = " = conn_";
  protected final String TEXT_61 = ".createStatement();";
  protected final String TEXT_62 = NL + "String query_";
  protected final String TEXT_63 = " = \"\";";
  protected final String TEXT_64 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	
	INode node = (INode)codeGenArgument.getArgument();
	
	String cid = node.getUniqueName();
	
	String frameworkType = ElementParameterParser.getValue(node,"__FRAMEWORK_TYPE__");
		
	String dbhost = ElementParameterParser.getValue(node, "__HOST__");
	
	String dbport = ElementParameterParser.getValue(node, "__PORT__");
	
	String dbname= ElementParameterParser.getValue(node, "__DBNAME__");
		
	String dbRootPath = ElementParameterParser.getValue(node, "__DBPATH__");	
	
	String dbuser= ElementParameterParser.getValue(node, "__USER__");
	
	String dbpwd= ElementParameterParser.getValue(node, "__PASS__");
	
    String commitEvery = ElementParameterParser.getValue(node, "__COMMIT_EVERY__");

    String dbquery= ElementParameterParser.getValue(node, "__QUERY__");
	       dbquery = dbquery.replaceAll("\n"," ");
    	   dbquery = dbquery.replaceAll("\r"," ");
    
	boolean usePrepareStatement = "true".equals(ElementParameterParser.getValue(node,"__USE_PREPAREDSTATEMENT__"));

    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    
if(("EMBEDED").equals(frameworkType))
{
	
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(dbRootPath);
    stringBuffer.append(TEXT_9);
    
}
else
{
	if(("JCCJDBC").equals(frameworkType))
	{
		
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(dbhost);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(dbport);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_15);
    
	}
	else
	{
		
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(dbhost);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(dbport);
    stringBuffer.append(TEXT_20);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_21);
    
	}
	
    stringBuffer.append(TEXT_22);
    
	String connectionFlag = ElementParameterParser.getValue(node, "__CONNECTION_FLAG__");
	if(("false").equals(connectionFlag))
	{
	
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(dbhost);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(dbport);
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
    
	}
}

    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(dbuser);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(dbpwd);
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
    
if(!("").equals(commitEvery)&&!("0").equals(commitEvery)){

    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(commitEvery);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_53);
    
}

    stringBuffer.append(TEXT_54);
    
	if (usePrepareStatement ) {

    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_57);
    stringBuffer.append(dbquery);
    stringBuffer.append(TEXT_58);
    
	} else {

    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_61);
    
	}

    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_63);
    stringBuffer.append(TEXT_64);
    return stringBuffer.toString();
  }
}
