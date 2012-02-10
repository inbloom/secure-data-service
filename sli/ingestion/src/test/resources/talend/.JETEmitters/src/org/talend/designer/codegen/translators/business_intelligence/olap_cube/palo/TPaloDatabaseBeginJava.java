package org.talend.designer.codegen.translators.business_intelligence.olap_cube.palo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.talend.core.model.metadata.IMetadataColumn;
import org.talend.core.model.metadata.IMetadataTable;
import org.talend.core.model.metadata.types.JavaTypesManager;
import org.talend.core.model.process.ElementParameterParser;
import org.talend.core.model.process.IConnection;
import org.talend.core.model.process.IConnectionCategory;
import org.talend.core.model.process.INode;
import org.talend.designer.codegen.config.CodeGeneratorArgument;

public class TPaloDatabaseBeginJava
{
  protected static String nl;
  public static synchronized TPaloDatabaseBeginJava create(String lineSeparator)
  {
    nl = lineSeparator;
    TPaloDatabaseBeginJava result = new TPaloDatabaseBeginJava();
    nl = null;
    return result;
  }

  public final String NL = nl == null ? (System.getProperties().getProperty("line.separator")) : nl;
  protected final String TEXT_1 = "";
  protected final String TEXT_2 = NL + NL + "org.talend.jpalo.palo p_";
  protected final String TEXT_3 = " = null;" + NL + "org.talend.jpalo.paloconnection pConn_";
  protected final String TEXT_4 = " = null;" + NL;
  protected final String TEXT_5 = NL + "\t// Use Existing" + NL + "\tp_";
  protected final String TEXT_6 = " = (org.talend.jpalo.palo)globalMap.get(\"";
  protected final String TEXT_7 = "\");" + NL + "\tpConn_";
  protected final String TEXT_8 = " =  (org.talend.jpalo.paloconnection)globalMap.get(\"";
  protected final String TEXT_9 = "\");";
  protected final String TEXT_10 = NL + "\t// Initialize jpalo" + NL + "\tp_";
  protected final String TEXT_11 = " = new org.talend.jpalo.palo(";
  protected final String TEXT_12 = ");" + NL + "\t// Open the connection" + NL + "\tpConn_";
  protected final String TEXT_13 = " = p_";
  protected final String TEXT_14 = ".connect(";
  protected final String TEXT_15 = ", ";
  protected final String TEXT_16 = ", ";
  protected final String TEXT_17 = ", ";
  protected final String TEXT_18 = ");";
  protected final String TEXT_19 = NL;
  protected final String TEXT_20 = NL + "\torg.talend.jpalo.palodatabases pDBs_";
  protected final String TEXT_21 = " = pConn_";
  protected final String TEXT_22 = ".getDatabases();" + NL + "\tpDBs_";
  protected final String TEXT_23 = ".createDatabase(";
  protected final String TEXT_24 = ");";
  protected final String TEXT_25 = NL + "\torg.talend.jpalo.palodatabases pDBs_";
  protected final String TEXT_26 = " = pConn_";
  protected final String TEXT_27 = ".getDatabases();" + NL + "\tif(null==pDBs_";
  protected final String TEXT_28 = ".getDatabase(";
  protected final String TEXT_29 = ")) pDBs_";
  protected final String TEXT_30 = ".createDatabase(";
  protected final String TEXT_31 = ");";
  protected final String TEXT_32 = NL + "\torg.talend.jpalo.palodatabases pDBs_";
  protected final String TEXT_33 = " = pConn_";
  protected final String TEXT_34 = ".getDatabases();" + NL + "\tif(null!=pDBs_";
  protected final String TEXT_35 = ".getDatabase(";
  protected final String TEXT_36 = ")) pDBs_";
  protected final String TEXT_37 = ".deleteDatabase(";
  protected final String TEXT_38 = ");" + NL + "\tpDBs_";
  protected final String TEXT_39 = ".createDatabase(";
  protected final String TEXT_40 = ");";
  protected final String TEXT_41 = NL + "\torg.talend.jpalo.palodatabases pDBs_";
  protected final String TEXT_42 = " = pConn_";
  protected final String TEXT_43 = ".getDatabases();" + NL + "\torg.talend.jpalo.palodatabase pDB_";
  protected final String TEXT_44 = " = pDBs_";
  protected final String TEXT_45 = ".getDatabase(";
  protected final String TEXT_46 = ");" + NL + "\tif(null != pDB_";
  protected final String TEXT_47 = ")" + NL + "\t    pDBs_";
  protected final String TEXT_48 = ".deleteDatabase(";
  protected final String TEXT_49 = ");\t" + NL + "\telse" + NL + "\t\t throw new RuntimeException (\"Database '\" + ";
  protected final String TEXT_50 = " + \"' not found. exiting...\");";
  protected final String TEXT_51 = NL + "globalMap.put(\"";
  protected final String TEXT_52 = "_DATABASENAME\",";
  protected final String TEXT_53 = ");";
  protected final String TEXT_54 = NL;

