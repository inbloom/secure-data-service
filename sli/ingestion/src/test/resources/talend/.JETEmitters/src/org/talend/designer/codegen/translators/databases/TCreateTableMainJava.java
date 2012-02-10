package org.talend.designer.codegen.translators.databases;

import org.talend.designer.codegen.config.CodeGeneratorArgument;
import org.talend.core.model.process.INode;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.database.EDatabaseTypeName;
import org.talend.core.model.metadata.MetadataTalendType;
import org.talend.core.model.metadata.types.TypesManager;
import org.talend.core.model.metadata.Dbms;
import org.talend.core.model.metadata.MetadataTalendType;
import org.talend.core.model.metadata.MappingTypeRetriever;
import java.util.List;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;

public class TCreateTableMainJava
{
  protected static String nl;
  public static synchronized TCreateTableMainJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TCreateTableMainJava result = new TCreateTableMainJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + "    try{";
  protected final String TEXT_3 = NL + "        ";
  protected final String TEXT_4 = NL + "            java.sql.Statement stmt_";
  protected final String TEXT_5 = " = conn_";
  protected final String TEXT_6 = ".createStatement();" + NL + "            stmt_";
  protected final String TEXT_7 = ".execute(\"";
  protected final String TEXT_8 = "\");";
  protected final String TEXT_9 = NL + "\t\t\t\t";
  protected final String TEXT_10 = NL + "\t            java.sql.DatabaseMetaData dbMetaData_";
  protected final String TEXT_11 = " = conn_";
  protected final String TEXT_12 = ".getMetaData();" + NL + "\t            java.sql.ResultSet rsTable_";
  protected final String TEXT_13 = " = dbMetaData_";
  protected final String TEXT_14 = ".getTables(null, dbschemaForSearch_";
  protected final String TEXT_15 = ", tableNameForSearch_";
  protected final String TEXT_16 = ", new String[]{\"TABLE\"});" + NL + "\t            boolean whetherExist_";
  protected final String TEXT_17 = " = false;" + NL + "\t            if(rsTable_";
  protected final String TEXT_18 = ".next()) {" + NL + "\t            \twhetherExist_";
  protected final String TEXT_19 = " = true;" + NL + "\t            }     ";
  protected final String TEXT_20 = NL + "\t            java.sql.DatabaseMetaData dbMetaData_";
  protected final String TEXT_21 = " = conn_";
  protected final String TEXT_22 = ".getMetaData();" + NL + "\t            java.sql.ResultSet rsTable_";
  protected final String TEXT_23 = " = dbMetaData_";
  protected final String TEXT_24 = ".getTables(null, null, null, new String[]{\"TABLE\"});" + NL + "\t            boolean whetherExist_";
  protected final String TEXT_25 = " = false;" + NL + "\t            while(rsTable_";
  protected final String TEXT_26 = ".next()) {" + NL + "\t                String table_";
  protected final String TEXT_27 = " = rsTable_";
  protected final String TEXT_28 = ".getString(\"TABLE_NAME\");" + NL + "\t                if(table_";
  protected final String TEXT_29 = ".equalsIgnoreCase(";
  protected final String TEXT_30 = ")) {" + NL + "\t                    whetherExist_";
  protected final String TEXT_31 = " = true;" + NL + "\t                    break;" + NL + "\t                }" + NL + "\t            }     ";
  protected final String TEXT_32 = NL + "                if(!whetherExist_";
  protected final String TEXT_33 = ") {" + NL + "                    java.sql.Statement stmt_";
  protected final String TEXT_34 = " = conn_";
  protected final String TEXT_35 = ".createStatement();" + NL + "                    stmt_";
  protected final String TEXT_36 = ".execute(\"";
  protected final String TEXT_37 = "\");                " + NL + "                }";
  protected final String TEXT_38 = NL + "                if(whetherExist_";
  protected final String TEXT_39 = ") {" + NL + "                    java.sql.Statement stmtDrop_";
  protected final String TEXT_40 = " = conn_";
  protected final String TEXT_41 = ".createStatement();" + NL + "                    stmtDrop_";
  protected final String TEXT_42 = ".execute(\"";
  protected final String TEXT_43 = "\");" + NL + "                }" + NL + "                java.sql.Statement stmt_";
  protected final String TEXT_44 = " = conn_";
  protected final String TEXT_45 = ".createStatement();" + NL + "                stmt_";
  protected final String TEXT_46 = ".execute(\"";
  protected final String TEXT_47 = "\");            ";
  protected final String TEXT_48 = NL + "        ";
  protected final String TEXT_49 = NL + "        globalMap.put(\"";
  protected final String TEXT_50 = "_QUERY\", \"";
  protected final String TEXT_51 = "\");        " + NL + "    } catch(Exception e) {" + NL + "        globalMap.put(\"";
  protected final String TEXT_52 = "_ERROR_MESSAGE\",e.getMessage());" + NL + "        throw new RuntimeException(\"Creating table failed\", e);" + NL + "    }";
  protected final String TEXT_53 = NL;
  protected final String TEXT_54 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    	
abstract class  Manager {
    protected String dbName;
    protected String tableName;
    protected String userName;
    protected String password;
    protected String cid;
    protected INode node;
    protected boolean temporary;
    protected abstract String getDriver();
    public abstract String getConnectionURL();
    protected abstract String getDBMSId();
    public Manager(String dbName, String userName, String password) {
        this.dbName = dbName;
        this.userName = userName;
        this.password = password;        
    }
    public Manager(String dbName, String userName, String password, String cid) {
        this(dbName, userName, password);
        this.cid = cid;
    }
    public Manager(String dbName, String tableName, String userName, String password, String cid) {
        this(dbName, userName, password, cid);
        this.tableName = tableName;
    }
    public String getConnection() {
        StringBuilder javaCode = new StringBuilder();
        javaCode.append(retrieveTable());
        javaCode.append("java.lang.Class.forName(\"" + getDriver()+ "\");\r\n");
        javaCode.append("java.sql.Connection conn_" + cid + "= java.sql.DriverManager.getConnection(" + getConnectionURL() + "," + userName + "," + password + ");\r\n");
        return javaCode.toString();
    }
    public String closeConnection() {
        StringBuilder javaCode = new StringBuilder();
        javaCode.append("if(conn_" + cid + " != null && !conn_" + cid + ".isClosed()) {\r\n");
        javaCode.append("conn_" + cid + ".close();\r\n");
        javaCode.append("}\r\n");
        return javaCode.toString();
    }
    protected String retrieveTable() {
        StringBuilder javaCode = new StringBuilder();
        javaCode.append("String tableName_" + cid + " = " + tableName + ";\r\n");
        return javaCode.toString();
    }
    public String getDropTableSQL() {
        StringBuilder dropTableSQL = new StringBuilder();
        dropTableSQL.append("DROP TABLE " + getLProtectedChar() + "\" + tableName_" + cid + " + \"" + getRProtectedChar());
        return dropTableSQL.toString();
    }
    public String getCreateTableSQL(List<IMetadataColumn> listColumn) {
        MappingTypeRetriever mappingType = MetadataTalendType.getMappingTypeRetriever(getDBMSId());
        StringBuilder createTableSQL = new StringBuilder();
        String temp = "";
        if(temporary) {
            temp = "TEMPORARY "; 
        }
        createTableSQL.append("CREATE " + temp + "TABLE " + getLProtectedChar( "\" + tableName_" + cid + " + \"" ) + "\" + tableName_" + cid + " + \"" + getRProtectedChar("\" + tableName_" + cid + " + \"") + "(");
        List<String> pkList = new ArrayList<String>();
        int count = 0;
        String ending = ",";
        for(IMetadataColumn metadataColumn : listColumn) {
            if(metadataColumn.isKey()) {
                pkList.add(getLProtectedChar(metadataColumn.getOriginalDbColumnName()) + metadataColumn.getOriginalDbColumnName() + getRProtectedChar(metadataColumn.getOriginalDbColumnName()));
            }
            createTableSQL.append(getLProtectedChar(metadataColumn.getOriginalDbColumnName()) + metadataColumn.getOriginalDbColumnName() + getRProtectedChar(metadataColumn.getOriginalDbColumnName()) + " ");
            String dataType = null;
            if(metadataColumn.getType() == null || metadataColumn.getType().trim().length() == 0) {
                dataType = mappingType.getDefaultSelectedDbType(metadataColumn.getTalendType());
            } else {
                dataType = metadataColumn.getType();
            }
            if ("mysql_id".equalsIgnoreCase(getDBMSId()) && dataType.endsWith("UNSIGNED") ) {
            	createTableSQL.append(dataType.substring(0,dataType.indexOf("UNSIGNED"))) ; 
            }else {
            	createTableSQL.append(dataType);
            }            
            Integer length = metadataColumn.getLength() == null ? 0 : metadataColumn.getLength();
            Integer precision = metadataColumn.getPrecision() == null ? 0 : metadataColumn.getPrecision();
            boolean lengthIgnored = mappingType.isLengthIgnored(getDBMSId(), dataType);
            boolean precisionIgnored = mappingType.isPrecisionIgnored(getDBMSId(), dataType);
            String prefix = "";
            String suffix = "";
            String comma = "";
            
            
            if(mappingType.isPreBeforeLength(getDBMSId(),dataType)) {
                if(!precisionIgnored) {
                    prefix = "(";
                    suffix = ") ";
                    createTableSQL.append(prefix + precision);
                }
                if(!lengthIgnored) {
                    prefix = (("").equals(prefix) ? "(" : prefix);
                    suffix = (("").equals(suffix) ? ") " : suffix);
                    if(precisionIgnored) {
                        createTableSQL.append(prefix);
                        comma = "";
                    } else {
                        comma = ",";
                    }
                    createTableSQL.append(comma + length);
                }
                createTableSQL.append(suffix);
            } else {
                if(!lengthIgnored) {                
                	if (("postgres_id".equalsIgnoreCase(getDBMSId()) || "postgresplus_id".equalsIgnoreCase(getDBMSId()) ) && metadataColumn.getLength() == null) {
                	}else {
	                    prefix = "(";
	                    suffix = ") ";
	                    createTableSQL.append(prefix + length);                	                	
                	}
                }
                if(!precisionIgnored) {
                    prefix = (("").equals(prefix) ? "(" : prefix);
                    suffix = (("").equals(suffix) ? ") " : suffix);
                    if(lengthIgnored) {
                        createTableSQL.append(prefix);
                        comma = "";                        
                    } else {
                        comma = ",";
                    }
                    createTableSQL.append(comma + precision);
                }
                
                if (("postgres_id".equalsIgnoreCase(getDBMSId()) || "postgresplus_id".equalsIgnoreCase(getDBMSId()) ) && metadataColumn.getLength() == null) {                	
                } else {
                	createTableSQL.append(suffix);
                }
                
                if("mysql_id".equalsIgnoreCase(getDBMSId()) && dataType.endsWith("UNSIGNED")) {
                    createTableSQL.append("UNSIGNED");
                }                
            }
            createTableSQL.append(getDefaultValueSQL(metadataColumn.getDefault()));            
            createTableSQL.append(setNullable(metadataColumn.isNullable()));            
            if(count == listColumn.size() - 1 && pkList.size() == 0) {
                ending = "";
            }
            createTableSQL.append(ending);
            count++;
        }
        if(pkList.size() > 0) {                
            createTableSQL.append("primary key(");                
            int i = 0;                
            for(String pk : pkList) {                    
                createTableSQL.append(pk);                    
                if(i != pkList.size() - 1) {                        
                    createTableSQL.append(",");                        
                }                    
                i++;                    
            }                
            createTableSQL.append(")");                
        }
        createTableSQL.append(")");
        return createTableSQL.toString();
    }
    protected String setNullable(boolean nullable) {
        if(!nullable) {
            return " not null ";
        } else {
            return "";
        }
    }    
    protected String getDefaultValueSQL(String defaultValue) {
        if(defaultValue == null || ("\"\"").equals(defaultValue) || ("").equals(defaultValue)) {
            return " ";
        } else if((defaultValue.startsWith("\"") || defaultValue.startsWith("'")) && (defaultValue.endsWith("\"") || defaultValue.endsWith("'"))) {
            return " default '" + defaultValue.substring(1,defaultValue.length() - 1) + "' ";
        } else if(defaultValue.equalsIgnoreCase("null")) {
            return " default null ";
        } else {
            return " default " + defaultValue + " ";
        }            
    }
    protected String getLProtectedChar() {
    	return "";
    }
    protected String getRProtectedChar() {
    	return "";
    }
    
