package org.talend.designer.codegen.translators.databases.oracle;

import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TOracleConnectionBeginJava
{
  protected static String nl;
  public static synchronized TOracleConnectionBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TOracleConnectionBeginJava result = new TOracleConnectionBeginJava();
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
  protected final String TEXT_22 = " = ";
  protected final String TEXT_23 = ";";
  protected final String TEXT_24 = NL + "        String url_";
  protected final String TEXT_25 = " = \"jdbc:oracle:thin:@\" + ";
  protected final String TEXT_26 = " + \":\" + ";
  protected final String TEXT_27 = " + \":\" + ";
  protected final String TEXT_28 = ";        ";
  protected final String TEXT_29 = NL + "        String url_";
  protected final String TEXT_30 = " = \"jdbc:oracle:thin:@(description=(address=(protocol=tcp)(host=\" + ";
  protected final String TEXT_31 = " + \")(port=\" + ";
  protected final String TEXT_32 = " + \"))(connect_data=(service_name=\" + ";
  protected final String TEXT_33 = " + \")))\";";
  protected final String TEXT_34 = NL + "        String url_";
  protected final String TEXT_35 = " = \"jdbc:oracle:oci8:@\" + ";
  protected final String TEXT_36 = ";";
  protected final String TEXT_37 = NL + "\t\t\tString url_";
  protected final String TEXT_38 = "=";
  protected final String TEXT_39 = ";" + NL + "\t\t\t";
  protected final String TEXT_40 = NL + "    \tglobalMap.put(\"connectionType_\" + \"";
  protected final String TEXT_41 = "\", \"";
  protected final String TEXT_42 = "\");";
  protected final String TEXT_43 = NL + "\t\tString sharedConnectionName_";
  protected final String TEXT_44 = " = ";
  protected final String TEXT_45 = ";\t\t\t";
  protected final String TEXT_46 = NL + "\t\t\tconn_";
  protected final String TEXT_47 = " = SharedDBConnection.getDBConnection(\"";
  protected final String TEXT_48 = "\",url_";
  protected final String TEXT_49 = ",sharedConnectionName_";
  protected final String TEXT_50 = ");\t" + NL + "\t\t\t";
  protected final String TEXT_51 = NL + "\t\t\t\tconn_";
  protected final String TEXT_52 = " = SharedDBConnection.getDBConnection(\"";
  protected final String TEXT_53 = "\",url_";
  protected final String TEXT_54 = ",userName_";
  protected final String TEXT_55 = " , password_";
  protected final String TEXT_56 = " , sharedConnectionName_";
  protected final String TEXT_57 = ");" + NL;
  protected final String TEXT_58 = NL + "\t\t\tconn_";
  protected final String TEXT_59 = " = java.sql.DriverManager.getConnection(url_";
  protected final String TEXT_60 = ");";
  protected final String TEXT_61 = NL + "\t\t\tconn_";
  protected final String TEXT_62 = " = java.sql.DriverManager.getConnection(url_";
  protected final String TEXT_63 = ",userName_";
  protected final String TEXT_64 = ",password_";
  protected final String TEXT_65 = ");";
  protected final String TEXT_66 = NL + "\t\t\tString atnParams_";
  protected final String TEXT_67 = " = ";
  protected final String TEXT_68 = ";" + NL + "\t\t\tatnParams_";
  protected final String TEXT_69 = " = atnParams_";
  protected final String TEXT_70 = ".replaceAll(\"&\", \"\\n\");" + NL + "\t\t\tjava.util.Properties atnParamsPrope_";
  protected final String TEXT_71 = " = new java.util.Properties();" + NL + "\t\t\tatnParamsPrope_";
  protected final String TEXT_72 = ".put(\"user\",userName_";
  protected final String TEXT_73 = ");" + NL + "\t\t\tatnParamsPrope_";
  protected final String TEXT_74 = ".put(\"password\",password_";
  protected final String TEXT_75 = ");" + NL + "\t\t\tatnParamsPrope_";
  protected final String TEXT_76 = ".load(new java.io.ByteArrayInputStream(atnParams_";
  protected final String TEXT_77 = ".getBytes()));" + NL + "\t\t\tconn_";
  protected final String TEXT_78 = " = java.sql.DriverManager.getConnection(url_";
  protected final String TEXT_79 = ", atnParamsPrope_";
  protected final String TEXT_80 = ");";
  protected final String TEXT_81 = NL + "        globalMap.put(\"localServiceName_\" + \"";
  protected final String TEXT_82 = "\",";
  protected final String TEXT_83 = ");";
  protected final String TEXT_84 = NL + "        globalMap.put(\"rac_url_\" + \"";
  protected final String TEXT_85 = "\", ";
  protected final String TEXT_86 = ");";
  protected final String TEXT_87 = NL + "        globalMap.put(\"host_\" + \"";
  protected final String TEXT_88 = "\",";
  protected final String TEXT_89 = ");" + NL + "        globalMap.put(\"port_\" + \"";
  protected final String TEXT_90 = "\",";
  protected final String TEXT_91 = ");" + NL + "        globalMap.put(\"dbname_\" + \"";
  protected final String TEXT_92 = "\",";
  protected final String TEXT_93 = ");";
  protected final String TEXT_94 = NL + NL + "\t";
  protected final String TEXT_95 = NL + NL + "\tString userName_";
  protected final String TEXT_96 = " = ";
  protected final String TEXT_97 = ";";
  protected final String TEXT_98 = NL + "\t" + NL + "\tString password_";
  protected final String TEXT_99 = " = ";
  protected final String TEXT_100 = ";" + NL + "\t" + NL + "\tjava.sql.Connection conn_";
  protected final String TEXT_101 = "=null;" + NL;
  protected final String TEXT_102 = NL + "\t";
  protected final String TEXT_103 = NL + "\t\t";
  protected final String TEXT_104 = NL + "\t\t" + NL + "\t\t";
  protected final String TEXT_105 = " " + NL + "\t";
  protected final String TEXT_106 = NL + NL + "\tglobalMap.put(\"conn_\" + \"";
  protected final String TEXT_107 = "\",conn_";
  protected final String TEXT_108 = ");" + NL + "\tglobalMap.put(\"dbschema_\" + \"";
  protected final String TEXT_109 = "\", ";
  protected final String TEXT_110 = ");" + NL + "\tglobalMap.put(\"username_\" + \"";
  protected final String TEXT_111 = "\",";
  protected final String TEXT_112 = ");" + NL + "\tglobalMap.put(\"password_\" + \"";
  protected final String TEXT_113 = "\",";
  protected final String TEXT_114 = ");";
  protected final String TEXT_115 = NL;

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
    		String rac_url = ElementParameterParser.getValue(node, "__RAC_URL__");
			if("ORACLE_RAC".equals(connectionType)) {

    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(rac_url);
    stringBuffer.append(TEXT_23);
    
    		} else if(("ORACLE_SID").equals(connectionType)) {

    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_25);
    stringBuffer.append(dbhost);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(dbport);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_28);
    
    		} else if(("ORACLE_SERVICE_NAME").equals(connectionType)) {

    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(dbhost);
    stringBuffer.append(TEXT_31);
    stringBuffer.append(dbport);
    stringBuffer.append(TEXT_32);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_33);
    
    		} else if(("ORACLE_OCI").equals(connectionType)) {

    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(localServiceName);
    stringBuffer.append(TEXT_36);
    
			}else if(("ORACLE_WALLET").equals(connectionType)){
			
    stringBuffer.append(TEXT_37);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(jdbcURL);
    stringBuffer.append(TEXT_39);
    
			}

    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(connectionType);
    stringBuffer.append(TEXT_42);
    	
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

    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(sharedConnectionName);
    stringBuffer.append(TEXT_45);
    
			if(("ORACLE_WALLET").equals(connectionType)) {

    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_47);
    stringBuffer.append(this.getDirverClassName(node));
    stringBuffer.append(TEXT_48);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    
			} else {

    

    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_52);
    stringBuffer.append(this.getDirverClassName(node));
    stringBuffer.append(TEXT_53);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_54);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_55);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_56);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_57);
    
			}

    
		}
		
		public void createConnection(INode node) {
		//dbproperties = ElementParameterParser.getValue(node, "__PROPERTIES__");
		String connectionType = ElementParameterParser.getValue(node, "__CONNECTION_TYPE__");
			if(("ORACLE_WALLET").equals(connectionType)) {

    stringBuffer.append(TEXT_58);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_59);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_60);
    
			} else if(dbproperties == null || ("\"\"").equals(dbproperties) || ("").equals(dbproperties)) {

    stringBuffer.append(TEXT_61);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_62);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_63);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_64);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_65);
    
			} else {

    stringBuffer.append(TEXT_66);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_67);
    stringBuffer.append(dbproperties);
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
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_78);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_79);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_80);
    
			}

    
		}
		
		public void afterComponentProcess(INode node){

    
	    	String connectionType = ElementParameterParser.getValue(node, "__CONNECTION_TYPE__");    
    		String localServiceName = ElementParameterParser.getValue(node, "__LOCAL_SERVICE_NAME__");
			if(("ORACLE_OCI").equals(connectionType)) {

    stringBuffer.append(TEXT_81);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_82);
    stringBuffer.append(localServiceName);
    stringBuffer.append(TEXT_83);
    
    		} else if("ORACLE_RAC".equals(connectionType)){
	    		String rac_url = ElementParameterParser.getValue(node, "__RAC_URL__");

    stringBuffer.append(TEXT_84);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_85);
    stringBuffer.append(rac_url);
    stringBuffer.append(TEXT_86);
        		}else {

    stringBuffer.append(TEXT_87);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_88);
    stringBuffer.append(dbhost);
    stringBuffer.append(TEXT_89);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_90);
    stringBuffer.append(dbport);
    stringBuffer.append(TEXT_91);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_92);
    stringBuffer.append(dbname);
    stringBuffer.append(TEXT_93);
    
    		}

    
	    }
	}//end class
	connUtil = new ConnectionUtil();

    //----------------------------component codes-----------------------------------------
    stringBuffer.append(TEXT_94);
    
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

    stringBuffer.append(TEXT_95);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_96);
    stringBuffer.append((dbuser != null && dbuser.trim().length() == 0)? "null":dbuser);
    stringBuffer.append(TEXT_97);
    //the tSQLiteConnection component not contain user and pass return null
    stringBuffer.append(TEXT_98);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_99);
    stringBuffer.append((dbpass != null && dbpass.trim().length() == 0)? "null":dbpass);
    stringBuffer.append(TEXT_100);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_101);
    
	if(isUseSharedConnection){

    stringBuffer.append(TEXT_102);
    connUtil.useShareConnection(node);
    
	}else {

    stringBuffer.append(TEXT_103);
    connUtil.classForName(node);
    stringBuffer.append(TEXT_104);
    connUtil.createConnection(node);
    
	}

    stringBuffer.append(TEXT_105);
    connUtil.setAutoCommit(node);
    
	connUtil.afterComponentProcess(node);

    stringBuffer.append(TEXT_106);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_107);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_108);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_109);
    stringBuffer.append(dbschema);
    stringBuffer.append(TEXT_110);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_111);
    stringBuffer.append(dbuser);
    stringBuffer.append(TEXT_112);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_113);
    stringBuffer.append(dbpass);
    stringBuffer.append(TEXT_114);
    stringBuffer.append(TEXT_115);
    return stringBuffer.toString();
  }
}