  public String generate(Object argument)
  {
    final StringBuffer stringBuffer = new StringBuffer();
    stringBuffer.append(TEXT_1);
    
	CodeGeneratorArgument codeGenArgument = (CodeGeneratorArgument) argument;
	INode node = (INode)codeGenArgument.getArgument();
	String cid = node.getUniqueName();

    boolean useExistingConnection = "true".equals(ElementParameterParser.getValue(node,"__USE_EXISTING_CONNECTION__"));


    String sServer = ElementParameterParser.getValue(node, "__SERVER__");
    String sServerport = ElementParameterParser.getValue(node, "__SERVERPORT__");
    String sUsername = ElementParameterParser.getValue(node, "__USERNAME__");
    String sPassword = ElementParameterParser.getValue(node, "__PASS__");

    String sDatabaseName = ElementParameterParser.getValue(node,"__DATABASE__");
    String sDatabaseAction = ElementParameterParser.getValue(node,"__DATABASE_ACTION__");
    

    String sDeploypalolibs = ElementParameterParser.getValue(node, "__DEPLOY_PALO_LIBS__");
    boolean bDeploypalolibs=false;
    if(sDeploypalolibs.equals("true"))bDeploypalolibs=true;


    stringBuffer.append(TEXT_2);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_3);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_4);
    
if(useExistingConnection){
	String connection = ElementParameterParser.getValue(node,"__CONNECTION__");
	String p = "p_" + connection;
	String pConn = "pConn_" + connection;


    stringBuffer.append(TEXT_5);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_6);
    stringBuffer.append(p);
    stringBuffer.append(TEXT_7);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_8);
    stringBuffer.append(pConn);
    stringBuffer.append(TEXT_9);
    
}else{

    stringBuffer.append(TEXT_10);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_11);
    stringBuffer.append(bDeploypalolibs);
    stringBuffer.append(TEXT_12);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_13);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_14);
    stringBuffer.append(sUsername);
    stringBuffer.append(TEXT_15);
    stringBuffer.append(sPassword);
    stringBuffer.append(TEXT_16);
    stringBuffer.append(sServer);
    stringBuffer.append(TEXT_17);
    stringBuffer.append(sServerport);
    stringBuffer.append(TEXT_18);
    
}

    stringBuffer.append(TEXT_19);
    
if(("CREATE").equals(sDatabaseAction)){

    stringBuffer.append(TEXT_20);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_21);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_22);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_23);
    stringBuffer.append(sDatabaseName);
    stringBuffer.append(TEXT_24);
    
}else if(("CREATE_IF_NOT_EXISTS").equals(sDatabaseAction)){

    stringBuffer.append(TEXT_25);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_26);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_27);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_28);
    stringBuffer.append(sDatabaseName);
    stringBuffer.append(TEXT_29);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_30);
    stringBuffer.append(sDatabaseName);
    stringBuffer.append(TEXT_31);
    
}else if(("DELETE_IF_EXISTS_AND_CREATE").equals(sDatabaseAction)){

    stringBuffer.append(TEXT_32);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_33);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_34);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_35);
    stringBuffer.append(sDatabaseName);
    stringBuffer.append(TEXT_36);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_37);
    stringBuffer.append(sDatabaseName);
    stringBuffer.append(TEXT_38);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_39);
    stringBuffer.append(sDatabaseName);
    stringBuffer.append(TEXT_40);
    
}else if(("DELETE").equals(sDatabaseAction)){

    stringBuffer.append(TEXT_41);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_42);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_43);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_44);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_45);
    stringBuffer.append(sDatabaseName);
    stringBuffer.append(TEXT_46);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_47);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_48);
    stringBuffer.append(sDatabaseName);
    stringBuffer.append(TEXT_49);
    stringBuffer.append(sDatabaseName);
    stringBuffer.append(TEXT_50);
    	
}

    stringBuffer.append(TEXT_51);
    stringBuffer.append(cid );
    stringBuffer.append(TEXT_52);
    stringBuffer.append(sDatabaseName);
    stringBuffer.append(TEXT_53);
    stringBuffer.append(TEXT_54);
    return stringBuffer.toString();
  }
}