    protected String getLProtectedChar(String keyword){
    	return getLProtectedChar();
    }
    protected String getRProtectedChar(String keyword){
    	return getRProtectedChar();
    }
    
    public void setNode(INode node){
    	this.node = node;
    }
	public String generateCode4TabelExist() {
	    boolean useExistingConnection = "true".equals(ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__"));
	    String connection = getConnectionName();
	    StringBuilder code = new StringBuilder();  
	    code.append("String tableNameForSearch_" + cid + "= " + getTableName4Search(useExistingConnection, connection) + ";\r\n");
	    if (hasSchema()) {
		    code.append("String dbschemaForSearch_" + cid + "= null;\r\n");
		    code.append("if(dbSchema_" + cid + "== null || dbSchema_" + cid + ".trim().length() == 0) {\r\n");
		  	code.append("dbschemaForSearch_" + cid + "= null" + ";\r\n");
		    code.append("} else {\r\n");
		    code.append("dbschemaForSearch_" + cid + "= " + getShemaName4Search(useExistingConnection, connection)+ ";\r\n");
		    code.append("}\r\n");
	    }
	    return code.toString();
	}
	
	protected String getConnectionName() {
	    return "";
	}
	
	protected String getTableName4Search(boolean useExistingConnection, String connection) {
	    return "\""+getLProtectedChar()+ "\" + " + ElementParameterParser.getValue(node,"__TABLE__") +" + \"" + getRProtectedChar() + "\""; 
	}
	
    protected String getUserName4Search(boolean useExistingConnection, String connection) {
        return "";   
    }

    /*
     * maybe some db need add getLProtectedChar() and getRProtectedChar() to schema name.
     * this because of some db default add getLProtectedChar() and getRProtectedChar() to schaem when create table. e.g:db2
     * 
     * in fact the db add getLProtectedChar() and getRProtectedChar() to scheam when create table that is wrong
    */
    protected String getShemaName4Search(boolean useExistingConnection, String connection) {
        return "";   
    }	
    
    protected boolean hasSchema() {
        return false;
    }
}

class AS400Manager extends Manager {
    private String host;
    private String dbproperty;
    private boolean useExistingConnection;
	private String connection;
    public AS400Manager(String host, String dbName, String tableName, String userName, String password, String cid,String dbproperties,boolean useExistingConnection, String connection ) {
        super(dbName, tableName, userName, password, cid);
        this.host = host;
        this.dbproperty = dbproperties;
        this.useExistingConnection = useExistingConnection;
		this.connection = connection;
    }
    protected String getDriver() {
        return "com.ibm.as400.access.AS400JDBCDriver";
    }
    public String getConnectionURL() {
        if(dbproperty == null || ("\"\"").equals(dbproperty) || ("").equals(dbproperty)){
            return "\"jdbc:as400://\" + " + host + " + \"/\" + "+ dbName;
        }else{
            return "\"jdbc:as400://\" + " + host + " + \"/\" + "+ dbName + "+\";\"+"+ dbproperty;
        }
    }
    protected String getDBMSId() {
        return "as400_id";
    }
    protected String getLProtectedChar() {
        return "";
    }
    protected String getRProtectedChar() {
        return "";
    }
    
    public String getConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append(retrieveTable());
            javaCode.append("java.sql.Connection conn_" + cid + " = (java.sql.Connection)globalMap.get(\"conn_" + connection + "\");");
        } else {
            javaCode.append(super.getConnection());
        }
        return javaCode.toString();
    }  
    public String closeConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append("");
        } else {
            javaCode.append(super.closeConnection());            
        }
        return javaCode.toString();
    }
}   
class AccessManager extends Manager {
    private String dbproperty;
    private boolean useExistingConnection;
	private String connection;
    public AccessManager(String dbName, String tableName, String userName, String password, String cid,String dbproperties,boolean useExistingConnection, String connection) {
        super(dbName, tableName, userName, password, cid);
		this.dbproperty = dbproperties;
        this.useExistingConnection = useExistingConnection;
		this.connection = connection;
    }
    protected String getDriver() {
        return "sun.jdbc.odbc.JdbcOdbcDriver";
    }
    public String getConnectionURL() {
        return "\"jdbc:odbc:Driver={Microsoft Access Driver (*.mdb)};DBQ=\" + " + dbName;
    }
    protected String getDBMSId() {
        return "access_id";
    }
    protected String getLProtectedChar() {
        return "[";
    }
    protected String getRProtectedChar() {
        return "]";
    }
	public String getConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append(retrieveTable());
            javaCode.append("java.sql.Connection conn_" + cid + " = (java.sql.Connection)globalMap.get(\"conn_" + connection + "\");");
        } else {
            javaCode.append(super.getConnection());
        }
        return javaCode.toString();
    }  
    public String closeConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append("");
        } else {
            javaCode.append(super.closeConnection());            
        }
        return javaCode.toString();
    }
}
class DB2Manager extends Manager {
    private String host;
    private String port;
    private String dbSchema;
    private boolean useExistingConnection;
	private String connection;
    public DB2Manager(String host, String port, String dbName, String tableName, String userName, String password, String cid, String dbSchema,boolean useExistingConnection, String connection) {
        super(dbName, tableName, userName, password, cid);
        this.host = host;
        this.port = port;
        this.dbSchema = dbSchema;
        this.useExistingConnection = useExistingConnection;
		this.connection = connection;
    }
    protected String getDriver() {
        return "com.ibm.db2.jcc.DB2Driver";
    }
    public String getConnectionURL() {
        return "\"jdbc:db2://\" + " + host + " + \":\" + " + port + " + \"/\" + " + dbName;
    }
    protected String getDBMSId() {
        return "ibmdb2_id";
    }
    protected String getLProtectedChar() {
        return "\\\"";
    }
    protected String getRProtectedChar() {
        return "\\\"";
    }
    
