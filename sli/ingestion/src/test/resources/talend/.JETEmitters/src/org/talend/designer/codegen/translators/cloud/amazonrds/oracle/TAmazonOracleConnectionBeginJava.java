package org.talend.designer.codegen.translators.cloud.amazonrds.oracle;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TAmazonOracleConnectionBeginJava
{
  protected static String nl;
  public static synchronized TAmazonOracleConnectionBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TAmazonOracleConnectionBeginJava result = new TAmazonOracleConnectionBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "\t";
  protected final String TEXT_2 = NL + "\t\t\tjava.lang.Class.forName(\"";
  protected final String TEXT_3 = "\");";
  protected final String TEXT_4 = NL + "\t\t\tString sharedConnectionName_";
  protected final String TEXT_5 = " = ";
  protected final String TEXT_6 = ";" + NL + "\t\t\tconn_";
  protected final String TEXT_7 = " = SharedDBConnection.getDBConnection(\"";
  protected final String TEXT_8 = "\",url_";
  protected final String TEXT_9 = ",userName_";
  protected final String TEXT_10 = " , password_";
  protected final String TEXT_11 = " , sharedConnectionName_";
  protected final String TEXT_12 = ");";
  protected final String TEXT_13 = NL + "\t\tconn_";
  protected final String TEXT_14 = " = java.sql.DriverManager.getConnection(url_";
  protected final String TEXT_15 = ",userName_";
  protected final String TEXT_16 = ",password_";
  protected final String TEXT_17 = ");";
  protected final String TEXT_18 = NL + "\t\t\tconn_";
  protected final String TEXT_19 = ".setAutoCommit(";
  protected final String TEXT_20 = ");";
  protected final String TEXT_21 = NL + "        String url_";
  protected final String TEXT_22 = " = \"jdbc:oracle:thin:@\" + ";
  protected final String TEXT_23 = " + \":\" + ";
  protected final String TEXT_24 = " + \":\" + ";
  protected final String TEXT_25 = ";        ";
  protected final String TEXT_26 = NL + "        String url_";
  protected final String TEXT_27 = " = \"jdbc:oracle:thin:@(description=(address=(protocol=tcp)(host=\" + ";
  protected final String TEXT_28 = " + \")(port=\" + ";
  protected final String TEXT_29 = " + \"))(connect_data=(service_name=\" + ";
  protected final String TEXT_30 = " + \")))\";";
  protected final String TEXT_31 = NL + "        String url_";
  protected final String TEXT_32 = " = \"jdbc:oracle:oci8:@\" + ";
  protected final String TEXT_33 = ";";
  protected final String TEXT_34 = NL + "\t\t\tString url_";
  protected final String TEXT_35 = "=";
  protected final String TEXT_36 = ";" + NL + "\t\t\t";
  protected final String TEXT_37 = NL + "    \tglobalMap.put(\"connectionType_\" + \"";
  protected final String TEXT_38 = "\", \"";
  protected final String TEXT_39 = "\");";
  protected final String TEXT_40 = NL + "\t\tString sharedConnectionName_";
  protected final String TEXT_41 = " = ";
  protected final String TEXT_42 = ";\t\t\t";
  protected final String TEXT_43 = NL + "\t\t\tconn_";
  protected final String TEXT_44 = " = SharedDBConnection.getDBConnection(\"";
  protected final String TEXT_45 = "\",url_";
  protected final String TEXT_46 = ",sharedConnectionName_";
  protected final String TEXT_47 = ");\t" + NL + "\t\t\t";
  protected final String TEXT_48 = NL + "\t\t\t\tconn_";
  protected final String TEXT_49 = " = SharedDBConnection.getDBConnection(\"";
  protected final String TEXT_50 = "\",url_";
  protected final String TEXT_51 = ",userName_";
  protected final String TEXT_52 = " , password_";
  protected final String TEXT_53 = " , sharedConnectionName_";
  protected final String TEXT_54 = ");" + NL;
  protected final String TEXT_55 = NL + "\t\t\tconn_";
  protected final String TEXT_56 = " = java.sql.DriverManager.getConnection(url_";
  protected final String TEXT_57 = ");";
  protected final String TEXT_58 = NL + "\t\t\tconn_";
  protected final String TEXT_59 = " = java.sql.DriverManager.getConnection(url_";
  protected final String TEXT_60 = ",userName_";
  protected final String TEXT_61 = ",password_";
  protected final String TEXT_62 = ");";
  protected final String TEXT_63 = NL + "\t\t\tString atnParams_";
  protected final String TEXT_64 = " = ";
  protected final String TEXT_65 = ";" + NL + "\t\t\tatnParams_";
  protected final String TEXT_66 = " = atnParams_";
  protected final String TEXT_67 = ".replaceAll(\"&\", \"\\n\");" + NL + "\t\t\tjava.util.Properties atnParamsPrope_";
  protected final String TEXT_68 = " = new java.util.Properties();" + NL + "\t\t\tatnParamsPrope_";
  protected final String TEXT_69 = ".put(\"user\",userName_";
  protected final String TEXT_70 = ");" + NL + "\t\t\tatnParamsPrope_";
  protected final String TEXT_71 = ".put(\"password\",password_";
  protected final String TEXT_72 = ");" + NL + "\t\t\tatnParamsPrope_";
  protected final String TEXT_73 = ".load(new java.io.ByteArrayInputStream(atnParams_";
  protected final String TEXT_74 = ".getBytes()));" + NL + "\t\t\tconn_";
  protected final String TEXT_75 = " = java.sql.DriverManager.getConnection(url_";
  protected final String TEXT_76 = ", atnParamsPrope_";
  protected final String TEXT_77 = ");";
  protected final String TEXT_78 = NL + "        globalMap.put(\"localServiceName_\" + \"";
  protected final String TEXT_79 = "\",";
  protected final String TEXT_80 = ");";
  protected final String TEXT_81 = NL + "        globalMap.put(\"host_\" + \"";
  protected final String TEXT_82 = "\",";
  protected final String TEXT_83 = ");" + NL + "        globalMap.put(\"port_\" + \"";
  protected final String TEXT_84 = "\",";
  protected final String TEXT_85 = ");" + NL + "        globalMap.put(\"dbname_\" + \"";
  protected final String TEXT_86 = "\",";
  protected final String TEXT_87 = ");";
  protected final String TEXT_88 = NL + NL + "\t";
  protected final String TEXT_89 = NL + NL + "\tString userName_";
  protected final String TEXT_90 = " = ";
  protected final String TEXT_91 = ";";
  protected final String TEXT_92 = NL + "\t" + NL + "\tString password_";
  protected final String TEXT_93 = " = ";
  protected final String TEXT_94 = ";" + NL + "\t" + NL + "\tjava.sql.Connection conn_";
  protected final String TEXT_95 = "=null;" + NL;
  protected final String TEXT_96 = NL + "\t";
  protected final String TEXT_97 = NL + "\t\t";
  protected final String TEXT_98 = NL + "\t\t" + NL + "\t\t";
  protected final String TEXT_99 = " " + NL + "\t";
  protected final String TEXT_100 = NL + NL + "\tglobalMap.put(\"conn_\" + \"";
  protected final String TEXT_101 = "\",conn_";
  protected final String TEXT_102 = ");" + NL + "\tglobalMap.put(\"dbschema_\" + \"";
  protected final String TEXT_103 = "\", ";
  protected final String TEXT_104 = ");" + NL + "\tglobalMap.put(\"username_\" + \"";
  protected final String TEXT_105 = "\",";
  protected final String TEXT_106 = ");" + NL + "\tglobalMap.put(\"password_\" + \"";
  protected final String TEXT_107 = "\",";
  protected final String TEXT_108 = ");";
  protected final String TEXT_109 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
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

    stringBuffer.append(TEXT_2);
    stringBuffer.append(this.getDirverClassName(node));
    stringBuffer.append(TEXT_3);
    
		}
		
		public void useShareConnection(INode node) {
			String sharedConnectionName = ElementParameterParser.getValue(node, "__SHARED_CONNECTION_NAME__");

    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(sharedConnectionName);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(this.getDirverClassName(node));
    stringBuffer.append(TEXT_8);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_9);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    
		}
		
		public void createConnection(INode node) {

    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    
		}
		
		public void setAutoCommit(INode node) {
			boolean setAutoCommit = "true".equals(ElementParameterParser.getValue(node, "__AUTO_COMMIT__"));

    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    stringBuffer.append(setAutoCommit);
    stringBuffer.append(TEXT_20);
    
		}
		
		public void afterComponentProcess(INode node){
	    }
	}//end DefaultUtil class
	
	DefaultConnectionUtil connUtil = new DefaultConnectionUtil();

    
	class ConnectionUtil extends DefaultConnectionUtil{
	
		public void createURL(INode node) {
			super.createURL(node);

    
    		String connectionType = ElementParameterParser.getValue(node, "__CONNECTION_TYPE__");    
			String jdbcURL = ElementParameterParser.getValue(node, "__JDBC_URL__");    
    		String localServiceName = ElementParameterParser.getValue(node, "__LOCAL_SERVICE_NAME__");
			if(("ORACLE_SID").equals(connectionType)) {

    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(dbhost);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(dbport);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_25);
    
    		} else if(("ORACLE_SERVICE_NAME").equals(connectionType)) {

    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(dbhost);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(dbport);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_30);
    
    		} else if(("ORACLE_OCI").equals(connectionType)) {

    stringBuffer.append(TEXT_31);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(localServiceName);
    stringBuffer.append(TEXT_33);
    
			}else if(("ORACLE_WALLET").equals(connectionType)){
			
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(jdbcURL);
    stringBuffer.append(TEXT_36);
    
			}

    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(connectionType);
    stringBuffer.append(TEXT_39);
    	
		}

		public String getDirverClassName(INode node){
			String dbVersion =  ElementParameterParser.getValue(node, "__DB_VERSION__"); 
			if("ojdbc5-11g.jar".equals(dbVersion) || "ojdbc6-11g.jar".equals(dbVersion) ){
			    return "oracle.jdbc.OracleDriver";	
			}else {
				return "oracle.jdbc.driver.OracleDriver";	
			}
		}	
	
		
		
		public void useShareConnection(INode node) {
		String sharedConnectionName = ElementParameterParser.getValue(node, "__SHARED_CONNECTION_NAME__");
		String connectionType = ElementParameterParser.getValue(node, "__CONNECTION_TYPE__"); 

    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(sharedConnectionName);
    stringBuffer.append(TEXT_42);
    
			if(("ORACLE_WALLET").equals(connectionType)) {

    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(this.getDirverClassName(node));
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    
			} else {

    

    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(this.getDirverClassName(node));
    stringBuffer.append(TEXT_50);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    
			}

    
		}
		
		public void createConnection(INode node) {
		//dbproperties = ElementParameterParser.getValue(node, "__PROPERTIES__");
		String connectionType = ElementParameterParser.getValue(node, "__CONNECTION_TYPE__"); 

    
			if(("ORACLE_WALLET").equals(connectionType)) {

    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_57);
    
			} else if(dbproperties == null || ("\"\"").equals(dbproperties) || ("").equals(dbproperties)) {

    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_62);
    
			} else {

    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(dbproperties);
    stringBuffer.append(TEXT_65);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_68);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_69);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_70);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_71);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_72);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_73);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_74);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_75);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_76);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_77);
    
			}

    
		}
		
		public void afterComponentProcess(INode node){

    
	    	String connectionType = ElementParameterParser.getValue(node, "__CONNECTION_TYPE__");    
    		String localServiceName = ElementParameterParser.getValue(node, "__LOCAL_SERVICE_NAME__");
			if(("ORACLE_OCI").equals(connectionType)) {

    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_79);
    stringBuffer.append(localServiceName);
    stringBuffer.append(TEXT_80);
    
    		} else {

    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_82);
    stringBuffer.append(dbhost);
    stringBuffer.append(TEXT_83);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_84);
    stringBuffer.append(dbport);
    stringBuffer.append(TEXT_85);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_86);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_87);
    
    		}

    
	    }
	}//end class
	connUtil = new ConnectionUtil();

    //----------------------------component codes-----------------------------------------
    stringBuffer.append(TEXT_88);
    
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

    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_90);
    stringBuffer.append((dbuser != null && dbuser.trim().length() == 0)? "null":dbuser);
    stringBuffer.append(TEXT_91);
    //the tSQLiteConnection component not contain user and pass return null
    stringBuffer.append(TEXT_92);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_93);
    stringBuffer.append((dbpass != null && dbpass.trim().length() == 0)? "null":dbpass);
    stringBuffer.append(TEXT_94);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_95);
    
	if(isUseSharedConnection){

    stringBuffer.append(TEXT_96);
    connUtil.useShareConnection(node);
    
	}else {

    stringBuffer.append(TEXT_97);
    connUtil.classForName(node);
    stringBuffer.append(TEXT_98);
    connUtil.createConnection(node);
    
	}

    stringBuffer.append(TEXT_99);
    connUtil.setAutoCommit(node);
    
	connUtil.afterComponentProcess(node);

    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_101);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_102);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_103);
    stringBuffer.append(dbschema);
    stringBuffer.append(TEXT_104);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_105);
    stringBuffer.append(dbuser);
    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_107);
    stringBuffer.append(dbpass);
    stringBuffer.append(TEXT_108);
    stringBuffer.append(TEXT_109);
    return stringBuffer.toString();
  }
}
