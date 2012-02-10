package org.talend.designer.codegen.translators.databases.teradata;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TTeradataConnectionBeginJava
{
  protected static String nl;
  public static synchronized TTeradataConnectionBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TTeradataConnectionBeginJava result = new TTeradataConnectionBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t\t\tjava.lang.Class.forName(\"";
  protected final String TEXT_2 = "\");";
  protected final String TEXT_3 = NL + "\t\t\tString sharedConnectionName_";
  protected final String TEXT_4 = " = ";
  protected final String TEXT_5 = ";" + NL + "\t\t\tconn_";
  protected final String TEXT_6 = " = SharedDBConnection.getDBConnection(\"";
  protected final String TEXT_7 = "\",url_";
  protected final String TEXT_8 = ",userName_";
  protected final String TEXT_9 = " , password_";
  protected final String TEXT_10 = " , sharedConnectionName_";
  protected final String TEXT_11 = ");";
  protected final String TEXT_12 = NL + "\t\tconn_";
  protected final String TEXT_13 = " = java.sql.DriverManager.getConnection(url_";
  protected final String TEXT_14 = ",userName_";
  protected final String TEXT_15 = ",password_";
  protected final String TEXT_16 = ");";
  protected final String TEXT_17 = NL + "\t\t\tconn_";
  protected final String TEXT_18 = ".setAutoCommit(";
  protected final String TEXT_19 = ");";
  protected final String TEXT_20 = NL + "\t\tString dbProperties_";
  protected final String TEXT_21 = " = ";
  protected final String TEXT_22 = ";" + NL + "\t\tString url_";
  protected final String TEXT_23 = " = null;" + NL + "\t\tif(dbProperties_";
  protected final String TEXT_24 = " == null || dbProperties_";
  protected final String TEXT_25 = ".trim().length() == 0) {" + NL + "\t\t\turl_";
  protected final String TEXT_26 = " = \"jdbc:teradata://\" + ";
  protected final String TEXT_27 = " + \"/DATABASE=\" + ";
  protected final String TEXT_28 = ";     " + NL + "\t\t} else {" + NL + "\t\t    url_";
  protected final String TEXT_29 = " = \"jdbc:teradata://\" + ";
  protected final String TEXT_30 = " + \"/DATABASE=\" + ";
  protected final String TEXT_31 = " + \",\" + ";
  protected final String TEXT_32 = ";" + NL + "\t\t}       ";
  protected final String TEXT_33 = NL + NL + "\t";
  protected final String TEXT_34 = NL + NL + "\tString userName_";
  protected final String TEXT_35 = " = ";
  protected final String TEXT_36 = ";";
  protected final String TEXT_37 = NL + "\t" + NL + "\tString password_";
  protected final String TEXT_38 = " = ";
  protected final String TEXT_39 = ";" + NL + "\t" + NL + "\tjava.sql.Connection conn_";
  protected final String TEXT_40 = "=null;" + NL;
  protected final String TEXT_41 = NL + "\t";
  protected final String TEXT_42 = NL + "\t\t";
  protected final String TEXT_43 = NL + "\t\t" + NL + "\t\t";
  protected final String TEXT_44 = " " + NL + "\t";
  protected final String TEXT_45 = NL + NL + "\tglobalMap.put(\"conn_\" + \"";
  protected final String TEXT_46 = "\",conn_";
  protected final String TEXT_47 = ");" + NL + "\tglobalMap.put(\"host_";
  protected final String TEXT_48 = "\", ";
  protected final String TEXT_49 = ");" + NL + "\tglobalMap.put(\"dbname_";
  protected final String TEXT_50 = "\", ";
  protected final String TEXT_51 = ");" + NL + "\tglobalMap.put(\"username_";
  protected final String TEXT_52 = "\", userName_";
  protected final String TEXT_53 = ");" + NL + "\tglobalMap.put(\"password_";
  protected final String TEXT_54 = "\", password_";
  protected final String TEXT_55 = ");";
  protected final String TEXT_56 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    
	class DefaultConnectionUtil {
	
		protected String cid ;
		protected String dbproperties ;
		protected String dbhost;
	    protected String dbport;
	    protected String dbname;
	    
	    public void beforeComponentProcess(INode node){
	    }
	    
		public void createURL(INode node) {
			cid = node.getUniqueName();
			dbproperties = ElementParameterParser.getValue(node, "__PROPERTIES__");
			dbhost = ElementParameterParser.getValue(node, "__HOST__");
	    	dbport = ElementParameterParser.getValue(node, "__PORT__");
	    	dbname = ElementParameterParser.getValue(node, "__DBNAME__");
		}
		
		public String getDirverClassName(INode node){
			return "";
		}
		
		public void classForName(INode node){

    stringBuffer.append(TEXT_1);
    stringBuffer.append(this.getDirverClassName(node));
    stringBuffer.append(TEXT_2);
    
		}
		
		public void useShareConnection(INode node) {
			String sharedConnectionName = ElementParameterParser.getValue(node, "__SHARED_CONNECTION_NAME__");

    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_4);
    stringBuffer.append(sharedConnectionName);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(this.getDirverClassName(node));
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    
		}
		
		public void createConnection(INode node) {

    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    
		}
		
		public void setAutoCommit(INode node) {
			boolean setAutoCommit = "true".equals(ElementParameterParser.getValue(node, "__AUTO_COMMIT__"));

    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(setAutoCommit);
    stringBuffer.append(TEXT_19);
    
		}
		
		public void afterComponentProcess(INode node){
	    }
	}//end DefaultUtil class
	
	DefaultConnectionUtil connUtil = new DefaultConnectionUtil();

    
	class ConnectionUtil extends DefaultConnectionUtil{
	
		public void createURL(INode node) {
			super.createURL(node);

    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(dbproperties);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(dbhost);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(dbhost);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(dbproperties);
    stringBuffer.append(TEXT_32);
    	
		}
	
		public String getDirverClassName(INode node){
			return "com.teradata.jdbc.TeraDriver";
		}
		
	}//end class
	connUtil = new ConnectionUtil();

    //----------------------------component codes-----------------------------------------
    stringBuffer.append(TEXT_33);
    
    CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
    INode node = (INode)codeGenArgument.getArgument();
	
    String cid = node.getUniqueName();
    String dbhost = ElementParameterParser.getValue(node, "__HOST__");
    String dbport = ElementParameterParser.getValue(node, "__PORT__");
    String dbschema = ElementParameterParser.getValue(node, "__DB_SCHEMA__");
    if(dbschema == null||dbschema.trim().length()==0) {
    	 dbschema = ElementParameterParser.getValue(node, "__SCHEMA_DB__");
    }
    String dbname = ElementParameterParser.getValue(node, "__DBNAME__");
    String dbuser = ElementParameterParser.getValue(node, "__USER__");
    String dbpass = ElementParameterParser.getValue(node, "__PASS__");
    String encoding = ElementParameterParser.getValue(node, "__ENCODING__");
    
	boolean isUseSharedConnection = ("true").equals(ElementParameterParser.getValue(node, "__USE_SHARED_CONNECTION__"));

    
	connUtil.beforeComponentProcess(node);
	
	connUtil.createURL(node);

    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append((dbuser != null && dbuser.trim().length() == 0)? "null":dbuser);
    stringBuffer.append(TEXT_36);
    //the tSQLiteConnection component not contain user and pass return null
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append((dbpass != null && dbpass.trim().length() == 0)? "null":dbpass);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    
	if(isUseSharedConnection){

    stringBuffer.append(TEXT_41);
    connUtil.useShareConnection(node);
    
	}else {

    stringBuffer.append(TEXT_42);
    connUtil.classForName(node);
    stringBuffer.append(TEXT_43);
    connUtil.createConnection(node);
    
	}

    stringBuffer.append(TEXT_44);
    connUtil.setAutoCommit(node);
    
	connUtil.afterComponentProcess(node);

    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_48);
    stringBuffer.append(dbhost);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(TEXT_56);
    return stringBuffer.toString();
  }
}