    public String getConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append(retrieveTable());
            javaCode.append("java.sql.Connection conn_" + cid + " = (java.sql.Connection)globalMap.get(\"conn_" + connection + "\");");
        } else {
            javaCode.append(super.getConnection());
        }
        return javaCode.toString();
    }  
    public String closeConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append("");
        } else {
            javaCode.append(super.closeConnection());            
        }
        return javaCode.toString();
    }
    protected String retrieveTable() {
        StringBuilder javaCode = new StringBuilder();
        javaCode.append(super.retrieveTable());
        if(useExistingConnection) {
            javaCode.append("String dbSchema_" + cid + " = (String)globalMap.get(\"tableschema_" + connection + "\");\r\n"); 
        } else {
            javaCode.append("String dbSchema_" + cid + " = " + dbSchema + ";\r\n");            
        }
        javaCode.append("if(dbSchema_" + cid + " != null && dbSchema_" + cid + ".trim().length() != 0) {");
        javaCode.append("tableName_" + cid + " = dbSchema_" + cid + " + \"" + getLProtectedChar() + "." + getRProtectedChar() + "\" + tableName_" + cid + ";\r\n");
        javaCode.append("}");
        return javaCode.toString();
    } 
}
class FirebirdManager extends Manager {
    private String host;
    private boolean useExistingConnection;
	private String connection;
    public FirebirdManager(String host, String dbName, String tableName, String userName, String password, String cid,boolean useExistingConnection, String connection ) {
        super(dbName, tableName, userName, password, cid);
        this.host = host;
        this.useExistingConnection = useExistingConnection;
		this.connection = connection;
    }
    protected String getDriver() {
        return "org.firebirdsql.jdbc.FBDriver";
    }
    public String getConnectionURL() {
        return "\"jdbc:firebirdsql:\" + " + host + " + \":\" + " + dbName;
    }
    protected String getDBMSId() {
        return "firebird_id";
    }
    protected String getLProtectedChar() {
        return "";
    }
    protected String getRProtectedChar() {
        return "";
    }
     public String getConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append(retrieveTable());
            javaCode.append("java.sql.Connection conn_" + cid + " = (java.sql.Connection)globalMap.get(\"conn_" + connection + "\");");
        } else {
            javaCode.append(super.getConnection());
        }
        return javaCode.toString();
    }  
    public String closeConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append("");
        } else {
            javaCode.append(super.closeConnection());            
        }
        return javaCode.toString();
    }
}
class HSQLDBManager extends Manager {
    private String runningMode;
    private String host;
    private String port;
    private String dbPath;
    private String dbAlias;
    private boolean tls;
    public HSQLDBManager(String host, String port, String dbPath, String dbName, String tableName, String dbAlias, String userName, String password, 
            String runningMode, boolean tls, String cid) {
        super(dbName, tableName, userName, password, cid);
        this.host = host;
        this.port = port;
        this.dbPath = dbPath;
        this.dbAlias = dbAlias;
        this.runningMode = runningMode;
        this.tls = tls;
    }
    protected String getDriver() {
        return "org.hsqldb.jdbcDriver";
    }
    public String getConnectionURL() {
        String connectionURL = null;
        if(("HSQLDB_SERVER").equals(runningMode)) {
            if(tls) {
                connectionURL = "\"jdbc:hsqldb:hsqls://\" + " + host + " + \":\" + " + port + " + \"/\" + " + dbAlias;
            } else {
                connectionURL = "\"jdbc:hsqldb:hsql://\" + " + host + " + \":\" + " + port + " + \"/\" + " + dbAlias;
            }
        } else if(("HSQLDB_WEBSERVER").equals(runningMode)) {
            if(tls) {
                connectionURL = "\"jdbc:hsqldb:https://\" + " + host + " + \":\" + " + port + " + \"/\" + " + dbAlias;
            } else {
                connectionURL = "\"jdbc:hsqldb:http://\" + " + host + " + \":\" + " + port + " + \"/\" + " + dbAlias;
            }               
        } else {
            connectionURL = "\"jdbc:hsqldb:file:\" + " + dbPath + " + \"/\" + " + dbName + " + \";ifexists=true\"";
        }
        return connectionURL;
    }
    protected String getDBMSId() {
        return "hsqldb_id";
    }
    protected String getLProtectedChar() {
        return "\\\"";
    }
    protected String getRProtectedChar() {
        return "\\\"";
    }
    public String closeConnection() {
        StringBuilder javaCode = new StringBuilder();
        javaCode.append("if(conn_" + cid + " != null && !conn_" + cid + ".isClosed()) {\r\n");
        javaCode.append("java.sql.Statement stmtClose_" + cid + " = conn_" + cid + ".createStatement();\r\n");
        javaCode.append("stmtClose_" + cid + ".execute(\"SHUTDOWN\");\r\n");
        javaCode.append("conn_" + cid + ".close();\r\n");
        javaCode.append("}\r\n");
        return javaCode.toString();
    }
}
class InformixManager extends Manager {
    private String host;
    private String port;
    private String dbServer;
	private String dbSchema;
	private boolean useExistingConnection;
	private String connection;
    public InformixManager(String host, String port, String dbName, String tableName, String dbServer, String userName, 
    						String password, String cid, String dbSchema, boolean useExistingConnection, String connection ) {
        super(dbName, tableName, userName, password, cid);
        this.host = host;
        this.port = port;
        this.dbServer = dbServer;
        this.dbSchema = dbSchema;
        this.useExistingConnection = useExistingConnection;
		this.connection = connection;
    }
    protected String getDriver() {
        return "com.informix.jdbc.IfxDriver";
    }
    public String getConnectionURL() {
        return "\"jdbc:informix-sqli://\" + " + host + " + \":\" + " + port + " + \"/\" + " + dbName + " + \":informixserver=\" + " + dbServer;
    }
    protected String getDBMSId() {
        return "informix_id";
    }
    protected String getLProtectedChar() {
        return "";
    }
    protected String getRProtectedChar() {
        return "";
    }
    
	public String getConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append(retrieveTable());
            javaCode.append("java.sql.Connection conn_" + cid + " = (java.sql.Connection)globalMap.get(\"conn_" + connection + "\");");
        } else {
            javaCode.append(super.getConnection());
        }
        return javaCode.toString();
    }  
    public String closeConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append("");
        } else {
            javaCode.append(super.closeConnection());            
        }
        return javaCode.toString();
    }
    protected String retrieveTable() {
        StringBuilder javaCode = new StringBuilder();
        javaCode.append(super.retrieveTable());
        if(useExistingConnection) {
            javaCode.append("String dbSchema_" + cid + " = (String)globalMap.get(\"dbschema_" + connection + "\");\r\n"); 
        } else {
            javaCode.append("String dbSchema_" + cid + " = " + dbSchema + ";\r\n");            
        }
        javaCode.append("if(dbSchema_" + cid + " != null && dbSchema_" + cid + ".trim().length() != 0) {");
        javaCode.append("tableName_" + cid + " = dbSchema_" + cid + " + \"" + getLProtectedChar() + "." + getRProtectedChar() + "\" + tableName_" + cid + ";\r\n");
        javaCode.append("}");
        return javaCode.toString();
    } 
}
class IngresManager extends Manager {
    private String host;
    private String port;
    private boolean useExistingConnection;
	private String connection;
    public IngresManager(String host, String port, String dbName, String tableName, String userName, String password, String cid, boolean useExistingConnection, String connection ) {
        super(dbName, tableName, userName, password, cid);
        this.host = host;
        this.port = port;
        this.useExistingConnection = useExistingConnection;
		this.connection = connection;
    }
    protected String getDriver() {
        return "com.ingres.jdbc.IngresDriver";
    }
    public String getConnectionURL() {
        return "\"jdbc:ingres://\" + " + host + " + \":\" + " + port + " + \"/\" + " + dbName;
    }
    protected String getDBMSId() {
        return "ingres_id";
    }
    protected String getLProtectedChar() {
        return "\\\"";
    }
    protected String getRProtectedChar() {
        return "\\\"";
    }
    public String getConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append(retrieveTable());
            javaCode.append("java.sql.Connection conn_" + cid + " = (java.sql.Connection)globalMap.get(\"conn_" + connection + "\");");
        } else {
            javaCode.append(super.getConnection());
        }
        return javaCode.toString();
    }  
    public String closeConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append("");
        } else {
            javaCode.append(super.closeConnection());            
        }
        return javaCode.toString();
    }
}
class InterbaseManager extends Manager {
    private String host;
    private boolean useExistingConnection;
	private String connection;
    public InterbaseManager(String host, String dbName, String tableName, String userName, String password, String cid, boolean useExistingConnection, String connection ) {
        super(dbName, tableName, userName, password, cid);
        this.host = host;
        this.useExistingConnection = useExistingConnection;
		this.connection = connection;
    }
    protected String getDriver() {
        return "interbase.interclient.Driver";
    }
    public String getConnectionURL() {
        return "\"jdbc:interbase://\" + " + host + " + \"/\" + " + dbName;
    }
    protected String getDBMSId() {
        return "interbase_id";
    }
    protected String getLProtectedChar() {
        return "\\\"";
    }
    protected String getRProtectedChar() {
        return "\\\"";
    }
    public String getConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append(retrieveTable());
            javaCode.append("java.sql.Connection conn_" + cid + " = (java.sql.Connection)globalMap.get(\"conn_" + connection + "\");");
        } else {
            javaCode.append(super.getConnection());
        }
        return javaCode.toString();
    }  
    public String closeConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append("");
        } else {
            javaCode.append(super.closeConnection());            
        }
        return javaCode.toString();
    }
}
class JavaDBManager extends Manager {
    private String host;
    private String port;
    private String dbRootPath;
    private String frameworkType;
    private boolean connectionFlag;
    public JavaDBManager(String host, String port, String dbRootPath, String dbName, String tableName, String userName, String password, String frameworkType, boolean connectionFlag, String cid) {
        super(dbName, tableName, userName, password, cid);
        this.host = host;
        this.port = port;
        this.dbRootPath = dbRootPath;
        this.frameworkType = frameworkType;
        this.connectionFlag = connectionFlag;
    }
    protected String getDriver() {
        String driver = null;
        if(("EMBEDED").equals(frameworkType)) {
            driver = "org.apache.derby.jdbc.EmbeddedDriver";
        }else if(("JCCJDBC").equals(frameworkType)) {
            driver = "com.ibm.db2.jcc.DB2Driver";
        } else {
            driver = "org.apache.derby.jdbc.ClientDriver";
        }
        return driver;
    }
    public String getConnectionURL() {
        String connectionURL = null;
        if(("EMBEDED").equals(frameworkType)) {
            connectionURL = "\"jdbc:derby:\" + " + dbName;
        } else if(("JCCJDBC").equals(frameworkType)) {
            connectionURL = "\"jdbc:derby:net://\" + " + host + " + \":\" + " + port + " + \"/\" + " + dbName;
        } else {
            connectionURL = "\"jdbc:derby://\" + " + host + " + \":\" + " + port + " + \"/\" + " + dbName;
        }
        return connectionURL;
    }
    public String getConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(("EMBEDED").equals(frameworkType)) {
            javaCode.append("System.setProperty(\"derby.system.home\", " + dbRootPath + ");\r\n");              
        } else {
            if(!connectionFlag) {
                javaCode.append(startServer());
            }
        }
        javaCode.append(super.getConnection());
        return javaCode.toString();
    }
    private String startServer() {
        StringBuilder javaCode = new StringBuilder();
        javaCode.append("org.apache.derby.drda.NetworkServerControl serverControl_" + cid + " = new org.apache.derby.drda.NetworkServerControl(java.net.InetAddress.getByName(" + host + "),Integer.parseInt(" + port + "));\r\n");
        javaCode.append("serverControl_" + cid + ".start(new java.io.PrintWriter(System.out,true));\r\n");
        javaCode.append("boolean isServerUp_" + cid + " = false;\r\n");
        javaCode.append("int timeOut_" + cid + " = 5;\r\n");
        javaCode.append("while(!isServerUp_" + cid + " && timeOut_" + cid + " > 0) {\r\n");
        javaCode.append("try {\r\n");
        javaCode.append("timeOut_" + cid + "--;\r\n");
        javaCode.append("serverControl_" + cid + ".ping();\r\n");
        javaCode.append("isServerUp_" + cid + " = true;\r\n");
        javaCode.append("} catch(Exception e) {\r\n");
        javaCode.append("Thread.currentThread().sleep(3000);\r\n");
        javaCode.append("}\r\n");
        javaCode.append("}\r\n");
        javaCode.append("if(!isServerUp_" + cid + ") {\r\n");
        javaCode.append("System.exit(1);\r\n");
        javaCode.append("}\r\n");
        return javaCode.toString();
    }
    public String closeConnection() {
        StringBuilder javaCode = new StringBuilder();
        javaCode.append(super.closeConnection());
        if(("EMBEDED").equals(frameworkType)) {
            javaCode.append("try {\r\n");
            javaCode.append("java.sql.DriverManager.getConnection(\"jdbc:derby:\" + " + dbName+ " + \" +;shutdown=true\");\r\n");
            javaCode.append("} catch(java.sql.SQLException se) {\r\n");
            javaCode.append("}\r\n");               
        }
        return javaCode.toString();
    }
    protected String getDBMSId() {
        return "javadb_id";
    }
    protected String getLProtectedChar() {
        return "\\\"";
    }
    protected String getRProtectedChar() {
        return "\\\"";
    }
}
class MaxDBManager extends Manager {
    private String host;
    private String port;
    public MaxDBManager(String host, String port, String dbName, String tableName, String userName, String password, String cid) {
        super(dbName, tableName, userName, password, cid);
        this.host = host;
        this.port = port;
    }
    protected String getDriver() {
        return "com.sap.dbtech.jdbc.DriverSapDB";
    }
    public String getConnectionURL() {
        return "\"jdbc:sapdb://\" + " + host + " + \":\" + " + port + " + \"/\" + " + dbName;
    }
    protected String getDBMSId() {
        return "maxdb_id";
    }
    protected String getLProtectedChar() {
        return "";
    }
    protected String getRProtectedChar() {
        return "";
    }
}
class MSSQLManager extends Manager {
    private String host;
    private String port;
    private boolean useExistingConnection;
    private String connection;
    private String dbproperty;
    public MSSQLManager(String host, String port, String dbName, String tableName, String userName, String password, boolean useExistingConnection, String connection, String cid, String dbproperty) {
        super(dbName, tableName, userName, password, cid);
        this.host = host;
        this.port = port;
        this.useExistingConnection = useExistingConnection;
        this.connection = connection;
        this.dbproperty = dbproperty;
    }
    protected String getDriver() {
        return "net.sourceforge.jtds.jdbc.Driver";
    }
    public String getConnectionURL() {
        if(dbproperty == null || dbproperty.equals("\"\"") || dbproperty.equals("")){
            return "\"jdbc:jtds:sqlserver://\" + " + host + " + \":\" + " + port + " + \"//\" + " + dbName;
        }
        
        return "\"jdbc:jtds:sqlserver://\" + " + host + " + \":\" + " + port + " + \"//\" + " + dbName + "+\";\"+"+ dbproperty;
    }
    public String getConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append(retrieveTable());
            javaCode.append("java.sql.Connection conn_" + cid + " = (java.sql.Connection)globalMap.get(\"conn_" + connection + "\");");
        } else {
            javaCode.append(super.getConnection());
        }
        return javaCode.toString();
    }
    protected String getDBMSId() {
        return "id_MSSQL";
    }
    protected String getLProtectedChar() {
        return "[";
    }
    protected String getRProtectedChar() {
        return "]";
    }
    public String closeConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append("");
        } else {
            javaCode.append(super.closeConnection());            
        }
        return javaCode.toString();
    }
}
class MysqlManager extends Manager {
    private String host;
    private String port;
    private boolean useExistingConnection;
    private String connection;        
    public MysqlManager(String host, String port, String dbName, String tableName, String userName, String password, boolean useExistingConnection, String connection, boolean temporary, String cid) {
        super(dbName, tableName, userName, password, cid);
        this.host = host;
        this.port = port;
        this.useExistingConnection = useExistingConnection;
        this.connection = connection;
        this.temporary = temporary;
    }
    protected String getDriver() {
        return "org.gjt.mm.mysql.Driver";
    }
    public String getConnectionURL() {
        return "\"jdbc:mysql://\" + " + host + " + \":\" + " + port + " + \"/\" + " + dbName;
    }
    public String getConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append(retrieveTable());
            javaCode.append("java.sql.Connection conn_" + cid + " = (java.sql.Connection)globalMap.get(\"conn_" + connection + "\");");
        } else {
            javaCode.append(super.getConnection());
        }
        return javaCode.toString();
    }        
    protected String getDBMSId() {
        return "mysql_id";
    }
    protected String getLProtectedChar() {
        return "`";
    }
    protected String getRProtectedChar() {
        return "`";
    }
    public String closeConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append("");
        } else {
            javaCode.append(super.closeConnection());            
        }
        return javaCode.toString();
    }    
}

class NetezzaManager extends Manager {
    private String host;
    private String port;
    private boolean useExistingConnection;
    private String connection;        
    public NetezzaManager(String host, String port, String dbName, String tableName, String userName, String password, boolean useExistingConnection, String connection, String cid) {
        super(dbName, tableName, userName, password, cid);
        this.host = host;
        this.port = port;
        this.useExistingConnection = useExistingConnection;
        this.connection = connection;
    }
    protected String getDriver() {
        return "org.netezza.Driver";
    }
    public String getConnectionURL() {
        return "\"jdbc:netezza://\" + " + host + " + \":\" + " + port + " + \"/\" + " + dbName;
    }
    public String getConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append(retrieveTable());
            javaCode.append("java.sql.Connection conn_" + cid + " = (java.sql.Connection)globalMap.get(\"conn_" + connection + "\");");
        } else {
            javaCode.append(super.getConnection());
        }
        return javaCode.toString();
    }        
    protected String getDBMSId() {
        return "netezza_id";
    }
    protected String getLProtectedChar() {
        return "";
    }
    protected String getRProtectedChar() {
        return "";
    }
    public String closeConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append("");
        } else {
            javaCode.append(super.closeConnection());            
        }
        return javaCode.toString();
    }    
}

class OracleManager extends Manager {
    private String host;
    private String port;
    private String dbSchema;
    private String connectionType;
    private boolean useExistingConnection;
    private String connection;
    private String jdbcURL;
    private String localServiceName;
    private String[] oracleKeyWords= {
    	"ACCESS" ,"AUDIT","COMPRESS","DESC" , 
    	"ADD","ACCESS","CONNECT","DISTINCT" ,
    	"ALL","BY","CREATE","DROP",
    	"ALTER","CHAR","CURRENT","ELSE",
    	"AND","CHECK","DATE","EXCLUSIVE",
    	"ANY","CLUSTER","DECIMAL","	EXISTS",
    	"AS","COLUMN","DEFAULT","FILE",
    	"ASC","COMMENT","DELETE","FLOAT",
    	"FOR","LONG","PCTFREE","SUCCESSFUL",
    	"FROM","MAXEXTENTS","PRIOR","SYNONYM",
    	"GRANT","MINUS","PRIVILEGES","SYSDATE",
    	"GROUP","MODE","PUBLIC","TABLE",
    	"HAVING","MODIFY","RAW","THEN",
    	"IDENTIFIED","NETWORK","RENAME","TO",
		"IMMEDIATE","NOAUDIT","RESOURCE","TRIGGER",
		"IN","NOCOMPRESS","REVOKE","UID",
		"INCREMENT","NOT","ROW","UNION",
		"INDEX","NOWAIT","ROWID","UNIQUE",
		"INITIAL","NULL","ROWNUM","UPDATE",
		"INSERT","NUMBER","ROWS","USER",
		"INTEGER","OF","SELECT","VALIDATE",
		"INTERSECT","OFFLINE","SESSION","VALUES",
		"INTO","ON","SET","VARCHAR",
		"IS","ONLINE","SHARE","VARCHAR2",
		"LEVEL","OPTION","SIZE","VIEW",
		"LIKE","OR","SMALLINT","WHENEVER",
		"LOCK","ORDER","START","WHERE","WITH"
    	};
            
    public OracleManager(String host, String port, String dbName, String tableName, String dbSchema, String useName, String password, String connectionType, boolean useExistingConnection, String connection, String cid,String jdbcURL,String localServiceName) {
        super(dbName, tableName, useName, password, cid);
        this.host = host;
        this.port = port;
        this.dbSchema = dbSchema;
        this.connectionType = connectionType;
        this.useExistingConnection = useExistingConnection;
        this.connection = connection;
        this.jdbcURL = jdbcURL;
        this.localServiceName = localServiceName;
    }
    protected String getDriver() {
        return "oracle.jdbc.driver.OracleDriver";
    }
    public String getConnectionURL() {
        String connectionURL = null;
        if(("ORACLE_SID").equals(connectionType)) {
            connectionURL = "\"jdbc:oracle:thin:@\" + " + host + " + \":\" + " + port + " + \":\" + " + dbName;
        } else if(("ORACLE_SERVICE_NAME").equals(connectionType)) {
            connectionURL = "\"jdbc:oracle:thin:@(description=(address=(protocol=tcp)(host=\" + " + host + " + \")(port=\" + " + port + " + \"))(connect_data=(service_name=\" + " + dbName + " + \")))\"";
        }
  		 else if(("ORACLE_OCI").equals(connectionType)) {
         connectionURL = "\"jdbc:oracle:oci8:@\" + " + localServiceName ;
        }
        else if(("ORACLE_WALLET").equals(connectionType)) {
         connectionURL = jdbcURL ;
				}
        return connectionURL;
    }
    public String getConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append(retrieveTable());
            javaCode.append("java.sql.Connection conn_" + cid + " = (java.sql.Connection)globalMap.get(\"conn_" + connection + "\");");
        }
         else if (("ORACLE_WALLET").equals(connectionType)) 
        	{
        	 javaCode.append(retrieveTable());
        	 javaCode.append("java.lang.Class.forName(\"" + getDriver()+ "\");\r\n");
          	 javaCode.append("java.sql.Connection conn_" + cid + "= java.sql.DriverManager.getConnection(" + getConnectionURL() + ");\r\n");
        	}
          else {
            javaCode.append(super.getConnection());
        }
        return javaCode.toString();
    }        
    protected String getDBMSId() {
        return "oracle_id";
    }
    protected String getLProtectedChar() {
        return "";
    }
    protected String getRProtectedChar() {
        return "";
    }
    protected boolean isOracleKeyword (String keyword) {
        for (int i=0 ; i < oracleKeyWords.length ; i++){
            if (oracleKeyWords[i].equalsIgnoreCase(keyword)) {
            	return true;
            }
        }
    	return false;
    }
    protected String getLProtectedChar(String keyword) {
    	if (isOracleKeyword(keyword)){
    		return "\\\"";
    	}
    	return getLProtectedChar();
    }
    protected String getRProtectedChar(String keyword) {
    	if (isOracleKeyword(keyword)){
    		return "\\\"";
    	}
    	return getRProtectedChar();
    }    
    public String closeConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append("");
        } else {
            javaCode.append(super.closeConnection());            
        }
        return javaCode.toString();
    }
    protected String retrieveTable() {
        StringBuilder javaCode = new StringBuilder();
        javaCode.append(super.retrieveTable());
        if(useExistingConnection) {
            javaCode.append("String dbSchema_" + cid + " = (String)globalMap.get(\"dbschema_" + connection + "\");\r\n"); 
        } else {
            javaCode.append("String dbSchema_" + cid + " = " + dbSchema + ";\r\n");            
        }
        javaCode.append("if(dbSchema_" + cid + " != null && dbSchema_" + cid + ".trim().length() != 0) {");
        javaCode.append("tableName_" + cid + " = dbSchema_" + cid + " + \"" + getLProtectedChar() + "." + getRProtectedChar() + "\" + tableName_" + cid + ";\r\n");
        javaCode.append("}");
        return javaCode.toString();
    }
    
    protected String getConnectionName() {
	    return ElementParameterParser.getValue(node,"__CONNECTION_ORACLE__");
	}
    
    protected String getTableName4Search(boolean useExistingConnection, String connection) {
        return "\""+getLProtectedChar()+ "\" + ((String)" + ElementParameterParser.getValue(node,"__TABLE__") + ").toUpperCase()" +" + \"" + getRProtectedChar() + "\""; 
    }
    
    protected String getUserName4Search(boolean useExistingConnection, String connection) {
        if (useExistingConnection) {
          return "((String)globalMap.get(\"username_" + connection + "\")).toUpperCase()";
        } else {
          return "((String)" + ElementParameterParser.getValue(node,"__USER__") + ").toUpperCase()";
        }    
    }

    protected String getShemaName4Search(boolean useExistingConnection, String connection) {
        return "dbSchema_" + cid + ".toUpperCase()"; 
    }   
    
    protected boolean hasSchema() {
        return true;
    }
}
class PostgreManager extends Manager {
    private String host;
    private String port;
    private String dbSchema;
    private boolean useExistingConnection;
    private String connection;        
    public PostgreManager(String host, String port, String dbName, String tableName, String dbSchema, String userName, String password, boolean useExistingConnection, String connection, String cid) {
        super(dbName, tableName, userName ,password, cid);
        this.host = host;
        this.port = port;
        this.dbSchema = dbSchema;
        this.useExistingConnection = useExistingConnection;
        this.connection = connection;
    }
    protected String getDriver() {
        return "org.postgresql.Driver";
    }
    public String getConnectionURL() {
        return "\"jdbc:postgresql://\" + " + host + " + \":\" + " + port + " + \"/\" + " + dbName;
    }
    public String getConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append(retrieveTable());
            javaCode.append("java.sql.Connection conn_" + cid + " = (java.sql.Connection)globalMap.get(\"conn_" + connection + "\");");
        } else {
            javaCode.append(super.getConnection());
        }
        return javaCode.toString();
    }        
    protected String getDBMSId() {
        return "postgres_id";
    }
    protected String getLProtectedChar() {
        return "\\\"";
    }
    protected String getRProtectedChar() {
        return "\\\"";
    }
    public String closeConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append("");
        } else {
            javaCode.append(super.closeConnection());            
        }
        return javaCode.toString();
    }
    protected String retrieveTable() {
        StringBuilder javaCode = new StringBuilder();
        javaCode.append(super.retrieveTable());
        if(useExistingConnection) {
            javaCode.append("String dbSchema_" + cid + " = (String)globalMap.get(\"schema_" + connection + "\");\r\n"); 
        } else {
            javaCode.append("String dbSchema_" + cid + " = " + dbSchema + ";\r\n");            
        }
        javaCode.append("if(dbSchema_" + cid + " != null && dbSchema_" + cid + ".trim().length() != 0) {");
        javaCode.append("tableName_" + cid + " = dbSchema_" + cid + " + \"" + getLProtectedChar() + "." + getRProtectedChar() + "\" + tableName_" + cid + ";\r\n");
        javaCode.append("}");
        return javaCode.toString();        
    }
}
class PostgrePlusManager extends Manager {
    private String host;
    private String port;
    private String dbSchema;
    private boolean useExistingConnection;
    private String connection;        
    public PostgrePlusManager(String host, String port, String dbName, String tableName, String dbSchema, String userName, String password, boolean useExistingConnection, String connection, String cid) {
        super(dbName, tableName, userName ,password, cid);
        this.host = host;
        this.port = port;
        this.dbSchema = dbSchema;
        this.useExistingConnection = useExistingConnection;
        this.connection = connection;
    }
    protected String getDriver() {
        return "org.postgresql.Driver";
    }
    public String getConnectionURL() {
        return "\"jdbc:postgresql://\" + " + host + " + \":\" + " + port + " + \"/\" + " + dbName;
    }
    public String getConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append(retrieveTable());
            javaCode.append("java.sql.Connection conn_" + cid + " = (java.sql.Connection)globalMap.get(\"conn_" + connection + "\");");
        } else {
            javaCode.append(super.getConnection());
        }
        return javaCode.toString();
    }        
    protected String getDBMSId() {
        return "postgresplus_id";
    }
    protected String getLProtectedChar() {
        return "\\\"";
    }
    protected String getRProtectedChar() {
        return "\\\"";
    }
    public String closeConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append("");
        } else {
            javaCode.append(super.closeConnection());            
        }
        return javaCode.toString();
    }
    protected String retrieveTable() {
        StringBuilder javaCode = new StringBuilder();
        javaCode.append(super.retrieveTable());
        if(useExistingConnection) {
            javaCode.append("String dbSchema_" + cid + " = (String)globalMap.get(\"schema_" + connection + "\");\r\n"); 
        } else {
            javaCode.append("String dbSchema_" + cid + " = " + dbSchema + ";\r\n");            
        }
        javaCode.append("if(dbSchema_" + cid + " != null && dbSchema_" + cid + ".trim().length() != 0) {");
        javaCode.append("tableName_" + cid + " = dbSchema_" + cid + " + \"" + getLProtectedChar() + "." + getRProtectedChar() + "\" + tableName_" + cid + ";\r\n");
        javaCode.append("}");
        return javaCode.toString();        
    }    
}
class SQLiteManager extends Manager {
	private boolean useExistingConnection;
	private String connection;
    public SQLiteManager(String dbName, String tableName, String userName, String password, String cid, boolean useExistingConnection, String connection ) {
        super(dbName, tableName, userName, password, cid);
		this.useExistingConnection = useExistingConnection;
		this.connection = connection;
    }
    protected String getDriver() {
        return "org.sqlite.JDBC";
    }
    public String getConnectionURL() {
        return "\"jdbc:sqlite:/\" + " + dbName.toLowerCase();
    }
    protected String getDBMSId() {
        return "sqlite_id";
    }
    protected String getLProtectedChar() {
        return "\\\"";
    }
    protected String getRProtectedChar() {
        return "\\\"";
    }
    public String getConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append(retrieveTable());
            javaCode.append("java.sql.Connection conn_" + cid + " = (java.sql.Connection)globalMap.get(\"conn_" + connection + "\");");
        } else {
            javaCode.append(super.getConnection());
        }
        return javaCode.toString();
    }  
    public String closeConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append("");
        } else {
            javaCode.append(super.closeConnection());            
        }
        return javaCode.toString();
    }
}
class SybaseManager extends Manager {
    private String host;
    private String port;
    private boolean useExistingConnection;
	private String connection;
    public SybaseManager(String host, String port, String dbName, String tableName, String userName, String password, String cid, boolean useExistingConnection, String connection) {
        super(dbName, tableName, userName, password, cid);
        this.host = host;
        this.port = port;
        this.useExistingConnection = useExistingConnection;
		this.connection = connection;
    }
    protected String getDriver() {
        return "com.sybase.jdbc3.jdbc.SybDriver";
    }
    public String getConnectionURL() {
        return "\"jdbc:sybase:Tds:\" + " + host + "+ \":\" + " + port + "+ \"/\" + " + dbName;
    }
    protected String getDBMSId() {
        return "sybase_id";
    }
    protected String getLProtectedChar() {
        return "";
    }
    protected String getRProtectedChar() {
        return "";
    }
    protected String setNullable(boolean nullable) {
        if(!nullable) {
            return " not null ";
        } else {
            return " null ";
        }
    } 
	public String getConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append(retrieveTable());
            javaCode.append("java.sql.Connection conn_" + cid + " = (java.sql.Connection)globalMap.get(\"conn_" + connection + "\");");
        } else {
            javaCode.append(super.getConnection());
        }
        return javaCode.toString();
    }  
    public String closeConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append("");
        } else {
            javaCode.append(super.closeConnection());            
        }
        return javaCode.toString();
    }   
}
class ODBCManager extends Manager {
    public ODBCManager(String dbName, String tableName, String userName, String password, String cid) {
        super(dbName, tableName, userName, password, cid);
    }
    protected String getDriver() {
        return "sun.jdbc.odbc.JdbcOdbcDriver";
    }
    public String getConnectionURL() {
        return "\"jdbc:odbc:\" + " + dbName;
    }
    protected String getDBMSId() {
        return "MSODBC";
    }
    protected String getLProtectedChar() {
        return "\\\"";
    }
    protected String getRProtectedChar() {
        return "\\\"";
    }
}
class TeradataManager extends Manager {
    private String host;
    private String port;
    private boolean useExistingConnection;
	private String connection;
    protected TeradataManager(String host, String port, String dbName, String tableName, 
    							String userName, String password, String cid, boolean useExistingConnection, String connection ) {
        super(dbName, tableName, userName, password, cid);
        this.host = host;
        this.port = port;
        this.useExistingConnection = useExistingConnection;
		this.connection = connection;
    }
    protected String getDriver() {
        return "com.teradata.jdbc.TeraDriver";
    }
    public String getConnectionURL() {
        return "\"jdbc:teradata://\" + " + host;
    }
    protected String getDBMSId() {
        return "teradata_id";
    }
    protected String getLProtectedChar() {
        return "\\\"";
    }
    protected String getRProtectedChar() {
        return "\\\"";
    }
    public String getConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append(retrieveTable());
            javaCode.append("java.sql.Connection conn_" + cid + " = (java.sql.Connection)globalMap.get(\"conn_" + connection + "\");");
        } else {
            javaCode.append(super.getConnection());
        }
        return javaCode.toString();
    }  
    public String closeConnection() {
        StringBuilder javaCode = new StringBuilder();
        if(useExistingConnection) {
            javaCode.append("");
        } else {
            javaCode.append(super.closeConnection());            
        }
        return javaCode.toString();
    }
	protected String retrieveTable() {
        StringBuilder javaCode = new StringBuilder();
        javaCode.append("String tableName_" + cid + " = null;\r\n");
		if(useExistingConnection) {
            javaCode.append("String dbname_" + cid + " = (String)globalMap.get(\"dbname_" + connection + "\");\r\n"); 
        } else {
            javaCode.append("String dbname_" + cid + " = " + dbName + ";\r\n");         
        }
       
        javaCode.append("if ( dbname_" + cid + " == null || dbname_" + cid + ".trim().length() == 0) { \r\n");
        javaCode.append("\t tableName_" + cid + " = " + tableName + ";\r\n");
        javaCode.append(" } else { \r\n");
		javaCode.append("\t tableName_" + cid + " = dbname_" + cid + " + \"\\\".\\\"\" + " + tableName + ";\r\n");
		javaCode.append("}\r\n");
        return javaCode.toString();
    }
    
      public String getCreateTableSQL(List<IMetadataColumn> listColumn) {
        MappingTypeRetriever mappingType = MetadataTalendType.getMappingTypeRetriever(getDBMSId());
        StringBuilder createTableSQL = new StringBuilder();
        
        String tableType = ElementParameterParser.getValue(node, "__CREATE_TABLE_TYPE__");
        
        createTableSQL.append("CREATE " + tableType + " TABLE " + getLProtectedChar( "\" + tableName_" + cid + " + \"" ) + "\" + tableName_" + cid + " + \"" + getRProtectedChar("\" + tableName_" + cid + " + \"") + "(");
        List<String> pkList = new ArrayList<String>();
        int count = 0;
        String ending = ",";
        for(IMetadataColumn metadataColumn : listColumn) {
            if(metadataColumn.isKey()) {
                pkList.add(getLProtectedChar(metadataColumn.getOriginalDbColumnName()) + metadataColumn.getOriginalDbColumnName() + getRProtectedChar(metadataColumn.getOriginalDbColumnName()));
            }
            createTableSQL.append(getLProtectedChar(metadataColumn.getOriginalDbColumnName()) + metadataColumn.getOriginalDbColumnName() + getRProtectedChar(metadataColumn.getOriginalDbColumnName()) + " ");
            String dataType = null;
            if(metadataColumn.getType() == null || metadataColumn.getType().trim().length() == 0) {
                dataType = mappingType.getDefaultSelectedDbType(metadataColumn.getTalendType());
            } else {
                dataType = metadataColumn.getType();
            }
            if ("mysql_id".equalsIgnoreCase(getDBMSId()) && dataType.endsWith("UNSIGNED") ) {
            	createTableSQL.append(dataType.substring(0,dataType.indexOf("UNSIGNED"))) ; 
            }else {
            	createTableSQL.append(dataType);
            }            
            Integer length = metadataColumn.getLength() == null ? 0 : metadataColumn.getLength();
            Integer precision = metadataColumn.getPrecision() == null ? 0 : metadataColumn.getPrecision();
            boolean lengthIgnored = mappingType.isLengthIgnored(getDBMSId(), dataType);
            boolean precisionIgnored = mappingType.isPrecisionIgnored(getDBMSId(), dataType);
            String prefix = "";
            String suffix = "";
            String comma = "";
            
            
            if(mappingType.isPreBeforeLength(getDBMSId(),dataType)) {
                if(!precisionIgnored) {
                    prefix = "(";
                    suffix = ") ";
                    createTableSQL.append(prefix + precision);
                }
                if(!lengthIgnored) {
                    prefix = (("").equals(prefix) ? "(" : prefix);
                    suffix = (("").equals(suffix) ? ") " : suffix);
                    if(precisionIgnored) {
                        createTableSQL.append(prefix);
                        comma = "";
                    } else {
                        comma = ",";
                    }
                    createTableSQL.append(comma + length);
                }
                createTableSQL.append(suffix);
            } else {
                if(!lengthIgnored) {                
                	if (("postgres_id".equalsIgnoreCase(getDBMSId()) || "postgresplus_id".equalsIgnoreCase(getDBMSId()) ) && metadataColumn.getLength() == null) {
                	}else {
	                    prefix = "(";
	                    suffix = ") ";
	                    createTableSQL.append(prefix + length);                	                	
                	}
                }
                if(!precisionIgnored) {
                    prefix = (("").equals(prefix) ? "(" : prefix);
                    suffix = (("").equals(suffix) ? ") " : suffix);
                    if(lengthIgnored) {
                        createTableSQL.append(prefix);
                        comma = "";                        
                    } else {
                        comma = ",";
                    }
                    createTableSQL.append(comma + precision);
                }
                
                if (("postgres_id".equalsIgnoreCase(getDBMSId()) || "postgresplus_id".equalsIgnoreCase(getDBMSId()) ) && metadataColumn.getLength() == null) {                	
                } else {
                	createTableSQL.append(suffix);
                }
                
                if("mysql_id".equalsIgnoreCase(getDBMSId()) && dataType.endsWith("UNSIGNED")) {
                    createTableSQL.append("UNSIGNED");
                }                
            }
            createTableSQL.append(getDefaultValueSQL(metadataColumn.getDefault()));            
            createTableSQL.append(setNullable(metadataColumn.isNullable()));            
            if(count == listColumn.size() - 1 && pkList.size() == 0) {
                ending = "";
            }
            createTableSQL.append(ending);
            count++;
        }
        if(pkList.size() > 0) {                
            createTableSQL.append("primary key(");                
            int i = 0;                
            for(String pk : pkList) {                    
                createTableSQL.append(pk);                    
                if(i != pkList.size() - 1) {                        
                    createTableSQL.append(",");                        
                }                    
                i++;                    
            }                
            createTableSQL.append(")");                
        }
        createTableSQL.append(")");
        return createTableSQL.toString();
    }
}
class StringUtil {
    public String getString(String source) {
        String tmp = null;
        if(source == null || ("").equals(source) || ("\"\"").equals(source)) {
            tmp = "";
        } else {
            if(source.startsWith("\"") && source.endsWith("\"")) {
                tmp = source.substring(1, source.length() - 1);
            } else {
                tmp = source;
            }
        }
        return tmp;
    }
}
class ManagerFactory {
    private StringUtil stringUtil = new StringUtil();
    public Manager createManager(String dbType, INode node) {
        Manager manager = null;
        String cid = node.getUniqueName();
        String tableName = ElementParameterParser.getValue(node,"__TABLE__");
        if(("ACCESS").equals(dbType)) {
            String dbName = ElementParameterParser.getValue(node, "__DBFILE_ACCESS__");
            String userName = ElementParameterParser.getValue(node, "__USER__");
            String password = ElementParameterParser.getValue(node, "__PASS__");
            String dbproperties = ElementParameterParser.getValue(node, "__PROPERTIES__");
            boolean useExistingConnection = ("true").equals(ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__"));
			String connection = stringUtil.getString(ElementParameterParser.getValue(node, "__CONNECTION_ACCESS__"));
            manager = new AccessManager(dbName, tableName, userName, password, cid,dbproperties,useExistingConnection, connection);
        } else if(("AS400").equals(dbType)) {
            String host = ElementParameterParser.getValue(node, "__HOST__");
            String dbName = ElementParameterParser.getValue(node, "__DBNAME__");
            String userName = ElementParameterParser.getValue(node, "__USER__");
            String password = ElementParameterParser.getValue(node, "__PASS__");
            String dbproperties = ElementParameterParser.getValue(node, "__PROPERTIES__");
            boolean useExistingConnection = ("true").equals(ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__"));
			String connection = stringUtil.getString(ElementParameterParser.getValue(node, "__CONNECTION_AS400__"));
            manager = new AS400Manager(host, dbName, tableName, userName, password, cid,dbproperties,useExistingConnection, connection);
        } else if(("DB2").equals(dbType)) {
            String host = ElementParameterParser.getValue(node, "__HOST__");
            String port = ElementParameterParser.getValue(node, "__PORT__");
            String dbName = ElementParameterParser.getValue(node, "__DBNAME__");
            String userName = ElementParameterParser.getValue(node, "__USER__");
            String password = ElementParameterParser.getValue(node, "__PASS__");
            String dbSchema = ElementParameterParser.getValue(node, "__SCHEMA_DB__");
            boolean useExistingConnection = ("true").equals(ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__"));
			String connection = stringUtil.getString(ElementParameterParser.getValue(node, "__CONNECTION_DB2__"));
            manager = new DB2Manager(host, port, dbName, tableName, userName, password, cid, dbSchema,useExistingConnection, connection);
        } else if(("FIREBIRD").equals(dbType)) {
            String host = ElementParameterParser.getValue(node, "__HOST__");
            String dbName = ElementParameterParser.getValue(node, "__DBFILE_FIREBIRD__");
            String userName = ElementParameterParser.getValue(node, "__USER__");
            String password = ElementParameterParser.getValue(node, "__PASS__");
            boolean useExistingConnection = ("true").equals(ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__"));
			String connection = stringUtil.getString(ElementParameterParser.getValue(node, "__CONNECTION_FIREBIRD__"));
            manager = new FirebirdManager(host, dbName, tableName, userName, password, cid, useExistingConnection, connection);
        } else if(("HSQLDB").equals(dbType)) {
            String host = ElementParameterParser.getValue(node, "__HOST__");
            String port = ElementParameterParser.getValue(node, "__PORT__");
            String dbPath = ElementParameterParser.getValue(node, "__DBPATH_HSQL__");
            String dbName = ElementParameterParser.getValue(node, "__DBNAME_HSQL__");
            String dbAlias = ElementParameterParser.getValue(node, "__DATABASE_ALIAS__");
            String userName = ElementParameterParser.getValue(node, "__USER__");
            String password = ElementParameterParser.getValue(node, "__PASS__");
            String runningMode = ElementParameterParser.getValue(node, "__RUNNING_MODE__");
            boolean tls = ("true").equals(ElementParameterParser.getValue(node, "__TLS__"));
            manager = new HSQLDBManager(host, port, dbPath, dbName, tableName, dbAlias, userName, password, runningMode, tls, cid);
        } else if(("INFORMIX").equals(dbType)) {
            String host = ElementParameterParser.getValue(node, "__HOST__");
            String port = ElementParameterParser.getValue(node, "__PORT__");
            String dbName = ElementParameterParser.getValue(node, "__DBNAME__");
            String dbServer = ElementParameterParser.getValue(node, "__DBSERVER__");
            String userName = ElementParameterParser.getValue(node, "__USER__");
            String password = ElementParameterParser.getValue(node, "__PASS__");
			String dbSchema = ElementParameterParser.getValue(node, "__SCHEMA_DB__");
			boolean useExistingConnection = ("true").equals(ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__"));
			String connection = stringUtil.getString(ElementParameterParser.getValue(node, "__CONNECTION_INFORMIX__"));
            manager = new InformixManager(host, port, dbName, tableName, dbServer, userName, password, cid, dbSchema,useExistingConnection, connection);
        } else if(("INGRES").equals(dbType)) {
            String host = ElementParameterParser.getValue(node, "__HOST__");
            String port = ElementParameterParser.getValue(node, "__PORT__");
            String dbName = ElementParameterParser.getValue(node, "__DBNAME__");
            String userName = ElementParameterParser.getValue(node, "__USER__");
            String password = ElementParameterParser.getValue(node, "__PASS__");
            boolean useExistingConnection = ("true").equals(ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__"));
			String connection = stringUtil.getString(ElementParameterParser.getValue(node, "__CONNECTION_INGRES__"));
            manager = new IngresManager(host, port, dbName, tableName, userName, password, cid,useExistingConnection, connection);
        } else if(("INTERBASE").equals(dbType)) {
            String host = ElementParameterParser.getValue(node, "__HOST__");
            String dbName = ElementParameterParser.getValue(node, "__DBFILE_INTERBASE__");
            String userName = ElementParameterParser.getValue(node, "__USER__");
            String password = ElementParameterParser.getValue(node, "__PASS__");
            boolean useExistingConnection = ("true").equals(ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__"));
			String connection = stringUtil.getString(ElementParameterParser.getValue(node, "__CONNECTION_INTERBASE__"));
            manager = new InterbaseManager(host, dbName, tableName, userName, password, cid,useExistingConnection, connection);
        } else if(("JAVADB").equals(dbType)) {
            String host = ElementParameterParser.getValue(node, "__HOST__");
            String port = ElementParameterParser.getValue(node, "__PORT__");
            String dbRootPath = ElementParameterParser.getValue(node, "__DBROOTPATH__");
            String dbName = ElementParameterParser.getValue(node, "__DBNAME_JAVADB__");
            String userName = ElementParameterParser.getValue(node, "__USER__");
            String password = ElementParameterParser.getValue(node, "__PASS__");
            String frameworkType = ElementParameterParser.getValue(node, "__FRAMEWORK_TYPE__");
            boolean connectionFlag = ("true").equals(ElementParameterParser.getValue(node, "__CONNECTION_FLAG__"));
            manager = new JavaDBManager(host, port, dbRootPath, dbName, tableName, userName, password, frameworkType, connectionFlag, cid);
        } else if(("MAXDB").equals(dbType)) {
            String host = ElementParameterParser.getValue(node, "__HOST__");
            String port = ElementParameterParser.getValue(node, "__PORT__");
            String dbName = ElementParameterParser.getValue(node, "__DBNAME__");
            String userName = ElementParameterParser.getValue(node, "__USER__");
            String password = ElementParameterParser.getValue(node, "__PASS__");
            manager = new MaxDBManager(host, port, dbName, tableName, userName, password, cid);
        } else if(("MSSQL").equals(dbType)) {
            String host = ElementParameterParser.getValue(node, "__HOST__");
            String port = ElementParameterParser.getValue(node, "__PORT__");
            String dbName = ElementParameterParser.getValue(node, "__DBNAME__");
            String userName = ElementParameterParser.getValue(node, "__USER__");
            String password = ElementParameterParser.getValue(node, "__PASS__");            
            boolean useExistingConnection = ("true").equals(ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__"));
            String connection = stringUtil.getString(ElementParameterParser.getValue(node, "__CONNECTION_MSSQL__"));
            String dbproperties = ElementParameterParser.getValue(node, "__PROPERTIES__");
            manager = new MSSQLManager(host, port, dbName, tableName, userName, password, useExistingConnection, connection, cid, dbproperties);
        } else if(("MYSQL").equals(dbType)) {
            String host = ElementParameterParser.getValue(node, "__HOST__");
            String port = ElementParameterParser.getValue(node, "__PORT__");
            String dbName = ElementParameterParser.getValue(node, "__DBNAME__");
            String userName = ElementParameterParser.getValue(node, "__USER__");
            String password = ElementParameterParser.getValue(node, "__PASS__");            
            boolean useExistingConnection = ("true").equals(ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__"));
            String connection = stringUtil.getString(ElementParameterParser.getValue(node, "__CONNECTION_MYSQL__"));
            boolean temporary = ("true").equals(ElementParameterParser.getValue(node, "__TEMPTABLE__"));
            manager = new MysqlManager(host, port, dbName, tableName, userName, password, useExistingConnection, connection, temporary, cid);
        } else if(("NETEZZA").equals(dbType)) {
            String host = ElementParameterParser.getValue(node, "__HOST__");
            String port = ElementParameterParser.getValue(node, "__PORT__");
            String dbName = ElementParameterParser.getValue(node, "__DBNAME__");
            String userName = ElementParameterParser.getValue(node, "__USER__");
            String password = ElementParameterParser.getValue(node, "__PASS__");            
            boolean useExistingConnection = ("true").equals(ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__"));
            String connection = stringUtil.getString(ElementParameterParser.getValue(node, "__CONNECTION_NETEZZA__"));
            manager = new NetezzaManager(host, port, dbName, tableName, userName, password, useExistingConnection, connection, cid);
        } else if(("DBORACLE").equals(dbType)) {
            String host = ElementParameterParser.getValue(node, "__HOST__");
            String port = ElementParameterParser.getValue(node, "__PORT__");
            String dbSchema = ElementParameterParser.getValue(node, "__SCHEMA_DB__");
            String dbName = ElementParameterParser.getValue(node, "__DBNAME__");
            String userName = ElementParameterParser.getValue(node, "__USER__");
            String password = ElementParameterParser.getValue(node, "__PASS__");
            String connectionType = ElementParameterParser.getValue(node, "__CONNECTION_TYPE__");
            boolean useExistingConnection = ("true").equals(ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__"));
            String connection = stringUtil.getString(ElementParameterParser.getValue(node, "__CONNECTION_ORACLE__"));
             String jdbcURL = ElementParameterParser.getValue(node, "__JDBC_URL__");
            String localServiceName = ElementParameterParser.getValue(node, "__LOCAL_SERVICE_NAME__");
            manager = new OracleManager(host, port, dbName, tableName, dbSchema, userName, password, connectionType, useExistingConnection, connection,cid,jdbcURL,localServiceName);
            } else if(("POSTGRE").equals(dbType)) {
            String host = ElementParameterParser.getValue(node, "__HOST__");
            String port = ElementParameterParser.getValue(node, "__PORT__");
            String dbSchema = ElementParameterParser.getValue(node, "__SCHEMA_DB__");
            String dbName = ElementParameterParser.getValue(node, "__DBNAME__");
            String userName = ElementParameterParser.getValue(node, "__USER__");
            String password = ElementParameterParser.getValue(node, "__PASS__");            
            boolean useExistingConnection = ("true").equals(ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__"));
            String connection = stringUtil.getString(ElementParameterParser.getValue(node, "__CONNECTION_POSTGRE__"));
            manager = new PostgreManager(host, port, dbName, tableName, dbSchema, userName, password, useExistingConnection, connection, cid);
        } else if(("POSTGREPLUS").equals(dbType)) {
            String host = ElementParameterParser.getValue(node, "__HOST__");
            String port = ElementParameterParser.getValue(node, "__PORT__");
            String dbSchema = ElementParameterParser.getValue(node, "__SCHEMA_DB__");
            String dbName = ElementParameterParser.getValue(node, "__DBNAME__");
            String userName = ElementParameterParser.getValue(node, "__USER__");
            String password = ElementParameterParser.getValue(node, "__PASS__");            
            boolean useExistingConnection = ("true").equals(ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__"));
            String connection = stringUtil.getString(ElementParameterParser.getValue(node, "__CONNECTION_POSTGREPLUS__"));
            manager = new PostgrePlusManager(host, port, dbName, tableName, dbSchema, userName, password, useExistingConnection, connection, cid);            
        } else if(("SQLITE").equals(dbType)) {
            String dbName = ElementParameterParser.getValue(node, "__DBFILE_SQLITE__");
            String userName = ElementParameterParser.getValue(node, "__USER__");
            String password = ElementParameterParser.getValue(node, "__PASS__");
            boolean useExistingConnection = ("true").equals(ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__"));
			String connection = stringUtil.getString(ElementParameterParser.getValue(node, "__CONNECTION_SQLITE__"));
            manager = new SQLiteManager(dbName, tableName, userName, password, cid,useExistingConnection, connection);
        } else if(("SYBASE").equals(dbType)) {
            String host = ElementParameterParser.getValue(node, "__HOST__");
            String port = ElementParameterParser.getValue(node, "__PORT__");
            String dbName = ElementParameterParser.getValue(node, "__DBNAME__");
            String userName = ElementParameterParser.getValue(node, "__USER__");
            String password = ElementParameterParser.getValue(node, "__PASS__");
            boolean useExistingConnection = ("true").equals(ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__"));
			String connection = stringUtil.getString(ElementParameterParser.getValue(node, "__CONNECTION_SYBASE__"));
            manager = new SybaseManager(host, port, dbName, tableName, userName, password, cid,useExistingConnection, connection);
        } else if(("TERADATA").equals(dbType)) {
            String host = ElementParameterParser.getValue(node, "__HOST__");
            String port = ElementParameterParser.getValue(node, "__PORT__");
            String dbName = ElementParameterParser.getValue(node, "__DBNAME__");
            String userName = ElementParameterParser.getValue(node, "__USER__");
            String password = ElementParameterParser.getValue(node, "__PASS__");
            boolean useExistingConnection = ("true").equals(ElementParameterParser.getValue(node, "__USE_EXISTING_CONNECTION__"));
			String connection = stringUtil.getString(ElementParameterParser.getValue(node, "__CONNECTION_TERADATA__"));
            manager = new TeradataManager(host, port, dbName, tableName, userName, password, cid,useExistingConnection, connection);
            manager.setNode(node);
        } else if(("ODBC").equals(dbType)) {
            String odbcName = ElementParameterParser.getValue(node, "__ODBCNAME__");
            String userName = ElementParameterParser.getValue(node, "__USER__");
            String password = ElementParameterParser.getValue(node, "__PASS__");
            manager = new ODBCManager(odbcName, tableName, userName, password, cid);
        }
        return manager;
    }
}
CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
INode node = (INode)codeGenArgument.getArgument();
List<IMetadataColumn> columnList = null;
List<IMetadataTable> metadatas = node.getMetadataList();
if(metadatas != null && metadatas.size() > 0) {
    IMetadataTable metadata = metadatas.get(0);
    columnList = metadata.getListColumns();
}
if(columnList != null && columnList.size() > 0) {
    boolean isRunInMultiThread = codeGenArgument.getIsRunInMultiThread();
    String dbType = ElementParameterParser.getValue(node,"__DBTYPE__");    
    ManagerFactory factory = new ManagerFactory();
    Manager manager = factory.createManager(dbType,node);
    manager.setNode(node);
    String tableAction = ElementParameterParser.getValue(node,"__TABLEACTION__");
    String cid = node.getUniqueName();    
    
    stringBuffer.append(TEXT_2);
    stringBuffer.append(TEXT_3);
    stringBuffer.append(manager.getConnection());
    
        if(("CREATE_TABLE").equals(tableAction)) {
            
    stringBuffer.append(TEXT_4);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_6);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(manager.getCreateTableSQL(columnList));
    stringBuffer.append(TEXT_8);
    
        } else {
            String tableName = ElementParameterParser.getValue(node,"__TABLE__");
            if("DBORACLE".equalsIgnoreCase(dbType)) {
			
    stringBuffer.append(TEXT_9);
    stringBuffer.append(manager.generateCode4TabelExist());
    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_11);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_14);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_18);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_19);
    
            } else {
            
    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_23);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_24);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_28);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(tableName);
    stringBuffer.append(TEXT_30);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_31);
    
            }
            
            if(("CREATE_IF_NOT_EXIST").equals(tableAction)) {
                
    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_35);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(manager.getCreateTableSQL(columnList));
    stringBuffer.append(TEXT_37);
    
            } else if(("DROP_IF_EXIST_CREATE").equals(tableAction)) {
                
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_39);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_40);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_42);
    stringBuffer.append(manager.getDropTableSQL());
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_45);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(manager.getCreateTableSQL(columnList));
    stringBuffer.append(TEXT_47);
    
            }
        }
        
    stringBuffer.append(TEXT_48);
    stringBuffer.append(manager.closeConnection());
    stringBuffer.append(TEXT_49);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_50);
    stringBuffer.append(manager.getCreateTableSQL(columnList));
    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid);
    stringBuffer.append(TEXT_52);
    
}

    stringBuffer.append(TEXT_53);
    stringBuffer.append(TEXT_54);
    return stringBuffer.toString();
  }
}
